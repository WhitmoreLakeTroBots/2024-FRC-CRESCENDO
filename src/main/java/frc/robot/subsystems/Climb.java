package frc.robot.subsystems;

//import frc.robot.commands.*;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.CANIDs;
import frc.utils.CommonLogic;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkBase.IdleMode;

/**
 *
 */
public class Climb extends SubsystemBase {


    private ClimbMode curClimbMode = ClimbMode.START;
  //  private double climbModeTol = 5;

  //  private CANSparkMax climbMotorRight;
    private CANSparkMax climbMotorLeft;
   // private double pivP = 10.0;
   // private double pivF = 0.0;

    private double minClimbPower = -0.4;
    private double maxClimbPower = 0.4;

    private double holdPosLeft = ClimbMode.HOLD.pos;
   // private double holdPosRight = ClimbMode.HOLD.pos;

    /**
    *
    */
    public Climb() {
        climbMotorLeft = new CANSparkMax(CANIDs.ClimbMotorLeftId, CANSparkMax.MotorType.kBrushless);
        CommonLogic.setSparkParamsBase(climbMotorLeft, false, 40, 40, IdleMode.kBrake);

    //    climbMotorRight = new CANSparkMax(CANIDs.ClimbMotorRightId, CANSparkMax.MotorType.kBrushless);
    //    CommonLogic.setSparkParamsBase(climbMotorRight, false, 10, 30, IdleMode.kBrake);

    }

    @Override
    public void periodic() {
        // This method will be called once per scheduler run

        switch(curClimbMode){

            case START:
                ClimbModeStart();
            break;

            case PRECLIMB:
                ClimbModePreClimb();
            break;

            case CLIMBING:
                ClimbModeClimbing();
            break;

            case HOLD:
                ClimbModeHold();
            break;

            default:
            curClimbMode = ClimbMode.START;


        }
        
    }

    private void ClimbModeStart(){
        //Holds Climber at Position 0, no hold power
        climbMotorLeft.set(CommonLogic.CapMotorPower(
            CommonLogic.gotoPosPIDF(ClimbMode.START.P, ClimbMode.START.F,
                 climbMotorLeft.getEncoder().getPosition(), ClimbMode.START.pos),
            minClimbPower, maxClimbPower));

        
    }

    private void ClimbModePreClimb(){
        //Raises Climb to top position, recquires minimun hold power
        climbMotorLeft.set(CommonLogic.CapMotorPower(
            CommonLogic.gotoPosPIDF(ClimbMode.PRECLIMB.P, ClimbMode.PRECLIMB.F,
                 climbMotorLeft.getEncoder().getPosition(), ClimbMode.PRECLIMB.pos),
            minClimbPower, maxClimbPower));

      
    }

    private void ClimbModeClimbing(){
        //Attempt to maintain level, climb til lowest arm hits minimal position
        //Dont start climbing until confirmed both arms are attached
        climbMotorLeft.set(CommonLogic.CapMotorPower(
            CommonLogic.gotoPosPIDF(ClimbMode.CLIMBING.P, ClimbMode.CLIMBING.F,
                 climbMotorLeft.getEncoder().getPosition(), ClimbMode.CLIMBING.pos),
            minClimbPower, maxClimbPower));
/*
        climbMotorRight.set(CommonLogic.CapMotorPower(
            CommonLogic.gotoPosPIDF(ClimbMode.CLIMBING.P, ClimbMode.CLIMBING.F,
                 climbMotorRight.getEncoder().getPosition(), ClimbMode.CLIMBING.pos),
            minClimbPower, maxClimbPower));

            if(CommonLogic.isInRange(climbMotorLeft.getEncoder().getPosition(), ClimbMode.CLIMBING.pos, climbModeTol) ||
                 CommonLogic.isInRange(climbMotorRight.getEncoder().getPosition(), ClimbMode.CLIMBING.pos, climbModeTol)){
                        //If right is true or left is true update hold positions and move to hold
                        holdPosLeft = climbMotorLeft.getEncoder().getPosition();
                        holdPosRight = climbMotorRight.getEncoder().getPosition();
                        curClimbMode = ClimbMode.HOLD;
                 }
                 */
    }

    private void ClimbModeHold(){
        //Auto level while holding arm in lowest position 
        climbMotorLeft.set(CommonLogic.CapMotorPower(
            CommonLogic.gotoPosPIDF(ClimbMode.HOLD.P, ClimbMode.HOLD.F,
                 climbMotorLeft.getEncoder().getPosition(), holdPosLeft),
            minClimbPower, maxClimbPower));

      
    }

    @Override
    public void simulationPeriodic() {
        // This method will be called once per scheduler run when in simulation

    }

    public void setClimbMode(ClimbMode newClimbMode){
        curClimbMode = newClimbMode;

    }


    
    

    // Put methods for controlling this subsystem
    // here. Call these from Commands.

    public enum ClimbMode {
        START(0.0, 0.01, 0.00),
        PRECLIMB(70.0, 0.001, 0.0),
        CLIMBING(20.0, 0.01, 0.0),
        HOLD(20.0, 10, -0.08);

        private final double pos;
        private final double P;
        private final double F;

        public double getPos() {
            return pos;
        }

        public double getP(){
            return P;
        }

        public double getF(){
            return F;
        }

    ClimbMode(double pos, double P, double F) {
            this.pos = pos;
            this.P = P;
            this.F = F;
        }
    }
}
