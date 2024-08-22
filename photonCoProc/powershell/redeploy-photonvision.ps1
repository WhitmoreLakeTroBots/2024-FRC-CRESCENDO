param(
    [string]$remoteMachine,
    [PSCredential]$credential,
    [string]$localJarPath,
    [string]$remoteJarPath,
    [string]$serviceName
)



# Replace with your target machine name and credentials
$computerName = "YourRemoteMachine"
$credential = Get-Credential

# Path to the JAR file locally and remotely
$localJarPath = "C:\path\to\your\photonvision.jar"
$remoteJarPath = "\\$computerName\C:\path\to\destination\photonvision.jar"
# Desired service name
$serviceName = "PhotonVisionService"
# Log file path
$logFile = "C:\path\to\log\redeploy.log"

# Function to write to log file
function Write-Log {
    param(
        [string]$message
    )
    $timestamp = Get-Date -Format "yyyy-MM-dd HH:mm:ss"
    $logEntry = "$timestamp - $message"
    $logEntry | Out-File -FilePath $logFile -Append
}

# Function to copy the JAR file to the remote machine
function Copy-JarToRemote {
    try {
        Copy-Item $localJarPath $remoteJarPath -Credential $credential
        Write-Log "JAR file copied successfully."
        return $true
    } catch [System.Management.Automation.RemoteException] {
        Write-Error "Failed to copy JAR file to remote machine: $($_.Exception.Message)"
        Write-Log "Error copying JAR file: $($_.Exception.Message)"
        return $false
    } catch [System.IO.FileNotFoundException] {
        Write-Error "Local JAR file not found: $localJarPath"
        Write-Log "Local JAR file not found: $localJarPath"
        return $false
    } catch [System.IO.IOException] {
        Write-Error "Error copying JAR file: $($_.Exception.Message)"
        Write-Log "Error copying JAR file: $($_.Exception.Message)"
        return $false
    }
}

# Function to stop and start the service
function Restart-Service {
    try {
        Stop-Service $serviceName -ComputerName $computerName -Credential $credential
        Start-Service $serviceName -ComputerName $computerName -Credential $credential
        Write-Log "Service restarted successfully."
        return $true
    } catch [System.Management.Automation.RemoteException] {
        Write-Error "Failed to restart service: $($_.Exception.Message)"
        Write-Log "Error restarting service: $($_.Exception.Message)"
        return $false
    } catch [System.Management.Automation.ItemNotFoundException] {
        Write-Error "Service not found: $serviceName"
        Write-Log "Service not found: $serviceName"
        return $false
    }
}

# Main script execution
Write-Log "Script started"
if (Copy-JarToRemote) {
    if (Restart-Service) {
        Write-Log "JAR file re-deployed successfully."
    } else {
        Write-Log "Service restart failed."
    }
} else {
    Write-Log "JAR file copy failed."
}
Write-Log "Script finished"