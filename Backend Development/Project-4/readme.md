# Weather API Backend

A Node.js/Express backend that fetches real-time weather data from OpenWeatherMap API and serves it via REST endpoints. Includes a frontend interface.

## What It Does

- Fetches current weather by city name
- Returns temperature, humidity, conditions
- Serves a frontend HTML page
- Environment variable-based API key management

## How to Run

```bash
npm install
cp .env.example .env      # Add your OpenWeatherMap API key
node server.js
```

Server runs on `http://localhost:5000`

## API Usage

```bash
GET /weather?city=London
```

Response:
```json
{
  "city": "London",
  "temp": "15°C",
  "humidity": "78%",
  "condition": "Cloudy"
}
```

## Environment Variables

Copy `.env.example` to `.env`:

```env
PORT=5000
WEATHER_API_KEY=your_openweathermap_api_key_here
```

Get a free API key at: [openweathermap.org](https://openweathermap.org/api)

## Project Structure

```
Project-4/
├── server.js          # Express server
├── index.html         # Frontend UI
├── frontend/          # Frontend assets
├── src/               # Source modules
├── .env.example       # Environment template
└── README.md
```
