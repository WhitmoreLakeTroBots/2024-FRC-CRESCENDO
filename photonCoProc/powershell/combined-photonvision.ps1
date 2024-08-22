param(
    [string]$remoteMachine,
    [PSCredential]$credential,
    [string]$localJarPath,
    [string]$remoteJarPath,
    [string]$serviceName
)

# Function to copy the JAR file to the remote machine
function Copy-JarToRemote {
    try {
        Copy-Item $localJarPath $remoteJarPath -Credential $credential
        Write-Host "JAR file copied successfully."
        return $true
    } catch [System.Management.Automation.RemoteException] {
        Write-Error "Failed to copy JAR file to remote machine: $($_.Exception.Message)"
        return $false
    } catch [System.IO.FileNotFoundException] {
        Write-Error "Local JAR file not found: $localJarPath"
        return $false
    } catch [System.IO.IOException] {
        Write-Error "Error copying JAR file: $($_.Exception.Message)"
        return $false
    }
}

# Function to create the Windows service
function Create-PhotonVisionService {
    try {
        New-Service -Name $serviceName -DisplayName "PhotonVision Service" -BinaryPathName $javaPath -StartupType Automatic -StartMode Delayed -Description $serviceDescription -Arguments $serviceArgs -Credential $credential
        # Set service dependencies
        Set-Service -Name $serviceName -Dependencies Tcpip -StartupType Automatic

        # Configure service recovery
        Set-ServiceRecovery -Name $serviceName -FirstFailureAction Restart -SecondFailureAction Restart -ThirdFailureAction Restart
        Write-Host "Service created successfully."
        return $true
    } catch [System.Management.Automation.RemoteException] {
        Write-Error "Failed to create Windows service: $($_.Exception.Message)"
        return $false
    }
}

# Function to stop and start the service
function Restart-Service {
    try {
        Stop-Service $serviceName -ComputerName $remoteMachine -Credential $credential
        Start-Service $serviceName -ComputerName $remoteMachine -Credential $credential
        Write-Host "Service restarted successfully."
        return $true
    } catch [System.Management.Automation.RemoteException] {
        Write-Error "Failed to restart service: $($_.Exception.Message)"
        return $false
    } catch [System.Management.Automation.ItemNotFoundException] {
        Write-Error "Service not found: $serviceName"
        return $false
    }
}

# Function to check if service exists
function ServiceExists {
    try {
        Get-Service -Name $serviceName -ComputerName $remoteMachine -Credential $credential | Out-Null
        return $true
    } catch [System.Management.Automation.ItemNotFoundException] {
        return $false
    }
}

# Function to compare file timestamps
function CompareFileTimestamps {
    $localFile = Get-Item $localJarPath
    $remoteFile = Get-Item $remoteJarPath -ErrorAction Stop

    if ($localFile.LastWriteTime -gt $remoteFile.LastWriteTime) {
        return $true # Local file is newer
    } else {
        return $false # Remote file is newer or same
    }
}

# Determine action based on file timestamps and service existence
if (ServiceExists) {
    if (CompareFileTimestamps) {
        Copy-JarToRemote
        Restart-Service
    } else {
        Write-Host "No changes detected. Skipping deployment."
    }
} else {
    Copy-JarToRemote
    Create-PhotonVisionService
}