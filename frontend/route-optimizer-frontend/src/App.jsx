

import { useState, useEffect } from 'react';
import { getOptimizedRoute } from './services/api';
import RouteMap from './components/RouteMap';
import JobList from './components/JobList';
import './index.css';

function App() {
  const [routeData, setRouteData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    getOptimizedRoute()
      .then(data => {
        setRouteData(data);
        setLoading(false);
      })
      .catch(() => {
        setError('Failed to load route. Is the backend running on port 8080?');
        setLoading(false);
      });
  }, []);

  if (loading) return <div className="center">⏳ Calculating optimized route...</div>;
  if (error)   return <div className="center error">❌ {error}</div>;

  return (
    <div className="app">
      <header>
        <h1>🗺️ Route Optimizer</h1>
        <p>Starting from <strong>{routeData.startLocation}</strong></p>
        <div className="stats">
          <span>📍 {routeData.totalStops} stops</span>
          <span>📏 {routeData.totalDistanceKm} km total</span>
          <span>⏱️ ~{routeData.estimatedTotalTime}</span>
        </div>
      </header>
      <main>
        <RouteMap data={routeData} />
        <JobList route={routeData.optimizedRoute} startLocation={routeData.startLocation} />
      </main>
    </div>
  );
}

export default App;