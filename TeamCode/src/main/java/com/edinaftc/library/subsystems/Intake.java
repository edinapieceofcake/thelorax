package com.edinaftc.library.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Intake extends Subsystem {
    enum Direction{
        Clockwise, Counterclockwise, Neither
    }

    private DcMotorEx _motor4321;
    private Direction _direction;

    public Intake(HardwareMap map){
        _motor4321 = map.get(DcMotorEx.class,"intake");
        _motor4321.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        _motor4321.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    @Override
    public void update(){
        if (_direction == Direction.Counterclockwise){
            _motor4321.setPower(.5);
        } else if (_direction == Direction.Clockwise){
            _motor4321.setPower(-.5);
        } else {
            _motor4321.setPower(0);
        }
    }

    public void solveDirection(boolean leftBumper, boolean rightBumper){
        _direction = Direction.Neither;

        if (leftBumper){
            _direction = Direction.Counterclockwise;
        } else if (rightBumper){
            _direction = Direction.Clockwise;
        }
    }
}
