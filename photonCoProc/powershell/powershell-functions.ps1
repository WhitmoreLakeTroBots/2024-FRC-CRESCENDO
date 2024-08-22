

$src_root = "C:\ansible-installers-2024"
$win_common_src = $src_root + "\win-common"
$ftc_src = $src_root + "\ftc"
$frc_src = $src_root + "\frc"
$fllc_src = $src_root + "\fllc"
$flle_src = $src_root + "\flle"


$win_pstools_path = $env:PUBLIC + "\PSTools"

$7zProg = $env:ProgramFiles + "\7-Zip\7z.exe"

$tempDir = "C:\temp"

$tempInstallLog = $tempDir + "\temp-install.log"
$finalInstallLog = $src_root + "\install-all.log"

$dev_tools_root_dir = $env:PUBLIC + "\dev_tools"


function run-command {
    param ([string] $file,
        [string[]] $install_args,
        [string] $work_dir)

    begin {

        if (Test-Path $tempInstallLog) {
            Remove-Item -Force $tempInstallLog
        }
        New-Item -ItemType file -Path $tempInstallLog | Out-File -FilePath "$tempDir\dummy.txt"

        Write-Output "*********************************************" | Tee-Object -FilePath $tempInstallLog -Append
    }

    process {

        if (Test-Path -Path $file) {
            Write-Output "$file  $install_args" | Tee-Object -FilePath $tempInstallLog -Append
            Start-Process -FilePath $file -ArgumentList "$install_args" -WorkingDirectory $work_dir -Wait -Verb: RunAs  #| Tee-Object -FilePath $tempInstallLog -Append

        }
        else {
            #Write-Output "ERROR: $file not found :("
            Write-Output "ERROR: $file not found :(" | Tee-Object -FilePath $tempInstallLog -Append

        }
    }

    end {
        Get-Content -Path $tempInstallLog | Add-Content -Path $finalInstallLog
    }
}

function unzip-file {
    param ([string] $zip_file,
        [string] $dest_dir)
    begin {
    }

    process {
        if (Test-Path -Path $zip_file) {
            run-command -file: $7zProg -install_args: @("x", $zip_file, "-aoa", "-o`"$dest_dir`"") -work_dir:$win_common_src
        }
        else {
            Write-Output "ERROR: $zip_file not found :(" | Tee-Object -FilePath $finalInstallLog -Append
        }
    }

    end {
        #Write-Output "Finished: $cmd"
    }
}

function set-environment-var {
    param ([string] $var_name,
        [string] $value,
        [string] $Scope)

    process {
        Write-Output "Setting ${var_name}=${value}" #| Tee-Object -FilePath: $finalInstallLog -Append
        [Environment]::SetEnvironmentVariable($var_name, $value, $Scope)
    }
}

#https://www.techtarget.com/searchitoperations/answer/Manage-the-Windows-PATH-environment-variable-with-PowerShell
Function Set-PathVariable {
    param (
        [string]$AddPath,
        [string]$RemovePath,
        [ValidateSet('Process', 'User', 'Machine')]
        [string]$Scope = 'Process'
    )
    $regexPaths = @()
    if ($PSBoundParameters.Keys -contains 'AddPath') {
        Write-Output "Adding ${AddPath} to the PATH" | Tee-Object -FilePath: $finalInstallLog -Append
        $regexPaths += [regex]::Escape($AddPath)
    }

    if ($PSBoundParameters.Keys -contains 'RemovePath') {
        Write-Output "Removing $RemovePath from PATH" | Tee-Object -FilePath: $finalInstallLog -Append
        $regexPaths += [regex]::Escape($RemovePath)
    }

    # this loops over the parameters of --AddPath and -RemovePath regex parameters
    # both will remove the item from the array
    [string[]]$arrPath = [System.Environment]::GetEnvironmentVariable('PATH', $Scope) -split ';'
    foreach ($p in $regexPaths) {
        $arrPath = $arrPath | Where-Object { $_ -notMatch "^$p\\?" }
    }

    [string] $value = ""
    $value = ($arrPath += $addPath) -join ";"
    Write-Output "" | Tee-Object -FilePath: $finalInstallLog -Append
    [System.Environment]::SetEnvironmentVariable('PATH', $value, $Scope)
    [System.Environment]::SetEnvironmentVariable('PATH', $value, "Process")
}

Function Clean-PathVariable {
    param (
        [ValidateSet('Process', 'User', 'Machine')]
        [string]$Scope = 'Process'
    )
    [string[]] $arrPath = [System.Environment]::GetEnvironmentVariable('PATH', $Scope) -split ';'
    [string] $value = ""
    foreach ($p in $arrPath) {
        $plength = ($p | Measure-Object -Character).Characters
        if ($plength -gt 0) {
            if (Test-Path $p) {
                #only add the value if the corresponding directlry exists
                $value = $value + ";" + $p
            }
            else {
                #do not add it to the path
                Write-Output "Removing path segment: $p"
            }
        }
    }
    Write-Output "" | Tee-Object -FilePath: $finalInstallLog -Append
    Write-Output "" | Tee-Object -FilePath: $finalInstallLog -Append
    Write-Output "Setting PATH=$value" | Tee-Object -FilePath: $finalInstallLog -Append
    [System.Environment]::SetEnvironmentVariable('PATH', $value, $Scope)
    Write-Output "" | Tee-Object -FilePath: $finalInstallLog -Append
    Write-Output "" | Tee-Object -FilePath: $finalInstallLog -Append
}

