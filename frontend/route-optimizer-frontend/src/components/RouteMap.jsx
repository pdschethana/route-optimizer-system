import { MapContainer, TileLayer, Marker, Popup, Polyline } from 'react-leaflet';
import L from 'leaflet';
import 'leaflet/dist/leaflet.css';

delete L.Icon.Default.prototype._getIconUrl;
L.Icon.Default.mergeOptions({
  iconRetinaUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon-2x.png',
  iconUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon.png',
  shadowUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-shadow.png',
});

const startIcon = new L.Icon({
  iconUrl: 'https://raw.githubusercontent.com/pointhi/leaflet-color-markers/master/img/marker-icon-green.png',
  shadowUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-shadow.png',
  iconSize: [25, 41],
  iconAnchor: [12, 41],
  popupAnchor: [1, -34],
});

const RouteMap = ({ data }) => {
  const { startLat, startLng, startLocation, optimizedRoute } = data;

  const polylinePoints = [
    [startLat, startLng],
    ...optimizedRoute.map(job => [
      parseFloat(job.geo_lat),
      parseFloat(job.geo_lng)
    ])
  ];

  return (
    <MapContainer
      center={[startLat, startLng]}
      zoom={13}
      style={{ height: '500px', width: '100%', borderRadius: '12px' }}
    >
      <TileLayer
        attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a>'
        url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
      />

      <Marker position={[startLat, startLng]} icon={startIcon}>
        <Popup><strong>🏁 START</strong><br />{startLocation}</Popup>
      </Marker>

      {optimizedRoute.map((job) => (
        <Marker
          key={job.id}
          position={[parseFloat(job.geo_lat), parseFloat(job.geo_lng)]}
        >
          <Popup>
            <strong>#{job.visitOrder} — {job.store_name}</strong><br />
            {job.job_id} | {job.job_type}<br />
            📍 {job.territory}<br />
            📏 {job.distanceFromPrevious} km from previous
          </Popup>
        </Marker>
      ))}

      <Polyline positions={polylinePoints} color="blue" weight={3} dashArray="8" />
    </MapContainer>
  );
};

export default RouteMap;