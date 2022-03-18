package com.edinaftc.opmodes.teleop;

import com.edinaftc.library.util.Stickygamepad;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotorEx;
@Disabled
@com.qualcomm.robotcore.eventloop.opmode.TeleOp(name = "ResetArm", group = "teleop")
public class ResetArm extends OpMode {
    private DcMotorEx vm;
    private DcMotorEx hm;
    private Robot robot;
    private Stickygamepad _gamepad1;
    private Stickygamepad _gamepad2;

    public void init() {
        hm = hardwareMap.get(DcMotorEx.class, "hm");
        vm = hardwareMap.get(DcMotorEx.class, "vm");
        _gamepad1 = new Stickygamepad(gamepad1);
        _gamepad2 = new Stickygamepad(gamepad2);
        robot = new Robot(this, telemetry);
        robot.start();
    }

    @Override
    public void start() {
    }

    public void loop() {

        _gamepad1.update();
        _gamepad2.update();

        hm.setPower(Math.pow(gamepad2.left_stick_x, 3));
        vm.setPower(Math.pow(gamepad2.left_stick_y, 3));

        telemetry.update();
    }

    @Override
    public  void stop() {
        robot.stop();
    }
}
