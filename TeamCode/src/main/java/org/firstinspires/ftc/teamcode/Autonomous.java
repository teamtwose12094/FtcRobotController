 // Script for Autonomous
package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.vuforia.CameraDevice;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.matrices.VectorF;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackableDefaultListener;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;

import java.util.ArrayList;
import java.util.List;

import static org.firstinspires.ftc.robotcore.external.navigation.AngleUnit.DEGREES;
import static org.firstinspires.ftc.robotcore.external.navigation.AxesOrder.XYZ;
import static org.firstinspires.ftc.robotcore.external.navigation.AxesOrder.YZX;
import static org.firstinspires.ftc.robotcore.external.navigation.AxesReference.EXTRINSIC;
import static org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer.CameraDirection.BACK;

public abstract class Autonomous extends LinearOpMode {
    //Declare OpMode members.
    protected HardwareK9bot robot = new HardwareK9bot();

    private static int TICKS_PER_REVOLUTION = 1120;
    private static double GEAR_RATIO = 30.0/90.0;//30/90; //OutputGear/OutputShaft
    private static double WHEEL_BASE = 8.0 + 5.0/8.0;
    private static double WHEEL_BASE_CIRCUMFERENCE = Math.PI *WHEEL_BASE;
    private static double WHEEL_DIAMETER = 3.0;
    private static double WHEEL_CIRCUMFERENCE = Math.PI * WHEEL_DIAMETER;
    private static double TICKS_PER_INCH = (double)TICKS_PER_REVOLUTION * GEAR_RATIO / (WHEEL_CIRCUMFERENCE);
    private static double ARBITRARY_MOVE_CORRECTION_COEFFICIENT = 30.0/30.0;
    private static double ARBITRARY_PIVOT_CORRECTION_COEFFICIENT = 93.0/90.0;
    private static double motorPowerCoefficient = 2.0;

    private static final String VUFORIA_KEY = "ATz1+9P/////AAABmeqS5/62ZUGpp5bTjFOlpkUQ/xkdYMvOFM8cjbv7n7uq3sYzUf93tbck4Wwz4tLtprq66GBhDQn1s06gkPiK4MJqUHZsdytuNcFacZO/2S66hK08CjwewQE8Wqs1T8I3wIEQENcMkWha0xwyR/2JfDGwQEGPnO56etL1eXzhScwqGARW1kOAS/zSzg4aWBUITk5FvDZG3lMxpZWIFEOmCIO92DR70BAc8QJz+51mzXvdFSb1kcwkvwcNWQ78ZRfnS41hq84A6Ps84PJRij48wy1oonI2tEXx/RHwoWOBcBFev7VNBDLWCo5VFQ3TtBJeHne5STFubET+3Eg1YWcuFhcAIc2zmVrh/W36NY6a4wkl";
    private static final String TFOD_MODEL_ASSET = "model_20230224_193844.tflite";
    private static final String[] LABELS = {
            "Purple",
            "Orange",
            "Green"
    };

    public List<Recognition> previousRecognitions = null;
    public String coneType = "null";

    private VuforiaLocalizer vuforia  = null;
    private TFObjectDetector tfod = null;

    @Override
    public void runOpMode() throws InterruptedException {
        robot.init(hardwareMap);
        waitForStart();
        //initVuforiaTFOD();
        //initVuforia();
        runPath();
        if (tfod != null) { tfod.shutdown(); }
    }

    abstract protected void runPath();

