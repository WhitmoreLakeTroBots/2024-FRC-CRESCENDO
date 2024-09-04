
function Write-Log {
    param(
        [string]$message,
        [ValidateRange(1, 5)][int]$verboseLevel = 1,  # Default to Info level
        [string]$logFile = "log.txt"
    )

    $verboseLevelMap = @{
        "" = 1
        "-Info-" = 2
        "-Warning-" = 3
        "-Error-" = 4
        "-Critical-" = 5
    }

     # Get the corresponding verbose level string from the map
     $verboseLevelString = ($VerboseLevelMap.GetEnumerator() | Where-Object { $_.Value -eq $verboseLevel }).Key

    $minimumLogLevel = $VerbosePreference.Level
    $minimumLogLevel = 2
    if ($verboseLevel -le $minimumLogLevel) {
        $timestamp = Get-Date -Format "yyyy-MM-dd HH:mm:ss"
        $logEntry = "$timestamp $verboseLevelString $message"
        $logEntry | Out-File -FilePath $logFile -Append
        Write-Host $logEntry
    }
}

function IsComputerOnline {
    param(
        [string ]$ComputerName,
        [string]$logFile = "log.txt"
    )

    try {
        Write-Log "Checking online status for $ComputerName"
        $result = Test-Connection -ComputerName $ComputerName -Count 1 -Quiet
        if ($result -eq $true) {
            Write-Log "$ComputerName is online"
            return $true
        }
         else {
            Write-Log "$ComputerName is offline"
            return $false
        }
    } catch [System.Exception] {
        Write-Log "Error checking online status for ${ComputerName}: $($_.Exception.Message)", "Error"
        return $false
    }
}
Export-ModuleMember -Function *

