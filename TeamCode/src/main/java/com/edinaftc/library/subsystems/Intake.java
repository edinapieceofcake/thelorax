package com.edinaftc.library.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class Intake extends Subsystem {
    enum Direction{
        Clockwise, Counterclockwise, Neither
    }

    private DcMotorEx _motor4321;
    private Direction _direction;

    public Intake(HardwareMap map){
        _motor4321 = map.get(DcMotorEx.class,"intake");
    }

    @Override
    public void update(){
        if (_direction == Direction.Counterclockwise){
            _motor4321.setPower(.5);
        } else if (_direction == Direction.Clockwise){
            _motor4321.setPower(-1);
        } else {
            _motor4321.setPower(0);
        }
    }

    public void runIntake(boolean leftBumper, boolean rightBumper){
        if (leftBumper){
            _direction = Direction.Counterclockwise;
        } else if (rightBumper){
            _direction = Direction.Clockwise;
        } else {
            _direction = Direction.Neither;
        }
    }

    public void displayTelemetry(Telemetry telemetry) {
        telemetry.addData("intake power ", "%f", _motor4321.getPower());
    }
}
