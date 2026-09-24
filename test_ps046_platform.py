import urllib.request
import urllib.error
import json
import time

GATEWAY_BASE = "http://localhost:8080"

def make_req(endpoint, method="GET", data=None, token=None, headers_override=None):
    url = GATEWAY_BASE + endpoint
    headers = {"Content-Type": "application/json"}
    if headers_override is not None:
        headers = headers_override
    elif token:
        headers["Authorization"] = f"Bearer {token}"
    
    body = json.dumps(data).encode("utf-8") if data is not None else None
    req = urllib.request.Request(url, data=body, headers=headers, method=method)
    try:
        with urllib.request.urlopen(req) as resp:
            status = resp.status
            content = resp.read().decode("utf-8")
            try:
                parsed = json.loads(content)
            except:
                parsed = content
            return status, parsed
    except urllib.error.HTTPError as e:
        content = e.read().decode("utf-8")
        try:
            parsed = json.loads(content)
        except:
            parsed = content
        return e.code, parsed
    except Exception as e:
        return 0, str(e)

print("=" * 60)
print("STARTING COMPLETE PS046 RUNTIME & BUSINESS FLOW TEST SUITE")
print("=" * 60)

# TEST 1: Auth Registration
test_user = f"driver_volt_{int(time.time())}"
print(f"\n[1] TEST AUTH REGISTRATION (POST /auth/register) with user {test_user}")
status, resp = make_req("/auth/register", method="POST", data={
    "username": test_user,
    "password": "Password123!",
    "role": "ROLE_USER"
})
print(f"Status: {status}, Response: {resp}")
assert status == 201, f"Expected 201, got {status}"

# TEST 2: Auth Login & JWT Token Retrieval
print(f"\n[2] TEST AUTH LOGIN (POST /auth/login) with user {test_user}")
status, resp = make_req("/auth/login", method="POST", data={
    "username": test_user,
    "password": "Password123!"
})
print(f"Status: {status}, Token received: {bool(resp.get('token'))}")
assert status == 200, f"Expected 200, got {status}"
jwt_token = resp["token"]
print(f"JWT Token preview: {jwt_token[:30]}...")

# TEST 3: JWT Security Matrix on Protected API (/users)
print("\n[3] TEST JWT SECURITY FILTER MATRIX (/users)")

# 3.1 No Token -> 401
s, r = make_req("/users", method="GET")
print(f"3.1 No Token -> Status: {s} (Expected: 401)")
assert s == 401, f"Expected 401, got {s}"

# 3.2 Non-Bearer Header -> 401
s, r = make_req("/users", method="GET", headers_override={"Authorization": "Basic 12345"})
print(f"3.2 Non-Bearer Header -> Status: {s} (Expected: 401)")
assert s == 401, f"Expected 401, got {s}"

# 3.3 Malformed Token -> 401
s, r = make_req("/users", method="GET", headers_override={"Authorization": "Bearer malformed.token.xyz"})
print(f"3.3 Malformed Token -> Status: {s} (Expected: 401)")
assert s == 401, f"Expected 401, got {s}"

# 3.4 Valid Token -> 200
s, r = make_req("/users", method="GET", token=jwt_token)
print(f"3.4 Valid Bearer Token -> Status: {s} (Expected: 200)")
assert s == 200, f"Expected 200, got {s}"

# TEST 4: User Service CRUD
print("\n[4] TEST USER SERVICE CRUD (/users)")
user_email = f"suraj_{int(time.time())}@voltgrid.com"
s, user_created = make_req("/users", method="POST", token=jwt_token, data={
    "name": "Suraj Kumar",
    "email": user_email,
    "phone": "+91 9876543210",
    "role": "ROLE_USER",
    "vehicleId": "VOLT-EV-882",
    "accountStatus": "ACTIVE"
})
print(f"Create User -> Status: {s}, ID: {user_created.get('id')}")
assert s == 201, f"Expected 201, got {s}"
user_id = user_created["id"]

s, user_fetched = make_req(f"/users/{user_id}", method="GET", token=jwt_token)
print(f"Get User by ID -> Status: {s}, Name: {user_fetched.get('name')}")
assert s == 200 and user_fetched["name"] == "Suraj Kumar"

s, user_updated = make_req(f"/users/{user_id}", method="PUT", token=jwt_token, data={
    "name": "Suraj Kumar (Verified)",
    "phone": "+91 9876543211"
})
print(f"Update User -> Status: {s}, Updated Name: {user_updated.get('name')}")
assert s == 200 and user_updated["name"] == "Suraj Kumar (Verified)"

# 404 Exception Test on Users
s, err_resp = make_req("/users/99999", method="GET", token=jwt_token)
print(f"Get Non-existent User (/users/99999) -> Status: {s} (Expected: 404)")
assert s == 404, f"Expected 404, got {s}"

# TEST 5: Station Service CRUD & Availability Setup
print("\n[5] TEST STATION SERVICE CRUD & AVAILABILITY (/stations)")
s, station_created = make_req("/stations", method="POST", token=jwt_token, data={
    "stationName": "VoltGrid HyperCharger Alpha",
    "location": "Sector 62, Cyber Hub",
    "connectorType": "CCS2",
    "chargingCapacity": 120.0,
    "status": "AVAILABLE",
    "totalConnectors": 1
})
print(f"Create Station -> Status: {s}, ID: {station_created.get('id')}, Status: {station_created.get('status')}")
assert s == 201, f"Expected 201, got {s}"
station_id = station_created["id"]
assert station_created["availableConnectors"] == 1
assert station_created["status"] == "AVAILABLE"

