# Lista mikroserwisow
$services = @(
    "auth-service",
    "orders-service",
    "products-service",
    "shipping-service",
    "warehouse-service",
    "analytics-service"
)

$servicesToRebuild = @()

Write-Host "Sprawdzanie zmian w mikroserwisach..." -ForegroundColor Cyan

foreach ($service in $services) {
    Write-Host "[->] Sprawdzanie zmian w $service..." -ForegroundColor White

    $diff = git diff --name-only HEAD -- $service

    if ([string]::IsNullOrWhiteSpace($diff)) {
        Write-Host "[OK] $service - brak zmian, pomijam." -ForegroundColor Green
    } else {
        Write-Host "[BUILD] $service - wykryto zmiany, buduje..." -ForegroundColor Yellow

        Push-Location $service
        mvn clean package -DskipTests
        Pop-Location

        $servicesToRebuild += $service
    }
}

if ($servicesToRebuild.Count -gt 0) {
    Write-Host "[->] Budowanie zmienionych serwis w Dockerze..." -ForegroundColor Cyan

    foreach ($service in $servicesToRebuild) {
        docker-compose build $service
    }

    docker-compose up -d

    Write-Host "`[DONE] Wystartowano zmienione serwisy: $($servicesToRebuild -join ', ')" -ForegroundColor Cyan
} else {
    Write-Host "`[OK] Brak zmian - nic nie zostało zbudowane." -ForegroundColor Green
}

