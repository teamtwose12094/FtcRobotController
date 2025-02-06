package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;

import java.util.Arrays;
import java.util.List;

public abstract class Autonomous extends LinearOpMode {
    Hardware hardware = new Hardware(this);
    Vision vision = new Vision(this);
    Config config = new Config();

    //Elapsed
    ElapsedTime runtime = new ElapsedTime();
    Telemetry.Item header = null;
    Telemetry.Item output = null;
    @Override
    public void runOpMode() throws InterruptedException {
        //Telementry
        telemetry.setCaptionValueSeparator("");
        telemetry.setItemSeparator("\n   ");
        telemetry.setAutoClear(false);
        header = telemetry.addData("","Starting Initialization");
        telemetry.update();

        //Init Hardware
        header.setValue("Initializing Hardware");
        telemetry.update();
        hardware.init();

        //Init Vision
        header.setValue("Initializing Vision");
        telemetry.update();
        vision.initAprilTagTFOD();
        vision.portal.resumeStreaming();

        //Wait For Start
        header.setValue("Initialized - Press Start");
        telemetry.update();
        //if (!isStarted()) {new Spawn(() -> {int i = 0;while (!isStarted()) {i++;if (i % 8 == 0) {header.setValue("Initialized - Press Start =n w n= *autonomouzzz*");} else if (i % 4 == 0) {header.setValue("Initialized - Press Start =- o -=");} else {header.setValue("Initialized - Press Start =O w O=");}telemetry.update();sleep(1000);}return null;});}
        waitForStart();

        //Reset Elpased
        runtime.reset();

        //Started
        header.setValue("Autonomous Started");
        telemetry.addData("","");
        telemetry.update();

        Telemetry.Line teleopOutput = telemetry.addLine("Teleop Output");
        output = teleopOutput.addData("\n   ","");
        telemetry.addData("","");

        Telemetry.Line teleopFeedback = telemetry.addLine("Teleop Feedback");
        telemetry.addData("","");
        telemetry.update();

        runPath();
    }

    abstract protected void runPath();

    protected void privotMotor(DcMotor motor, double angle, double speed) {
        angle = -angle;
        int distance = (int) ((((double) angle) / 360) * hardware.hdHexCPR * hardware.spurFourtyToOne);
        double power = 0;
        power = Math.signum(angle);
        power = inclamp(power,0.01);

        motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motor.setTargetPosition(distance);
        motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motor.setPower(power * speed);
        boolean dontExit = true;
        double start = System.currentTimeMillis();
        while (motor.isBusy() && opModeIsActive() && dontExit) {
            if (System.currentTimeMillis() - start > 10000) {dontExit = false;}
            idle();
        }
        motor.setPower(0);
        motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        sleep(10);
    }

