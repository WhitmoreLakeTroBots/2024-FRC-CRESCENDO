package org.wlrobotics.common
import java.net.InetAddress

 class ComputerChecker {
    boolean findOnlineComputer(String computerName) {
        boolean returnValue = true

        try {
            returnValue = InetAddress.getByName(computerName).isReachable(1000) // 1000ms timeout

        } catch (Exception e) {
            returnValue = false
        }


        return returnValue
    }

    String getHostAddress (computerName) {

        return InetAddress.getByName(computerName).getHostAddress().toString()

    }

    static void main(String[] args) {

        if (args.length != 1) {
            println "Usage: groovy ComputerChecker.groovy <computer_name>"
            return
        }

        def computerName = args[0]
        def online = findOnlineComputer(computerName)

        if (online) {
            println "${computerName} is online"
        } else {
            println "${computerName} is offline"
        }
    }
}