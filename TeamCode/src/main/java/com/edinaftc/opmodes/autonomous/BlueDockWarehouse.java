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
    private SampleMecanumDrive drive;
    private DcMotorEx vm;
    private DcMotorEx hm;
    private DcMotorEx intake;

    @Override
    public void runOpMode() throws InterruptedException {
        drive = new SampleMecanumDrive(hardwareMap);
        FrightFrenzy frightFrenzy = new FrightFrenzy(hardwareMap, "rightwebcam");
        vm = hardwareMap.get(DcMotorEx.class, "vm");
        hm = hardwareMap.get(DcMotorEx.class, "hm");
        intake = hardwareMap.get(DcMotorEx.class, "intake");
        Stickygamepad g1 = new Stickygamepad(gamepad1);
        long sleepTime2 = 0;
        int vmPosition = 0;
        int hmPosition = 0;
        double xLocation = 0;
        double yLocation = 0;
        frightFrenzy.freightFrenzyDetector.cx0 = 130;
        frightFrenzy.freightFrenzyDetector.cx1 = 430;
        frightFrenzy.freightFrenzyDetector.cx2 = 750;

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

            if (g1.left_bumper) {
                frightFrenzy.freightFrenzyDetector.cx0 -= 10;
                frightFrenzy.freightFrenzyDetector.cx1 -= 10;
                frightFrenzy.freightFrenzyDetector.cx2 -= 10;
            } else if (g1.right_bumper) {
                frightFrenzy.freightFrenzyDetector.cx0 += 10;
                frightFrenzy.freightFrenzyDetector.cx1 += 10;
                frightFrenzy.freightFrenzyDetector.cx2 += 10;
            }

            telemetry.addData("current sleep time", "%d", sleepTime2);
            telemetry.addData("press y for 3 second delay", "");
            telemetry.addData("press b for 5 second delay","");
            telemetry.addData("press a for 7 second delay","");
            telemetry.addData("press x for 10 second delay","");
            telemetry.addData("press dpad down to reset delay","");
            telemetry.addData("press left bumper to move dots left", "");
            telemetry.addData("press right bumber to move dots right", "");
            telemetry.addData("location ", frightFrenzy.freightFrenzyDetector.getLocation());
            telemetry.addData("left dot location", frightFrenzy.freightFrenzyDetector.cx0);
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
            vmPosition = 1170;
            hmPosition = 1388;
            xLocation = 4;
            yLocation = 47.5;
        } else if (location == FreightFrenzyLocation.middle){
            vmPosition = 1682;
            hmPosition = 1413;
            xLocation = 3;
            yLocation = 42;
        } else {
            vmPosition = 2288;
            hmPosition = 1334;
            xLocation = -1;
            yLocation = 39;
        }

        vm.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        vm.setTargetPosition(vmPosition);
        vm.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        vm.setPower(.75);
        sleep(250);
        hm.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        hm.setTargetPosition(hmPosition);
        hm.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        hm.setPower(.75);

        TrajectorySequence trajectory = drive.trajectorySequenceBuilder(new Pose2d(14, 66, Math.toRadians(0)))
                .strafeTo(new Vector2d(xLocation, yLocation))
                .build();
        drive.followTrajectorySequence(trajectory);

        telemetry.addData("hm", hm.getCurrentPosition());
        telemetry.addData("vm", vm.getCurrentPosition());
        telemetry.update();

        if (location == FreightFrenzyLocation.left) {
            intake.setPower(-1);
        } else {
            intake.setPower(-.5);
        }

        sleep(300);
        intake.setPower(0);
        hm.setTargetPosition(0);

        // turn on intake to try and pickup element
        trajectory = drive.trajectorySequenceBuilder(new Pose2d(xLocation, yLocation, Math.toRadians(0)))
                .strafeTo(new Vector2d(12, 64.5))
                .addDisplacementMarker(() -> vm.setTargetPosition(500))
                .forward(15)
                .addDisplacementMarker(() -> {
                    vm.setTargetPosition(150);
                    intake.setPower(.5);
                })
                .forward(15)
                .addDisplacementMarker(() -> vm.setTargetPosition(100))
                .forward(4)
                .build();

        // strafe back to wall and drive forward to get element
        drive.followTrajectorySequence(trajectory);
        vm.setTargetPosition(-75);
        sleep(250);
        intake.setPower(0);

        vm.setTargetPosition(2288);
        sleep(150);

        // drive backwards and strafe to alliance hub
        // 2, 39 should be used below. if 2, 39 changes, then we need to change the value below
        trajectory = drive.trajectorySequenceBuilder(new Pose2d(46, 64.5, Math.toRadians(0)))
                .back(34)
                .addDisplacementMarker(() -> hm.setTargetPosition(1334))
                .strafeTo(new Vector2d(0, 40))
                .build();

        drive.followTrajectorySequence(trajectory);

        intake.setPower(-.5);
        sleep(300);
        intake.setPower(0);
        hm.setTargetPosition(0);

        // the 2, 39 comes from above
        trajectory = drive.trajectorySequenceBuilder(new Pose2d(0, 40, Math.toRadians(0)))
                .strafeTo(new Vector2d(12, 64.5))
                .addDisplacementMarker(() -> vm.setTargetPosition(500))
                .forward(16)
                .addDisplacementMarker(() -> {
                    vm.setTargetPosition(150);
                    intake.setPower(.5);
                })
                .forward(16)
                .addDisplacementMarker(() -> vm.setTargetPosition(100))
                .forward(4)
                .build();

        drive.followTrajectorySequence(trajectory);
        vm.setTargetPosition(-75);
        sleep(250);
        intake.setPower(0);

        vm.setTargetPosition(2288);
        sleep(150);

        // the 48 comes from the 30 + 12
        // the 0, 40 is the alliance hub location and needs to match what we enter below
        trajectory = drive.trajectorySequenceBuilder(new Pose2d(48, 64.5, Math.toRadians(0)))
                .back(36)
                .addDisplacementMarker(() -> hm.setTargetPosition(1334))
                .strafeTo(new Vector2d(-1, 39))
                .build();

        drive.followTrajectorySequence(trajectory);

        intake.setPower(-.5);
        sleep(300);
        intake.setPower(0);

        // the 0, 40 comes from above
        trajectory = drive.trajectorySequenceBuilder(new Pose2d(-1, 39, Math.toRadians(0)))
                .strafeTo(new Vector2d(12, 64.5))
                .addDisplacementMarker(() -> hm.setTargetPosition(0))
                .forward(35)
                .addDisplacementMarker(() -> vm.setTargetPosition(200))
                .strafeRight(25)
                .turn(Math.toRadians(90))
                .strafeRight(15)
                .build();

        drive.followTrajectorySequence(trajectory);

        sleep(1000);
    }
}
