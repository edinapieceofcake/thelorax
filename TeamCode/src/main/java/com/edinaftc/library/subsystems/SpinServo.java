package com.edinaftc.library.subsystems;


import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class SpinServo extends Subsystem {



     final double incrementSpeed = 0.01;
     final double maxPosition = 1.0;
     final double minPosition = 0.0;
    double position = (maxPosition-minPosition)/2;
    boolean isSpeedIncreasing = true;
     Servo servo;
     boolean clockWise;
     boolean counterClockWise;

    public SpinServo(HardwareMap map) {
        //What is name of the servo in robot for spin duck
        servo = map.servo.get("");
    }

    @Override
    public void update() {
     if (clockWise){
            //Move servo from 0  to 1
            if(isSpeedIncreasing) {
                position = position+incrementSpeed;
                if(position >= maxPosition){
                    position = maxPosition;
                    isSpeedIncreasing= false;

                }
            }


        }  if (counterClockWise){
            //move from 0 to -1
            position = position - incrementSpeed;
            if(position<= minPosition){
                position = minPosition;
                isSpeedIncreasing = false;
            }

        }

        servo.setPosition(position);
    }


    public void spin(boolean leftBumper, boolean rightBumper)  {

        if(leftBumper)  {
            counterClockWise = true;
            clockWise = false;
        }
            else if(rightBumper) {
            clockWise = true;
            counterClockWise = false;
        }


       // servo = hardwareMap.servo.get("rightBumper");
        /*servo.setPosition(servoPos);

        waitForStart();

        servoPos = 0.5;
        servo.setPosition(servoPos);
        sleep(2000);

        servoPos = 1.0;

        servo.setPosition(servoPos);

        idle();
*/
    }



}
