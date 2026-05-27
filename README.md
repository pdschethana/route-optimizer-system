# Route Optimizer

A route optimization solution for field jobs starting from Sunquick Lanka Pvt Ltd.
Built with Spring Boot (Java 21) and React + Vite.

---

 Tech Stack

| Layer      | Technology              |
|------------|-------------------------|
| Backend    | Spring Boot 4.0.6, Java 21, Maven|
| Frontend   | React 18, Vite          |
| Map        | Leaflet.js + OpenStreetMap (free) |
| Algorithm  | Nearest Neighbor (Haversine distance) |
| Testing    | JUnit 5, Mockito        |

---

## Project Structure
oute-optimizer/
├── backend/route-optimizer-backend/   ← Spring Boot
└── frontend/route-optimizer-frontend/ ← React + Vite

---

## Setup Instructions

### Prerequisites
- Java 21
- Maven
- Node.js 18+
- npm

---

### Backend Setup

```bash
cd backend/route-optimizer-backend
./mvnw spring-boot:run
```

Backend runs at: http://localhost:8080

---

### Frontend Setup

```bash
cd frontend/route-optimizer-frontend
npm install
npm run dev
```

Frontend runs at: http://localhost:5173

---

## API Documentation

### Base URL

http://localhost:8080/api

### Endpoints

#### GET /api/route/optimize
Returns the optimized route for all pending jobs.

**Response:**
```json
{
    "estimatedTotalTime": "18 min",
    "message": "Route optimized successfully",
    "optimizedRoute": [
        {
            "distanceFromPrevious": 2.85,
            "estimatedTime": "4 min",
            "geo_lat": "7.2940094",
            "geo_lng": "79.8396836",
            "id": "65",
            "job_id": "JR#100065",
            "job_type": "Dispensing",
            "status": "Pending",
            "store_name": "DOLPHINE HOTEL",
            "territory": "Negombo",
            "vendor_id": "2",
            "visitOrder": 1
        },
        {
            "distanceFromPrevious": 0.0,
            "estimatedTime": "0 min",
            "geo_lat": "7.2940094",
            "geo_lng": "79.8396836",
            "id": "64",
            "job_id": "JR#100064",
            "job_type": "Registration",
            "status": "Pending",
            "store_name": "DOLPHINE HOTEL",
            "territory": "Negombo",
            "vendor_id": "2",
            "visitOrder": 2
        },
        {
            "distanceFromPrevious": 5.39,
            "estimatedTime": "8 min",
            "geo_lat": "7.2455846",
            "geo_lng": "79.8418470",
            "id": "63",
            "job_id": "JR#100063",
            "job_type": "Power",
            "status": "Pending",
            "store_name": "GOLDI SANDS",
            "territory": "Negombo",
            "vendor_id": "5",
            "visitOrder": 3
        },
        {
            "distanceFromPrevious": 3.55,
            "estimatedTime": "5 min",
            "geo_lat": "7.2373457",
            "geo_lng": "79.8729596",
            "id": "62",
            "job_id": "JR#100062",
            "job_type": "Hardware",
            "status": "Pending",
            "store_name": "SURANGA CATERS- NEGAMBO",
            "territory": "Negombo",
            "vendor_id": "8",
            "visitOrder": 4
        }
    ],
    "startLat": 7.2906,
    "startLng": 79.8653,
    "startLocation": "Sunquick Lanka Pvt Ltd",
    "totalDistanceKm": 11.79,
    "totalStops": 4
}
```

#### GET /api/route/health
Health check endpoint.

Response: Route Optimizer API is running ✅

---

## Algorithm

Uses the Nearest Neighbor algorithm:
1. Start at Sunquick Lanka Pvt Ltd
2. Find the closest unvisited job using Haversine formula
3. Visit it, then repeat until all jobs are visited
4. Returns the ordered visit sequence

---

## Running Tests

```bash
cd backend/route-optimizer-backend
./mvnw test
```

Expected: 9 tests passed