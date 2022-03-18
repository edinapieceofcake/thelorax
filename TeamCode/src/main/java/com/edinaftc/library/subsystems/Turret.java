package com.edinaftc.library.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.opencv.core.Mat;


public class Turret extends Subsystem{
    private DcMotorEx vMotor;
    private DcMotorEx hMotor;
    private boolean sharedHubButton;
    private boolean sharedHubRunning;
    private boolean turretRecenter;
    private boolean turretRecenterRunning;
    private boolean allianceHubButton;
    private boolean allianceHubRunning;
    private boolean raisingForRecenter;
    private boolean isRedTeleop;
    double xPower;
    double yPower;

    public Turret(HardwareMap map){
        vMotor = map.get(DcMotorEx.class, "vm");
        hMotor = map.get(DcMotorEx.class, "hm");

        vMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        //vMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        vMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        hMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        //hMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        hMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        sharedHubButton = false;
        sharedHubRunning = false;
        turretRecenter = false;
        turretRecenterRunning = false;
        allianceHubButton = false;
        allianceHubRunning = false;
        raisingForRecenter = false;
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
            vMotor.setTargetPosition(2500);
            vMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            vMotor.setPower(1);
            allianceHubRunning = true;
            allianceHubButton = false;
            sharedHubRunning = false;
            sharedHubButton = false;
            raisingForRecenter = false;
            turretRecenterRunning = false;
            turretRecenter = false;
        }

        if (allianceHubRunning) {
            if ((vMotor.getCurrentPosition() >= 1180) && !hMotor.isBusy()) {
                if (isRedTeleop)
                    hMotor.setTargetPosition(-1300);
                else
                    hMotor.setTargetPosition(1300);

                hMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                hMotor.setPower(1);
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
            vMotor.setTargetPosition(750);
            vMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            vMotor.setPower(1);
            allianceHubRunning = false;
            allianceHubButton = false;
            sharedHubRunning = true;
            sharedHubButton = false;
            raisingForRecenter = false;
            turretRecenterRunning = false;
            turretRecenter = false;
        }

        if (sharedHubRunning) {
            if ((vMotor.getCurrentPosition() >= 680) && !hMotor.isBusy()) {
                if (isRedTeleop)
                    hMotor.setTargetPosition(1300);
                else
                    hMotor.setTargetPosition(-1300);
                hMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                hMotor.setPower(1);
            }

            if (isRedTeleop) {
                if (((vMotor.getCurrentPosition() < 720) && (vMotor.getCurrentPosition() > 680)) &&
                        (hMotor.getCurrentPosition() > 1280)) {
                    resetStuff(0.0, 0.0);
                }
            } else {
                if (((vMotor.getCurrentPosition() < 720) && (vMotor.getCurrentPosition() > 680)) &&
                        (hMotor.getCurrentPosition() < -1280)) {
                    resetStuff(0.0, 0.0);
                }
            }

            if ((xPower != 0) || (yPower != 0)) {
                resetStuff(xPower, yPower);
            }
        }

        if (turretRecenter) {
            if (vMotor.getCurrentPosition() > 600) {
                hMotor.setTargetPosition(0);
                hMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                hMotor.setPower(1);
                raisingForRecenter = false;
            } else {
                vMotor.setTargetPosition(600);
                vMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                vMotor.setPower(1);
                raisingForRecenter = true;
            }

            allianceHubRunning = false;
            allianceHubButton = false;
            turretRecenterRunning = true;
            turretRecenter = false;
            sharedHubRunning = false;
            sharedHubButton = false;
        }

        if (turretRecenterRunning) {
            if (raisingForRecenter && (vMotor.getCurrentPosition() > 580)) {
                raisingForRecenter = false;
                vMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                vMotor.setPower(0);
                hMotor.setTargetPosition(0);
                hMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                hMotor.setPower(1);
            }

            if ((hMotor.getCurrentPosition() <= 250) && (hMotor.getCurrentPosition() >= -250)) {
                vMotor.setTargetPosition(150);
                vMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                vMotor.setPower(1);
            }

            if (((hMotor.getCurrentPosition() < 30) && (hMotor.getCurrentPosition() > -30)) &&
                (vMotor.getCurrentPosition() < 175)) {
                resetStuff(0.0, 0.0);
            }

            if ((xPower != 0) || (yPower != 0)) {
                resetStuff(xPower, yPower);
            }
        }

        if (!sharedHubRunning && !turretRecenterRunning && !allianceHubRunning) {
            if (vMotor.getCurrentPosition() < 450) {
                if ((Math.abs(hMotor.getCurrentPosition()) > 700) || (Math.abs(hMotor.getCurrentPosition()) < 115)){
                    hMotor.setPower(Math.pow(xPower, 3));
                } else if ((hMotor.getCurrentPosition() > -700) && (hMotor.getCurrentPosition() < 0) && (xPower < 0)) {
                    hMotor.setPower(Math.pow(xPower, 3));
                } else if ((hMotor.getCurrentPosition() < 700) && (hMotor.getCurrentPosition() > 0) && (xPower > 0)) {
                    hMotor.setPower(Math.pow(xPower, 3));
                } else {
                    hMotor.setPower(0);
                }
            } else {
                hMotor.setPower(Math.pow(xPower, 3));
            }

            vMotor.setPower(Math.pow(yPower, 3));
        }
    }

    private void resetStuff(double xPower, double yPower) {
        allianceHubRunning = false;
        allianceHubButton = false;
        sharedHubRunning = false;
        sharedHubButton = false;
        turretRecenterRunning = false;
        turretRecenter = false;
        raisingForRecenter = false;
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
        telemetry.addData("xPower", xPower);
        telemetry.addData("yPower", yPower);
    }
}