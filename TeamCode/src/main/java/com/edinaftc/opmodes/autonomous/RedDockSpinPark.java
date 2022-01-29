package com.edinaftc.opmodes.autonomous;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.edinaftc.library.roadrunner.drive.SampleMecanumDrive;
import com.edinaftc.library.roadrunner.trajectorysequence.TrajectorySequence;
import com.edinaftc.library.util.Stickygamepad;
import com.edinaftc.library.vision.freightfrenzy.FreightFrenzyDetector;
import com.edinaftc.library.vision.freightfrenzy.FreightFrenzyLocation;
import com.edinaftc.library.vision.freightfrenzy.FrightFrenzy;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;

/*
 * This is an example of a more complex path to really test the tuning.
 */
@Autonomous(group = "drive")
public class RedDockSpinPark extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        SampleMecanumDrive drive = new SampleMecanumDrive(hardwareMap);
        FrightFrenzy frightFrenzy = new FrightFrenzy(hardwareMap, "leftwebcam");
        CRServo spinner = hardwareMap.crservo.get("spinner");
        DcMotorEx vm = hardwareMap.get(DcMotorEx.class, "vm");
        DcMotorEx hm = hardwareMap.get(DcMotorEx.class, "hm");
        DcMotorEx intake = hardwareMap.get(DcMotorEx.class, "intake");
        Stickygamepad g1 = new Stickygamepad(gamepad1);
        long sleepTime3 = 0;
        int vmPosition = 0;
        int hmPosition = 0;
        double xLocation = 0;
        double yLocation = 0;
        double spinX = -62;
        double spinY = -62;

        while (!isStarted()){

            g1.update();
            if (g1.y)
                sleepTime3 = 3000;
            else if (g1.b)
                sleepTime3 = 5000;
            else if (g1.a)
                sleepTime3 = 7000;
            else if (g1.x)
                sleepTime3 = 10000;
            else if (g1.dpad_down)
                sleepTime3 = 0;

            telemetry.addData("current sleep time", "%d", sleepTime3);
            telemetry.addData("press y for 3 second delay", "");
            telemetry.addData("press b for 5 second delay","");
            telemetry.addData("press a for 7 second delay","");
            telemetry.addData("press x for 10 second delay","");
            telemetry.addData("press dpad down to reset delay","");
            telemetry.addData("location ", frightFrenzy.freightFrenzyDetector.getLocation());
            telemetry.addData("l, m ,r",  "%f %f %f" , frightFrenzy.freightFrenzyDetector.left / 1000,
                    frightFrenzy.freightFrenzyDetector.middle / 1000, frightFrenzy.freightFrenzyDetector.right / 1000);
            telemetry.addData("l r, g, b", "%f %f %f", frightFrenzy.freightFrenzyDetector.leftR / 1000,
                    frightFrenzy.freightFrenzyDetector.leftG / 1000, frightFrenzy.freightFrenzyDetector.leftB / 1000);
            telemetry.addData("m r, g, b", "%f %f %f", frightFrenzy.freightFrenzyDetector.middleR / 1000,
                    frightFrenzy.freightFrenzyDetector.middleG / 1000, frightFrenzy.freightFrenzyDetector.middleB / 1000);
            telemetry.addData("r r, g, b", "%f %f %f", frightFrenzy.freightFrenzyDetector.rightR / 1000,
                    frightFrenzy.freightFrenzyDetector.rightG / 1000, frightFrenzy.freightFrenzyDetector.rightB / 1000);
            telemetry.update();
        }

        FreightFrenzyLocation location = frightFrenzy.freightFrenzyDetector.getLocation();

        if (isStopRequested()) return;

        sleep(sleepTime3);

        if (location == FreightFrenzyLocation.left) {
            vmPosition = 1264;
            hmPosition = -662;
            xLocation = -19;
            yLocation = -45.5;
            sleepTime3 = 1200;
        } else if (location == FreightFrenzyLocation.middle){
            vmPosition = 1882;
            hmPosition = -700;
            xLocation = -18;
            yLocation = -45;
            sleepTime3 = 1200;
        } else {
            vmPosition = 2400;
            hmPosition = -740;
            xLocation = -18;
            yLocation = -40;
            sleepTime3 = 2250;
            spinY = -63;
        }

        vm.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        vm.setTargetPosition(vmPosition);
        vm.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        vm.setPower(.5);
        sleep(250);
        TrajectorySequence traj1 = drive.trajectorySequenceBuilder(new Pose2d(-34, -66, Math.toRadians(0)))
                .strafeTo(new Vector2d(xLocation, yLocation))
                .build();
        drive.followTrajectorySequence(traj1);

        hm.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        hm.setTargetPosition(hmPosition);
        hm.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        hm.setPower(.5);
        telemetry.addData("hm", hm.getCurrentPosition());
        telemetry.addData("vm", vm.getCurrentPosition());
        telemetry.update();
        sleep(sleepTime3);
        intake.setPower(-.5);
        sleep(300);
        intake.setPower(0);
        hm.setTargetPosition(0);
        sleep(1000);
        vm.setTargetPosition(400);
        sleep(2000);

        TrajectorySequence traj2 = drive.trajectorySequenceBuilder(new Pose2d(xLocation, yLocation, Math.toRadians(0)))
                .strafeTo(new Vector2d(spinX, spinY))
                .build();
        drive.followTrajectorySequence(traj2);

        spinner.setPower(-.3);
        sleep(5000);
        spinner.setPower(0);
        TrajectorySequence traj3 = drive.trajectorySequenceBuilder(new Pose2d(spinX, spinY, Math.toRadians(0)))
                .strafeTo(new Vector2d(-67, -38))
                .build();
        drive.followTrajectorySequence(traj3);
        vm.setTargetPosition(0);
        sleep(500);
    }
}
