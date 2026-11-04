
param (
    [string]$Tags,
    [switch]$Report,
    [switch]$NoReport
)

$ErrorActionPreference = "Stop"

$ProjectRoot = $PSScriptRoot

$AllureResultsDirectory = Join-Path `
    $ProjectRoot "target\allure-results"

$AllureReportFile = Join-Path `
    $ProjectRoot "target\allure-report\index.html"

$OriginalMavenOpts = $env:MAVEN_OPTS

function Enable-Utf8Console {
    $Utf8 = New-Object System.Text.UTF8Encoding($false)

    [Console]::InputEncoding = $Utf8
    [Console]::OutputEncoding = $Utf8
    $global:OutputEncoding = $Utf8

    if ($env:OS -eq "Windows_NT") {
        chcp 65001 | Out-Null
    }

    if ([string]::IsNullOrWhiteSpace($env:MAVEN_OPTS)) {
        $env:MAVEN_OPTS = "-Dfile.encoding=UTF-8"
    }
    elseif ($env:MAVEN_OPTS -notmatch "-Dfile.encoding=") {
        $env:MAVEN_OPTS += " -Dfile.encoding=UTF-8"
    }
}

function Load-TestCredentials {
    if ([string]::IsNullOrWhiteSpace($env:TEST_USERNAME)) {
        $env:TEST_USERNAME = Read-Host `
            "Nhap ten dang nhap kiem thu"
    }

    if ([string]::IsNullOrWhiteSpace($env:TEST_PASSWORD)) {
        $SecurePassword = Read-Host `
            "Nhap mat khau kiem thu" -AsSecureString

        try {
            $env:TEST_PASSWORD = (
            [System.Net.NetworkCredential]::new(
                    "",
                    $SecurePassword
            )
            ).Password
        }
        finally {
            $SecurePassword.Dispose()
        }
    }

    if ([string]::IsNullOrWhiteSpace($env:TEST_USERNAME) -or
            [string]::IsNullOrWhiteSpace($env:TEST_PASSWORD)) {
        throw "Test credentials are required."
    }
}

function Clear-TestCredentials {
    Remove-Item Env:TEST_USERNAME `
        -ErrorAction SilentlyContinue

    Remove-Item Env:TEST_PASSWORD `
        -ErrorAction SilentlyContinue
}

function Restore-MavenOptions {
    if ($null -eq $OriginalMavenOpts) {
        Remove-Item Env:MAVEN_OPTS `
            -ErrorAction SilentlyContinue
    }
    else {
        $env:MAVEN_OPTS = $OriginalMavenOpts
    }
}

function Show-TestHeader {
    Write-Host ""
    Write-Host "========================================"
    Write-Host " Cucumber Web Automation"
    Write-Host "========================================"

    if ([string]::IsNullOrWhiteSpace($Tags)) {
        Write-Host "Test scope : ALL"
    }
    else {
        Write-Host "Test tags  : $Tags"
    }

    Write-Host "Credentials: environment / interactive"
    Write-Host "Encoding   : UTF-8"

    if ($NoReport) {
        Write-Host "Allure     : disabled"
    }
    else {
        Write-Host "Allure     : automatic"
    }

    Write-Host "========================================"
    Write-Host ""
}

function Generate-AllureReport {

    if (-not (Test-Path $AllureResultsDirectory)) {
        Write-Warning "Allure results directory was not found."
        return
    }

    $ResultFiles = @(
        Get-ChildItem `
            -Path $AllureResultsDirectory `
            -Filter "*-result.json" `
            -File
    )

    if ($ResultFiles.Count -eq 0) {
        Write-Warning "No Allure test results found."
        return
    }

    Write-Host ""
    Write-Host "========================================"
    Write-Host " Generating Allure Report"
    Write-Host "========================================"
    Write-Host "Result files: $($ResultFiles.Count)"

    & mvn allure:report

    if ($LASTEXITCODE -ne 0) {
        throw "Allure report generation failed."
    }

    if (-not (Test-Path $AllureReportFile)) {
        throw "Allure report index.html was not found."
    }

    Write-Host ""
    Write-Host "Allure Report generated successfully:"
    Write-Host $AllureReportFile
    Write-Host ""

    Start-Process $AllureReportFile
}

$TestExitCode = 1

try {
    Enable-Utf8Console
    Load-TestCredentials
    Show-TestHeader

    Push-Location $ProjectRoot

    try {
        $MavenArguments = @("clean", "test")

        if (-not [string]::IsNullOrWhiteSpace($Tags)) {
            $MavenArguments += "-Dcucumber.filter.tags=$Tags"
        }

        & mvn @MavenArguments

        $TestExitCode = $LASTEXITCODE

        Clear-TestCredentials

        # Tự động tạo báo cáo sau khi chạy test.
        # Vẫn xuất báo cáo nếu có test thất bại.
        if (-not $NoReport) {
            Generate-AllureReport
        }
    }
    finally {
        Pop-Location
    }
}
finally {
    Clear-TestCredentials
    Restore-MavenOptions
}

exit $TestExitCode
