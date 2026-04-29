# SmartLoad Optimization API

A stateless REST API that optimizes truck load planning by selecting the best combination of orders to maximize carrier payout while respecting truck weight, volume, hazmat, route, and time-window constraints.

## How to Run

```bash
git clone https://github.com/todipratik/load-planner.git
cd load-planner
docker compose up --build
```

Service will be available at `http://localhost:8080`

## Health Check

```bash
curl http://localhost:8080/actuator/health
```

Expected response:
```json
{"status": "UP"}
```

## API Endpoint

### POST /api/v1/load-optimizer/optimize

#### Example Request

```bash
curl -X POST http://localhost:8080/api/v1/load-optimizer/optimize \
  -H "Content-Type: application/json" \
  -d '{
    "truck": {
      "id": "truck-123",
      "maxWeightLbs": 44000,
      "maxVolumeCuft": 3000
    },
    "orders": [
      {
        "id": "ord-001",
        "payoutCents": 250000,
        "weightLbs": 18000,
        "volumeCuft": 1200,
        "origin": "Los Angeles, CA",
        "destination": "Dallas, TX",
        "pickupDate": "2025-12-05",
        "deliveryDate": "2025-12-09",
        "isHazmat": false
      },
      {
        "id": "ord-002",
        "payoutCents": 180000,
        "weightLbs": 12000,
        "volumeCuft": 900,
        "origin": "Los Angeles, CA",
        "destination": "Dallas, TX",
        "pickupDate": "2025-12-04",
        "deliveryDate": "2025-12-10",
        "isHazmat": false
      },
      {
        "id": "ord-003",
        "payoutCents": 320000,
        "weightLbs": 30000,
        "volumeCuft": 1800,
        "origin": "Los Angeles, CA",
        "destination": "Dallas, TX",
        "pickupDate": "2025-12-06",
        "deliveryDate": "2025-12-08",
        "isHazmat": true
      }
    ]
  }'
```

#### Example Response

```json
{
  "truckId": "truck-123",
  "selectedOrderIds": ["ord-001", "ord-002"],
  "totalPayoutCents": 430000,
  "totalWeightLbs": 30000.0,
  "totalVolumeCuft": 2100.0,
  "utilizationWeightPercent": 68.18,
  "utilizationVolumePercent": 70.0
}
```

## HTTP Status Codes

| Status | Reason |
|--------|--------|
| 200 | Success |
| 400 | Invalid input (missing fields, invalid dates, duplicate order IDs) |
| 413 | Too many orders (max 22) |
| 500 | Internal server error |

## Algorithm & Design

### Optimization Algorithm
The core optimizer uses **bitmask enumeration** — a classic approach for the 0/1 knapsack problem with n ≤ 22 orders (2²² ≈ 4M combinations).

For each bitmask combination it checks:
- Total weight does not exceed truck capacity
- Total volume does not exceed truck capacity
- All selected orders are time-window compatible

Time-window compatibility is **precomputed once** into a boolean matrix before the bitmask loop — avoiding repeated date parsing across millions of iterations.

### Constraints Handling
- **Route compatibility** — orders are grouped by origin|destination lane. The optimizer runs independently on each group and picks the best result.
- **Hazmat isolation** — within each route group, hazmat and non-hazmat orders are split into separate subgroups. They are never mixed.
- **Time windows** — two orders are compatible if their pickup-to-delivery windows overlap.
- **Money** — all monetary values are handled as integer cents (long) — never float or double.

### Edge Cases Handled
- Empty result when no feasible combination exists → returns 200 with empty selectedOrderIds
- Invalid date format → 400
- Delivery date before pickup date → 400
- Duplicate order IDs → 400
- Orders exceeding limit of 22 → 413