    protected void move(double distance, int angle, double speed) {
        distance = distance * config.wheelCPMM;
        angle = angle-135;

        boolean backLeftActive = true;
        boolean backRightActive = true;
        boolean frontLeftActive = true;
        boolean frontRightActive = true;

        double backLeftPower = 0;
        double backRightPower = 0;
        double frontLeftPower = 0;
        double frontRightPower = 0;

        int backLeftDistance = 0;
        int backRightDistance = 0;
        int frontLeftDistance = 0;
        int frontRightDistance = 0;

        //When all are equal to Math.cos(Math.toRadians(45)) that is full forward, power should be 1...
        backLeftPower = (-Math.cos(Math.toRadians((double) angle))) / Math.cos(Math.toRadians(45));
        backRightPower = (Math.sin(Math.toRadians((double) angle))) / Math.cos(Math.toRadians(45));
        frontLeftPower = (Math.sin(Math.toRadians((double) angle))) / Math.cos(Math.toRadians(45));
        frontRightPower = (Math.cos(Math.toRadians((double) angle))) / Math.cos(Math.toRadians(45));

        backLeftPower = inclamp(backLeftPower, 0.01);
        backRightPower = inclamp(backRightPower, 0.01);
        frontLeftPower = inclamp(frontLeftPower, 0.01);
        frontRightPower = inclamp(frontRightPower, 0.01);

        backLeftDistance = (int)(distance * backLeftPower);
        backRightDistance = (int)(distance * backRightPower);
        frontLeftDistance = (int)(distance * frontLeftPower);
        frontRightDistance = (int)(distance * frontRightPower);

        backLeftDistance = outclamp(backLeftDistance,10);
        backRightDistance = outclamp(backRightDistance,10);
        frontLeftDistance = outclamp(frontLeftDistance,10);
        frontRightDistance = outclamp(frontRightDistance,10);

        hardware.backLeftDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        hardware.backRightDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        hardware.frontLeftDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        hardware.frontRightDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        hardware.backLeftDrive.setTargetPosition(backLeftDistance);
        hardware.backRightDrive.setTargetPosition(backRightDistance);
        hardware.frontLeftDrive.setTargetPosition(frontLeftDistance);
        hardware.frontRightDrive.setTargetPosition(frontRightDistance);

        hardware.backLeftDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        hardware.backRightDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        hardware.frontLeftDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        hardware.frontRightDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        hardware.backLeftDrive.setPower(backLeftPower * speed);
        hardware.backRightDrive.setPower(backRightPower * speed);
        hardware.frontLeftDrive.setPower(frontLeftPower * speed);
        hardware.frontRightDrive.setPower(frontRightPower * speed);

        boolean dontExit = true;
        double start = System.currentTimeMillis();

        while ((backRightActive && frontRightActive && backLeftActive && frontLeftActive) && dontExit && opModeIsActive()) {
            if (System.currentTimeMillis() - start > config.drivetrainTimeout) {dontExit = false;}
            idle();
            if (!hardware.backLeftDrive.isBusy() && backLeftActive) {hardware.backLeftDrive.setPower(0); backLeftActive = false;}
            if (!hardware.backRightDrive.isBusy() && backRightActive) {hardware.backRightDrive.setPower(0); backRightActive = false;}
            if (!hardware.frontLeftDrive.isBusy() && frontLeftActive) {hardware.frontLeftDrive.setPower(0); frontLeftActive = false;}
            if (!hardware.frontRightDrive.isBusy() && frontRightActive) {hardware.frontRightDrive.setPower(0); frontRightActive = false;}
        }

        hardware.backLeftDrive.setPower(0);
        hardware.backRightDrive.setPower(0);
        hardware.frontLeftDrive.setPower(0);
        hardware.frontRightDrive.setPower(0);

        hardware.backLeftDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        hardware.backRightDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        hardware.frontLeftDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        hardware.frontRightDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        hardware.backLeftDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        hardware.backRightDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        hardware.frontLeftDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        hardware.frontRightDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        sleep(10);
    }

    protected void pivot(int angle, double speed) {
        int distance = (int) ((((double) angle) / 360) * config.wheelBaseCircumference * config.wheelCPMM);

        boolean backLeftActive = true;
        boolean backRightActive = true;
        boolean frontLeftActive = true;
        boolean frontRightActive = true;

        double backLeftPower = 0;
        double backRightPower = 0;
        double frontLeftPower = 0;
        double frontRightPower = 0;

        int backLeftDistance = 0;
        int backRightDistance = 0;
        int frontLeftDistance = 0;
        int frontRightDistance = 0;

        backLeftPower = Math.signum(angle);
        backRightPower = Math.signum(angle);
        frontLeftPower = -Math.signum(angle);
        frontRightPower = Math.signum(angle);

        backLeftPower = inclamp(backLeftPower,0.01);
        backRightPower = inclamp(backRightPower,0.01);
        frontLeftPower = inclamp(frontLeftPower,0.01);
        frontRightPower = inclamp(frontRightPower,0.01);

        //Not exactly sure why Math.cos(Math.toRadians(45) needs to be here also when it yields a number larger than 1 but.. eh. it worked.
        //Equivalent to dividing power in the move function by same constant
        backLeftDistance = (int)(distance / Math.cos(Math.toRadians(45)));
        backRightDistance = (int)(distance / Math.cos(Math.toRadians(45)));
        frontLeftDistance = (int)(-distance / Math.cos(Math.toRadians(45)));
        frontRightDistance = (int)(distance / Math.cos(Math.toRadians(45)));

        backLeftDistance = outclamp(backLeftDistance,10);
        backRightDistance = outclamp(backRightDistance,10);
        frontLeftDistance = outclamp(frontLeftDistance,10);
        frontRightDistance = outclamp(frontRightDistance,10);

        hardware.backLeftDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        hardware.backRightDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        hardware.frontLeftDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        hardware.frontRightDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        hardware.backLeftDrive.setTargetPosition(backLeftDistance);
        hardware.backRightDrive.setTargetPosition(backRightDistance);
        hardware.frontLeftDrive.setTargetPosition(frontLeftDistance);
        hardware.frontRightDrive.setTargetPosition(frontRightDistance);

        hardware.backLeftDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        hardware.backRightDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        hardware.frontLeftDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        hardware.frontRightDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        hardware.backLeftDrive.setPower(backLeftPower * speed);
        hardware.backRightDrive.setPower(backRightPower * speed);
        hardware.frontLeftDrive.setPower(frontLeftPower * speed);
        hardware.frontRightDrive.setPower(frontRightPower * speed);

        boolean dontExit = true;
        double start = System.currentTimeMillis();
        while ((backRightActive || frontRightActive || backLeftActive || frontLeftActive) && dontExit && opModeIsActive()) {
            idle();
            if (System.currentTimeMillis() - start > config.drivetrainTimeout) {dontExit = false;}
            if (!hardware.backLeftDrive.isBusy() && backLeftActive) {hardware.backLeftDrive.setPower(0);backLeftActive = false;}
            if (!hardware.backRightDrive.isBusy() && backRightActive) {hardware.backRightDrive.setPower(0);backRightActive = false;}
            if (!hardware.frontLeftDrive.isBusy() && frontLeftActive) {hardware.frontLeftDrive.setPower(0);frontLeftActive = false;}
            if (!hardware.frontRightDrive.isBusy() && frontRightActive) {hardware.frontRightDrive.setPower(0);frontRightActive = false;}
        }

        hardware.backLeftDrive.setPower(0);
        hardware.backRightDrive.setPower(0);
        hardware.frontLeftDrive.setPower(0);
        hardware.frontRightDrive.setPower(0);

        hardware.backLeftDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        hardware.backRightDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        hardware.frontLeftDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        hardware.backRightDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        hardware.backLeftDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        hardware.backRightDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        hardware.frontLeftDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        hardware.frontRightDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        sleep(10);
    }

