Import-Module .\wlrobotics-ps-modules.psm1
#Import-Module Write-Log

$LogFile = "log.txt"

try {
    Write-Host "Hello, world!"
    Write-Log "" -logFile $LogFile
    Write-Log "Hello, world! message logged successfully." -logFile $LogFile
    IsComputerOnline -ComputerName "Kudux" -logFile $LogFile

} catch {
    Write-Log "An error occurred: $_" -logFile $LogFile
    throw $_
} finally {
    Write-Log "Finally block executed." -logFile $LogFile
}