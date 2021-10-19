package com.edinaftc.library.subsystems;


import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class SpinServo extends Subsystem {



     final double incrementSpeed = 0.01;
     final double maxPosition = 1.0;
     final double minPosition = 0.0;
    double position = (maxPosition-minPosition)/2;
    boolean isSpeedIncreasing = true;
     Servo servo;
     CRServo continuousServo;
     boolean clockWise;
     boolean counterClockWise;

    public SpinServo(HardwareMap map) {
        //What is name of the servo in robot for spin duck
        servo = map.get(Servo.class,"");
        continuousServo = map.get(CRServo.class, "");
    }

    @Override
    public void update() {
    /* if (clockWise){
            //Move servo from 0  to 1
            if(isSpeedIncreasing) {
                position = position+incrementSpeed;
                if(position >= maxPosition){
                    position = maxPosition;
                    isSpeedIncreasing= false;
                    servo.setPosition(position);
                }
            }


        }  if (counterClockWise){
            //move from 0 to -1
            position = position - incrementSpeed;
            if(position<= minPosition){
                position = minPosition;
                isSpeedIncreasing = false;
                servo.setPosition(position);
            }

        }*/

     //Continuous servo
       if(clockWise) {
           //full power forward
           continuousServo.setPower(1);
       } else if (counterClockWise) {
           //full power backward
           continuousServo.setPower(-1);
       } else {
           //stop servo
           continuousServo.setPower(0);
       }
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
