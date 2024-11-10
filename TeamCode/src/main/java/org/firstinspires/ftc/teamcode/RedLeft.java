package org.firstinspires.ftc.teamcode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

@com.qualcomm.robotcore.eventloop.opmode.Autonomous(name="Red Left", group = "Autonomous")
public class RedLeft extends Autonomous {
    Event event = new Event();
    Config config = new Config();

    //Elapsed
    private final ElapsedTime runtime = new ElapsedTime();
    @Override
    public void runPath() {
        hardware.arm.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        hardware.arm.setTargetPosition(-250);
        hardware.arm.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        hardware.arm.setPower(1);
        hardware.grabber.setPosition(config.grabberStartPosition);

        pivot(90, 0.5); //Pivot Right
        move(600, 0, 0.5); //Move Forward
        hardware.arm.setPower(0.5);
        hardware.arm.setTargetPosition(0);

    }
}