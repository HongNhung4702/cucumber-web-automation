param (
    [string]$Tags,
    [switch]$Report
)

$ErrorActionPreference = "Stop"

$SecretsFile = Join-Path `
    $HOME `
    ".cucumber-web-automation\secrets.ps1"

$AllureResultsDirectory = Join-Path `
    $PSScriptRoot `
    "target\allure-results"

$AllureReportDirectory = Join-Path `
    $PSScriptRoot `
    "target\allure-report"

$AllureReportFile = Join-Path `
    $AllureReportDirectory `
    "index.html"

$OriginalMavenOpts = $env:MAVEN_OPTS


function Enable-Utf8Console {

    $Utf8 = New-Object `
        System.Text.UTF8Encoding($false)

    [Console]::InputEncoding = $Utf8
    [Console]::OutputEncoding = $Utf8

    $global:OutputEncoding = $Utf8

    if ($env:OS -eq "Windows_NT") {
        chcp 65001 | Out-Null
    }

    if ([string]::IsNullOrWhiteSpace(
            $env:MAVEN_OPTS
    )) {

        $env:MAVEN_OPTS =
        "-Dfile.encoding=UTF-8"

    } elseif (
    $env:MAVEN_OPTS -notmatch
            "-Dfile.encoding="
    ) {

        $env:MAVEN_OPTS =
        $env:MAVEN_OPTS +
                " -Dfile.encoding=UTF-8"
    }
}


function Restore-MavenOptions {

    if ($null -eq $OriginalMavenOpts) {

        Remove-Item Env:MAVEN_OPTS `
            -ErrorAction SilentlyContinue

    } else {

        $env:MAVEN_OPTS =
        $OriginalMavenOpts
    }
}


function Clear-TestCredentials {

    Remove-Item Env:TEST_USERNAME `
        -ErrorAction SilentlyContinue

    Remove-Item Env:TEST_PASSWORD `
        -ErrorAction SilentlyContinue
}


function Validate-SecretsFile {

    if (-not (Test-Path $SecretsFile)) {

        throw @"
Local credentials file was not found:

$SecretsFile
"@
    }
}


function Load-TestCredentials {

    . $SecretsFile

    if ([string]::IsNullOrWhiteSpace(
            $env:TEST_USERNAME
    )) {

        throw "TEST_USERNAME is not configured."
    }

    if ([string]::IsNullOrWhiteSpace(
            $env:TEST_PASSWORD
    )) {

        throw "TEST_PASSWORD is not configured."
    }
}


function Build-MavenTestArguments {

    $Arguments = @(
        "clean",
        "test"
    )

    if (-not [string]::IsNullOrWhiteSpace(
            $Tags
    )) {

        $Arguments +=
        "-Dcucumber.filter.tags=$Tags"
    }

    return $Arguments
}


function Show-TestHeader {

    Write-Host ""
    Write-Host "========================================"
    Write-Host " Cucumber Web Automation"
    Write-Host "========================================"

    if ([string]::IsNullOrWhiteSpace(
            $Tags
    )) {

        Write-Host "Test scope : ALL"

    } else {

        Write-Host "Test tags  : $Tags"
    }

    Write-Host "Secrets    : local environment"

    if ($Report) {

        Write-Host "Allure     : generate and open"

    } else {

        Write-Host "Allure     : results only"
    }

    Write-Host "Encoding   : UTF-8"
    Write-Host "========================================"
    Write-Host ""
}


function Generate-AllureReport {

    if (-not (Test-Path $AllureResultsDirectory)) {

        throw @"
Allure results were not found:

$AllureResultsDirectory
"@
    }

    Write-Host ""
    Write-Host "========================================"
    Write-Host " Generating Allure Report"
    Write-Host "========================================"
    Write-Host ""

    & mvn allure:report

    if ($LASTEXITCODE -ne 0) {
        throw "Allure report generation failed."
    }

    if (-not (Test-Path $AllureReportFile)) {

        throw @"
Allure report file was not found:

$AllureReportFile
"@
    }

    Write-Host ""
    Write-Host "Allure Report:"
    Write-Host $AllureReportFile
    Write-Host ""

    Start-Process $AllureReportFile
}


$TestExitCode = 1


try {

    # =========================
    # UTF-8
    # =========================

    Enable-Utf8Console

    # =========================
    # Credentials
    # =========================

    Validate-SecretsFile
    Load-TestCredentials

    # =========================
    # Test execution
    # =========================

    Show-TestHeader

    $MavenArguments =
    Build-MavenTestArguments

    & mvn @MavenArguments

    $TestExitCode =
    $LASTEXITCODE

    # Credentials are no longer required
    # after test execution.
    Clear-TestCredentials

    # =========================
    # Allure Report
    # =========================

    if ($Report) {
        Generate-AllureReport
    }

} finally {

    Clear-TestCredentials
    Restore-MavenOptions
}


exit $TestExitCode