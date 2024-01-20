package frc.robot.subsystems;


import frc.robot.commands.*;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

import edu.wpi.first.wpilibj.motorcontrol.PWMSparkMax;



/**
 *
 */
public class Swerve extends SubsystemBase {
    
private PWMSparkMax motorController1;

    
    /**
    *
    */
    public Swerve() {
motorController1 = new PWMSparkMax(0);
 addChild("Motor Controller 1",motorController1);
 motorController1.setInverted(false);


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

