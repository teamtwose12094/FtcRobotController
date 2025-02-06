package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.ExposureControl;
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.GainControl;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.concurrent.TimeUnit;

public class Hardware {
    private LinearOpMode opMode = null;

    //Motor Variables
    double hdHexCPR = 28.0;
    double coreHexCPR = 288.0;

    double ultraFiveToOne = 5.23;
    double ultraFourToOne = 3.61;
    double ultraThreeToOne = 2.89;
    double spurFourtyToOne = 40.0;
    double spurTwentyToOne = 20.0;

    //Gear Variables
    double fifteenToothDiameter = 12.7; //mm
    double thirtyToothDiameter = 24.0; //mm
    double fourtyFiveToothDiameter = 35.3; //mm
    double sixtyToothDiameter = 46.5; //mm
    double seventyTwoToothDiameter = 55.5; //mm
    double ninetyToothDiameter = 69.0; //mm
    double oneTwentyFiveToothDiameter = 93.5; //mm

    //Wheel Variables
    double mecanumWheelDiameter = 80.0; //mm
    double mecanumWheelCircumfrence = Math.PI * mecanumWheelDiameter;

    //Motors
    public DcMotor leftDrive = null;

    public DcMotor rightDrive = null;

    public DcMotor arm = null;
    public DcMotor winch = null;
    public DcMotor lift = null;

    //Servos
    public Servo grabber = null;
    public Servo wrist = null;
    public Servo droneShooter = null;

    //Constructor
    public Hardware() {}
    public Hardware(LinearOpMode opMde) {
        opMode = opMde;
    }

    public void init() { /* Initialize standard Hardware interfaces */
        //Define and Initialize Motors
        leftDrive = opMode.hardwareMap.dcMotor.get("left_drive"); //Ch P3
        rightDrive = opMode.hardwareMap.dcMotor.get("right_drive"); //Ch P2


        arm = opMode.hardwareMap.dcMotor.get("arm"); //Ex P0
        winch = opMode.hardwareMap.dcMotor.get("winch"); //Ex P2
        lift = opMode.hardwareMap.dcMotor.get("lift"); //Ex P3

        // Set all motors to zero power
        leftDrive.setPower(0);
        rightDrive.setPower(0);


        arm.setPower(0);
        winch.setPower(0);
        lift.setPower(0);

        //Set all motors to run using encoders.
        leftDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        leftDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);



        arm.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        arm.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        winch.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        winch.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        lift.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        lift.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        //Define and initialize ALL installed servos.
        grabber = opMode.hardwareMap.servo.get("grabber"); //Ch P0
        wrist = opMode.hardwareMap.servo.get("wrist"); //Ch P1
        droneShooter = opMode.hardwareMap.servo.get("drone_shooter"); //Ch P2
    }
}