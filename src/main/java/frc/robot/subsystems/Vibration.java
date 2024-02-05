package frc.robot.subsystems;

//import frc.robot.commands.*;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import frc.robot.RobotContainer;
import frc.robot.commands.intakeCommands.intakeCmd;
import frc.robot.commands.intakeCommands.pivotCmd;
import frc.utils.RobotMath;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.GenericHID.RumbleType;

/**
 *
 */
public class Vibration extends SubsystemBase {

    private CommandXboxController driverController;
    private CommandXboxController articController;

    private double curTime;

    private STATUS dStatus = STATUS.STOPPED;
    private STATUS aStatus = STATUS.STOPPED;


    private double dDur;
    private double aDur;

    /**
    *
    */
    public Vibration() {
        driverController = RobotContainer.getInstance().m_driverController;
        articController = RobotContainer.getInstance().m_articController;
    }

    @Override
    public void periodic() {
        // This method will be called once per scheduler run
        //example for triggering vib
        //driverController.getHID().setRumble(RumbleType.kBothRumble, 1);

        //call runVib() to check and end vibs
    
    }

    @Override
    public void simulationPeriodic() {
        // This method will be called once per scheduler run when in simulation

    }

    // Put methods for controlling this subsystem
    // here. Call these from Commands.

    public void StartVib(VIBALERT newAlert){
        //Set up Alert, and reset timer to start timer
        switch (newAlert.con) {
            case DRIVE:
                //start vib 
                driverController.getHID().setRumble(newAlert.rum, newAlert.intense);
                //set timer target  
                break;

            case ARTIC:
                //start vib
                driverController.getHID().setRumble(newAlert.rum, newAlert.intense);
                //set timer target

                break;  
        
            default:
                break;
        }


    }

    private void RunVib(){
        //should check the status of both Driver and Artic controller vibrations and end based on timmers

        //look for difference between driver vib time target vs runtime
        
        //example for get current time in Seconds
        //RobotMath.getTime();

        //look for difference between artic vib time target vs runtime

    }

    public enum CTRL{
            DRIVE,
            ARTIC;
        }

    public enum STATUS{
            STOPPED,
            VIBING;

    }

    public enum VIBALERT{

        DRIVERLONG(CTRL.DRIVE, RumbleType.kBothRumble, 0.8, 750),
        DRIVERSHORT(CTRL.DRIVE, RumbleType.kBothRumble, 0.8, 400),
        ARTICLONG(CTRL.ARTIC, RumbleType.kBothRumble, 0.8, 750),
        ARTICSHORT(CTRL.ARTIC, RumbleType.kBothRumble, 0.8, 400);
        
        
        private CTRL con;
        private RumbleType rum;
        private double intense;
        private long msec;

        public CTRL getCon() {
            return con;
        }

        public double getIntense() {
            return intense;
        }

        public long getMsec() {
            return msec;
        }

        public RumbleType getRum() {
            return rum;
        }

        VIBALERT(CTRL con, RumbleType rum, double intense, long msec){
            this.con = con;
            this.rum = rum;
            this.intense = intense;
            this.msec = msec;
        }

    }

}
