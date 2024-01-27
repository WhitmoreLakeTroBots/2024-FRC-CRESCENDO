package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.utils.RobotMath;
import java.io.IOException;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.math.geometry.Transform3d;
import edu.wpi.first.math.geometry.Translation3d;
import edu.wpi.first.apriltag.AprilTagFieldLayout;
import edu.wpi.first.apriltag.AprilTagFields;
import org.photonvision.*;
import org.photonvision.targeting.PhotonTrackedTarget;
import java.util.List;
import java.util.Optional;

public class SubPoseEstimator extends SubsystemBase {
    Pose3d robotFieldPose = new Pose3d(0.0, 0.0, 0.0, new Rotation3d());

    private final PhotonCamera cam12 = new PhotonCamera("cam11");
    public List<PhotonTrackedTarget> tags;
    private String visionPose3d_str = "";
    private AprilTagFieldLayout aprilTagFieldLayout = null;
    // default camera position
    private final Transform3d cam12_2_robotTransform3d = new Transform3d(new Translation3d(0, 0, 0),
            new Rotation3d(Math.toRadians(0), Math.toRadians(0), Math.toRadians(0)));
   
    public SubPoseEstimator() {

        try {
            aprilTagFieldLayout = AprilTagFieldLayout
                    .loadFromResource(AprilTagFields.k2024Crescendo.m_resourceFile);  
                     // The parameter for loadFromResource() will be different depending on the game.
        } catch (IOException e) {
            System.err.println("Could not load april tag field layout");
            System.out.println(e);
        }
    }

    @Override
    public void periodic() {
        // This method will be called once per scheduler run
        tags = cam12.getLatestResult().getTargets();
        processTags();
    }

    @Override
    public void simulationPeriodic() {
        // This method will be called once per scheduler run when in simulation
    }

    private void processTags() {
        var results = cam12.getLatestResult();

        if (results.hasTargets()) {

            Optional<Pose3d> bestTagPose = aprilTagFieldLayout.getTagPose(results.getBestTarget().getFiducialId());
            // visionPose3d_str = "HasTargets";
            if (bestTagPose.isPresent()) {
                PhotonTrackedTarget target = results.getBestTarget();

                robotFieldPose = PhotonUtils.estimateFieldToRobotAprilTag(
                        target.getBestCameraToTarget(),
                        aprilTagFieldLayout.getTagPose(target.getFiducialId()).orElse(null),
                        // aprilTagFieldLayout.getTagPose(m_tag_ID).orElse(null),
                        cam12_2_robotTransform3d);

                visionPose3d_str = String.format("vision: %.2f    %.2f    %.0f",
                        robotFieldPose.getX(),
                        robotFieldPose.getY(),
                        Math.toDegrees(robotFieldPose.getRotation().getZ()));
            } else {
                visionPose3d_str = String.format("vision: %.2f    %.2f    %.0f",
                        99.0, 99.0, 0.0);
            }
        }
    }

    public double getDistanceFromTagInInches(int tagID) {
        double distance = -99;
        try {
            for (int i = 0; i < tags.size(); i++) {
                if (tags.get(i).getFiducialId() == tagID) {
                    distance = tags.get(i).getBestCameraToTarget().getX();
                }
            }
            if (distance != -99) {
                return RobotMath.metersToInches(distance);
            } else {
                return -99;
            }
        } catch (Exception e) {
            return -99;
        }
    }
    // used to put data to the dashboard
    public String getPose2dString() {
        return visionPose3d_str;
    }
}