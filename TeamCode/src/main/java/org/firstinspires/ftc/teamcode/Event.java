package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.Gamepad;

import java.util.ArrayList;

public class Event extends Thread {
    LinearOpMode opMode = null;

    //Constructor
    public Event() {}
    public Event(LinearOpMode opMde) {
        opMode = opMde;
    }

    ArrayList<Button> buttons = new ArrayList<Button>();

    public void bind(Button button) {
        buttons.add(button);
    }

    public Button Button(Gamepad gamepad, String buttonString) {
        Button button = new Button(gamepad, buttonString);
        bind(button);
        return button;
    }

    public void tick() {
        for (Integer i = 0; i < buttons.size(); i++) {buttons.get(i).tick();}
    }

    public void run()  {
        opMode.waitForStart();
        while(opMode.opModeIsActive()) {
            tick();
        }
    }

    public void init() {
        Event thread = this;
        thread.start();
    }
}