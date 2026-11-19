
param(
    [string]$Tags = "@TC_01",
    [switch]$All
)

$ErrorActionPreference = "Stop"

$folder = Join-Path $env:USERPROFILE ".qnu-test"
$usernameFile = Join-Path $folder "username.txt"
$passwordFile = Join-Path $folder "password.txt"

try {
    if (-not (Test-Path $usernameFile) -or
            -not (Test-Path $passwordFile)) {
        throw "Chua thiet lap thong tin dang nhap kiem thu."
    }

    $env:TEST_USERNAME = (
    Get-Content $usernameFile -Raw
    ).Trim()

    $encryptedPassword = (
    Get-Content $passwordFile -Raw
    ).Trim()

    $secure = ConvertTo-SecureString -String $encryptedPassword

    try {
        $env:TEST_PASSWORD = (
        [System.Net.NetworkCredential]::new(
                "",
                $secure
        )
        ).Password
    }
    finally {
        $secure.Dispose()
    }

    if ($All) {
        Write-Host "Running full test suite..."

        # Khong truyen Tags de chay tat ca Scenario.
        & "$PSScriptRoot\run-tests.ps1"
    }
    else {
        Write-Host "Running tests with tags: $Tags"

        & "$PSScriptRoot\run-tests.ps1" -Tags $Tags
    }

    if ($LASTEXITCODE -ne 0) {
        throw "Test execution failed. Exit code: $LASTEXITCODE"
    }
}
finally {
    Remove-Item Env:TEST_USERNAME -ErrorAction SilentlyContinue
    Remove-Item Env:TEST_PASSWORD -ErrorAction SilentlyContinue
}
