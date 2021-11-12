package com.edinaftc.opmodes.teleop;

import com.edinaftc.library.util.Stickygamepad;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

@com.qualcomm.robotcore.eventloop.opmode.TeleOp(name = "BlueTeleop", group = "teleop")
public class BlueTeleOp extends OpMode {
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

        robot.servo.spin(-gamepad2.left_trigger);

        robot.lift.setLiftPower(gamepad2.left_stick_y, gamepad2.right_trigger);

        robot.drive.displayTelemetry(telemetry);
        robot.lift.displayTelemetry(telemetry);

        telemetry.update();
    }

    @Override
    public  void stop() {
        robot.stop();
    }
}
