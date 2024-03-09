package frc.robot.subsystems;

//import frc.robot.commands.*;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.RobotContainer;
import frc.robot.commands.intakeCommands.intakeCmd;
import frc.robot.commands.intakeCommands.pivotCmd;
import frc.utils.cmdDelay;

import java.util.Map;

import com.revrobotics.CANSparkMax;

import edu.wpi.first.wpilibj.CAN;
import edu.wpi.first.wpilibj.DigitalInput;

/**
 *
 */
public class HealthCheck extends SubsystemBase {

   // public DigitalInput IntakeSen;

    private int faultCount = 0; 
    private static double TempTreshold = 75;  //temp in Celsius

    private Map <String, CANSparkMax> RobotSparks;
    /**
    *
    */
    public HealthCheck() {

        //addChild("Digital Input 1" , BeamBreak1);

    }

        
    public void registerSparkMAx(String SparkName, CANSparkMax newSpark){
    //make method that allows us to register each SparkMax so that we can loop through for health checks
    RobotSparks.put(SparkName, newSpark);
    
    }

    @Override
    public void periodic() {
        // This method will be called once per scheduler run
       // checkIntake();
       CAMHealthCheck();
    }

    @Override
    public void simulationPeriodic() {
        // This method will be called once per scheduler run when in simulation

    }

    // Put methods for controlling this subsystem
    // here. Call these from Commands.

private void CAMHealthCheck(){
    //make list of health checks for each CAN / SPARK MAX to run 

    for(Map.Entry<String, CANSparkMax> e : RobotSparks.entrySet()){
        //run tests

        tempCheck(e.getValue().getMotorTemperature(), e.getKey());
        // other checks 

        }
    }


    


private void tempCheck(double Temp, String name){
    if (Temp >= TempTreshold){
        //send alert
        writeError("Motor" + name  +" has temp of " + Temp 
            + " which exceedes Temp Threashold of " + TempTreshold  );
    }
}

private void writeError(String nMsg){

    System.err.println(nMsg);
    faultCount++; 
}

public int getFaultCount(){
    return faultCount;
}

public void resetFaultCount(){
    faultCount = 0;
}

}
