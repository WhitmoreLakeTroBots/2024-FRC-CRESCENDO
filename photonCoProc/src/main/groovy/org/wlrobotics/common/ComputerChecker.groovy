import java.net.InetAddress
import java.util.logging.Logger

class ComputerChecker {
    //static def logger = Logger.getLogger('computer_check')

    static boolean findOnlineComputer(String computerName) {
        boolean returnValue = true

        try {
            // logger.info("Pinging ${computerName}")
            // println "THERE"
            InetAddress.getByName(computerName).isReachable(1000) // 1000ms timeout
        } catch (Exception e) {
            //logger.severe("Failed to ping ${computerName}: ${e.message}")
            returnValue = false
        }
        return returnValue
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