    protected  void privotMotor(DcMotor motor,double angle,double power) {
        angle = -angle;
        double distance = (((double)angle)/360)*WHEEL_CIRCUMFERENCE;

        double motorPower = 0;
        int motorDistance = 0;
        motorPower = Math.signum(angle);
        motorPower = inclamp(motorPower,0.01);
        motorDistance = (int)(distance*TICKS_PER_INCH);
        motorDistance = outclamp(motorDistance,10);

        motor.setMode(DcMotor.RunMode.RESET_ENCODERS);
        motor.setTargetPosition(motorDistance);
        motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motor.setPower(motorPower*power*motorPowerCoefficient);
        boolean dontExit = true;
        double start = System.currentTimeMillis();
        while (motor.isBusy() && opModeIsActive() && dontExit) {
            if (System.currentTimeMillis() - start > 10000) {dontExit = false;}
            idle();
        }
        motor.setPower(0);
        motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motor.setMode(DcMotor.RunMode.RESET_ENCODERS);

        sleep(10);
    }

    protected void move(double distance, int angle, double power, int mode) {
        if (mode == 0) {mode = 1;}
        distance = distance*ARBITRARY_MOVE_CORRECTION_COEFFICIENT;
        angle = angle-45-90+1;
        boolean frontRightActive = true;
        boolean frontLeftActive = true;
        boolean backRightActive = true;
        boolean backLeftActive = true;

        double frontRightPower = 0;
        double frontLeftPower = 0;
        double backRightPower = 0;
        double backLeftPower = 0;

        int frontRightDistance = 0;
        int frontLeftDistance = 0;
        int backRightDistance = 0;
        int backLeftDistance = 0;

        frontRightPower = (Math.cos(Math.toRadians((double)angle)));
        backRightPower = (Math.sin(Math.toRadians((double)angle)));
        frontLeftPower = (-Math.sin(Math.toRadians((double)angle)));
        backLeftPower = (-Math.cos(Math.toRadians((double)angle)));

        frontRightPower = inclamp(frontRightPower,0.01);
        backRightPower = inclamp(backRightPower,0.01);
        backLeftPower = inclamp(backLeftPower,0.01);
        frontLeftPower = inclamp(frontLeftPower,0.01);

        frontRightDistance = (int)(distance*TICKS_PER_INCH*frontRightPower);
        frontLeftDistance = (int)(distance*TICKS_PER_INCH*frontLeftPower);
        backRightDistance = (int)(distance*TICKS_PER_INCH*backRightPower);
        backLeftDistance = (int)(distance*TICKS_PER_INCH*backLeftPower);

        frontRightDistance = outclamp(frontRightDistance,10);
        frontLeftDistance = outclamp(frontLeftDistance,10);
        backRightDistance = outclamp(backRightDistance,10);
        backLeftDistance = outclamp(backLeftDistance,10);

        robot.frontLeftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.backLeftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.frontRightMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.backRightMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        robot.frontLeftMotor.setTargetPosition(frontLeftDistance);
        robot.backLeftMotor.setTargetPosition(backLeftDistance);
        robot.frontRightMotor.setTargetPosition(frontRightDistance);
        robot.backRightMotor.setTargetPosition(backRightDistance);

        robot.frontLeftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        robot.backLeftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        robot.frontRightMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        robot.backRightMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        robot.frontRightMotor.setPower(frontRightPower*motorPowerCoefficient*power);
        robot.backRightMotor.setPower(backRightPower*motorPowerCoefficient*power);
        robot.frontLeftMotor.setPower(frontLeftPower*motorPowerCoefficient*power);
        robot.backLeftMotor.setPower(backLeftPower*motorPowerCoefficient*power);

//        telemetry.addData("frontLeftTargetDistance",robot.frontLeftMotor.getTargetPosition());
//        telemetry.addData("frontLeftCurrentDistance",robot.frontLeftMotor.getCurrentPosition());
//        telemetry.addData("frontLeftPower",robot.frontLeftMotor.getPower());
//
//        telemetry.addData("frontRightTargetDistance",robot.frontRightMotor.getTargetPosition());
//        telemetry.addData("frontRightCurrentDistance",robot.frontRightMotor.getCurrentPosition());
//        telemetry.addData("frontRightPower",robot.frontRightMotor.getPower());
//
//        telemetry.addData("backLeftTargetDistance",robot.backLeftMotor.getTargetPosition());
//        telemetry.addData("backLeftCurrentDistance",robot.backLeftMotor.getCurrentPosition());
//        telemetry.addData("backLeftPower",robot.backLeftMotor.getPower());
//
//        telemetry.addData("backRightTargetDistance",robot.backRightMotor.getTargetPosition());
//        telemetry.addData("backRightCurrentDistance",robot.backRightMotor.getCurrentPosition());
//        telemetry.addData("backRightPower",robot.backRightMotor.getPower());

        boolean dontExit = true;
        double start = System.currentTimeMillis();
        int i = 0;

        if (mode == 1) {
            while ((backRightActive /*|| frontRightActive || backLeftActive || frontLeftActive*/) && dontExit && opModeIsActive()) {
                if (System.currentTimeMillis() - start > 10000) {
                    dontExit = false;
                }
                idle();
                i++;
                //All Stop
                if (!robot.backRightMotor.isBusy() && backRightActive) {
                    robot.frontLeftMotor.setPower(0);
                    robot.backRightMotor.setPower(0);
                    robot.backLeftMotor.setPower(0);
                    robot.frontRightMotor.setPower(0);
                    backRightActive = false;
//                    telemetry.addData("backRightElapsed (s)", (System.currentTimeMillis() - start) * 1000);
//                    telemetry.addData("backRightIteration", i);
//                    telemetry.addData("backRightBusy", robot.backRightMotor.isBusy());
//                    telemetry.addData("backLeftBusy", robot.backLeftMotor.isBusy());
//                    telemetry.addData("frontRightBusy", robot.frontRightMotor.isBusy());
//                    telemetry.addData("frontLeftBusy", robot.frontLeftMotor.isBusy());
                }
            }
        }

        if (mode == 2) {
            while ((backRightActive || frontRightActive || backLeftActive || frontLeftActive) && dontExit && opModeIsActive()) {
                if (System.currentTimeMillis() - start > 10000) {dontExit = false;}
                idle();
                i++;
                //Telemetry Regular Stop
                if (!robot.backRightMotor.isBusy() && backRightActive) {
                    robot.backRightMotor.setPower(0);
                    backRightActive = false;
//                    telemetry.addData("backRightElapsed (s)",(System.currentTimeMillis() - start)*1000);
//                    telemetry.addData("backRightIteration",i);
                }
                if (!robot.backLeftMotor.isBusy() && backLeftActive) {
                    robot.backLeftMotor.setPower(0);
                    backLeftActive = false;
//                    telemetry.addData("backLeftElapsed (s)",(System.currentTimeMillis() - start)*1000);
//                    telemetry.addData("backLeftIteration",i);
                }
                if (!robot.frontRightMotor.isBusy() && frontRightActive) {
                    robot.frontRightMotor.setPower(0);
                    frontRightActive = false;
//                    telemetry.addData("frontRightElapsed (s)",(System.currentTimeMillis() - start)*1000);
//                    telemetry.addData("frontRightIteration",i);
                }
                if (!robot.frontLeftMotor.isBusy() && frontLeftActive) {
                    robot.frontLeftMotor.setPower(0);
                    frontLeftActive = false;
//                    telemetry.addData("frontLeftElapsed (s)",(System.currentTimeMillis() - start)*1000);
//                    telemetry.addData("frontLeftIteration",i);
                }
            }
        }

        robot.frontLeftMotor.setPower(0);
        robot.frontRightMotor.setPower(0);
        robot.backLeftMotor.setPower(0);
        robot.backRightMotor.setPower(0);

        robot.frontLeftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.backLeftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.frontRightMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.backRightMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        robot.frontLeftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.backLeftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.frontRightMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.backRightMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        sleep(10);
    }

