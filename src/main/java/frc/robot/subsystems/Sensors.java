package frc.robot.subsystems;

//import frc.robot.commands.*;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

import edu.wpi.first.wpilibj.DigitalInput;

/**
 *
 */
public class Sensors extends SubsystemBase {

    private DigitalInput digitalInput1;

    /**
    *
    */
    public Sensors() {
        digitalInput1 = new DigitalInput(0);
        addChild("Digital Input 1", digitalInput1);

    }

    @Override
    public void periodic() {
        // This method will be called once per scheduler run

    }

    @Override
    public void simulationPeriodic() {
        // This method will be called once per scheduler run when in simulation

    }

    // Put methods for controlling this subsystem
    // here. Call these from Commands.

}
