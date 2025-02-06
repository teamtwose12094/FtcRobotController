package org.firstinspires.ftc.teamcode;

import java.util.concurrent.Callable;

public class Spawn extends Thread {
    Callable<Void> function;

    //Constructor
    public Spawn(Callable<Void> func) {
        function = func;
        Spawn thread = this;
        thread.start();
    }

    public void run()  {
        try {function.call();} catch (Exception e) {throw new RuntimeException(e);}
    }
}