package org.firstinspires.ftc.teamcode;

@com.qualcomm.robotcore.eventloop.opmode.Autonomous(name = "Left Red Terminal", group = "Autonomous")
public class LeftRedTerminal extends Autonomous {
    @Override
    public void runPath() {
        //Ideal Runpath
        //move(24,-90,1);
        //move(24,90,1);
        //initVuforiaTFOD();
        navigateToCone();

        //Modified
        //move(32,0,1,2);
    }
}
