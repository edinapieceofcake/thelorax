package com.edinaftc.library.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;


public class Turret extends Subsystem{
    private DcMotorEx vMotor;
    private DcMotorEx hMotor;
    double xPos;
    double yPos;

    public Turret(HardwareMap map){
        vMotor = map.get(DcMotorEx.class, "vm");
        hMotor = map.get(DcMotorEx.class, "hm");

        vMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        vMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        vMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        hMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        hMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        hMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

    }


    public void moveArm(double x, double y) {
        this.xPos = x;
        this.yPos = y;
    }

    public void update(){
        hMotor.setPower(xPos);
        vMotor.setPower(yPos);
    }


    public void displayTelemetry(Telemetry telemetry) {
        telemetry.addData("vm", "%d", vMotor.getCurrentPosition());
        telemetry.addData("hm", "%d", hMotor.getCurrentPosition());

    }

}

/*{
    private DcMotorEx _lift;
    private Servo _bucket;
    private double _power;
    private double _trigger;
    private boolean _movingUp;

    public Turret(HardwareMap map){
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
                _bucket.setPosition(.45);
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
        telemetry.addData("up", "%s", _movingUp);
        telemetry.addData("bucket", "%f", _bucket.getPosition());
    }
}*/
