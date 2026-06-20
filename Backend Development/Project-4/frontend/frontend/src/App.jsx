import { useState } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { Cloud, Droplets, MapPin, Search, Thermometer, AlertCircle } from 'lucide-react';

export default function App() {
  const [city, setCity] = useState('');
  const [weather, setWeather] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const fetchWeather = async (e) => {
    e.preventDefault();
    if (!city.trim()) return;

    setLoading(true);
    setError('');
    setWeather(null);

    try {
      // Calling your Secure Backend Middleman!
      const res = await fetch(`http://localhost:5000/api/weather?city=${encodeURIComponent(city)}`);
      const data = await res.json();

      if (!res.ok) throw new Error(data.error || 'Failed to fetch');
      
      setWeather(data);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="flex items-center justify-center min-h-screen p-4">
      <motion.div 
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        className="w-full max-w-md p-8 backdrop-blur-xl bg-white/10 border border-white/20 rounded-3xl shadow-2xl"
      >
        <div className="flex items-center justify-center gap-3 mb-8">
          <motion.div 
            animate={{ rotate: 360 }} 
            transition={{ duration: 20, repeat: Infinity, ease: "linear" }}
          >
            <Cloud className="w-8 h-8 text-blue-400" />
          </motion.div>
          <h1 className="text-2xl font-bold tracking-wider text-transparent bg-clip-text bg-gradient-to-r from-blue-400 to-purple-400">
            ATMOSPHERE
          </h1>
        </div>

        <form onSubmit={fetchWeather} className="relative mb-6">
          <input
            type="text"
            value={city}
            onChange={(e) => setCity(e.target.value)}
            placeholder="Enter city name..."
            className="w-full py-4 pl-12 pr-4 text-white placeholder-gray-400 transition-all bg-black/20 border border-white/10 rounded-2xl focus:outline-none focus:ring-2 focus:ring-blue-500/50 focus:border-transparent"
          />
          <Search className="absolute w-5 h-5 text-gray-400 transform -translate-y-1/2 left-4 top-1/2" />
          <button 
            type="submit" 
            disabled={loading}
            className="absolute right-2 top-2 bottom-2 px-4 bg-blue-500 hover:bg-blue-600 rounded-xl transition-colors disabled:opacity-50"
          >
            {loading ? '...' : 'Go'}
          </button>
        </form>

        <AnimatePresence mode="wait">
          {error && (
            <motion.div 
              key="error"
              initial={{ opacity: 0, height: 0 }}
              animate={{ opacity: 1, height: 'auto' }}
              exit={{ opacity: 0, height: 0 }}
              className="flex items-center gap-2 p-4 mb-4 text-red-200 bg-red-500/20 rounded-2xl border border-red-500/30"
            >
              <AlertCircle className="w-5 h-5" />
              <p className="text-sm">{error}</p>
            </motion.div>
          )}

          {weather && (
            <motion.div
              key="result"
              initial={{ opacity: 0, scale: 0.9 }}
              animate={{ opacity: 1, scale: 1 }}
              exit={{ opacity: 0, scale: 0.9 }}
              transition={{ type: 'spring', bounce: 0.4 }}
              className="space-y-6"
            >
              <div className="flex flex-col items-center justify-center p-6 bg-black/20 rounded-3xl border border-white/5">
                <motion.div 
                  initial={{ y: 10, opacity: 0 }} 
                  animate={{ y: 0, opacity: 1 }} 
                  transition={{ delay: 0.1 }}
                  className="flex items-center gap-2 text-gray-300"
                >
                  <MapPin className="w-4 h-4" />
                  <span className="font-medium tracking-wide">{weather.location}, {weather.country}</span>
                </motion.div>
                
                <motion.div 
                  initial={{ scale: 0.5, opacity: 0 }}
                  animate={{ scale: 1, opacity: 1 }}
                  transition={{ type: "spring", delay: 0.2 }}
                  className="text-7xl font-bold my-4 bg-clip-text text-transparent bg-gradient-to-b from-white to-gray-400"
                >
                  {weather.temperature_c}°
                </motion.div>

                <motion.div 
                  initial={{ y: -10, opacity: 0 }}
                  animate={{ y: 0, opacity: 1 }}
                  transition={{ delay: 0.3 }}
                  className="text-lg text-blue-300 font-medium tracking-widest uppercase"
                >
                  {weather.condition}
                </motion.div>
              </div>

              <motion.div 
                initial={{ y: 20, opacity: 0 }}
                animate={{ y: 0, opacity: 1 }}
                transition={{ delay: 0.4 }}
                className="flex gap-4"
              >
                <div className="flex-1 flex items-center gap-3 p-4 bg-black/20 rounded-2xl border border-white/5">
                  <Droplets className="w-6 h-6 text-blue-400" />
                  <div>
                    <p className="text-xs text-gray-400 uppercase tracking-wider">Humidity</p>
                    <p className="font-bold text-lg">{weather.humidity}%</p>
                  </div>
                </div>
                <div className="flex-1 flex items-center gap-3 p-4 bg-black/20 rounded-2xl border border-white/5">
                  <Thermometer className="w-6 h-6 text-red-400" />
                  <div>
                    <p className="text-xs text-gray-400 uppercase tracking-wider">Status</p>
                    <p className="font-bold text-sm text-green-400">Secured</p>
                  </div>
                </div>
              </motion.div>
            </motion.div>
          )}
        </AnimatePresence>
      </motion.div>
    </div>
  );
}