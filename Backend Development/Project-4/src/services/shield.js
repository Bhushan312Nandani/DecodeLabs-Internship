// src/services/shield.js
const axios = require('axios');

class Shield {
  static async executeWithRetry(url, options = {}, retries = 3, baseDelay = 1000) {
    for (let attempt = 0; attempt < retries; attempt++) {
      try {
        return await axios({
          url,
          ...options,
          timeout: 4000 // 4 seconds request isolation threshold
        });
      } catch (error) {
        const isLastAttempt = attempt === retries - 1;
        const is4xxClientError = error.response && error.response.status >= 400 && error.response.status < 500;
        if (is4xxClientError || isLastAttempt) {
          throw error;
        }
        const backoffFactor = Math.pow(2, attempt);
        const theoreticalDelay = baseDelay * backoffFactor;
        const jitter = Math.random() * 500; // Adds up to 500ms of structural variance
        const totalWaitTime = theoreticalDelay + jitter;

        console.warn(`[SHIELD]: Upstream failure detected. Backoff retry trigger ${attempt + 1}/${retries} launched. Staggering thread execution for ${Math.round(totalWaitTime)}ms...`);
        
        await new Promise(resolve => setTimeout(resolve, totalWaitTime));
      }
    }
  }

  static categorizeError(error) {
    if (error.code === 'ECONNABORTED') {
      return { status: 504, message: "Upstream system exceeded latency threshold bounds." };
    }
    if (error.response) {
      return { 
        status: error.response.status, 
        message: `Upstream domain error: ${error.response.data?.error?.message || 'Invalid operational transmission.'}`
      };
    }
    return { status: 500, message: "Internal backend gateway interface processing anomaly." };
  }
}

module.exports = Shield;