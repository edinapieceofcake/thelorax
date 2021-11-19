package com.edinaftc.library.subsystems;


import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class SpinServo extends Subsystem {

    CRServo continuousServo;
    double _leftTrigger;

    public SpinServo(HardwareMap map) {
        //What is name of the servo in robot for spin duck
        continuousServo = map.get(CRServo.class, "spinner");

        //set to stop
        continuousServo.setPower(0.0);
    }

    @Override
    public void update() {
       if(_leftTrigger < 0.0){
           //full power forward
           continuousServo.setPower(1);
       } else if (_leftTrigger > 0.0) {
           //full power backward
           continuousServo.setPower(-1);
       } else {
           //stop servo
           continuousServo.setPower(0.0);
       }
    }

    public void spin(double leftTrigger)  {
        _leftTrigger = leftTrigger;
    }

    public void displayTelemetry(Telemetry telemetry) {
        telemetry.addData("trigger, power",  "%f %f", _leftTrigger, continuousServo.getPower());
    }
}
