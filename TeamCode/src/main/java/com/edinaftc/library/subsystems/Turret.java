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
        if (allianceHubButton) {
            allianceHubRunning = true;
            vMotor.setTargetPosition(2500);
            vMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            vMotor.setPower(.75);
            allianceHubButton = false;
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
                if (((vMotor.getCurrentPosition() < 2520) && (vMotor.getCurrentPosition() > 2480)) &&
                        (hMotor.getCurrentPosition() < -1280)) {
                    resetStuff(0.0, 0.0);
                }
            } else {
                if (((vMotor.getCurrentPosition() < 2520) && (vMotor.getCurrentPosition() > 2480)) &&
                        (hMotor.getCurrentPosition() > 1280)) {
                    resetStuff(0.0, 0.0);
                }
            }

            if ((xPower != 0) || (yPower != 0)) {
                resetStuff(xPower, yPower);
            }
        }

        if (sharedHubButton) {
            sharedHubRunning = true;
            vMotor.setTargetPosition(870);
            vMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            vMotor.setPower(.75);
            sharedHubButton = false;
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
                if (((vMotor.getCurrentPosition() < 850) && (vMotor.getCurrentPosition() > 890)) &&
                        (hMotor.getCurrentPosition() > 1280)) {
                    resetStuff(0.0, 0.0);
                }
            } else {
                if (((vMotor.getCurrentPosition() < 850) && (vMotor.getCurrentPosition() > 890)) &&
                        (hMotor.getCurrentPosition() < -1280)) {
                    resetStuff(0.0, 0.0);
                }
            }

            if ((xPower != 0) || (yPower != 0)) {
                resetStuff(xPower, yPower);
            }
        }

        if (turretRecenter) {
            turretRecenterRunning = true;
            hMotor.setTargetPosition(0);
            hMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            hMotor.setPower(.75);
            turretRecenter = false;
        }

        if (turretRecenterRunning) {
            if ((hMotor.getCurrentPosition() <= 250) && (hMotor.getCurrentPosition() >= -250) && !vMotor.isBusy()) {
                vMotor.setTargetPosition(100);
                vMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                vMotor.setPower(1);
            }

            if (((hMotor.getCurrentPosition() < 30) && (hMotor.getCurrentPosition() > -30)) &&
            (vMotor.getCurrentPosition() < 200)) {
                resetStuff(0.0, 0.0);
            }

            if ((xPower != 0) || (yPower != 0)) {
                resetStuff(xPower, yPower);
            }
        }

        if (!sharedHubRunning && !turretRecenterRunning && !allianceHubRunning) {
            hMotor.setPower(xPower);
            vMotor.setPower(yPower);
        }
    }

    private void resetStuff(double xPower, double yPower) {
        allianceHubRunning = false;
        sharedHubRunning = false;
        turretRecenterRunning = false;
        hMotor.setPower(xPower);
        vMotor.setPower(yPower);
        vMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        hMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
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
