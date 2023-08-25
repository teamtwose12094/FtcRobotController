package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

//@Autonomous(name="Test_Config",group = "Autonomous")
public class TestConfig extends LinearOpMode {
    Servo servo;
    @Override
    public void runOpMode() {
        //Motors
        DcMotor motor0 = hardwareMap.get(DcMotor.class, "motor0");
        motor0.setDirection(DcMotor.Direction.FORWARD);
        DcMotor motor1 = hardwareMap.get(DcMotor.class, "motor1");
        motor0.setDirection(DcMotor.Direction.FORWARD);
        DcMotor motor2 = hardwareMap.get(DcMotor.class, "motor2");
        motor0.setDirection(DcMotor.Direction.FORWARD);
        DcMotor motor3 = hardwareMap.get(DcMotor.class, "motor3");
        motor0.setDirection(DcMotor.Direction.FORWARD);

        //Servos
        Servo servo0 = hardwareMap.get(Servo.class, "servo0");
        Servo servo1 = hardwareMap.get(Servo.class, "servo1");
        Servo servo2 = hardwareMap.get(Servo.class, "servo2");
        Servo servo3 = hardwareMap.get(Servo.class, "servo3");

        //Telemetry
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            //Start Telemetry
            telemetry.addData("Status", "Running");
            telemetry.update();

            //Move Servos
            servo0.setPosition(1);
            servo1.setPosition(1);
            servo2.setPosition(1);
            servo3.setPosition(1);

            //Move Motors
            motor0.setPower(2);
            motor1.setPower(2);
            motor2.setPower(2);
            motor3.setPower(2);
        }

        //Hello
    }
}
