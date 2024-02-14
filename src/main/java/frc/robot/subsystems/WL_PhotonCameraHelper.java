package frc.robot.subsystems;

import java.util.Map;
import java.io.IOException;
import java.util.HashMap;
import org.photonvision.PhotonCamera;
import org.photonvision.targeting.PhotonPipelineResult;
import org.photonvision.targeting.PhotonTrackedTarget;

import edu.wpi.first.apriltag.AprilTagFieldLayout;
import edu.wpi.first.apriltag.AprilTagFields;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Transform3d;
import edu.wpi.first.math.geometry.Translation3d;
import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.RobotContainer;

public class WL_PhotonCameraHelper extends SubsystemBase{

    Map <String, WL_PhotonCamera> cameras = new HashMap <String, WL_PhotonCamera>();
    public CameraWithTags tagsVisible = CameraWithTags.NoCamerasWithTags;
    private Pose3d AveragePose3d = new Pose3d (0.0, 0.0, 0.0, new Rotation3d (0.0, 0.0, 0.0));

    private double sumx, sumy, sumz, sumYaw, sumPitch, sumRoll = 0;
    private String AveragePoseString = "";
    public WL_PhotonCameraHelper () {


    }

    public void add (WL_PhotonCamera cam){

        cameras.put(cam.getCamName(), cam);

    }


    private void zeroSums () {
        sumx = 0.0;
        sumy = 0.0;
        sumz = 0.0;
        sumPitch = 0.0;
        sumRoll = 0.0;
        sumYaw = 0.0;
    }

    private Pose3d calcAveragePose3d(double poses){

        return new Pose3d(sumx / poses, sumy / poses, sumz/poses,
               new Rotation3d(sumRoll/poses, sumPitch/poses, sumYaw/poses ));

    }

    public void periodic () {
        CameraWithTags localCameraTags = CameraWithTags.NoCamerasWithTags;

        zeroSums();
        int cameras_withTags = 0;
        double sumLatencyMillis = 0;
        // robot axis labled in these web pages
        // https://docs.wpilib.org/en/stable/docs/software/basic-programming/coordinate-system.html
        // https://docs.photonvision.org/en/latest/docs/apriltag-pipelines/coordinate-systems.html

        for (Map.Entry<String, WL_PhotonCamera> e : cameras.entrySet()){
            if (e.getValue().hasTargets()) {
                localCameraTags = CameraWithTags.SomeCamerasWithTags;
                sumx = e.getValue().getRobotPose3d().getX();
                sumy = e.getValue().getRobotPose3d().getY();
                sumz = e.getValue().getRobotPose3d().getZ();
                sumRoll = e.getValue().getRobotPose3d().getRotation().getX();
                sumPitch = e.getValue().getRobotPose3d().getRotation().getY();
                sumYaw = e.getValue().getRobotPose3d().getRotation().getZ();
                cameras_withTags++;
                sumLatencyMillis = sumLatencyMillis + e.getValue().getLatencyMillis();
            }
            else {
                // Currently not sure what to do here.  No tags visible on this camera
            }
        }

        if (cameras_withTags > 0) {
            AveragePose3d = calcAveragePose3d(cameras_withTags);
            RobotContainer.getInstance().m_robotDrive.addVision(AveragePose3d.toPose2d(),(sumLatencyMillis / cameras_withTags) );
            AveragePoseString = String.format ("Cams: %s X: %.2f Y: %.2f Yaw: %.0f",
                 cameras_withTags, AveragePose3d.getX(), AveragePose3d.getY(), AveragePose3d.getRotation().getZ() );
        }
        else {
            AveragePose3d = new Pose3d (0.0, 0.0, 0.0, new Rotation3d (0.0, 0.0, 0.0));
            AveragePoseString = "No Tags Visible";

        }
        tagsVisible = localCameraTags;
    }

    public String getCamString(String camName){
        String retString = "";

        try {
            retString = cameras.get(camName).toString();
        }

        catch(NullPointerException ex)
        {
            retString = String.format("%s not Found. %s", camName, cameras.size());
        }
        return retString;
    }


    public enum CameraWithTags{
        NoCamerasWithTags,
        SomeCamerasWithTags
    }

    public Pose3d getAveragePose3d () {
        return AveragePose3d;

    }
    public String getAveragePoseString () {

        return AveragePoseString;
    }
}
