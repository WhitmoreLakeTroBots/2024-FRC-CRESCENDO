# Replace with your target machine name and credentials
$computerName = "YourRemoteMachine"
$credential = Get-Credential

# Path to the JAR file locally
$localJarPath = "C:\path\to\your\photonvision.jar"
# Desired service name
$serviceName = "PhotonVisionService"
# Path to the Java executable on the remote machine (adjust accordingly)
$javaPath = "\\$computerName\C:\Program Files\Java\jdk-11.0.17\bin\java.exe"

# Import the logging module
Import-Module .\wlrobotics-ps-modules.psm1


# Function to copy the JAR file to the remote machine
function Copy-JarToRemote {
    $remotePath = "\\$computerName\C:\path\to\destination\photonvision.jar"
    try {
        Copy-Item $localJarPath $remotePath -Credential $credential
    } catch [System.Management.Automation.RemoteException] {
        Write-Error "Failed to copy JAR file to remote machine: $($_.Exception.Message)"
        return $false
    }
    return $true
}

# Function to create the Windows service
function Create-PhotonVisionService {
    $serviceArgs = "-jar C:\path\to\destination\photonvision.jar"
    $serviceDescription = "PhotonVision Service"

    try {
        New-Service -Name $serviceName -DisplayName "PhotonVision Service" -BinaryPathName $javaPath -StartupType Automatic -StartMode Delayed -Description $serviceDescription -Arguments $serviceArgs -Credential $credential
        # Set service dependencies
        Set-Service -Name $serviceName -Dependencies Tcpip -StartupType Automatic

        # Configure service recovery
        Set-ServiceRecovery -Name $serviceName -FirstFailureAction Restart -SecondFailureAction Restart -ThirdFailureAction Restart
    } catch [System.Management.Automation.RemoteException] {
        Write-Error "Failed to create Windows service: $($_.Exception.Message)"
        return $false
    }
    return $true
}

# Main script execution
if (Copy-JarToRemote) {
    Write-Host "JAR file copied successfully."
    if (Create-PhotonVisionService) {
        Write-Host "Service created successfully."
    } else {
        Write-Host "Service creation failed."
    }
} else {
    Write-Host "JAR file copy failed."
}