    protected void move2(double distance, int angle, double power,int mode) {
        if (mode == 0) {mode = 1;}
        distance = distance*ARBITRARY_MOVE_CORRECTION_COEFFICIENT;
        angle = angle-45-90+1;
        boolean frontRightActive = true;
        boolean frontLeftActive = true;
        boolean backRightActive = true;
        boolean backLeftActive = true;

        double frontRightPower = 0;
        double frontLeftPower = 0;
        double backRightPower = 0;
        double backLeftPower = 0;

        int frontRightDistance = 0;
        int frontLeftDistance = 0;
        int backRightDistance = 0;
        int backLeftDistance = 0;

        frontRightPower = (Math.cos(Math.toRadians((double)angle)));
        backRightPower = (Math.sin(Math.toRadians((double)angle)));
        frontLeftPower = (-Math.sin(Math.toRadians((double)angle)));
        backLeftPower = (-Math.cos(Math.toRadians((double)angle)));

        frontRightPower = inclamp(frontRightPower,0.01);
        backRightPower = inclamp(backRightPower,0.01);
        backLeftPower = inclamp(backLeftPower,0.01);
        frontLeftPower = inclamp(frontLeftPower,0.01);

        frontRightDistance = (int)(distance*TICKS_PER_INCH*frontRightPower);
        frontLeftDistance = (int)(distance*TICKS_PER_INCH*frontLeftPower);
        backRightDistance = (int)(distance*TICKS_PER_INCH*backRightPower);
        backLeftDistance = (int)(distance*TICKS_PER_INCH*backLeftPower);

        frontRightDistance = outclamp(frontRightDistance,10);
        frontLeftDistance = outclamp(frontLeftDistance,10);
        backRightDistance = outclamp(backRightDistance,10);
        backLeftDistance = outclamp(backLeftDistance,10);

        robot.frontLeftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.backLeftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.frontRightMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.backRightMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        robot.frontLeftMotor.setTargetPosition(frontLeftDistance);
        robot.backLeftMotor.setTargetPosition(backLeftDistance);
        robot.frontRightMotor.setTargetPosition(frontRightDistance);
        robot.backRightMotor.setTargetPosition(backRightDistance);

        robot.frontLeftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.backLeftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.frontRightMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.backRightMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        robot.frontRightMotor.setPower(frontRightPower*motorPowerCoefficient*power);
        robot.backRightMotor.setPower(backRightPower*motorPowerCoefficient*power);
        robot.frontLeftMotor.setPower(frontLeftPower*motorPowerCoefficient*power);
        robot.backLeftMotor.setPower(backLeftPower*motorPowerCoefficient*power);

//        telemetry.addData("frontLeftTargetDistance",robot.frontLeftMotor.getTargetPosition());
//        telemetry.addData("frontLeftCurrentDistance",robot.frontLeftMotor.getCurrentPosition());
//        telemetry.addData("frontLeftPower",robot.frontLeftMotor.getPower());
//
//        telemetry.addData("frontRightTargetDistance",robot.frontRightMotor.getTargetPosition());
//        telemetry.addData("frontRightCurrentDistance",robot.frontRightMotor.getCurrentPosition());
//        telemetry.addData("frontRightPower",robot.frontRightMotor.getPower());
//
//        telemetry.addData("backLeftTargetDistance",robot.backLeftMotor.getTargetPosition());
//        telemetry.addData("backLeftCurrentDistance",robot.backLeftMotor.getCurrentPosition());
//        telemetry.addData("backLeftPower",robot.backLeftMotor.getPower());
//
//        telemetry.addData("backRightTargetDistance",robot.backRightMotor.getTargetPosition());
//        telemetry.addData("backRightCurrentDistance",robot.backRightMotor.getCurrentPosition());
//        telemetry.addData("backRightPower",robot.backRightMotor.getPower());

        boolean dontExit = true;
        double start = System.currentTimeMillis();
        int i = 0;

        if (mode == 1) {
            while ((backRightActive /*|| frontRightActive || backLeftActive || frontLeftActive*/) && dontExit && opModeIsActive()) {
                if (System.currentTimeMillis() - start > 10000) {
                    dontExit = false;
                }
                idle();
                i++;
                //AllStop
                if (Math.abs(robot.backRightMotor.getCurrentPosition()) > Math.abs(backRightDistance) && backRightActive) {
                    robot.frontLeftMotor.setPower(0);
                    robot.backRightMotor.setPower(0);
                    robot.backLeftMotor.setPower(0);
                    robot.frontRightMotor.setPower(0);
                    backRightActive = false;
//                    telemetry.addData("backRightElapsed (s)", (System.currentTimeMillis() - start) * 1000);
//                    telemetry.addData("backRightIteration", i);
//                    telemetry.addData("backRightDistance", robot.backRightMotor.getCurrentPosition());
//                    telemetry.addData("backLeftDistance", robot.backLeftMotor.getCurrentPosition());
//                    telemetry.addData("frontRightDistance", robot.frontRightMotor.getCurrentPosition());
//                    telemetry.addData("frontLeftDistance", robot.frontLeftMotor.getCurrentPosition());
                }
            }
        }

        if (mode == 2) {
            while ((backRightActive || frontRightActive || backLeftActive || frontLeftActive) && dontExit && opModeIsActive()) {
                if (System.currentTimeMillis() - start > 10000) {
                    dontExit = false;
                }
                idle();
                i++;
                //Telemetry Regular Stop
                if (Math.abs(robot.backRightMotor.getCurrentPosition()) > Math.abs(backRightDistance) && backRightActive) {
                    robot.backRightMotor.setPower(0);
                    backRightActive = false;
//                    telemetry.addData("backRightElapsed (s)", (System.currentTimeMillis() - start) * 1000);
//                    telemetry.addData("backRightIteration", i);
                }
                if (Math.abs(robot.backLeftMotor.getCurrentPosition()) > Math.abs(backLeftDistance) && backLeftActive) {
                    robot.backLeftMotor.setPower(0);
                    backLeftActive = false;
//                    telemetry.addData("backLeftElapsed (s)", (System.currentTimeMillis() - start) * 1000);
//                    telemetry.addData("backLeftIteration", i);
                }
                if (Math.abs(robot.frontRightMotor.getCurrentPosition()) > Math.abs(frontRightDistance) && frontRightActive) {
                    robot.frontRightMotor.setPower(0);
                    frontRightActive = false;
//                    telemetry.addData("frontRightElapsed (s)", (System.currentTimeMillis() - start) * 1000);
//                    telemetry.addData("frontRightIteration", i);
                }
                if (Math.abs(robot.frontLeftMotor.getCurrentPosition()) > Math.abs(frontLeftDistance) && frontLeftActive) {
                    robot.frontLeftMotor.setPower(0);
                    frontLeftActive = false;
//                    telemetry.addData("frontLeftElapsed (s)", (System.currentTimeMillis() - start) * 1000);
//                    telemetry.addData("frontLeftIteration", i);
                }
            }
        }

        robot.frontLeftMotor.setPower(0);
        robot.frontRightMotor.setPower(0);
        robot.backLeftMotor.setPower(0);
        robot.backRightMotor.setPower(0);

        robot.frontLeftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.backLeftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.frontRightMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.backRightMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        robot.frontLeftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.backLeftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.frontRightMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.backRightMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        sleep(10);
    }

