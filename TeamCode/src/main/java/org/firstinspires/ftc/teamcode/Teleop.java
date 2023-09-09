/*
Copyright (c) 2016 Robert Atkinson

All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted (subject to the limitations in the disclaimer below) provided that
the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list
of conditions and the following disclaimer.

Redistributions in binary form must reproduce the above copyright notice, this
list of conditions and the following disclaimer in the documentation and/or
other materials provided with the distribution.

Neither the name of Robert Atkinson nor the names of his contributors may be used to
endorse or promote products derived from this software without specific prior
written permission.

NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESSFOR A PARTICULAR PURPOSE
ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.vuforia.CameraDevice;
import com.qualcomm.ftccommon.SoundPlayer;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;

import java.util.List;

@TeleOp(name = "Teleop", group = "K9bot")
public class Teleop extends LinearOpMode {
    //Declare OpMode members.
    HardwareK9bot robot = new HardwareK9bot();

    private static double teleopTankVersion = 1; //Run V1 Program
    private static double motorPowerCoefficient = 2; //Double Motor Power
    private static double stickThreshhold = 0.1; //Minimum Magnitude From Center Of Thumbstick

    private static final String VUFORIA_KEY = "ATz1+9P/////AAABmeqS5/62ZUGpp5bTjFOlpkUQ/xkdYMvOFM8cjbv7n7uq3sYzUf93tbck4Wwz4tLtprq66GBhDQn1s06gkPiK4MJqUHZsdytuNcFacZO/2S66hK08CjwewQE8Wqs1T8I3wIEQENcMkWha0xwyR/2JfDGwQEGPnO56etL1eXzhScwqGARW1kOAS/zSzg4aWBUITk5FvDZG3lMxpZWIFEOmCIO92DR70BAc8QJz+51mzXvdFSb1kcwkvwcNWQ78ZRfnS41hq84A6Ps84PJRij48wy1oonI2tEXx/RHwoWOBcBFev7VNBDLWCo5VFQ3TtBJeHne5STFubET+3Eg1YWcuFhcAIc2zmVrh/W36NY6a4wkl";
    private VuforiaLocalizer vuforia  = null;

    @Override
    public void runOpMode() {

        robot.init(hardwareMap); /* Initialize the hardware variables. The init() method of the hardware class does all the work here*/
        telemetry.addData("Hello human,", "it is I, the FRENCHIEST FRY");
        telemetry.update(); //Send telemetry message to signify robot waiting;
        waitForStart(); //Wait for the game to start (driver presses PLAY)
        //initVuforia();

        //Sounds
        int backupID   = hardwareMap.appContext.getResources().getIdentifier("backup",   "raw", hardwareMap.appContext.getPackageName());
        boolean backupFound = false;
        if (backupID != 0) {
            backupFound   = SoundPlayer.getInstance().preload(hardwareMap.appContext, backupID);
        }
        double backupLength = 1070;
        boolean backupPlaying = false;
        double backupStartTime = System.currentTimeMillis();

        //Slowmode
        boolean slowMode = false;
        boolean lastState = false;

        //Motor Stuff
        int TICKS_PER_REVOLUTION = 1120;
        int TICKS_PER_REVOLUTION_ULTRA = 1120;

        //Linear Slide
        robot.linSlideMotor1.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.linSlideMotor1.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.linSlideMotor1.setPower(0);
        robot.linSlideMotor2.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.linSlideMotor2.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.linSlideMotor2.setPower(0);
        int linSlideDistance = 0;
        int linSlideMax = 670;
        int linSlideMin = 0;
        int linSlideIncrement = 2;

        //Joint
        //robot.jointMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        //robot.jointMotor.setPower(0);
        //robot.jointMotor.setTargetPosition(robot.jointMotor.getCurrentPosition());
        //robot.jointMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        //robot.jointMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        //int jointDistance = 0;
        //int jointMax = 120*3*4*5;
        //int jointMin = 0;
        //int jointIncrement = 1;

        //Grab Servo
        int grabPos = -6250;
        int grabMax = -4600; //Down position
        int grabMin = -10000; //Up position
        int grabIncrement = 10;

        //Run until the end of the match (driver presses STOP)

        telemetry.addData("Hello human,", "Debug1");telemetry.update();

        while (opModeIsActive()) {
            double frontRightPower = 0;
            double frontLeftPower = 0;
            double backRightPower = 0;
            double backLeftPower = 0;
            //double carouselPower = 0;
            double linSlidePower = 0;
            //double jointPower = 0;
            if (teleopTankVersion == 1) {
                //Robot Movement
                //Lateral Movement, FBLR
                double rightStickY = gamepad1.right_stick_y;
                double rightStickX = gamepad1.right_stick_x;
                double rightStickAppliedAngle = Math.toDegrees(Math.atan2(rightStickY,rightStickX)); //Actual Angle Of Right Thumbstick
                double rightStickAppliedPower = Math.sqrt(Math.pow(rightStickY,2)+Math.pow(rightStickX,2)); //Magnitude Of Tumbstick From Center
                double rightStickAdaptedAngle = rightStickAppliedAngle-45; //Adapted Angle For Program Use
                //Pivot
                double leftStickX = -gamepad1.left_stick_x;
                double leftStickY = gamepad1.left_stick_y;
                double leftStickAppliedAngle = Math.toDegrees(Math.atan2(leftStickY,leftStickX))+90; //Actual Angle Of Right Thumbstick
                double leftStickAppliedPower = Math.sqrt(Math.pow(leftStickY,2)+Math.pow(leftStickX,2)); //Magnitude Of Tumbstick From Center
                double leftStickAngledPower = leftStickAppliedPower*Math.sin(Math.toRadians(leftStickAppliedAngle)); //Power Adapted Based On Angle Of Thumbstick
                //Slowmode
                if (gamepad1.y != lastState) {
                    if (gamepad1.y == true) {
                        slowMode = !slowMode;
                    }
                    lastState = gamepad1.y;
                }
                //Lateral
                double lateralMulti = 0.80;
                if (slowMode) {
                    lateralMulti = 0.20;
                }
                if (Math.abs(rightStickAppliedPower) > stickThreshhold) {
                    frontRightPower = (Math.cos(Math.toRadians(rightStickAdaptedAngle)) * (rightStickAppliedPower-stickThreshhold)*lateralMulti);
                    backRightPower = (Math.sin(Math.toRadians(rightStickAdaptedAngle)) * (rightStickAppliedPower-stickThreshhold)*lateralMulti);
                    frontLeftPower = (-Math.sin(Math.toRadians(rightStickAdaptedAngle)) * (rightStickAppliedPower-stickThreshhold)*lateralMulti);
                    backLeftPower = (-Math.cos(Math.toRadians(rightStickAdaptedAngle)) * (rightStickAppliedPower-stickThreshhold)*lateralMulti);
                }
                //Pivot
                double pivotMulti = 0.65;
                if (slowMode) {
                    pivotMulti = 0.15;
                }
                if (Math.abs(leftStickAppliedPower) > stickThreshhold) {
                    frontRightPower += (leftStickAngledPower-stickThreshhold)*-pivotMulti;
                    backRightPower += (leftStickAngledPower-stickThreshhold)*-pivotMulti;
                    frontLeftPower += (leftStickAngledPower-stickThreshhold)*-pivotMulti;
                    backLeftPower += (leftStickAngledPower-stickThreshhold)*-pivotMulti;
                }
                //Carousel Movement
                //if (gamepad1.dpad_left) {
                //    carouselPower = -motorMultiplivePower;
                //} else if (gamepad1.dpad_right) {
                //    carouselPower = motorMultiplivePower;
                //}
                //Linear Slide
                if (gamepad2.right_trigger > 0.3 && robot.linSlideMotor1.getCurrentPosition()-linSlideIncrement < linSlideMax) {
                    linSlideDistance = robot.linSlideMotor1.getCurrentPosition();
                    linSlideDistance += linSlideIncrement;
                    linSlidePower = motorPowerCoefficient;
                } else if (gamepad2.left_trigger > 0.3 && robot.linSlideMotor1.getCurrentPosition()+linSlideIncrement > linSlideMin) {
                    linSlideDistance = robot.linSlideMotor1.getCurrentPosition();
                    linSlideDistance -= linSlideIncrement;
                    linSlidePower = -motorPowerCoefficient;
                }
                //if (rightStickY>stickThreshhold) {
                //    if (backupFound) {
                //        if (!backupPlaying) {
                //            backupPlaying = true;
                //            backupStartTime = System.currentTimeMillis();
                //            SoundPlayer.getInstance().startPlaying(hardwareMap.appContext, backupID);
                //        } else {
                //            if ((backupStartTime + backupLength) < System.currentTimeMillis()) {
                //                backupPlaying = false;
                //            }
                //        }
                //    }
                //    CameraDevice.getInstance().setFlashTorchMode(true);
                //} else {
                //    CameraDevice.getInstance().setFlashTorchMode(false);
                //}
                //Joint
                //if (gamepad2.dpad_up && robot.jointMotor.getCurrentPosition()-jointIncrement < jointMax) {
                //    jointDistance += jointIncrement;
                //    jointPower = -motorMultiplivePower;
                //} else if (gamepad2.dpad_down && robot.jointMotor.getCurrentPosition()+jointIncrement > jointMin) {
                //    jointDistance -= jointIncrement;
                //    jointPower = motorMultiplivePower;
                //} else if (gamepad2.dpad_down || gamepad2.dpad_up) {
                //    jointPower = 0.1;
                //} else {
                //    jointPower = 0.1;
                //}
                //Garbber
                if (gamepad2.right_bumper && grabPos+grabIncrement < grabMax){
                    grabPos += grabIncrement;
                } else if (gamepad2.left_bumper && grabPos-grabIncrement > grabMin){
                    grabPos -= grabIncrement;
                }

                //Set Power To Motors
                robot.frontRightMotor.setPower(frontRightPower*motorPowerCoefficient);
                robot.backRightMotor.setPower(backRightPower*motorPowerCoefficient);
                robot.frontLeftMotor.setPower(frontLeftPower*motorPowerCoefficient);
                robot.backLeftMotor.setPower(backLeftPower*motorPowerCoefficient);
                //robot.carouselMotor.setPower(carouselPower*motorMultiplivePower);
                robot.linSlideMotor1.setPower(linSlidePower);
                robot.linSlideMotor2.setPower(-linSlidePower);
                //robot.jointMotor.setTargetPosition(jointDistance);
                //robot.jointMotor.setPower(jointPower);
                robot.grab1.setPosition(((double)(grabPos+2750)+5000)/5000);
                robot.grab2.setPosition(-(((double)(grabPos+2750)+5000)/5000));

                //telemetry.addData("robot.jointMotor.setPower", jointPower);
                //telemetry.addData("robot.jointMotor.setTargetPosition", jointDistance);
                telemetry.addData("robot.linSlideMotor.setPower", linSlidePower);
                telemetry.addData("robot.grab.setPosition", grabPos);
                //telemetry.addData("robot.carouselMotor.setPower", carouselPower*motorMultiplivePower);

                telemetry.addData("robot.frontRightMotor.setPower", frontRightPower*motorPowerCoefficient);
                telemetry.addData("robot.frontLeftMotor.setPower", frontLeftPower*motorPowerCoefficient);
                telemetry.addData("robot.backLeftMotor.setPower", backLeftPower*motorPowerCoefficient);
                telemetry.addData("robot.backRightMotor.setPower", backRightPower*motorPowerCoefficient);

                telemetry.update();
            }
        }
    }

    public static double clamp(double val, double min, double max) {
        return Math.max(min, Math.min(max, val));
    }

    // Initialize Vuforia and Tensorflow Object Detection
    // All encompassing method for initializng object recognition. Called prior to runpath in OP modes and after start has been pressed
    // Can this be ran prior to waitForStart()?
//    public void initVuforia() {
//        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();
//        parameters.vuforiaLicenseKey = VUFORIA_KEY;
//        parameters.cameraDirection = VuforiaLocalizer.CameraDirection.BACK;
//        vuforia = ClassFactory.getInstance().createVuforia(parameters);
//    }
}


