# start_services.ps1 - Launch all VoltGrid microservices
$ErrorActionPreference = "Continue"

Write-Host "==========================================================" -ForegroundColor Cyan
Write-Host "   Starting VoltGrid PS046 Microservices Architecture     " -ForegroundColor Cyan
Write-Host "==========================================================" -ForegroundColor Cyan

$jvmArgs = "-Xms64m -Xmx192m -XX:TieredStopAtLevel=1"

# 1. Eureka Server
Write-Host "[1/7] Starting Eureka Server (Port 8761)..." -ForegroundColor Yellow
Start-Process java -ArgumentList "$jvmArgs -jar eureka-server/target/eureka-server-1.0.0-SNAPSHOT.jar" -WindowStyle Minimized
Start-Sleep -Seconds 8

# 2. Auth Service
Write-Host "[2/7] Starting Auth Service (Port 8081)..." -ForegroundColor Yellow
Start-Process java -ArgumentList "$jvmArgs -jar auth-service/target/auth-service-1.0.0-SNAPSHOT.jar" -WindowStyle Minimized

# 3. User Service
Write-Host "[3/7] Starting User Service (Port 8082)..." -ForegroundColor Yellow
Start-Process java -ArgumentList "$jvmArgs -jar user-service/target/user-service-1.0.0-SNAPSHOT.jar" -WindowStyle Minimized

# 4. Station Service
Write-Host "[4/7] Starting Station Service (Port 8083)..." -ForegroundColor Yellow
Start-Process java -ArgumentList "$jvmArgs -jar station-service/target/station-service-1.0.0-SNAPSHOT.jar" -WindowStyle Minimized

# 5. Billing Service
Write-Host "[5/7] Starting Billing Service (Port 8085)..." -ForegroundColor Yellow
Start-Process java -ArgumentList "$jvmArgs -jar billing-service/target/billing-service-1.0.0-SNAPSHOT.jar" -WindowStyle Minimized

# 6. Session Service
Write-Host "[6/7] Starting Session Service (Port 8084)..." -ForegroundColor Yellow
Start-Process java -ArgumentList "$jvmArgs -jar session-service/target/session-service-1.0.0-SNAPSHOT.jar" -WindowStyle Minimized

Start-Sleep -Seconds 6

# 7. API Gateway
Write-Host "[7/7] Starting API Gateway (Port 8080)..." -ForegroundColor Yellow
Start-Process java -ArgumentList "$jvmArgs -jar api-gateway/target/api-gateway-1.0.0-SNAPSHOT.jar" -WindowStyle Minimized

Write-Host "`nAll 7 services launched in background!" -ForegroundColor Green
Write-Host "Waiting 15 seconds for Eureka registration..." -ForegroundColor Cyan
Start-Sleep -Seconds 15

Write-Host "==========================================================" -ForegroundColor Green
Write-Host "VoltGrid Platform is READY!" -ForegroundColor Green
Write-Host "API Gateway:    http://localhost:8080" -ForegroundColor White
Write-Host "Eureka Registry: http://localhost:8761" -ForegroundColor White
Write-Host "Postman Collection: VoltGrid_PS046.postman_collection.json" -ForegroundColor Yellow
Write-Host "==========================================================" -ForegroundColor Green
