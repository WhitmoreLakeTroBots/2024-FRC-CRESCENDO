package frc.robot.subsystems;

import java.util.Map;
import java.io.IOException;
import java.util.HashMap;
import org.photonvision.PhotonCamera;
import org.photonvision.targeting.PhotonPipelineResult;
import org.photonvision.targeting.PhotonTrackedTarget;

import edu.wpi.first.apriltag.AprilTagFieldLayout;
import edu.wpi.first.apriltag.AprilTagFields;
import edu.wpi.first.math.geometry.Transform3d;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class WL_PhotonCameraHelper extends SubsystemBase{

    Map <String, WL_PhotonCamera> cameras = new HashMap <String, WL_PhotonCamera>();

    WL_PhotonCameraHelper () {



    }

    public void add (WL_PhotonCamera cam){

        cameras.put(cam.getName(), cam);

    }


    public void remove (String key) {
        cameras.remove (key);

    }

    public void periodic () {
        for (Map.Entry<String, WL_PhotonCamera> e : cameras.entrySet()){
            e.getValue().periodic();
        }
    }

}
