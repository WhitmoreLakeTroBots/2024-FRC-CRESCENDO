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
import org.photonvision.targeting.PhotonPipelineResult;
import org.photonvision.targeting.PhotonTrackedTarget;
import com.fasterxml.jackson.databind.ext.OptionalHandlerFactory;

import java.util.List;
import java.util.Optional;

public class SubPoseEstimator extends SubsystemBase {
    public Pose3d robotFieldPose = new Pose3d(0.0, 0.0, 0.0, new Rotation3d());

    private final PhotonCamera cam11A = new PhotonCamera("Cam11A");
    private final PhotonCamera cam11B = new PhotonCamera("Cam11B");
    private Pose3d cam11AFieldPose = robotFieldPose;
    private Pose3d cam11BFieldPose = robotFieldPose;

    public List<PhotonTrackedTarget> tags;
    private String visionPose3d_str11A = "";
    private String visionPose3d_str11B = "";
    private String visionPose3d_strAVG = "";
    private PhotonPipelineResult cam11AResult = null;
    private PhotonPipelineResult cam11BResult = null;

    private AprilTagFieldLayout aprilTagFieldLayout = null;
    // default camera position
    private final Transform3d cam11A_RobotTransform = new Transform3d(
            new Translation3d(RobotMath.inchesToMeters(0),
                    RobotMath.inchesToMeters(0), RobotMath.inchesToMeters(0)),
            new Rotation3d(Math.toRadians(0), Math.toRadians(0), Math.toRadians(0)));
    // -10 14.5 37

    private final Transform3d cam11B_RobotTransform = new Transform3d(
            new Translation3d(RobotMath.inchesToMeters(0),
                    RobotMath.inchesToMeters(0), RobotMath.inchesToMeters(0)),
            new Rotation3d(Math.toRadians(0), Math.toRadians(0), Math.toRadians(0)));

    // -8 24 34
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

        cam11AFieldPose = processTags(cam11A.getLatestResult(), cam11A_RobotTransform);
        cam11BFieldPose = processTags(cam11B.getLatestResult(), cam11B_RobotTransform);

        robotFieldPose = averageFieldPose();

    }

    @Override
    public void simulationPeriodic() {
        // This method will be called once per scheduler run when in simulation
    }

    private Pose3d averageFieldPose() {
        Pose3d sumPose = null;

        if ((cam11AFieldPose == null) && (cam11BFieldPose != null)) {
            sumPose = cam11BFieldPose;
            visionPose3d_str11A = "No Tags Visible";

            visionPose3d_str11B = String.format("vision: %.2f    %.2f    %.2f    %.0f",
                    cam11BFieldPose.getX(),
                    cam11BFieldPose.getY(),
                    cam11BFieldPose.getZ(),
                    Math.toDegrees(cam11BFieldPose.getRotation().getZ()));
            visionPose3d_strAVG = visionPose3d_str11B;

        } else if ((cam11AFieldPose != null) && (cam11BFieldPose == null)) {
            sumPose = cam11AFieldPose;
            visionPose3d_str11B = "No Tags Visible";

            visionPose3d_str11A = String.format("vision: %.2f    %.2f    %.2f    %.0f",
                    cam11AFieldPose.getX(),
                    cam11AFieldPose.getY(),
                    cam11AFieldPose.getZ(),
                    Math.toDegrees(cam11AFieldPose.getRotation().getZ()));
            visionPose3d_strAVG = visionPose3d_str11A;

        } else if ((cam11AFieldPose != null) && (cam11BFieldPose != null)) {

            sumPose = new Pose3d(average(cam11AFieldPose.getX(), cam11BFieldPose.getX()),
                    average(cam11AFieldPose.getY(), cam11BFieldPose.getY()),
                    average(cam11AFieldPose.getZ(), cam11BFieldPose.getZ()),
                    new Rotation3d(average(cam11AFieldPose.getRotation().getX(), cam11BFieldPose.getRotation().getX()),
                            average(cam11AFieldPose.getRotation().getY(), cam11BFieldPose.getRotation().getY()),
                            average(cam11AFieldPose.getRotation().getZ(), cam11BFieldPose.getRotation().getZ())));

            visionPose3d_strAVG = String.format("vision: %.2f    %.2f    %.2f    %.0f",
                    sumPose.getX(),
                    sumPose.getY(),
                    sumPose.getZ(),
                    Math.toDegrees(sumPose.getRotation().getZ()));
                    
            visionPose3d_str11A = String.format("vision: %.2f    %.2f    %.2f    %.0f",
                    cam11AFieldPose.getX(),
                    cam11AFieldPose.getY(),
                    cam11AFieldPose.getZ(),
                    Math.toDegrees(cam11AFieldPose.getRotation().getZ()));
                    
            visionPose3d_str11B = String.format("vision: %.2f    %.2f    %.2f    %.0f",
                    cam11BFieldPose.getX(),
                    cam11BFieldPose.getY(),
                    cam11BFieldPose.getZ(),
                    Math.toDegrees(cam11BFieldPose.getRotation().getZ()));

        }
        return sumPose;
    }

    private double average(double parm1, double parm2) {
        return (parm1 + parm2) / 2;
    }

    private Pose3d processTags(PhotonPipelineResult r, Transform3d robotToCamera) {
        Pose3d fieldPose = null;
        if (r.hasTargets()) {

            Optional<Pose3d> bestTagPose = aprilTagFieldLayout.getTagPose(r.getBestTarget().getFiducialId());
            // visionPose3d_str = "HasTargets";
            if (bestTagPose.isPresent()) {
                PhotonTrackedTarget target = r.getBestTarget();

                fieldPose = PhotonUtils.estimateFieldToRobotAprilTag(
                        target.getBestCameraToTarget(),
                        aprilTagFieldLayout.getTagPose(target.getFiducialId()).orElse(null),
                        // aprilTagFieldLayout.getTagPose(m_tag_ID).orElse(null),
                        robotToCamera);
            }
        
        }
        return fieldPose;
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
    public String getPoseAVG() {
        return visionPose3d_strAVG;
    }

    public String getPose11A() {
        return visionPose3d_str11A;
    }

    public String getPose11B() {
        return visionPose3d_str11B;
    }
}