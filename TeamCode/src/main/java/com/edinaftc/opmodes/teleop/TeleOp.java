package com.edinaftc.opmodes.teleop;

import com.edinaftc.library.Stickygamepad;
import com.edinaftc.library.Vector2d;
import com.edinaftc.skystone.Robot;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

@com.qualcomm.robotcore.eventloop.opmode.TeleOp(name = "Teleop", group = "teleop")
public class TeleOp extends OpMode {
    private Robot robot;
    private Stickygamepad _gamepad1;
    private Stickygamepad _gamepad2;

    public void init() {
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

        robot.drive.setVelocity(gamepad1.left_stick_x, gamepad1.left_stick_y, gamepad1.right_stick_x, gamepad1.left_stick_button);

        robot.intake.solveDirection(_gamepad1.left_bumper, _gamepad1.right_bumper);

        robot.servo.spin(_gamepad2.left_bumper, _gamepad2.right_bumper);

        robot.drive.displayTelemetry(telemetry);

        telemetry.update();
    }

    @Override
    public  void stop() {
        robot.stop();
    }
}
