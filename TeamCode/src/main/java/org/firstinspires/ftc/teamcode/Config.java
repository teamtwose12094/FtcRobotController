package org.firstinspires.ftc.teamcode;

public class Config {
    Hardware hardware = new Hardware();

    //Constructor
    public Config() {}

    //Drivertrain
    double wheelCPMM = (((hardware.hdHexCPR * hardware.ultraFiveToOne * hardware.ultraFourToOne) / hardware.mecanumWheelCircumfrence));
    double wheelBaseWidth = 385.0; //mm
    double wheelBaseLength = 305.0; //mm
    double wheelBaseDiameter = Math.sqrt(Math.pow(wheelBaseWidth, 2.0) + Math.pow(wheelBaseLength, 2.0)); //mm
    double wheelBaseCircumference = Math.PI * wheelBaseDiameter; //mm
    long drivetrainTimeout = 10000;
    long recognitionTimeout = 5000;    //Will give up finding recognition after 5000ms
    long recognitionFoundLeeway = 500; //Will take an additional 500ms after fining a recognition in case it changes its mind

    //General
    double stickDeadzone = 0.1;
    long loopTime = 20; //ms
    long loopsPerSecond = 1000/loopTime;

    //Drive
    double driveSpeedMultiplier = 2;
    double slowDriveSpeedMultiplier = 0.5;
    double acceleration = 0.05;

    //Pivot
    double pivotSpeedMultiplier = 1;
    double slowPivotSpeedMultiplier = 0.5;

    //Arm
    double armMinimumPosition = 0;
    double armMaximumPosition = hardware.hdHexCPR*hardware.ultraFourToOne*hardware.ultraFiveToOne*hardware.ultraFiveToOne;
    double armStep = 10;

    //Lift
    double liftMinimumPosition = 0;
    double liftMaximumPosition = hardware.coreHexCPR;
    double liftStep = 2;

    //Winch
    double winchMinimumPosition = 0;
    double winchMaximumPosition = hardware.hdHexCPR*hardware.ultraFourToOne*hardware.ultraFourToOne*hardware.ultraFourToOne*5;
    double winchStep = 20;

    //Wrist
    double wristMinimumPosition = (45.0)/270.0;
    double wristMaximumPosition = (270.0)/270.0;
    double wristStartPosition = wristMinimumPosition;
    double wristStep = 4/270.0; // 2/270.0

    //Grabber
    double grabberMinimumPosition = (270.0-170.0)/270.0;
    double grabberMaximumPosition = (270.0)/270.0;
    double grabberStartPosition = grabberMaximumPosition;
    double grabberStep = 4/270.0; // 2/270.0

    //double grabber2PixelPosition = (170.0)/270.0;
    //double grabber0PixelPosition = (000.0)/270.0;
    //double grabber2PixelPosition = (270.0)/270.0;
    //double grabber0PixelPosition = (100.0)/270.0;
    //double grabberStartPosition = grabber2PixelPosition;

    //Drone Shooter
    double droneShooterClosePosition = (135.0-15.0)/270.0;
    double droneShooterOpenPosition = (135.0+90.0)/270.0;
    double droneShooterStartPosition = droneShooterClosePosition;

    //Servo
    //Centre == 135.0

    //Camera
    double webCamWidth = 720;
    double webCamHeight = 480;

    //April Tag
    double aprilDriveGain  =  0.02; //0.02
    double aprilStrafeGain =  0.015;//0.015
    double aprilTurnGain   =  0.01; //0.01

    double aprilMaxAutoDrive = 2;
    double aprilMaxAutoStrafe= 2;
    double aprilMaxAutoTurn  = 1.2;

    //TFOD
    double minCertainty = 65;
}