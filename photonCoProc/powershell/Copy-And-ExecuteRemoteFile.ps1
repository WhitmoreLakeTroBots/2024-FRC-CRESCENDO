# Script Name: CopyAndExecuteRemoteFile.ps1
# Description: Copies a file to a remote machine and executes it with a timeout.
# Author: Gemini
# Version: 1.0

param(
    [string]$SrcFile,
    [string]$DestComputer,
    [string]$DestFile,
    [string]$Log = "C:\temp\remote_file_execution.log",
    [int]$Timeout = 60
)

function Copy-And-ExecuteRemoteFile {
    param(
        [string]$SourceFile,
        [string]$DestinationComputer,
        [string]$DestinationPath,
        [System.Management.Automation.PSCredential]$Credential,
        [string]$LogFile = "c:\temp\remote_file_execution.log",
        [int]$TimeoutSeconds = 60
    )

    try {
        # Check if the source file exists
        if (!(Test-Path $SourceFile)) {
            Write-Log -logFile $LogFile  -message "Source file '$SourceFile' not found =:("
            return
        }
        # Establish a remote session
        Write-Log -logFile $LogFile  -message "Establishing remote session to $DestinationComputer" -verboseLevel 3
        # Validate credentials using Test-Connection
        Write-Log -logFile $LogFile  -message  "Validating $DestinationComputer is on the network" -verboseLevel 3
        try {
           if (! (Test-Connection $DestinationComputer -Quiet)) {
                Write-Log -logFile $LogFile  -message "$DestinationComputer is NOT on the network =:("
                return
            }
        }
        catch {
            Write-Log -logFile $LogFile  -message "$DestinationComputer is NOT on the network =:( $($_.Exception.Message)"
            throw $_.Exception
        }
        Write-Log -logFile $LogFile  -message  "$DestinationComputer is ONLINE =:)" -verboseLevel 3

        $securePassword = $credential.GetNetworkCredential().Password
        #$plainPassword = ConvertFromSecureString -SecureString $securePassword -AsPlainText -Force

        Write-Log -logFile $LogFile  -message "$($credential.GetNetworkCredential().UserName) :: $securePassword" -verboseLevel 5

        $Global:Session = $null
        try {
            $Global:Session = New-PSSession -ComputerName $DestinationComputer #-Credential $Credential

        }
        catch [System.Management.Automation.RemoteException] {
            Write-Log -logFile $LogFile  -message "***********************************************"
            Write-Log -logFile $LogFile  -message "You may need to run this powershell command in an 'Administrator Powershell Window' -> 'Enable-PSRemoting'"
            Write-Log -logFile $LogFile  -message "ERROR: Failed to create remote session due to System.Management.Automation.RemoteException =:("
            Write-Log -logFile $LogFile  -message "$($_.Exception.Message)"
            Write-Log -logFile $LogFile  -message "***********************************************"
            throw $_.Exception
        }
        catch [System.Management.Automation.PSSessionOpenFailedException] {
            Write-Log -logFile $LogFile  -message "***********************************************"
            Write-Log -logFile $LogFile  -message "You may need to run this powershell command in an 'Administrator Powershell Window' -> 'Enable-PSRemoting'"
            Write-Log -logFile $LogFile  -message "ERROR: Failed to create remote session due to System.Management.Automation.PSSessionOpenFailedException exception =:("
            Write-Log -logFile $LogFile  -message "$($_.Exception.Message)"
            Write-Log -logFile $LogFile  -message "***********************************************"
            throw $_.Exception
        }
        catch {
            Write-Log -logFile $LogFile  -message "***********************************************"
            Write-Log -logFile $LogFile  -message "You may need to run this powershell command in an 'Administrator Powershell Window' -> 'Enable-PSRemoting'"
            Write-Log -logFile $LogFile  -message "ERROR: Failed to create remote session due to general exception =:("
            Write-Log -logFile $LogFile  -message "$($_.Exception.Message)"
            Write-Log -logFile $LogFile  -message "***********************************************"
            throw $_.Exception
        }

        #Check if session creation failed
        if (-not $Global:Session) {
            Write-Log -logFile $LogFile  -message "***********************************************"
            Write-Log -logFile $LogFile  -message "You may need to run this powershell command in an 'Administrator Powershell Window' -> 'Enable-PSRemoting'"
            Write-Log -logFile $LogFile  -message "ERROR: Failed to create remote session due to unknown error =:("
            Write-Log -logFile $LogFile  -message "***********************************************"
            return
        }
        Write-Log -logFile $LogFile  -message  "Remote session to $DestinationComputer as $($credential.GetNetworkCredential().UserName) is a Success =:)"
        # Check if the destination directory exists and create it if not
        $RemoteDirectory = Split-Path $DestinationPath -Parent

        Write-Log -logFile $LogFile  -message  "Testing $DestinationComputer :: $RemoteDirectory"  -verboseLevel 4
        $remotePathExists = Invoke-Command -Session $session -ScriptBlock {  param($rDir) Test-Path -Path $rDir } -ArgumentList $RemoteDirectory
        Write-Log -logFile $LogFile  -message  "Testing $DestinationComputer :: $RemoteDirectory exists returned '$remotePathExists'" -verboseLevel 4

        if (-not $remotePathExists) {
            Write-Log -logFile $LogFile  -message  "Creating remote directory: $RemoteDirectory  =:)"  -verboseLevel 3
            New-Item -Path $RemoteDirectory -ItemType Directory -Force -Session $Global:Session
        }
        else {
            Write-Log -logFile $LogFile  -message  "Remote directory: $RemoteDirectory exists =:)"  -verboseLevel 3
        }

        # Copy the file to the remote machine
        Write-Log -logFile $LogFile  -message "Copying file to remote machine: $SourceFile -> $DestinationPath"
        #Copy-Item -Path $SourceFile -Destination $DestinationPath -Session $Global:Session

        Invoke-Command -Session $Global:Session -ScriptBlock {
            param($sourceFile, $destinationPath)
            Copy-Item -Path $sourceFile -Destination $destinationPath
        } -ArgumentList $SourceFile, $DestinationPath


        # Invoke the file on the remote machine
        Write-Log -logFile $LogFile  -message "Executing file on remote: $DestinationComputer :: $DestinationPath"
        $Job = Invoke-Command -Session $Global:Session -FilePath $DestinationPath -AsJob

        Write-Log -logFile $LogFile -message "********************************************"

        # Receive any interactive output from the job
        while ($job.State -eq "Running") {
            $output = Receive-Job $job -Wait
            $outputLines = $output.Split("`n")
            foreach ($line in $outputLines) {
                Write-Log -logFile $LogFile -message $line
            }
        }

        Write-Log -logFile $LogFile -message "********************************************"
        # Wait for the job to complete or time out
        # Wait-Job $Job -Timeout $TimeoutSeconds

        if ($Job.State -eq "Completed") {
            Write-Log -logFile $LogFile  -message "Job completed successfully =:)"
        } elseif ($Job.State -eq "Stopped") {
            Write-Log -logFile $LogFile  -message  "Job timed out after $TimeoutSeconds seconds =:("
        } else {
            Write-Log -logFile $LogFile  -message "Job failed with error: $($Job.ErrorDetails.Message) =:("
        }

        # Remove the copied file from the remote machine
        #Write-Verbose "Removing copied file from remote machine: $DestinationPath" | Tee-Object -FilePath $LogFile -Append
        #Remove-Item -Path $DestinationPath -Session $Global:Session

        # Close the remote session
        Write-Log -logFile $LogFile  -message "Closing remote session"
        Remove-PSSession $Global:Session
    } catch {
        Write-Log -logFile $LogFile  -message  "An error occurred: $($_.Exception.Message)"
    }
    finally {
        if (-not $Global:Session) {
            Remove-PSSession $Global:Session
        }
    }
}