    protected void pivot(int angle, double power) {
        angle = (int)(((double)-angle)*ARBITRARY_PIVOT_CORRECTION_COEFFICIENT);
        angle = angle;
        double distance = (((double)angle)/360)*WHEEL_BASE_CIRCUMFERENCE*2;
        boolean frontRightActive = true;
        boolean frontLeftActive = true;
        boolean backRightActive = true;
        boolean backLeftActive = true;

        double frontRightPower = 0;
        double frontLeftPower = 0;
        double backRightPower = 0;
        double backLeftPower = 0;

        int frontRightDistance = 0;
        int frontLeftDistance = 0;
        int backRightDistance = 0;
        int backLeftDistance = 0;

        frontRightPower = Math.signum(angle);
        backRightPower = Math.signum(angle);
        frontLeftPower = Math.signum(angle);
        backLeftPower = Math.signum(angle);

        frontRightPower = inclamp(frontRightPower,0.01);
        backRightPower = inclamp(backRightPower,0.01);
        backLeftPower = inclamp(backLeftPower,0.01);
        frontLeftPower = inclamp(frontLeftPower,0.01);

        frontRightDistance = (int)(distance*TICKS_PER_INCH);
        frontLeftDistance = (int)(distance*TICKS_PER_INCH);
        backRightDistance = (int)(distance*TICKS_PER_INCH);
        backLeftDistance = (int)(distance*TICKS_PER_INCH);

        frontRightDistance = outclamp(frontRightDistance,10);
        frontLeftDistance = outclamp(frontLeftDistance,10);
        backRightDistance = outclamp(backRightDistance,10);
        backLeftDistance = outclamp(backLeftDistance,10);

        robot.frontLeftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.backLeftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.frontRightMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.backRightMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        robot.frontLeftMotor.setTargetPosition(frontLeftDistance);
        robot.backLeftMotor.setTargetPosition(backLeftDistance);
        robot.frontRightMotor.setTargetPosition(frontRightDistance);
        robot.backRightMotor.setTargetPosition(backRightDistance);

        robot.frontLeftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        robot.backLeftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        robot.frontRightMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        robot.backRightMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        robot.frontLeftMotor.setPower(frontLeftPower*motorPowerCoefficient*power);
        robot.backLeftMotor.setPower(backLeftPower*motorPowerCoefficient*power);
        robot.frontRightMotor.setPower(frontRightPower*motorPowerCoefficient*power);
        robot.backRightMotor.setPower(backRightPower*motorPowerCoefficient*power);

        boolean dontExit = true;
        double start = System.currentTimeMillis();
        while ((backRightActive || frontRightActive || backLeftActive || frontLeftActive) && dontExit && opModeIsActive()) {
            idle();
            if (System.currentTimeMillis() - start > 10000) {dontExit = false;}
            if (!robot.backRightMotor.isBusy() && backRightActive) {robot.backRightMotor.setPower(0);backRightActive = false;}
            if (!robot.backLeftMotor.isBusy() && backLeftActive) {robot.backLeftMotor.setPower(0);backLeftActive = false;}
            if (!robot.frontRightMotor.isBusy() && frontRightActive) {robot.frontRightMotor.setPower(0);frontRightActive = false;}
            if (!robot.frontLeftMotor.isBusy() && frontLeftActive) {robot.frontLeftMotor.setPower(0);frontLeftActive = false;}
        }

        robot.frontLeftMotor.setPower(0);
        robot.frontRightMotor.setPower(0);
        robot.backLeftMotor.setPower(0);
        robot.backRightMotor.setPower(0);

        robot.frontLeftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.backLeftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.frontRightMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.backRightMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        robot.frontLeftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.backLeftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.frontRightMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.backRightMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        
        sleep(10);
    }

