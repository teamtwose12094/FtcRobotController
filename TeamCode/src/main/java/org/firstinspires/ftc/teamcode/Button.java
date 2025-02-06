package org.firstinspires.ftc.teamcode;
import com.qualcomm.robotcore.hardware.Gamepad;

import java.util.concurrent.Callable;
import java.util.HashSet;
import java.lang.Thread;

public class Button extends Thread {
    public Gamepad gamepad;
    public String buttonString;
    public Boolean toggleState = false;
    public Boolean lastState = false;
    public Boolean currentState = false;
    public Boolean active = false;

    //Constructor
    public Button(Gamepad gmpd, String btnstr) {
        gamepad = gmpd;
        buttonString = btnstr;
    }

    HashSet<Callable> buttonUp = new HashSet<Callable>();
    HashSet<Callable> buttonDown = new HashSet<Callable>();

    public void ButtonUp(Callable<Void> func) {
        buttonUp.add(func);
    }

    public void ButtonDown(Callable<Void> func) {
        buttonDown.add(func);
    }

    public void run()  {
        if (lastState != currentState) {
            lastState = currentState;
            if (currentState) {
                toggleState = !toggleState;
                for (Callable<Void> func : buttonDown) {try {func.call();} catch (Exception e) {throw new RuntimeException(e);}}
            } else {
                for (Callable<Void> func : buttonUp) {try {func.call();} catch (Exception e) {throw new RuntimeException(e);}}
            }
        }
    }

    public void tick() {
        Boolean state = false;
        if (buttonString == "a") {state = gamepad.a;} else
        if (buttonString == "b") {state = gamepad.b;} else
        if (buttonString == "y") {state = gamepad.y;} else
        if (buttonString == "x") {state = gamepad.x;} else
        if (buttonString == "circle") {state = gamepad.circle;} else
        if (buttonString == "cross") {state = gamepad.cross;} else
        if (buttonString == "triangle") {state = gamepad.triangle;} else
        if (buttonString == "square") {state = gamepad.square;} else
        if (buttonString == "dpad_down") {state = gamepad.dpad_down;} else
        if (buttonString == "dpad_up") {state = gamepad.dpad_up;} else
        if (buttonString == "dpad_left") {state = gamepad.dpad_left;} else
        if (buttonString == "dpad_right") {state = gamepad.dpad_right;} else
        if (buttonString == "right_bumper") {state = gamepad.right_bumper;} else
        if (buttonString == "left_bumper") {state = gamepad.left_bumper;} else
        if (buttonString == "right_trigger") {state = gamepad.right_trigger > 0.5;} else
        if (buttonString == "left_trigger") {state = gamepad.left_trigger > 0.5;} else
        if (buttonString == "right_stick_y") {state = gamepad.right_stick_y > 0.5;} else
        if (buttonString == "left_stick_y") {state = gamepad.left_stick_y > 0.5;} else
        if (buttonString == "right_stick_x") {state = gamepad.right_stick_x > 0.5;} else
        if (buttonString == "left_stick_x") {state = gamepad.left_stick_x > 0.5;} else
        if (buttonString == "right_stick") {state = Math.sqrt(Math.pow(gamepad.right_stick_y,2) + Math.pow(gamepad.right_stick_x,2)) > 0.5;} else
        if (buttonString == "left_stick") {state = Math.sqrt(Math.pow(gamepad.left_stick_y,2) + Math.pow(gamepad.left_stick_x,2)) > 0.5;} else
        if (buttonString == "back") {state = gamepad.back;} else
        if (buttonString == "guide") {state = gamepad.guide;} else
        if (buttonString == "left_stick_buttonString") {state = gamepad.left_stick_button;} else
        if (buttonString == "right_stick_button") {state = gamepad.right_stick_button;} else
        if (buttonString == "options") {state = gamepad.options;} else
        if (buttonString == "ps") {state = gamepad.ps;} else
        if (buttonString == "share") {state = gamepad.share;} else
        if (buttonString == "start") {state = gamepad.start;} else
        if (buttonString == "touchpad") {state = gamepad.touchpad;} else
        if (buttonString == "touchpad_finger_1") {state = gamepad.touchpad_finger_1;} else
        if (buttonString == "touchpad_finger_2") {state = gamepad.touchpad_finger_2;} else
        if (buttonString == "atRest") {state = gamepad.atRest();} else
        if (buttonString == "isRumbling") {state = gamepad.isRumbling();}

        currentState = state;
        active = state;
        Button thread = this;
        thread.start();
    }
}