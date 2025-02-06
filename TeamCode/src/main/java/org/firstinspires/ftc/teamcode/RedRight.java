package org.firstinspires.ftc.teamcode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

@com.qualcomm.robotcore.eventloop.opmode.Autonomous(name="Red Right", group = "Autonomous")
public class RedRight extends Autonomous {
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


        pivot(-90, 0.5); //Pivot Robot
        move(1100, 0, 0.5); //Move Forward
    }
}