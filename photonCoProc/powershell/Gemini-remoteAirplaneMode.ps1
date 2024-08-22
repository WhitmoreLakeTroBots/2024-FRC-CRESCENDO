param(
    [Parameter(Mandatory=$true, HelpMessage="Remote computer name")]
    [string]$ComputerName,
    [Parameter(Mandatory=$true, HelpMessage="Credentials for remote computer")]
    [PSCredential]$Credential,
    [Parameter(Mandatory=$false, HelpMessage="Log file path")]
    [string]$LogFile = "disable_network.log"
)

PowerShell
# Import the logging module
Import-Module .\wlrobotics-psmodules.ps1m


function Disable-RemoteAdapters {
    param(
        [string]$ComputerName,
        [PSCredential]$Credential
    )

    try {
        $adapters = Get-NetAdapter -ComputerName $ComputerName -Credential $Credential

        $wifiAdapters = $adapters | Where-Object {$_.PhysicalMediaType -eq "802.11"}
        foreach ($adapter in $wifiAdapters) {
            Disable-NetAdapter -Name $adapter.Name -ComputerName $ComputerName -Credential $Credential
            Write-Log "Disabled Wi-Fi adapter: $adapter.Name"
        }

        $bluetoothAdapters = $adapters | Where-Object {$_.Name -like "*Bluetooth*"}
        foreach ($adapter in $bluetoothAdapters) {
            Disable-NetAdapter -Name $adapter.Name -ComputerName $ComputerName -Credential $Credential
            Write-Log "Disabled Bluetooth adapter: $adapter.Name"
        }
    } catch [System.Exception] {
        Write-Log "Error disabling adapters: $($_.Exception.Message)", "Error"
    }
}

Write-Log "Script started"
Disable-RemoteAdapters -ComputerName $ComputerName -Credential $Credential
Write-Log "Script finished"