    public static int outclamp(int num,int threshhold) {
        if (num < threshhold && num > -threshhold) {
            num = threshhold*(num/Math.abs(num));
        }
        return num;
    }
    public static double inclamp(double num,double replace) {
        if (num == 0 || num == -0) {
            num = replace*(num/Math.abs(num));
        }
        return num;
    }

    // Initialize Vuforia and Tensorflow Object Detection
    // All encompassing method for initializng object recognition. Called prior to runpath in OP modes and after start has been pressed
    // Can this be ran prior to waitForStart()?
    public void initVuforia() {
        if (opModeIsActive()) {
            VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();
            parameters.vuforiaLicenseKey = VUFORIA_KEY;
            parameters.cameraDirection = VuforiaLocalizer.CameraDirection.BACK;
            //parameters.cameraName = hardwareMap.get(WebcamName.class, "Webcam 1");
            vuforia = ClassFactory.getInstance().createVuforia(parameters);
            CameraDevice.getInstance().setFlashTorchMode(true);
        }
    }
    public void initTFOD() {
        if (opModeIsActive()) {
            int tfodMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("tfodMonitorViewId", "id", hardwareMap.appContext.getPackageName());
            TFObjectDetector.Parameters tfodParameters = new TFObjectDetector.Parameters(tfodMonitorViewId);
            tfodParameters.minResultConfidence = 0.70f;
            tfodParameters.isModelTensorFlow2 = true;
            tfodParameters.inputSize = 320;
            tfod = ClassFactory.getInstance().createTFObjectDetector(tfodParameters, vuforia);
            tfod.loadModelFromAsset(TFOD_MODEL_ASSET, LABELS);
            if (tfod != null) {
                tfod.activate();
                tfod.setZoom(1, 16.0 / 9.0);
            }
        }
    }
    public void initVuforiaTFOD() {
        //Vuforia
        initVuforia();
        //TFOD
        initTFOD();
    }

