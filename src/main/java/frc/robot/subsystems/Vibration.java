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

   // private CommandXboxController driverController;
   // private CommandXboxController articController;

    private double curTime;

     private double dDur;
    private double aDur;

    /**
    *
    */
    public Vibration() {
     //   driverController = RobotContainer.getInstance().m_driverController;
     //   articController = RobotContainer.getInstance().m_articController;
    }

    @Override
    public void periodic() {
        // This method will be called once per scheduler run
        //example for triggering vib
        //driverController.getHID().setRumble(RumbleType.kBothRumble, 1);

        //call runVib() to check and end vibs
        RunVibDrive();
        RunVibArtic();
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
                RobotContainer.getInstance().m_driverController.getHID().setRumble(newAlert.rum, newAlert.intense);
                //set timer target 
                dDur = RobotMath.getTime() + newAlert.sec;
                break;

            case ARTIC:
                //start vib
                RobotContainer.getInstance().m_articController.getHID().setRumble(newAlert.rum, newAlert.intense);
                //set timer target
                aDur = RobotMath.getTime() + newAlert.sec;
                break;  
        
            default:
                break;
        }


    }

    private void RunVibDrive(){
        //should check the status of both Driver and Artic controller vibrations and end based on timmers
        
        //look for difference between driver vib time target vs runtime
        
        //example for get current time in Seconds
        //curTime = RobotMath.getTime();
        if(RobotMath.getTime() >= dDur){
            RobotContainer.getInstance().m_driverController.getHID().setRumble(RumbleType.kBothRumble, 0);
        }
        //look for difference between artic vib time target vs runtime

    }

  private void RunVibArtic(){
        //should check the status of both Driver and Artic controller vibrations and end based on timmers
        
        //look for difference between driver vib time target vs runtime
        
        //example for get current time in Seconds
        //curTime = RobotMath.getTime();
          if(RobotMath.getTime() >= aDur){
            RobotContainer.getInstance().m_articController.getHID().setRumble(RumbleType.kBothRumble, 0);
        }
        //look for difference between artic vib time target vs runtime
    }

    

    public enum CTRL{
            DRIVE,
            ARTIC;
        }



    public enum VIBALERT{

        DRIVERLONG(CTRL.DRIVE, RumbleType.kBothRumble, 1, 0.750),
        DRIVERSHORT(CTRL.DRIVE, RumbleType.kBothRumble, 1, 0.400),
        ARTICLONG(CTRL.ARTIC, RumbleType.kBothRumble, 1, 0.750),
        ARTICSHORT(CTRL.ARTIC, RumbleType.kBothRumble, 1, 0.400);
        
        
        private CTRL con;
        private RumbleType rum;
        private double intense;
        private double sec;

        public CTRL getCon() {
            return con;
        }

        public double getIntense() {
            return intense;
        }

        public double getSec() {
            return sec;
        }

        public RumbleType getRum() {
            return rum;
        }

        VIBALERT(CTRL con, RumbleType rum, double intense, double sec){
            this.con = con;
            this.rum = rum;
            this.intense = intense;
            this.sec = sec;
        }

    }

}
