
param(
    [Parameter(Mandatory=$false,HelpMessage="uri to download from")][string]$uri,
    [Parameter(Mandatory=$true,HelpMessage="Fully qualified file name to save file to")][string]$dest,
    [Parameter(Mandatory=$false,HelpMessage="Verbose output")][switch]$Verbose,
    [Parameter(Mandatory=$false,HelpMessage="Log file path")][string]$LogFile = "download.log",
    [Parameter(Mandatory=$false,HelpMessage="Maximum retry attempts")][int]$MaxRetries = 3,
    [Parameter(Mandatory=$false,HelpMessage="Retry delay in seconds")][int]$RetryDelay = 5
)

PowerShell
# Import the logging module
Import-Module .\Logging.psm1

function DownloadFile {
    param(
        [string]$uri,
        [string]$dest,
        [int]$maxRetries,
        [int]$retryDelay
    )

    $retryCount = 0
    while ($retryCount -lt $maxRetries) {
        try {
            $Response = Invoke-WebRequest -Uri "$uri" -OutFile "$dest"
            $StatusCode = $Response.StatusCode
            if ($Verbose) { Write-Host "Success: Downloaded file to $dest" }
            return $true
        } catch [System.Exception] {
            Write-Log "Download failed: $($_.Exception.Message)", "Error"
            $retryCount++
            if ($retryCount -lt $maxRetries) {
                Write-Log "Retrying download in $retryDelay seconds (attempt $retryCount of $maxRetries)"
                Start-Sleep -Seconds $retryDelay
            }
        }
    }
    return $false
}

$StatusCode = 0

try {
    if (Test-Path -Path $dest -PathType Leaf) {
        if ($Verbose) { Write-Host "INFO: $dest Already Exists... Removing it." }
        Remove-Item $dest
    } else {
        Write-Log "ERROR: $dest is a PATH and not a File: :("
        $StatusCode = 1
    }

    if ($StatusCode -eq 0) {
        Write-Log "Downloading: $uri"
        if (DownloadFile -uri $uri -dest $dest -maxRetries $MaxRetries -retryDelay $RetryDelay) {
            Write-Log "Success: File $dest downloaded successfully"
        } else {
            Write-Log "ERROR: Maximum retry attempts reached. Download failed."
            $StatusCode = 1
        }
    }

    if ($null -eq $StatusCode) {
        if (Test-Path $dest) {
            $StatusCode = 0
            Write-Log "Success: File $dest downloaded successfully"
        } else {
            Write-Log "ERROR: File $dest Not found after download"
            $StatusCode = 1
        }
    } else {
        Write-Log "ERROR: Download Failed with code $StatusCode"
    }
} catch {
    Write-Log "Unexpected error: $($_.Exception.Message)", "Error"
}

$StatusCode