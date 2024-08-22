# Define the destination path
$destinationPath = "C:\ansible-installers-2024"

# Create the destination directory if it doesn't exist
if (-not (Test-Path -Path $destinationPath)) {
    New-Item -ItemType Directory -Path $destinationPath
}

# Get the current directory
$currentDirectory = Get-Location

# Copy all files from the current directory and subfolders to the destination
Get-ChildItem -Path $currentDirectory -Recurse -File | Copy-Item -Destination $destinationPath