param(
    [Parameter(Mandatory = $true)]
    [string]$ComputerName,
    [Parameter(Mandatory = $true)]
    [string]$logFile = "log.txt"
)

Import-Module .\wlrobotics-ps-modules.psm1


$LogFile = "log.txt"

try {
    Write-Host "Hello, world!"
    Write-Log "" -logFile $LogFile
    Write-Log "Hello, world! message logged successfully." -logFile $LogFile
    IsComputerOnline -ComputerName "Kudu"
} catch {
    Write-Log "An error occurred: $_" -logFile $LogFile
    throw $_
} finally {
    Write-Log "Finally block executed." -logFile $LogFile
}