    // (Wait until specific thing is found)
    public Recognition findCone() {
        boolean dontExit = true;
        double start = System.currentTimeMillis();
        double timeOut = 5000;
        double i = 0;
        Recognition cone = null;
        while (dontExit && opModeIsActive()) {
            cone = isLookingAtCone();
            if ((System.currentTimeMillis() - start > timeOut) || cone != null) {
                dontExit = false;
                telemetry.addData("Ex","Ex");telemetry.update();
            }
        }
        if (opModeIsActive()) {
            sleep(3000);
        }
        if (opModeIsActive()) {
            cone = isLookingAtCone();
            telemetry.addData("Ex", "Ex2");
            telemetry.update();
        }
        return cone;
    }

    // (Check for a specific thing)
    public Recognition isLookingAtCone() {
        //Recognition cone = null;
        Recognition bestRecognition = null;
        float highestRecogniton = 0;
        List<Recognition> updatedRecognitions = tfod.getUpdatedRecognitions();
        telemetry.addData("F","F");
        if (updatedRecognitions == null) {updatedRecognitions = previousRecognitions;}
        previousRecognitions = updatedRecognitions;
        if (updatedRecognitions != null && opModeIsActive()) {
            telemetry.addData("T","T");
            int i = 0;
            for (Recognition recognition : updatedRecognitions) {
                i++;
                if ((recognition.getLabel().equals("Green") || recognition.getLabel().equals("Orange") || recognition.getLabel().equals("Purple")) && recognition.getConfidence() > highestRecogniton) {
                    highestRecogniton = recognition.getConfidence();
                    bestRecognition = recognition;
                }
            }
        }
        return bestRecognition;
    }

