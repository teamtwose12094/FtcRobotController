package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;

import java.util.List;

@TeleOp(name = "Teleop")
public class Teleop extends LinearOpMode {
    Hardware hardware = new Hardware(this);
    Vision vision = new Vision(this);
    Event event = new Event(this);
    Config config = new Config();

    //Drive Motor Paramaters
    double driveSpeedMultiplier = config.driveSpeedMultiplier;
    double pivotSpeedMultiplier = config.pivotSpeedMultiplier;

    //Drive
    boolean reverseSteering = true;
    double drive;
    double strafe;
    double turn;

    //Arm
    double armPosition = 0;

    //Arm
    double winchPosition = 0;

    //Linear Slide
    double liftPosition = 0;

    //Wrist
    double wristPosition = config.wristStartPosition;

    //Grabber
    //String grabberStep = "2Pixel";
    double grabberPosition = config.grabberStartPosition;

    //Drone Shooter
    double droneShooterPosition = config.droneShooterStartPosition;

    //Elapsed
    ElapsedTime runtime = new ElapsedTime();
    @Override
    public void runOpMode() {
        //Telementry
        telemetry.setCaptionValueSeparator("");
        telemetry.setItemSeparator("\n   ");
        telemetry.setAutoClear(false);
        Telemetry.Item header = telemetry.addData("","Starting Initialization");
        telemetry.update();

        //Init Hardware
        header.setValue("Initializing Hardware");
        telemetry.update();
        hardware.init();

        //Read Encoders
        hardware.arm.setTargetPosition((int) -armPosition);
        hardware.arm.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        hardware.winch.setTargetPosition((int) winchPosition);
        hardware.winch.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        hardware.lift.setTargetPosition((int) liftPosition);
        hardware.lift.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        hardware.backLeftDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        hardware.backRightDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        hardware.frontLeftDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        hardware.frontRightDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        //Init Event
        //header.setValue("Initializing Event");
        //telemetry.update();
        //event.init();

        //Init Vision
        //header.setValue("Initializing Vision");
        //telemetry.update();
        //vision.initAprilTagTFOD();
        //vision.portal.resumeStreaming();

        //Wait For Start
        header.setValue("Initialized - Press Start");
        telemetry.update();
        //if (!isStarted()) {new Spawn(() -> {int i = 0;while (!isStarted()) {i++;if (i % 8 == 0) {header.setValue("Initialized - Press Start =^ w ^= *telmeowop*");} else if (i % 4 == 0) {header.setValue("Initialized - Press Start =- o -=");} else {header.setValue("Initialized - Press Start =O w O=");}telemetry.update();sleep(1000);}return null;});}
        waitForStart();

        //Reset Elpased
        runtime.reset();

        //Started
        header.setValue("TeleOp Started");
        telemetry.addData("","");
        telemetry.update();

        telemetry.addLine("Player A - Driver")
                .addData("Dpad Left", "Strafe Left")
                .addData("Dpad Right", "Strafe Right")
                .addData("Dpad Up", "Strafe Forward")
                .addData("Dpad Down", "Strafe Backward")
                .addData("Left Stick","Pivot")
                .addData("Right Stick","Drive")
                .addData("Triangle: ","Toggle Slow Mode")
                .addData("Square: ","Reverse Steering")
                .addData("Left & Right Trigger: ","Shoot Drone");
        telemetry.addData("","");

        telemetry.addLine("Player B - Arm")
                .addData("Dpad Up: ","Arm Up")
                .addData("Dpad Down: ","Arm Down")
                .addData("Dpad Left: ","Wrist In")       // Switch these
                .addData("Dpad Right: ","Wrist Out")     // Switch these
                .addData("Right Bumper: ","Grabber In")
                .addData("Left Bumper: ","Grabber Out")
                .addData("Square: ","Winch In")
                .addData("Cross: ","Winch Out")
                .addData("Left Trigger: ","Lift Up")     // Switch these
                .addData("Right Trigger: ","Lift Down"); // Switch these
        telemetry.addData("","");

        Telemetry.Line teleopOutput = telemetry.addLine("Teleop Output");
        Telemetry.Item output = teleopOutput.addData("\n   ","");
        telemetry.addData("","");

        Telemetry.Line teleopFeedback = telemetry.addLine("Teleop Feedback");
        telemetry.addData("","");
        telemetry.update();

        //Buttons Gamepad 1
        Button strafeLeft = event.Button(gamepad1, "dpad_left");;
        Button strafeRight = event.Button(gamepad1, "dpad_right");
        Button driveForward = event.Button(gamepad1, "dpad_up");;
        Button driveBackward = event.Button(gamepad1, "dpad_down");
        Button slowMode = event.Button(gamepad1, "triangle");
        Button switchSteering = event.Button(gamepad1, "square");
        Button shotConfirmRight = event.Button(gamepad1, "right_trigger");
        Button shotConfirmLeft = event.Button(gamepad1, "left_trigger");
        //Buttons Gempad 2
        Button armUp = event.Button(gamepad2, "dpad_up");
        Button armDown = event.Button(gamepad2, "dpad_down");
        Button wristIn = event.Button(gamepad2, "dpad_left");
        Button wristOut = event.Button(gamepad2, "dpad_right");
        Button grabberIn = event.Button(gamepad2, "right_bumper");
        Button grabberOut = event.Button(gamepad2, "left_bumper");
        Button winchIn = event.Button(gamepad2, "square");
        Button winchOut = event.Button(gamepad2, "cross");
        Button liftDown = event.Button(gamepad2, "right_trigger");
        Button liftUp = event.Button(gamepad2, "left_trigger");

        //Add Listener Callbacks
        slowMode.ButtonDown(() -> {
            if (slowMode.toggleState) {
                driveSpeedMultiplier = config.slowDriveSpeedMultiplier;
                pivotSpeedMultiplier = config.slowPivotSpeedMultiplier;
            } else {
                driveSpeedMultiplier = config.driveSpeedMultiplier;
                pivotSpeedMultiplier = config.pivotSpeedMultiplier;
            }
            return null;
        });

        switchSteering.ButtonDown(() -> {
            reverseSteering = !reverseSteering;
            return null;
        });

        while (opModeIsActive()) {
            event.tick();

            //Drive Gamepad 1
            if (!reverseSteering) {
                drive  = gamepad1.right_stick_y  * driveSpeedMultiplier;
                strafe = gamepad1.right_stick_x  * driveSpeedMultiplier;
                turn   = -gamepad1.left_stick_x * pivotSpeedMultiplier;
                if (strafeLeft.active) {
                    strafe = -0.5;
                } else if (strafeRight.active) {
                    strafe = 0.5;
                }
                if (driveForward.active) {
                    drive = -0.5;
                } else if (driveBackward.active) {
                    drive = 0.5;
                }
            } else {
                drive  = -gamepad1.right_stick_y  * driveSpeedMultiplier;
                strafe = -gamepad1.right_stick_x  * driveSpeedMultiplier;
                turn   = -gamepad1.left_stick_x * pivotSpeedMultiplier;
                if (strafeLeft.active) {
                    strafe = 0.5;
                } else if (strafeRight.active) {
                    strafe = -0.5;
                }
                if (driveForward.active) {
                    drive = 0.5;
                } else if (driveBackward.active) {
                    drive = -0.5;
                }
            }
            accelerateRobot(drive, strafe, turn, config.acceleration);
            //moveRobot(drive, strafe, turn);

            //Drone Shooter Gamepad 1
            if (shotConfirmRight.active && shotConfirmLeft.active) {
                droneShooterPosition = config.droneShooterOpenPosition;
            } else {
                droneShooterPosition = config.droneShooterClosePosition;
            }
            hardware.droneShooter.setPosition(droneShooterPosition);

            //Arm Gamepad 2
            if (armUp.active) {
                if (armPosition < config.armMaximumPosition) {
                    armPosition += config.armStep;
                    hardware.arm.setPower(-1);
                }
            } else if (armDown.active) {
                if (armPosition > config.armMinimumPosition) {
                    armPosition -= config.armStep;
                    hardware.arm.setPower(-1);
                } else {
                    hardware.arm.setPower(0);
                }
            }
            hardware.arm.setTargetPosition((int) -armPosition);

            //Lift Gamepad 2
            if (liftUp.active) {
                if (liftPosition < config.liftMaximumPosition) {
                    liftPosition += config.liftStep;
                    hardware.lift.setPower(1);
                }
            } else if (liftDown.active) {
                if (liftPosition > config.liftMinimumPosition) {
                    liftPosition -= config.liftStep;
                    hardware.lift.setPower(1);
                }
            } else if (liftPosition > config.liftMaximumPosition + config.liftStep) {
                hardware.lift.setPower(1);
            }
            hardware.lift.setTargetPosition((int) liftPosition);

            //Winch Gamepad 2
            if (winchIn.active) {
                //if (winchPosition < config.winchMaximumPosition) {
                    winchPosition += config.winchStep;
                    hardware.winch.setPower(1);
                //}
            } else if (winchOut.active) {
                //if (winchPosition > config.winchMinimumPosition) {
                winchPosition -= config.winchStep;
                hardware.winch.setPower(1);
            }// else {
            //    hardware.winch.setPower(0);
            //}
            hardware.winch.setTargetPosition((int) winchPosition);

            //Wrist Gamepad 1
            if (wristOut.active) {
                if (wristPosition < config.wristMaximumPosition) {
                    wristPosition += config.wristStep;
                }
            } else if (wristIn.active) {
                if (wristPosition > config.wristMinimumPosition) {
                    wristPosition -= config.wristStep;
                }
            }
            hardware.wrist.setPosition(wristPosition);

            //Grabber
            if (grabberIn.active) {
                if (grabberPosition < config.grabberMaximumPosition) {
                    grabberPosition += config.grabberStep;
                }
            } else if (grabberOut.active) {
                if (grabberPosition > config.grabberMinimumPosition) {
                    grabberPosition -= config.grabberStep;
                }
            }
            hardware.grabber.setPosition(grabberPosition);

            //List<Recognition> currentRecognitions = vision.tfod.getRecognitions();
            //String out = "";
            //for (Recognition recognition : currentRecognitions) {
            //    out = out + "\nLabel: " + recognition.getLabel() + ", Confidence: " + recognition.getConfidence() + ", Size: " + recognition.getHeight() + "x" + recognition.getWidth();
            //}
            //output.setValue(out);

            telemetry.update();
        }

        header.setValue("Teleop Has Come To A Stop");
        telemetry.update();
    }

    double aX = 0;
    double aY = 0;
    double aYaw = 0;
    public void accelerateRobot(double x, double y, double yaw, double acceleration) {
        if (Math.abs(x - aX) > acceleration) {aX += Math.signum(x - aX) * acceleration;} else {aX = x;}
        if (Math.abs(y - aY) > acceleration) {aY += Math.signum(y - aY) * acceleration;} else {aY = y;}
        if (Math.abs(yaw - aYaw) > acceleration) {aYaw += Math.signum(yaw - aYaw) * acceleration;} else {aYaw = yaw;}
        aX = Math.signum(x) * Math.min(Math.abs(aX), Math.abs(x));
        aY = Math.signum(y) * Math.min(Math.abs(aY), Math.abs(y));
        aYaw = Math.signum(yaw) * Math.min(Math.abs(aYaw), Math.abs(yaw));

        double frontLeftPower    =  aX -aY -aYaw;
        double frontRightPower   =  aX +aY +aYaw;
        double backLeftPower     =  aX +aY -aYaw;
        double backRightackPower =  aX -aY +aYaw;

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

    public void moveRobot(double x, double y, double yaw) {
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
}