    public static int outclamp(int num,int threshhold) { //If a number is within range (threshhold, -threshhold) it will return result rounded up to the threshhold (min value)
        if (num < threshhold && num > -threshhold) {
            num = threshhold*(int)Math.signum(num);
        }
        return num;
    }

    public static double inclamp(double num,double replace) { //If number is zero, sets it to the replacement vlaue
        if (num == 0 || num == -0) {
            num = replace*Math.signum(num);
        }
        return num;
    }

    protected void tagetObject(String label, double xOffset, double targetHeight) { //xOffset in px, yOffset in px
        vision.portal.resumeStreaming();
        boolean targetFound = false;
        double distanceDelta = 1000;
        double error = 20; //px
        Recognition desiredRecognition = null;

        boolean dontExit = true;
        double start = System.currentTimeMillis();
        while (opModeIsActive() && distanceDelta > error && dontExit) {
            idle();
            if (System.currentTimeMillis() - start > config.drivetrainTimeout) {dontExit = false;}
            targetFound = false;

            List<Recognition> currentRecognitions = vision.tfod.getRecognitions();
            for (Recognition recognition : currentRecognitions) {
                if (recognition.getLabel() == label && recognition.getConfidence() * 100 > config.minCertainty) {
                    targetFound = true;
                    desiredRecognition = recognition;
                    break;
                }
            }

            if (targetFound) {
                double x = (desiredRecognition.getLeft() + desiredRecognition.getRight()) / 2 ;
                double h = desiredRecognition.getHeight();

                double rangeError = (h - targetHeight);
                double headingError = (x - (xOffset + config.webCamWidth/2));
                distanceDelta = Math.sqrt(Math.pow(rangeError, 2) + Math.pow(headingError, 2));

                double drive = Range.clip(rangeError * config.aprilDriveGain, -config.aprilMaxAutoDrive, config.aprilMaxAutoDrive);
                double turn = 0;//Range.clip(headingError * config.aprilTurnGain, -config.aprilMaxAutoTurn, config.aprilMaxAutoTurn);
                double strafe = Range.clip(-headingError * config.aprilStrafeGain, -config.aprilMaxAutoStrafe, config.aprilMaxAutoStrafe);

                moveRobot(drive, strafe, turn);
            } else {
                moveRobot(0, 0, 0);
            }
        }
        moveRobot(0, 0, 0);
        if (opModeIsActive()) {vision.portal.stopStreaming();}
    }