# 404 Exception Test on Stations
s, err_resp = make_req("/stations/99999", method="GET", token=jwt_token)
print(f"Get Non-existent Station (/stations/99999) -> Status: {s} (Expected: 404)")
assert s == 404, f"Expected 404, got {s}"

# TEST 6: Plug-In Workflow & Grid Load Balancing
print("\n[6] TEST PLUG-IN WORKFLOW & CHARGING SESSION CREATION (/sessions)")
s, session_created = make_req("/sessions", method="POST", token=jwt_token, data={
    "userId": user_id,
    "stationId": station_id,
    "chargingRate": 60.0
})
print(f"Plug-In / Create Session -> Status: {s}, Session ID: {session_created.get('id')}, Status: {session_created.get('status')}")
assert s == 201, f"Expected 201, got {s}"
session_id = session_created["id"]
assert session_created["status"] == "ACTIVE"

# Verify station is now OCCUPIED and grid load increased to 60.0 kW
s, station_occupied = make_req(f"/stations/{station_id}", method="GET", token=jwt_token)
print(f"Station after Plug-in -> Connectors Available: {station_occupied.get('availableConnectors')}, Status: {station_occupied.get('status')}, Grid Load: {station_occupied.get('gridLoad')} kW")
assert station_occupied["availableConnectors"] == 0
assert station_occupied["status"] == "OCCUPIED"
assert station_occupied["gridLoad"] == 60.0

# TEST 7: Double-Booking / Station Unavailable Prevention Test
print("\n[7] TEST CONCURRENCY / DOUBLE-BOOKING PROTECTION")
s, reject_resp = make_req("/sessions", method="POST", token=jwt_token, data={
    "userId": user_id,
    "stationId": station_id,
    "chargingRate": 60.0
})
print(f"Second Plug-in on Occupied Station -> Status: {s} (Expected: 409 Conflict / Service Error)")
assert s in [409, 500, 502], f"Expected rejection status, got {s}"
print("Successfully blocked second plug-in request when station has 0 connectors available!")

# TEST 8: Energy Telemetry Ingestion & Dynamic Billing Reflection
print("\n[8] TEST REAL-TIME ENERGY TELEMETRY & DYNAMIC BILLING")
# Ingest 30.0 kWh consumed
s, energy_updated = make_req(f"/sessions/{session_id}/energy", method="PUT", token=jwt_token, data={
    "energyConsumed": 30.0
})
print(f"Energy Update -> Status: {s}, Consumed: {energy_updated.get('energyConsumed')} kWh")
assert s == 200 and energy_updated["energyConsumed"] == 30.0

# Verify Billing record reflects dynamic rate (30 kWh * 15.0 rate = 450.0)
s, billing_info = make_req(f"/billing/session/{session_id}", method="GET", token=jwt_token)
print(f"Billing for Session -> Status: {s}, Energy: {billing_info.get('energyConsumed')} kWh, Rate: {billing_info.get('rate')}, Amount: {billing_info.get('amount')}, Status: {billing_info.get('billingStatus')}")
assert s == 200
assert billing_info["energyConsumed"] == 30.0
assert billing_info["amount"] == 450.0
assert billing_info["billingStatus"] == "PENDING"

# TEST 9: Session Completion & Station Release Lifecycle
print("\n[9] TEST SESSION COMPLETION & STATION RELEASE")
s, session_completed = make_req(f"/sessions/{session_id}/complete", method="PUT", token=jwt_token)
print(f"Complete Session -> Status: {s}, Session State: {session_completed.get('status')}")
assert s == 200 and session_completed["status"] == "COMPLETED"

# Verify station is restored to AVAILABLE with 0 grid load
s, station_restored = make_req(f"/stations/{station_id}", method="GET", token=jwt_token)
print(f"Station after Session Complete -> Available Connectors: {station_restored.get('availableConnectors')}, Status: {station_restored.get('status')}, Grid Load: {station_restored.get('gridLoad')} kW")
assert station_restored["availableConnectors"] == 1
assert station_restored["status"] == "AVAILABLE"
assert station_restored["gridLoad"] == 0.0

# TEST 10: Payment / Billing Status Update
print("\n[10] TEST BILLING PAYMENT (PUT /billing/{id}/pay)")
billing_id = billing_info["id"]
s, billing_paid = make_req(f"/billing/{billing_id}/pay", method="PUT", token=jwt_token)
print(f"Pay Bill -> Status: {s}, Billing Status: {billing_paid.get('billingStatus')}")
assert s == 200 and billing_paid["billingStatus"] == "PAID"

# TEST 11: List endpoints and DELETE verification
print("\n[11] TEST GET ALL AND DELETE CRUD ENDPOINTS")
s, users_list = make_req("/users", method="GET", token=jwt_token)
assert s == 200 and isinstance(users_list, list) and len(users_list) > 0
print(f"GetAll Users -> Count: {len(users_list)}")

s, stations_list = make_req("/stations", method="GET", token=jwt_token)
assert s == 200 and isinstance(stations_list, list) and len(stations_list) > 0
print(f"GetAll Stations -> Count: {len(stations_list)}")

s, del_user = make_req(f"/users/{user_id}", method="DELETE", token=jwt_token)
print(f"DELETE User -> Status: {del_user}")
assert s == 200

s, user_after_del = make_req(f"/users/{user_id}", method="GET", token=jwt_token)
assert s == 404
print(f"Verify Deleted User 404 -> Confirmed: {s}")

print("\n" + "=" * 60)
print("ALL 11 VERIFICATION FLOWS PASSED WITH 100% SUCCESS!")
print("=" * 60)
