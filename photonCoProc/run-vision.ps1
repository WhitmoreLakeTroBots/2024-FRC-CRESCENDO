
$Env:PATH += ';C:\shared\jdk-11.0.24+8;C:\shared\jdk-11.0.24+8\bin'
$Env:JAVAHOME = 'C:\shared\jdk-11.0.24+8'

while ($true){

		# First we create the request.
		$HTTP_Request = [System.Net.WebRequest]::Create('http://localhost:5800/#/dashboard')

		# We then get a response from the site.
		$HTTP_Response = $HTTP_Request.GetResponse()

		# We then get the HTTP code as an integer.
		$HTTP_Status = [int]$HTTP_Response.StatusCode

		If ($HTTP_Status -eq 200) {
			# Do nothing we are good =:)
		}
		Else {
			#Start-Sleep -Seconds 1
			Write-Host "The PHOTON Is Down Starting it NOW !!"
			#the processes will wait here until photon is killed or dies.
			C:\shared\jdk-11.0.24+8\bin\java.exe -jar photonvision-v2024.3.1-winx64.jar
		}

	
	# Finally, we clean up the http request by closing it.
	If ($HTTP_Response -ne $null) { $HTTP_Response.Close() }
	
	Start-Sleep -Seconds 2
}