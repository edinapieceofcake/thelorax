package com.edinaftc.library.subsystems;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.Servo;

public class SpinServo extends LinearOpMode {


    Servo servo;
    double servoPos = 0.0;

    @Override
    public void runOpMode()  throws InterruptedException{


        servo = hardwareMap.servo.get("");
        servo.setPosition(servoPos);

        waitForStart();

        servoPos = 0.5;
        servo.setPosition(servoPos);
        sleep(2000);

        servoPos = 1.0;

        servo.setPosition(servoPos);

        idle();

    }



}