    // (Take recognition and move to position)
    public void navigateToCone() {
        initVuforia();
        move(12.5,0,0.25,2); //Move Up To Cone
        pivot(20,1); //Pivot Left So Camera Sees Cone
        move(5.5,0,0.25,2);
        //initVuforiaTFOD();
        initTFOD();
        //move(6.5,-90,0.25,2); //Move Left So Camera Sees Cone
        Recognition cone = findCone();
        move(-5.5,0,0.25,2);
        pivot(-20,1); //Pivot Right Back To Middle
        //move(6.5,85,0.25,2); //Move Right Back To Middle
        if (cone != null) {
            String type = cone.getLabel();
            telemetry.addData("Type",type);telemetry.update();
            if (type.equals("Green")) { //Actually Sees Purple (1)
                coneType = type;
                telemetry.addData("Type",coneType);telemetry.update();
                move(9,180,0.25,2); //Move Back To Start
                move(39,-90,0.25,2); //Move Left
                move(38,0,0.25,2); //Go Straight
            } else if (type.equals("Orange")) { //Sees Orange (2)
                coneType = type;
                telemetry.addData("Type",coneType);telemetry.update();
                move(25,0,0.25,2); //Go Straight
            } else if (type.equals("Purple")) { //Actually Sees Green (3)
                coneType = type;
                telemetry.addData("Type",coneType);telemetry.update();
                move(9,180,0.25,2); //Move Back To Start
                move(36,90,0.25,2); //Move Right
                move(38,0,0.25,2); //Go Straight
            } else {
                randomPark();
            }
        } else {
            randomPark();
        }
        CameraDevice.getInstance().setFlashTorchMode(false);
    }

