package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.ExposureControl;
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.GainControl;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import org.firstinspires.ftc.vision.tfod.TfodProcessor;

import java.util.concurrent.TimeUnit;

public class Vision {
    private LinearOpMode opMode = null;

    //Vision Portal
    public VisionPortal portal;

    //April Tag
    public AprilTagProcessor aprilTag;

    //TFOD
    public TfodProcessor tfod;

    //Constructor
    public Vision() {}
    public Vision(LinearOpMode opMde) {
        opMode = opMde;
    }

    public void initAprilTag() {
        aprilTag = new AprilTagProcessor.Builder().build();
        portal = new VisionPortal.Builder()
                .setCamera(opMode.hardwareMap.get(WebcamName.class, "Webcam 1"))
                .addProcessor(aprilTag)
                .build();
        setManualExposure(6, 250);
        portal.stopStreaming();
    }

    public void initTFOD() {
        String[] labels = {"Blue Prop", "Red Prop"};
        tfod = new TfodProcessor.Builder().setModelAssetName("Model.tflite").setModelLabels(labels).build();
        portal = new VisionPortal.Builder()
                .setCamera(opMode.hardwareMap.get(WebcamName.class, "Webcam 1"))
                .addProcessor(tfod)
                .build();
        setManualExposure(6, 250);
        portal.stopStreaming();
    }

    public void initAprilTagTFOD() {
        String[] labels = {"Blue Prop", "Red Prop"};
        tfod = new TfodProcessor.Builder().setModelAssetName("Model.tflite").setModelLabels(labels).build();
        aprilTag = new AprilTagProcessor.Builder().build();
        portal = new VisionPortal.Builder()
                .setCamera(opMode.hardwareMap.get(WebcamName.class, "Webcam 1"))
                .addProcessor(aprilTag)
                .addProcessor(tfod)
                .build();
        setManualExposure(6, 250);
        portal.stopStreaming();
    }

    private void setManualExposure( int exposureMS, int gain) {
        if (portal == null) {return;}
        if (portal.getCameraState() != VisionPortal.CameraState.STREAMING) {while (!opMode.isStopRequested() && (portal.getCameraState() != VisionPortal.CameraState.STREAMING)) {opMode.sleep(20);}}
        if (!opMode.isStopRequested()) {
            ExposureControl exposureControl = portal.getCameraControl(ExposureControl.class);
            if (exposureControl.getMode() != ExposureControl.Mode.Manual) {
                exposureControl.setMode(ExposureControl.Mode.Manual);
                opMode.sleep(50);
            }
            exposureControl.setExposure((long)exposureMS, TimeUnit.MILLISECONDS);
            opMode.sleep(20);
            GainControl gainControl = portal.getCameraControl(GainControl.class);
            gainControl.setGain(gain);
            opMode.sleep(20);
        }
    }
}