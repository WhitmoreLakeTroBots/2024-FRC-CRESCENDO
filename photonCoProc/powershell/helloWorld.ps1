
try {
    Write-Output "Hello, world!"
} catch {
    throw $_
} finally {
    Write-Output "Finally block executed."
}