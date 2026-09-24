# stop_services.ps1 - Gracefully terminate all running Java microservices
Write-Host "Stopping all running VoltGrid Java microservices..." -ForegroundColor Yellow
Get-Process -Name java -ErrorAction SilentlyContinue | Stop-Process -Force
Write-Host "All Java microservices stopped." -ForegroundColor Green
