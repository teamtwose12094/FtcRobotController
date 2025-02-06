package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.util.List;

@Autonomous(name="Test Config",group = "Autonomous")
public class TestConfig extends LinearOpMode {
    @Override
    public void runOpMode() {
        //Telementry
        telemetry.setCaptionValueSeparator("");
        telemetry.setItemSeparator("\n   ");
        telemetry.setAutoClear(false);
        Telemetry.Item header = telemetry.addData("","Press Start");
        Telemetry.Item status = telemetry.addData("Status","Initialized");

        //Wait For Start
        waitForStart();

        //Started
        header.setValue("Test Started");
        status.setValue("Running");

        //Motors
        List<DcMotor> motors = hardwareMap.getAll(DcMotor.class);
        for (int i = 0; i < motors.size(); i++) {
            motors.get(i).setPower(1);
        }

        //Servoss
        List<Servo> servos = hardwareMap.getAll(Servo.class);
        for (int i = 0; i < servos.size(); i++) {
            servos.get(i).setPosition(1);
        }

        //CRServoss
        List<CRServo> crservos = hardwareMap.getAll(CRServo.class);
        for (int i = 0; i < crservos.size(); i++) {
            crservos.get(i).setPower(1);
        }

        //Telemetry
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        while (opModeIsActive()) {idle();}
    }
}