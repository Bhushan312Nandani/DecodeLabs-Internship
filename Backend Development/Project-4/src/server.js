// src/server.js
const express = require('express');
const cors = require('cors'); // Add this line
require('dotenv').config();

const Shield = require('./services/shield');
const Translator = require('./services/translator');

const app = express();
const PORT = process.env.PORT || 5000;

app.use(cors());
app.use(express.json());

app.get('/api/weather', async (req, res) => {
  const { city } = req.query;

  // Initial validation guard line
  if (!city) {
    return res.status(400).json({ error: "Query parameter 'city' must be explicitly declared." });
  }
  const apiKey = process.env.WEATHER_API_KEY;
  
  // [THE VAULT: APPEND & DISPATCH] Build the decoupled outbound query
  const upstreamUrl = `https://api.weatherapi.com/v1/current.json?key=${apiKey}&q=${encodeURIComponent(city)}`;

  try {
    console.log(`[FACADE HUB]: Processing request token for city: "${city}"`);

    // [THE MESSENGER & THE SHIELD]: Non-blocking execution with active error containment
    const response = await Shield.executeWithRetry(upstreamUrl, { method: 'GET' });

    // [THE TRANSLATOR]: Schema alignment and functional asset truncation
    const cleanPayload = Translator.alignWeatherSchema(response.data);

    // Return the polished payload contract to the application user
    return res.status(200).json(cleanPayload);

  } catch (error) {
    // [THE SHIELD: ERROR CONTAINMENT]: Intercept crash paths cleanly
    const topology = Shield.categorizeError(error);
    console.error(`[CRITICAL EXCEPTION BOUNDARY]: Error mapping handled. Status applied: ${topology.status}`);
    
    return res.status(topology.status).json({ error: topology.message });
  }
});

// System Activation
app.listen(PORT, () => {
  console.log(`
  =============================================================
  🚀 BACKEND MIDDLEMAN ENGINE RUNNING
  =============================================================
  🔒 Perimeters Monitored : THE VAULT Enabled
  ⚡ Transport Model      : THE MESSENGER (Axios Non-Blocking)
  🔄 Schema Transpiler   : THE TRANSLATOR Active
  🛡️ Shock Absorption    : THE SHIELD (Exponential Jitter Backoff)
  🟢 Gateway Terminal     : http://localhost:${PORT}
  =============================================================
  `);
});