    public void randomPark() {
        double randomPos = Math.ceil(Math.random()*2);
        if (randomPos == 1) { //1
            move(9,180,0.25,2); //Move Back To Start
            move(39,-90,0.25,2); //Move Left
            move(38,0,0.25,2); //Go Straight
        } else if (randomPos == 3) { //2
            move(25,0,0.25,2); //Go Straight
        } else if (randomPos == 2) { //3
            move(9,180,0.25,2); //Move Back To Start
            move(36,90,0.25,2); //Move Right
            move(38,0,0.25,2); //Go Straight
        }
        telemetry.addData("Type","Random");telemetry.update();
    }

    /*@Deprecated
    // Method waits until it recognizes a duck (Recognitions are updates) and returns information. Method yeilds
    // (Wait until specific thing is found)
    public Recognition findDuck() {
        boolean dontExit = true;
        double start = System.currentTimeMillis();
        double timeOut = 7000;
        double i = 0;
        Recognition duck = null;
        while (dontExit && opModeIsActive()) {
            duck = islookingAtDuck();
            if ((System.currentTimeMillis() - start > timeOut) || duck != null) {
                dontExit = false;
                telemetry.addData("Ex","Ex");telemetry.update();
            }
        }
        telemetry.addData("Ex","Ex2");telemetry.update();
        return duck;
    }*/

    /*@Deprecated
    // Checks for updated recognitions matching the type "Duck" and returns the recogniton for the duck if found. Returns null otherwise as nothing was found
    // (Check for a specific thing)
    public Recognition islookingAtDuck() {
        Recognition duck = null;
        List<Recognition> updatedRecognitions = tfod.getUpdatedRecognitions();
        telemetry.addData("F","F");
        if (updatedRecognitions == null) {updatedRecognitions = previousRecognitions;}
        previousRecognitions = updatedRecognitions;
        if (updatedRecognitions != null) {
            telemetry.addData("T","T");
            int i = 0;
            for (Recognition recognition : updatedRecognitions) {
                i++;
                if (recognition.getLabel().equals("Duck")) {
                    duck = recognition;
                }
            }
        }
        return duck;
    }*/

    /*@Deprecated
    // Takes recognition and uses coordinates to determine object randomization. Moves to corrosponding spot.
    // (Take recognition and move to position)
    public void navigateToDuck() {
        Recognition duck = findDuck();
        if (duck != null) {
            double position = (duck.getRight() + duck.getRight()) / 2;
            telemetry.addData("Pos",position);telemetry.update();
            move(20,0,1);
            if (position > 0 && position < 333) {
                duckPosition = 1;
                telemetry.addData("Pos",duckPosition);telemetry.update();
                move(10, 0, 1);
                move(-10, 0, 1);
            } else if (position > 333 && position < 666) {
                duckPosition = 2;
                telemetry.addData("Pos",duckPosition);telemetry.update();
                move(20, -90, 1);
                move(10, 0, 1);
                move(-10, 0, 1);
                move(-20, 90, 1);
            } else if (position > 666 && position < 1000) {
                duckPosition = 3;
                telemetry.addData("Pos",duckPosition);telemetry.update();
                move(40, -90, 1);
                move(10, 0, 1);
                move(-10, 0, 1);
                move(-40, 90, 1);
            }
            move(-20, 0, 1);
        }
    }*/
}



