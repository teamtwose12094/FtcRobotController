package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

public class    HardwareK9bot {
    //Public OpMode members.
    //Motors
    public DcMotor frontLeftMotor = null;
    public DcMotor frontRightMotor = null;
    public DcMotor backLeftMotor = null;
    public DcMotor backRightMotor = null;
    public DcMotor carouselMotor = null;
    public DcMotor linSlideMotor1 = null;
    public DcMotor linSlideMotor2 = null;
    public DcMotor jointMotor = null;

    //Servos
    public Servo grab1 = null;
    public Servo grab2 = null;

    //Local OpMode members.
    HardwareMap hwMap = null;
    private ElapsedTime period = new ElapsedTime();

    //Constructor
    public HardwareK9bot() {}

    public void init(HardwareMap ahwMap) { /* Initialize standard Hardware interfaces */
        //Save reference to HW Map
        hwMap = ahwMap;

        //Define and Initialize Motors
        frontLeftMotor = hwMap.dcMotor.get("front_left_drive");
        frontRightMotor = hwMap.dcMotor.get("front_right_drive");
        backLeftMotor = hwMap.dcMotor.get("back_left_drive");
        backRightMotor = hwMap.dcMotor.get("back_right_drive");
        //carouselMotor = hwMap.dcMotor.get("carousel_motor");
        linSlideMotor1 = hwMap.dcMotor.get("linear_motor1");
        linSlideMotor2 = hwMap.dcMotor.get("linear_motor2");
        //jointMotor = hwMap.dcMotor.get("joint_motor");

        // Set all motors to zero power
        frontLeftMotor.setPower(0);
        frontRightMotor.setPower(0);
        backLeftMotor.setPower(0);
        backRightMotor.setPower(0);
        //carouselMotor.setPower(0);
        linSlideMotor1.setPower(0);
        linSlideMotor2.setPower(0);
        //jointMotor.setPower(0);

        //Set all motors to run without encoders.
        frontLeftMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        frontRightMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backLeftMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backRightMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        //carouselMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        linSlideMotor1.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        linSlideMotor2.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        //jointMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        //Define and initialize ALL installed servos.
        grab1 = hwMap.servo.get("grab1");
        grab2 = hwMap.servo.get("grab2");
    }

    //waitForTick implements a periodic delay. However, this acts like a metronome with a regular periodic tick.  This is used to compensate for varying processing times for each cycle.The function looks at the elapsed cycle time, and sleeps for the remaining time interval.@param periodMs  Length of wait cycle in mSec.
    public void waitForTick(long periodMs) {
        long remaining = periodMs - (long) period.milliseconds();
        //Sleep for the remaining portion of the regular cycle period.
        if (remaining > 0) {
            try {
                Thread.sleep(remaining);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        // Reset the cycle clock for the next pass.
        period.reset();
    }
}
