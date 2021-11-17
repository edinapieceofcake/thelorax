package com.edinaftc.library.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class Lift extends Subsystem {
    private DcMotorEx _lift;
    private Servo _bucket;
    private double _power;
    private double _trigger;
    private boolean _movingUp;

    public Lift(HardwareMap map){
        _lift = map.get(DcMotorEx.class,"lift");
        _lift.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        _lift.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        _lift.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        _bucket = map.get(Servo.class,"bucket");
        _movingUp = false;
    }

    public void update(){
        if (_trigger <= 0.8)
            if ((_lift.getCurrentPosition() > 150) && _movingUp)
                _bucket.setPosition(.3);
            else
                _bucket.setPosition(-1);
        else
            _bucket.setPosition(1);

        _lift.setPower(-_power);

    }

    public void setLiftPower(double power, double trigger){
        _power = power;
        _trigger = trigger;
        if (_power < 0)
            _movingUp = true;
        else if (_power > 0)
            _movingUp = false;
    }

    public void displayTelemetry(Telemetry telemetry) {
        telemetry.addData("lift", "%d", _lift.getCurrentPosition());
        telemetry.addData("trigger", "%f", _trigger);
        telemetry.addData("power", "%f", _power);
    }
}
