# Complete End-to-End Test Script
# Creates organizer, event, and sends bulk invitations

Write-Host "`n=== Event Planner - Complete Test ===" -ForegroundColor Cyan

$excelFile = "src\main\java\com\event_planner\event_planner\participants.xlsx"

# Configuration
$organizerEmail = "testorg_$(Get-Date -Format 'HHmmss')@event.com"
$organizerPassword = "TestPass123!"

Write-Host "`n[Step 1] Registering new organizer: $organizerEmail" -ForegroundColor Yellow
$registerBody = @{
    name = "Test Event Organizer"
    email = $organizerEmail
    password = $organizerPassword
} | ConvertTo-Json

try {
    $regResult = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/register/organizer" `
        -Method POST `
        -ContentType "application/json" `
        -Body $registerBody
    Write-Host "  Organizer registered: ID $($regResult.idUser)" -ForegroundColor Green
} catch {
    Write-Host "  Registration failed: $($_.Exception.Message)" -ForegroundColor Red
    Write-Host "  Trying to use existing organizer..." -ForegroundColor Yellow
    $organizerEmail = "manager@event.com"
    $organizerPassword = "manager123"
}

Write-Host "`n[Step 2] Logging in as organizer" -ForegroundColor Yellow
$loginBody = @{
    email = $organizerEmail
    password = $organizerPassword
} | ConvertTo-Json

try {
    $loginResult = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" `
        -Method POST `
        -ContentType "application/json" `
        -Body $loginBody
    
    $token = $loginResult.token
    $userId = $loginResult.user.id
    Write-Host "  Logged in successfully (User ID: $userId)" -ForegroundColor Green
    Write-Host "  User role: $($loginResult.user.role)" -ForegroundColor Gray
} catch {
    Write-Host "  Login failed: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

Write-Host "`n[Step 3] Creating event" -ForegroundColor Yellow
$eventBody = @{
    name = "Tech Conference $(Get-Date -Format 'yyyy-MM-dd HH:mm')"
    description = "Testing bulk invitation feature with real emails"
    location = "Virtual Event / Online"
    date = (Get-Date).AddDays(30).ToString("yyyy-MM-ddTHH:mm:ss")
} | ConvertTo-Json

Write-Host "  Event details:" -ForegroundColor Gray
Write-Host "  - Sending to: http://localhost:8080/api/event/create" -ForegroundColor Gray
Write-Host "  - Token length: $($token.Length)" -ForegroundColor Gray

try {
    $eventResult = Invoke-RestMethod -Uri "http://localhost:8080/api/event/create" `
        -Method POST `
        -ContentType "application/json" `
        -Headers @{Authorization = "Bearer $token"} `
        -Body $eventBody
    
    $eventId = $eventResult.id
    Write-Host "  Event created: ID $eventId" -ForegroundColor Green
    Write-Host "  Name: $($eventResult.name)" -ForegroundColor White
    Write-Host "  Date: $($eventResult.date)" -ForegroundColor White
} catch {
    Write-Host "  Event creation failed: $($_.Exception.Message)" -ForegroundColor Red
    Write-Host "  Status: $($_.Exception.Response.StatusCode)" -ForegroundColor Yellow
    
    # Fallback: ask for manual event ID
    Write-Host "`n  Please create an event manually or enter an existing event ID:" -ForegroundColor Yellow
    $eventId = Read-Host "  Event ID"
    if ([string]::IsNullOrWhiteSpace($eventId)) {
        Write-Host "  No event ID provided. Exiting." -ForegroundColor Red
        exit 1
    }
}

Write-Host "`n[Step 4] Checking Excel file" -ForegroundColor Yellow
if (-not (Test-Path $excelFile)) {
    Write-Host "  Excel file not found: $excelFile" -ForegroundColor Red
    exit 1
}
$fileSize = (Get-Item $excelFile).Length
Write-Host "  Found Excel file: $fileSize bytes" -ForegroundColor Green

Write-Host "`n[Step 5] Uploading Excel and sending invitations" -ForegroundColor Yellow

try {
    # Read file
    $fileBytes = [System.IO.File]::ReadAllBytes((Resolve-Path $excelFile))
    $boundary = [System.Guid]::NewGuid().ToString()
    $LF = "`r`n"
    
    # Build multipart form data
    $bodyLines = @(
        "--$boundary",
        "Content-Disposition: form-data; name=`"file`"; filename=`"participants.xlsx`"",
        "Content-Type: application/vnd.openxmlformats-officedocument.spreadsheetml.sheet$LF",
        [System.Text.Encoding]::GetEncoding("iso-8859-1").GetString($fileBytes),
        "--$boundary--$LF"
    ) -join $LF
    
    Write-Host "  Uploading to event ID: $eventId" -ForegroundColor Gray
    
    $response = Invoke-RestMethod `
        -Uri "http://localhost:8080/api/invitations/events/$eventId/bulk-import" `
        -Method POST `
        -ContentType "multipart/form-data; boundary=$boundary" `
        -Headers @{Authorization = "Bearer $token"} `
        -Body $bodyLines
    
    Write-Host "  SUCCESS!" -ForegroundColor Green
    Write-Host "`n  Results:" -ForegroundColor Cyan
    Write-Host "    Total Parsed: $($response.totalParsed)" -ForegroundColor White
    Write-Host "    Total Sent: $($response.totalSent)" -ForegroundColor White
    Write-Host "    Message: $($response.message)" -ForegroundColor White
    
    if ($response.emails -and $response.emails.Count -gt 0) {
        Write-Host "`n  Invitations sent to:" -ForegroundColor Cyan
        $response.emails | ForEach-Object { Write-Host "    $_ (sent)" -ForegroundColor Green }
    }
    
    Write-Host "`n=== TEST COMPLETE ===" -ForegroundColor Green
    Write-Host "Check your Gmail inbox for invitation emails!" -ForegroundColor Yellow
    
} catch {
    Write-Host "  FAILED!" -ForegroundColor Red
    Write-Host "  Error: $($_.Exception.Message)" -ForegroundColor Red
    if ($_.ErrorDetails.Message) {
        Write-Host "  Details: $($_.ErrorDetails.Message)" -ForegroundColor Yellow
    }
    exit 1
}
