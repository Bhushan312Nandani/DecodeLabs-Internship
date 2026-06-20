package com.decodelabs.exchange.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Fetches live exchange rates from the Frankfurter API.
 *
 * ── API Info ──────────────────────────────────────────────────────
 * Provider  : Frankfurter (frankfurter.app)
 * Source    : European Central Bank (ECB)
 * Key       : NONE — completely free, no registration
 * Endpoint  : GET https://api.frankfurter.app/latest?from=USD
 * Response  : { "base":"USD", "date":"2024-01-15", "rates":{"EUR":0.92,...} }
 *
 * ── Caching Strategy ─────────────────────────────────────────────
 * ECB rates update once per business day. We cache for 60 minutes
 * to avoid unnecessary API calls. On first run, rates are fetched
 * immediately. The refresh button forces a new fetch.
 *
 * ── Fallback ─────────────────────────────────────────────────────
 * If the API is unreachable (no internet, timeout), hardcoded
 * representative rates are used. The UI will show "⚠ Offline Rates".
 *
 * USD is ALWAYS 1.0 (it is the base currency for all rates).
 */
public class RateService {

    // Frankfurter migrated from frankfurter.app → frankfurter.dev/v1/ (old domain returns HTTP 301)
    // Query param also changed: ?from=USD → ?base=USD
    private static final String API_URL      = "https://api.frankfurter.dev/v1/latest?base=USD";
    private static final int    CACHE_MINS   = 60;
    private static final int    TIMEOUT_SECS = 10;

    private final HttpClient   httpClient;
    private final ObjectMapper objectMapper;

    // In-memory cache
    private Map<String, Double> cachedRates = new HashMap<>();
    private LocalDateTime       lastFetched = null;
    private boolean             usingLiveRates = false;

    // ── Fallback rates (representative values, used when offline) ────────────
    private static final Map<String, Double> FALLBACK_RATES;
    static {
        Map<String, Double> r = new HashMap<>();
        r.put("USD", 1.0);      r.put("EUR", 0.9234);   r.put("GBP", 0.7856);
        r.put("INR", 94.32);    r.put("JPY", 149.72);   r.put("AUD", 1.5312);
        r.put("CAD", 1.3567);   r.put("CHF", 0.8923);   r.put("CNY", 7.2456);
        r.put("SGD", 1.3421);   r.put("AED", 3.6725);   r.put("SAR", 3.7502);
        r.put("MXN", 17.1234);  r.put("BRL", 4.9876);   r.put("KRW", 1325.40);
        r.put("HKD", 7.8234);   r.put("NOK", 10.5612);  r.put("SEK", 10.4523);
        r.put("DKK", 6.8923);   r.put("NZD", 1.6234);   r.put("ZAR", 18.7623);
        r.put("THB", 35.4523);  r.put("MYR", 4.7123);   r.put("IDR", 15623.0);
        r.put("PHP", 56.3412);  r.put("TRY", 30.8912);  r.put("PLN", 4.0123);
        r.put("CZK", 22.8912);  r.put("HUF", 355.12);   r.put("BGN", 1.8056);
        r.put("RON", 4.5923);   r.put("ISK", 136.5);    r.put("ILS", 3.7123);
        FALLBACK_RATES = Collections.unmodifiableMap(r);
    }

    public RateService() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(TIMEOUT_SECS))
                .followRedirects(HttpClient.Redirect.NORMAL) // Follow HTTP 301/302 redirects
                .build();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Returns exchange rates, using the cache if still valid.
     * On first call (or expired cache), fetches from the Frankfurter API.
     *
     * @return unmodifiable map: currency code → rate relative to USD (USD = 1.0)
     */
    public Map<String, Double> getRates() {
        if (isCacheValid()) {
            return Collections.unmodifiableMap(cachedRates);
        }
        return fetchAndCache();
    }

    /**
     * Forces a fresh fetch from the API, ignoring the cache.
     * Called by the UI Refresh button.
     *
     * @return freshly fetched (or fallback) rates
     */
    public Map<String, Double> forceRefresh() {
        lastFetched = null; // invalidate cache
        return fetchAndCache();
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private boolean isCacheValid() {
        return !cachedRates.isEmpty()
                && lastFetched != null
                && LocalDateTime.now().isBefore(lastFetched.plusMinutes(CACHE_MINS));
    }

    private Map<String, Double> fetchAndCache() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .GET()
                    .timeout(Duration.ofSeconds(TIMEOUT_SECS))
                    .header("Accept", "application/json")
                    .build();

            HttpResponse<String> response = httpClient.send(
                    request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                Map<String, Double> parsed = parseRates(response.body());
                cachedRates    = new HashMap<>(parsed);
                lastFetched    = LocalDateTime.now();
                usingLiveRates = true;
                System.out.printf("[RateService] ✓ Live rates fetched at %s (%d currencies)%n",
                        lastFetched.format(DateTimeFormatter.ofPattern("HH:mm:ss")),
                        cachedRates.size());
                return Collections.unmodifiableMap(cachedRates);

            } else {
                System.err.printf("[RateService] API returned HTTP %d — using fallback rates%n",
                        response.statusCode());
                return useFallback();
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("[RateService] Request interrupted — using fallback rates");
            return useFallback();

        } catch (Exception e) {
            System.err.printf("[RateService] Network error (%s) — using fallback rates%n",
                    e.getClass().getSimpleName());
            return useFallback();
        }
    }

    /**
     * Parses the Frankfurter JSON response.
     * Response shape: {"base":"USD","date":"2024-01-15","rates":{"EUR":0.92,...}}
     * USD is added manually as 1.0 (it's the base, so not included in the response).
     */
    private Map<String, Double> parseRates(String json) throws Exception {
        JsonNode root = objectMapper.readTree(json);
        Map<String, Double> rates = new HashMap<>();

        rates.put("USD", 1.0); // base currency — always 1.0

        JsonNode ratesNode = root.get("rates");
        if (ratesNode != null && ratesNode.isObject()) {
            ratesNode.fields().forEachRemaining(entry ->
                    rates.put(entry.getKey(), entry.getValue().asDouble()));
        }
        return rates;
    }

    private Map<String, Double> useFallback() {
        cachedRates    = new HashMap<>(FALLBACK_RATES);
        lastFetched    = LocalDateTime.now();
        usingLiveRates = false;
        return Collections.unmodifiableMap(cachedRates);
    }

    // ── Status accessors (used by UI status bar) ──────────────────────────────

    public boolean isUsingLiveRates()        { return usingLiveRates; }
    public LocalDateTime getLastFetchedTime(){ return lastFetched; }
    public int getCachedCurrencyCount()      { return cachedRates.size(); }
}
