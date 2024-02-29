package frc.robot.commands.autonCommands;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.RobotContainer;
import frc.robot.commands.driveCommands.cmdResetGyro;
import frc.robot.commands.driveCommands.turnCmd;
import frc.utils.cmdDelay;


public class turnTest extends SequentialCommandGroup {

    public turnTest() {
        addCommands(new cmdResetGyro());
        addCommands(new turnCmd(35, 0.3));
        addCommands(new cmdDelay(2));
        addCommands(new turnCmd(-35, 0.3));

    }

}
