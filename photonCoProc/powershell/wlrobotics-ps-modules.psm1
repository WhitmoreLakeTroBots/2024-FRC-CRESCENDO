function Write-Log {
    param(
        [string]$message,
        [string]$level = "Info",
        [string]$logFile = "log.txt"
    )

    $timestamp = Get-Date -Format "yyyy-MM-dd HH:mm:ss"
    $logEntry = "$timestamp - $level - $message"
    $logEntry | Out-File -FilePath $logFile -Append
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

Export-Modulemember -Function Write-Log
Export-Modulemember -Function IsComputerOnline

