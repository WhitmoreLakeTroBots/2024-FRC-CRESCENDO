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
import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.RobotContainer;

public class WL_PhotonCameraHelper extends SubsystemBase{

    Map <String, WL_PhotonCamera> cameras = new HashMap <String, WL_PhotonCamera>();
    public CameraWithTags tagsVisible = CameraWithTags.NoCamerasWithTags;
    private Pose3d AveragePose3d = new Pose3d (0.0, 0.0, 0.0, new Rotation3d (0.0, 0.0, 0.0));

    public WL_PhotonCameraHelper () {


    }

    public void add (WL_PhotonCamera cam){

        cameras.put(cam.getCamName(), cam);

    }


    public void periodic () {
        CameraWithTags localCameraTags = CameraWithTags.NoCamerasWithTags;

        int cameras_withTags = 0;
        double sumLatencyMillis = 0;
        Pose3d sumPose = new Pose3d();
        for (Map.Entry<String, WL_PhotonCamera> e : cameras.entrySet()){
            if (e.getValue().hasTargets()) {
                localCameraTags = CameraWithTags.SomeCamerasWithTags;

                cameras_withTags++;
                sumPose = sumPose.plus(new Transform3d (e.getValue().getRobotPose3d().getTranslation(), e.getValue().getRobotPose3d().getRotation()));
                sumLatencyMillis = sumLatencyMillis + e.getValue().getLatencyMillis();

            }
            else {
                // Currently not sure what to do here.
            }
        }

        if (cameras_withTags > 0) {
            AveragePose3d = sumPose.div(cameras_withTags);
            RobotContainer.getInstance().m_robotDrive.addVision(AveragePose3d.toPose2d(),(sumLatencyMillis / cameras_withTags) );

        }
        else {
            AveragePose3d = new Pose3d (0.0, 0.0, 0.0, new Rotation3d (0.0, 0.0, 0.0));
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
}
