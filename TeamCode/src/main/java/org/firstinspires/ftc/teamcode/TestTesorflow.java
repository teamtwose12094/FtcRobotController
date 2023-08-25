package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;

import java.util.List;

//@Autonomous(name="Test_Tensorflow",group = "Autonomous")
public class TestTesorflow extends LinearOpMode {
    final String VUFORIA_KEY = "ATz1+9P/////AAABmeqS5/62ZUGpp5bTjFOlpkUQ/xkdYMvOFM8cjbv7n7uq3sYzUf93tbck4Wwz4tLtprq66GBhDQn1s06gkPiK4MJqUHZsdytuNcFacZO/2S66hK08CjwewQE8Wqs1T8I3wIEQENcMkWha0xwyR/2JfDGwQEGPnO56etL1eXzhScwqGARW1kOAS/zSzg4aWBUITk5FvDZG3lMxpZWIFEOmCIO92DR70BAc8QJz+51mzXvdFSb1kcwkvwcNWQ78ZRfnS41hq84A6Ps84PJRij48wy1oonI2tEXx/RHwoWOBcBFev7VNBDLWCo5VFQ3TtBJeHne5STFubET+3Eg1YWcuFhcAIc2zmVrh/W36NY6a4wkl";
    final String TFOD_MODEL_ASSET = "PowerPlay.tflite";
    final String[] LABELS = {
            "1 Bolt",
            "2 Bulb",
            "3 Panel"
    };

    List<Recognition> previousRecognitions = null;
    String coneType = "null";

    VuforiaLocalizer vuforia  = null;
    TFObjectDetector tfod;

    public void initVuforiaTFOD() {
        //Vuforia
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();
        parameters.vuforiaLicenseKey = VUFORIA_KEY;
        parameters.cameraDirection = VuforiaLocalizer.CameraDirection.BACK;
        vuforia = ClassFactory.getInstance().createVuforia(parameters);
        //TFOD
        int tfodMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("tfodMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        TFObjectDetector.Parameters tfodParameters = new TFObjectDetector.Parameters(tfodMonitorViewId);
        tfodParameters.minResultConfidence = 0.10f;
        tfodParameters.isModelTensorFlow2 = true;
        tfodParameters.inputSize = 320;
        tfod = ClassFactory.getInstance().createTFObjectDetector(tfodParameters, vuforia);
        tfod.loadModelFromAsset(TFOD_MODEL_ASSET, LABELS);
        if (tfod != null) {
            tfod.activate();
            tfod.setZoom(1, 16.0/9.0);
        }
    }

    // (Wait until specific thing is found)
    public Recognition findCone() {
        boolean dontExit = true;
        double start = System.currentTimeMillis();
        double timeOut = 7000;
        double i = 0;
        Recognition cone = null;
        while (dontExit && opModeIsActive()) {
            cone = isLookingAtCone();
            if ((System.currentTimeMillis() - start > timeOut) || cone != null) {
                dontExit = false;
                telemetry.addData("Ex","Ex");telemetry.update();
            }
        }
        telemetry.addData("Ex","Ex2");telemetry.update();
        return cone;
    }

    // (Check for a specific thing)
    public Recognition isLookingAtCone() {
        Recognition cone = null;
        List<Recognition> updatedRecognitions = tfod.getUpdatedRecognitions();
        telemetry.addData("F","F");
        if (updatedRecognitions == null) {updatedRecognitions = previousRecognitions;}
        previousRecognitions = updatedRecognitions;
        if (updatedRecognitions != null) {
            telemetry.addData("T","T");
            int i = 0;
            for (Recognition recognition : updatedRecognitions) {
                i++;
                if (recognition.getLabel().equals("1 Bolt") || recognition.getLabel().equals("2 Bulb") || recognition.getLabel().equals("3 Panel")) {
                    cone = recognition;
                }
            }
        }
        return cone;
    }

    // (Take recognition and move to position)
    public void navigateToCone() {
        telemetry.addData("Movement","move(18,1,1);");
        Recognition cone = findCone();
        if (cone != null) {
            String type = cone.getLabel();
            telemetry.addData("Type", type);
            if (type.equals("1 Bolt")) {
                coneType = type;
                telemetry.addData("Type",coneType);
                telemetry.addData("Movement","move(18,180,1);");

                telemetry.addData("Movement","move(24,-90,1);");
                telemetry.addData("Movement","move(30,1,1);");
            } else if (type.equals("2 Bulble")) {
                coneType = type;
                telemetry.addData("Type",coneType);
                //move(30,1,1);
                telemetry.addData("Movement","move(12,1,1);");
            } else if (type.equals("3 Panel")) {
                coneType = type;
                telemetry.addData("Type",coneType);
                telemetry.addData("Movement","move(18,180,1);");

                telemetry.addData("Movement","move(24,90,1);");
                telemetry.addData("Movement","move(30,1,1);");
            } else {
                telemetry.addData("Movement","random");
            }
        } else {
            telemetry.addData("Movement","random");
        }

        telemetry.update();
    }

    @Override
    public void runOpMode() {
        waitForStart();

        while (opModeIsActive()) {
            initVuforiaTFOD();
            navigateToCone();
            sleep(100000);
            tfod.shutdown();
        }
    }
}
