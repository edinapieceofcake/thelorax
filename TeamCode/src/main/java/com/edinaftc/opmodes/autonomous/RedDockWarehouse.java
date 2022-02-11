package com.edinaftc.opmodes.autonomous;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.edinaftc.library.roadrunner.drive.SampleMecanumDrive;
import com.edinaftc.library.roadrunner.trajectorysequence.TrajectorySequence;
import com.edinaftc.library.util.Stickygamepad;
import com.edinaftc.library.vision.freightfrenzy.FreightFrenzyLocation;
import com.edinaftc.library.vision.freightfrenzy.FrightFrenzy;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;

import java.util.Arrays;

/*
 * This is an example of a more complex path to really test the tuning.
 */
@Autonomous(group = "drive")
public class RedDockWarehouse extends LinearOpMode {
    private SampleMecanumDrive drive;
    private DcMotorEx vm;
    private DcMotorEx hm;
    private DcMotorEx intake;

    @Override
    public void runOpMode() throws InterruptedException {
        drive = new SampleMecanumDrive(hardwareMap);
        FrightFrenzy frightFrenzy = new FrightFrenzy(hardwareMap, "leftwebcam");
        Stickygamepad g1 = new Stickygamepad(gamepad1);
        CRServo spinner = hardwareMap.crservo.get("spinner");
        vm = hardwareMap.get(DcMotorEx.class, "vm");
        hm = hardwareMap.get(DcMotorEx.class, "hm");
        intake = hardwareMap.get(DcMotorEx.class, "intake");
        int vmPosition = 0;
        int hmPosition = 0;
        double xLocation = 0;
        double yLocation = 0;

        vm.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        vm.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        hm.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        hm.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        while (!isStarted()){
            g1.update();

            if (g1.left_bumper) {
                frightFrenzy.freightFrenzyDetector.cx0 -= 10;
                frightFrenzy.freightFrenzyDetector.cx1 -= 10;
                frightFrenzy.freightFrenzyDetector.cx2 -= 10;
            } else if (g1.right_bumper) {
                frightFrenzy.freightFrenzyDetector.cx0 += 10;
                frightFrenzy.freightFrenzyDetector.cx1 += 10;
                frightFrenzy.freightFrenzyDetector.cx2 += 10;
            }

            telemetry.addData("press left bumper to move dots left", "");
            telemetry.addData("press right bumber to move dots right", "");
            telemetry.addData("location ", frightFrenzy.freightFrenzyDetector.getLocation());
            telemetry.addData("left dot location", frightFrenzy.freightFrenzyDetector.cx0);
            telemetry.addData("l r", "%f", frightFrenzy.freightFrenzyDetector.leftR / 1000);
            telemetry.addData("m r", "%f", frightFrenzy.freightFrenzyDetector.middleR / 1000);
            telemetry.addData("r r", "%f", frightFrenzy.freightFrenzyDetector.rightR / 1000);
            telemetry.update();
        }

        FreightFrenzyLocation location = frightFrenzy.freightFrenzyDetector.getLocation();

        if (isStopRequested()) return;

        if (location == FreightFrenzyLocation.left) {
            vmPosition = 1170;
            hmPosition = -1388;
            xLocation = 3;
            yLocation = -46.5;
        } else if (location == FreightFrenzyLocation.middle){
            vmPosition = 1682;
            hmPosition = -1413;
            xLocation = 2;
            yLocation = -42;
        } else {
            vmPosition = 2188;
            hmPosition = -1334;
            xLocation = -2;
            yLocation = -38;
        }

        // low
        // vm = 1032
        // hm = -1413
        // x,y = 4, -44
        // medium
        // vm = 1682
        // hm = -1413
        // x,y = 4, -44
        // high
        // vm = 2108
        // hm = -1334
        // x,y = 0, -40

        vm.setTargetPosition(vmPosition);
        vm.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        vm.setPower(.75);
        sleep(250);
        hm.setTargetPosition(hmPosition);
        hm.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        hm.setPower(.75);

        TrajectorySequence trajectory = drive.trajectorySequenceBuilder(new Pose2d(14, -66, Math.toRadians(0)))
                .strafeTo(new Vector2d(xLocation, yLocation))
                .addTemporalMarker(() -> {
                    if (location == FreightFrenzyLocation.left) {
                        intake.setPower(-1);
                    } else {
                        intake.setPower(-.5);
                    }
                })
                .build();

        drive.followTrajectorySequence(trajectory);

        // first time in
        trajectory = drive.trajectorySequenceBuilder(new Pose2d(xLocation, yLocation, Math.toRadians(0)))
                .waitSeconds(.3)
                .addTemporalMarker(() -> {
                    hm.setTargetPosition(0);
                    intake.setPower(0);
                })
                .addTemporalMarker(1, () -> vm.setTargetPosition(500))
                .strafeTo(new Vector2d(12, -66.5))
                .forward(10)
                .addTemporalMarker(() -> {
                    vm.setTargetPosition(150);
                    intake.setPower(.5);
                })
                .forward(20)
                .addTemporalMarker(() -> vm.setTargetPosition(100))
                .forward(4)
                .waitSeconds(.3)
                .addTemporalMarker(() -> intake.setPower(0))
                .build();

        drive.followTrajectorySequence(trajectory);

        vm.setTargetPosition(2288);
        sleep(150);

        // the 46 comes from the 12 + 10 + 20 + 4
        trajectory = drive.trajectorySequenceBuilder(new Pose2d(46, -66.5, Math.toRadians(0)))
                .addTemporalMarker(() -> hm.setTargetPosition(-1334))
                .back(34)
                .strafeTo(new Vector2d(0, -40))
                .addTemporalMarker(() -> intake.setPower(-.5))
                .build();

        drive.followTrajectorySequence(trajectory);

        // the -2, -40 comes from the strafeto position
        trajectory = drive.trajectorySequenceBuilder(new Pose2d(0, -40, Math.toRadians(0)))
                .waitSeconds(.3)
                .addTemporalMarker(() -> {
                    hm.setTargetPosition(0);
                    intake.setPower(0);
                })
                .addTemporalMarker(1, () -> vm.setTargetPosition(500))
                .strafeTo(new Vector2d(12, -66.5))
                .forward(10)
                .addTemporalMarker(() -> {
                    vm.setTargetPosition(150);
                    intake.setPower(.5);
                })
                .forward(22)
                .addTemporalMarker(() -> vm.setTargetPosition(100))
                .forward(4)
                .waitSeconds(.3)
                .addTemporalMarker(() -> intake.setPower(0))
                .build();

        drive.followTrajectorySequence(trajectory);

        // the 48 comes from the 12 + 10 + 22 + 4
        vm.setTargetPosition(2288);
        sleep(150);

        trajectory = drive.trajectorySequenceBuilder(new Pose2d(48, -66.5, Math.toRadians(0)))
                .addTemporalMarker(() -> hm.setTargetPosition(-1334))
                .back(36)
                .strafeTo(new Vector2d(0, -40))
                .addTemporalMarker(() -> intake.setPower(-.5))
                .build();

        drive.followTrajectorySequence(trajectory);

        trajectory = drive.trajectorySequenceBuilder(new Pose2d(-0, -40, Math.toRadians(0)))
                .waitSeconds(.3)
                .addTemporalMarker(() -> {
                    hm.setTargetPosition(0);
                    intake.setPower(0);
                })
                .strafeTo(new Vector2d(14, -66))
                .forward(25)
                .addTemporalMarker(() -> vm.setTargetPosition(200))
                .strafeLeft(25)
                .addTemporalMarker(() -> vm.setTargetPosition(0))
                .build();

        drive.followTrajectorySequence(trajectory);

        sleep(1000);
    }
}
