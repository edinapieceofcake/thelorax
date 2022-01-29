package com.edinaftc.opmodes.autonomous;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.edinaftc.library.roadrunner.drive.SampleMecanumDrive;
import com.edinaftc.library.roadrunner.trajectorysequence.TrajectorySequence;
import com.edinaftc.library.roadrunner.trajectorysequence.TrajectorySequenceBuilder;
import com.edinaftc.library.util.Stickygamepad;
import com.edinaftc.library.vision.freightfrenzy.FreightFrenzyLocation;
import com.edinaftc.library.vision.freightfrenzy.FrightFrenzy;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;

/*
 * This is an example of a more complex path to really test the tuning.
 */
@Autonomous(group = "drive")
public class BlueDockWarehouse extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        SampleMecanumDrive drive = new SampleMecanumDrive(hardwareMap);
        FrightFrenzy frightFrenzy = new FrightFrenzy(hardwareMap, "rightwebcam");
        DcMotorEx vm = hardwareMap.get(DcMotorEx.class, "vm");
        DcMotorEx hm = hardwareMap.get(DcMotorEx.class, "hm");
        DcMotorEx intake = hardwareMap.get(DcMotorEx.class, "intake");
        Stickygamepad g1 = new Stickygamepad(gamepad1);
        long sleepTime2 = 0;
        int vmPosition = 0;
        int hmPosition = 0;
        double xLocation = 0;
        double yLocation = 0;
        frightFrenzy.freightFrenzyDetector.cx0 = 210;
        frightFrenzy.freightFrenzyDetector.cx1 = 560;
        frightFrenzy.freightFrenzyDetector.cx2 = 860;

        vm.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        vm.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        while (!isStarted()){

            g1.update();
            if (g1.y)
                sleepTime2 = 3000;
            else if (g1.b)
                sleepTime2 = 5000;
            else if (g1.a)
                sleepTime2 = 7000;
            else if (g1.x)
                sleepTime2 = 10000;
            else if (g1.dpad_down)
                sleepTime2 = 0;

            telemetry.addData("current sleep time", "%d", sleepTime2);
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

        sleep(sleepTime2);

        if (location == FreightFrenzyLocation.left) {
            vmPosition = 1032;
            hmPosition = 1413;
            xLocation = 4;
            yLocation = 43;
            sleepTime2 = 2250;
        } else if (location == FreightFrenzyLocation.middle){
            vmPosition = 1682;
            hmPosition = 1413;
            xLocation = 3;
            yLocation = 42;
            sleepTime2 = 2250;
        } else {
            vmPosition = 2288;
            hmPosition = 1334;
            xLocation = 0;
            yLocation = 40;
            sleepTime2 = 2250;
        }

        vm.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        vm.setTargetPosition(vmPosition);
        vm.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        vm.setPower(.5);
        sleep(250);

        TrajectorySequence traj1 = drive.trajectorySequenceBuilder(new Pose2d(14, 66, Math.toRadians(0)))
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
        sleep(sleepTime2);
        intake.setPower(-.5);
        sleep(300);
        intake.setPower(0);
        hm.setTargetPosition(0);
        sleep(500);
        vm.setTargetPosition(400);
        sleep(2000);

        TrajectorySequence traj2 = drive.trajectorySequenceBuilder(new Pose2d(xLocation, yLocation, Math.toRadians(0)))
                .strafeTo(new Vector2d(14, 64.5))
                .forward(25)
                .strafeRight(25)
                .build();
        drive.followTrajectorySequence(traj2);
        vm.setTargetPosition(0);
        sleep(500);
    }
}
