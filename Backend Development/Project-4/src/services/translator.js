// src/services/translator.js

class Translator {
  static alignWeatherSchema(rawData) {
    if (!rawData || !rawData.location || !rawData.current) {
      throw new Error("Schema alignment failed: Incoming raw payload structure is corrupted.");
    }
    return {
      location: rawData.location.name,
      country: rawData.location.country,
      temperature_c: Math.round(rawData.current.temp_c),
      condition: rawData.current.condition.text,
      humidity: rawData.current.humidity,
      processedAt: new Date().toISOString()
    };
  }
}

module.exports = Translator;