Import-Module "$(Split-Path $MyInvocation.MyCommand.Path)\wlrobotics-ps-modules.psm1"

$User = $env:USERNAME
$PWord = $env:PASSWORD

$currentExecutionPolicy = Get-ExecutionPolicy
Write-Log -logFile $Log -message "Current Execution Policy: $currentExecutionPolicy"

if ($PSSession.Runspace.ExecutionContext.SessionState.CurrentScope.SessionState.Internal.ExecutionContext.IsElevated) {
    Write-Log -logFile $Log -message "Script is running as administrator"
} else {
    Write-Log -logFile $Log -message "WARNING: script $($MyInvocation.MyCommand.Path) is not running as administrator"
}

Write-Log -logFile $Log -message  "Credential :: $User/********"
$password = ConvertTo-SecureString -AsPlainText -Force -String "$PWord"
$Credential = New-Object System.Management.Automation.PSCredential("$User", $password)

Write-Log -logFile $Log -message "SourceFile :: $SrcFile"
Write-Log -logFile $Log -message "DestinationComputer :: $DestComputer"
Write-Log -logFile $Log -message "DestinationPath :: $DestFile"
#Write-Log -logFile $Log -message "Credential :: $Credential"
Write-Log -logFile $Log -message "LogFile :: $Log"
Write-Log -logFile $Log -message "TimeoutSeconds  :: $Timeout"

Copy-And-ExecuteRemoteFile `
    -SourceFile "$SrcFile" `
    -DestinationComputer "$DestComputer" `
    -DestinationPath "$DestFile" `
    -Credential $Credential `
    -LogFile $Log `
    -TimeoutSeconds $Timeout
