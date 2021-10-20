package com.edinaftc.library.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Lift extends Subsystem {
    private DcMotorEx _lift;
    private double _power;

    public Lift(HardwareMap map){
        _lift = map.get(DcMotorEx.class,"lift");
        _lift.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        _lift.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    public void update(){
        _lift.setPower(-_power);
    }

    public void setLiftPower(double power){
        _power = power;
    }
}
