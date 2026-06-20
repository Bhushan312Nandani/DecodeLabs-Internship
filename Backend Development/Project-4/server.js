// server.js
const express = require('express');
const axios = require('axios');
require('dotenv').config(); // Loads environment variables into process.env

const app = express();
const PORT = process.env.PORT || 5000;

// Middleware to parse incoming JSON payloads
app.use(express.json());

/**
 * Endpoint: Get Weather Data
 * Acts as the Backend Middleman
 */
app.get('/api/weather', async (req, res) => {
    // 1. Extract query parameters from frontend request
    const { city } = req.query;

    if (!city) {
        return res.status(400).json({ error: 'City parameter is required.' });
    }

    // [ROLE 1: THE VAULT] Retrieve the secret key safely from the environment
    const apiKey = process.env.WEATHER_API_KEY; 

    // Define the external third-party API endpoint URL
    const externalApiUrl = `https://api.weatherapi.com/v1/current.json?key=${apiKey}&q=${city}`;

    // [ROLE 2 & 4: THE MESSENGER & THE SHIELD] Managing latency and handling unexpected shocks
    try {
        // Axios automatically rejects promises on 4xx/5xx codes and applies built-in timeouts
        const response = await axios.get(externalApiUrl, {
            timeout: 5000 // 5 seconds timeout to prevent unshielded cascading hangs
        });

        // [ROLE 3: THE TRANSLATOR] Unpack data and filter massive 100+ parameter payloads
        const rawData = response.data; // Axios handles automatic JSON parsing

        const cleanedPayload = {
            location: rawData.location.name,
            country: rawData.location.country,
            temperature_c: rawData.current.temp_c,
            condition: rawData.current.condition.text,
            humidity: rawData.current.humidity,
            processedAt: new Date().toISOString()
        };

        // Return the clean, lightweight payload to our user
        return res.json(cleanedPayload);

    } catch (error) {
        // [ROLE 4: THE SHIELD] Intercept failures gracefully instead of freezing the interface
        console.error('External API Error:', error.message);

        if (error.code === 'ECONNABORTED') {
            return res.status(504).json({ error: 'The upstream server took too long to respond.' });
        }

        if (error.response) {
            // Third-party API responded with a status outside of the 2xx range
            return res.status(error.response.status).json({ 
                error: `Upstream service error: ${error.response.data.error?.message || 'Invalid Request'}` 
            });
        } else {
            // Network failure or something else went wrong
            return res.status(500).json({ error: 'An unexpected backend processing error occurred.' });
        }
    }
});

// Start the middleman server
app.listen(PORT, () => {
    console.log(`🚀 Backend Middleman running on http://localhost:${PORT}`);
});