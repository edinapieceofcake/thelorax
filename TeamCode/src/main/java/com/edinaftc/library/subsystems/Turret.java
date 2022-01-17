package com.edinaftc.library.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;


public class Turret extends Subsystem{
    private DcMotorEx vMotor;
    private DcMotorEx hMotor;
    private boolean sharedHubButton;
    private boolean sharedHubRunning;
    private boolean turretRecenter;
    private boolean turretRecenterRunning;
    private boolean allianceHubButton;
    private boolean allianceHubRunning;
    private boolean turretMoving;
    private boolean isRedTeleop;
    double xPower;
    double yPower;

    public Turret(HardwareMap map){
        vMotor = map.get(DcMotorEx.class, "vm");
        hMotor = map.get(DcMotorEx.class, "hm");

        vMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        vMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        vMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        hMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        hMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        hMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        sharedHubButton = false;
        sharedHubRunning = false;
        turretRecenter = false;
        turretRecenterRunning = false;
        allianceHubButton = false;
        allianceHubRunning = false;
        turretMoving = false;
        isRedTeleop = false;
    }

    public void moveArm(double x, double y, boolean turretRecenter, boolean sharedHubButton, boolean allianceHubButton, boolean isRedTeleop) {
        this.xPower = x;
        this.yPower = y;
        this.turretRecenter = turretRecenter;
        this.sharedHubButton = sharedHubButton;
        this.allianceHubButton = allianceHubButton;
        this.isRedTeleop = isRedTeleop;
    }

    public void update(){
        if (allianceHubButton && !turretMoving) {
            allianceHubRunning = true;
            vMotor.setTargetPosition(1800);
            vMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            vMotor.setPower(.75);
            allianceHubButton = false;
            turretMoving = true;
        }

        if (allianceHubRunning) {
            if ((vMotor.getCurrentPosition() >= 1180) && !hMotor.isBusy()) {
                if (isRedTeleop)
                    hMotor.setTargetPosition(-1300);
                else
                    hMotor.setTargetPosition(1300);

                hMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                hMotor.setPower(.75);
            }

            if (isRedTeleop) {
                if (((vMotor.getCurrentPosition() < 1820) && (vMotor.getCurrentPosition() > 1780)) &&
                        (hMotor.getCurrentPosition() < -1280)) {
                    allianceHubRunning = false;
                    hMotor.setPower(0);
                    vMotor.setPower(0);
                    vMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                    hMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                    turretMoving = false;
                }
            } else {
                if (((vMotor.getCurrentPosition() < 1820) && (vMotor.getCurrentPosition() > 1780)) &&
                        (hMotor.getCurrentPosition() > 1280)) {
                    allianceHubRunning = false;
                    hMotor.setPower(0);
                    vMotor.setPower(0);
                    vMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                    hMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                    turretMoving = false;
                }
            }

            if ((xPower != 0) || (yPower != 0)) {
                allianceHubRunning = false;
                hMotor.setPower(xPower);
                vMotor.setPower(yPower);
                vMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                hMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                turretMoving = false;
            }
        }

        if (sharedHubButton && !turretMoving) {
            sharedHubRunning = true;
            vMotor.setTargetPosition(700);
            vMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            vMotor.setPower(.75);
            sharedHubButton = false;
            turretMoving = true;
        }

        if (sharedHubRunning) {
            if ((vMotor.getCurrentPosition() >= 680) && !hMotor.isBusy()) {
                if (isRedTeleop)
                    hMotor.setTargetPosition(1300);
                else
                    hMotor.setTargetPosition(-1300);
                hMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                hMotor.setPower(.75);
            }

            if (isRedTeleop) {
                if (((vMotor.getCurrentPosition() < 720) && (vMotor.getCurrentPosition() > 680)) &&
                        (hMotor.getCurrentPosition() > 1280)) {
                    sharedHubRunning = false;
                    hMotor.setPower(0);
                    vMotor.setPower(0);
                    vMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                    hMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                    turretMoving = false;
                }
            } else {
                if (((vMotor.getCurrentPosition() < 720) && (vMotor.getCurrentPosition() > 680)) &&
                        (hMotor.getCurrentPosition() < -1280)) {
                    sharedHubRunning = false;
                    hMotor.setPower(0);
                    vMotor.setPower(0);
                    vMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                    hMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                    turretMoving = false;
                }
            }

            if ((xPower != 0) || (yPower != 0)) {
                sharedHubRunning = false;
                hMotor.setPower(xPower);
                vMotor.setPower(yPower);
                vMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                hMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                turretMoving = false;
            }
        }

        if (turretRecenter && !turretMoving) {
            turretRecenterRunning = true;
            hMotor.setTargetPosition(0);
            hMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            hMotor.setPower(.75);
            turretRecenter = false;
            turretMoving = true;
        }

        if (turretRecenterRunning) {
            if ((hMotor.getCurrentPosition() <= 250) && (hMotor.getCurrentPosition() >= -250)) {
                vMotor.setTargetPosition(160);
                vMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                vMotor.setPower(1);
            }

            if (((hMotor.getCurrentPosition() < 30) && (hMotor.getCurrentPosition() > -30)) &&
            (vMotor.getCurrentPosition() < 200)) {
                turretRecenterRunning = false;
                hMotor.setPower(0);
                vMotor.setPower(0);
                vMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                hMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                turretMoving = false;
            }

            if ((xPower != 0) || (yPower != 0)) {
                turretRecenterRunning = false;
                hMotor.setPower(xPower);
                vMotor.setPower(yPower);
                vMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                hMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                turretMoving = false;
            }
        }

        if (!sharedHubRunning && !turretRecenterRunning && !allianceHubRunning) {
            hMotor.setPower(xPower);
            vMotor.setPower(yPower);
        }
    }

    public void displayTelemetry(Telemetry telemetry) {
        telemetry.addData("vm, hm", "%d %d", vMotor.getCurrentPosition(), hMotor.getCurrentPosition());
        telemetry.addData("vpower, hpower", "%f %f", vMotor.getPower(), hMotor.getPower());
        telemetry.addData("recenter", turretRecenterRunning);
        telemetry.addData("shared", sharedHubRunning);
        telemetry.addData("alliance", allianceHubRunning);
        telemetry.addData("isRed", isRedTeleop);
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