    protected void targetAprilTag(int tagID, double xOffset, double yOffset) { //xOffset in inches, yOffset in inches
        vision.portal.resumeStreaming();
        double ixOffset = xOffset/25.4;
        double iyOffset = yOffset/25.4;
        boolean targetFound = false;
        double distanceDelta = 1000;
        double error = 240/25.4; //120mm/25.4 = inches
        AprilTagDetection desiredTag = null;
        int equivilantTagID;
        if (tagID > 3) { equivilantTagID = tagID - 3;} else {equivilantTagID = tagID + 3;}

        boolean dontExit = true;
        double start = System.currentTimeMillis();
        while (opModeIsActive() && distanceDelta > error && dontExit) {
            idle();
            if (System.currentTimeMillis() - start > config.drivetrainTimeout) {dontExit = false;}
            targetFound = false;

            List<AprilTagDetection> currentDetections = vision.aprilTag.getDetections();
            for (AprilTagDetection detection : currentDetections) {
                if (detection.metadata != null) {
                    if ((tagID < 0) || (detection.id == tagID) || (detection.id == equivilantTagID)) {
                        targetFound = true;
                        desiredTag = detection;
                        break;
                    }
                }
            }

            if (targetFound) {
                double rangeError = (desiredTag.ftcPose.range - iyOffset);
                double headingError = (desiredTag.ftcPose.bearing - ixOffset);
                double yawError = desiredTag.ftcPose.yaw;
                distanceDelta = Math.sqrt(Math.pow(rangeError, 2) + Math.pow(headingError, 2));

                double drive = Range.clip(rangeError * config.aprilDriveGain, -config.aprilMaxAutoDrive, config.aprilMaxAutoDrive);
                double turn = Range.clip(headingError * config.aprilTurnGain, -config.aprilMaxAutoTurn, config.aprilMaxAutoTurn);
                double strafe = Range.clip(-yawError * config.aprilStrafeGain, -config.aprilMaxAutoStrafe, config.aprilMaxAutoStrafe);

                moveRobot(drive, strafe, turn);
            } else {
                moveRobot(0, 0, 0);
            }
        }
        moveRobot(0, 0, 0);
        if (opModeIsActive()) {vision.portal.stopStreaming();}
    }

    public void moveRobot(double x, double y, double yaw) {
        hardware.backLeftDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        hardware.backRightDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        hardware.frontLeftDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        hardware.frontRightDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        double frontLeftPower    =  x -y -yaw;
        double frontRightPower   =  x +y +yaw;
        double backLeftPower     =  x +y -yaw;
        double backRightackPower =  x -y +yaw;

        double max = Math.max(Math.abs(frontLeftPower), Math.abs(frontRightPower));
        max = Math.max(max, Math.abs(backLeftPower));
        max = Math.max(max, Math.abs(backRightackPower));

        if (max > 1.0) {
            frontLeftPower /= max;
            frontRightPower /= max;
            backLeftPower /= max;
            backRightackPower /= max;
        }

        hardware.backLeftDrive.setPower(backLeftPower);
        hardware.backRightDrive.setPower(-backRightackPower);
        hardware.frontLeftDrive.setPower(-frontLeftPower);
        hardware.frontRightDrive.setPower(-frontRightPower);
    }

    public int getRandomization(String[] labels) {
        Recognition desiredRecognition = getRecognition(labels);
        if (desiredRecognition != null) {
            double x = (desiredRecognition.getLeft() + desiredRecognition.getRight()) / 2 ;
            if (x < config.webCamWidth/2) {
                return 1;
            } else if (x >= config.webCamWidth/2) {
                return 2;
            }
        }
        return -1;
    }

    public Recognition getRecognition(String labels[]) {
        vision.portal.resumeStreaming();
        Recognition desiredRecognition = null;

        boolean dontExit = true;
        double start = System.currentTimeMillis();
        double targetFoundTime = 0;
        while (opModeIsActive() && dontExit) {
            String recognitions = "";
            idle();
            if (System.currentTimeMillis() - start > config.recognitionTimeout) {dontExit = false;}
            if (System.currentTimeMillis() - targetFoundTime > config.recognitionFoundLeeway && targetFoundTime != 0) {dontExit = false;}

            double highestConfidence = 0;
            List<Recognition> currentRecognitions = vision.tfod.getRecognitions();
            for (Recognition recognition : currentRecognitions) {
                double confidence = recognition.getConfidence() * 100;
                if (Arrays.asList(labels).contains(recognition.getLabel()) && confidence > config.minCertainty && confidence > highestConfidence) {
                    highestConfidence = confidence;
                    desiredRecognition = recognition;
                    if (targetFoundTime == 0) {targetFoundTime = System.currentTimeMillis();}
                }
                recognitions = recognitions + recognition.getLabel() + " " + confidence + "%\n";
            }
            output.setValue(recognitions);
            telemetry.update();
        }
        if (opModeIsActive()) {vision.portal.stopStreaming();}
        return desiredRecognition;
    }
}

