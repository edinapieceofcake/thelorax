package com.edinaftc.opmodes.autonomous;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
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
        long sleepTime4 = 0;
        int vmPosition = 0;
        int hmPosition = 0;
        double xLocation = 0;
        double yLocation = 0;


        while (!isStarted()){

            g1.update();
            if (g1.y)
                sleepTime4 = 3000;
            else if (g1.b)
                sleepTime4 = 5000;
            else if (g1.a)
                sleepTime4 = 7000;
            else if (g1.x)
                sleepTime4 = 10000;
            else if (g1.dpad_down)
                sleepTime4 = 0;

            telemetry.addData("current sleep time", "%d", sleepTime4);
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

        sleep(sleepTime4);

        if (location == FreightFrenzyLocation.left) {
            vmPosition = 1032;
            hmPosition = -1413;
            xLocation = 2;
            yLocation = -43;
            sleepTime4 = 2250;
        } else if (location == FreightFrenzyLocation.middle){
            vmPosition = 1682;
            hmPosition = -1413;
            xLocation = 1;
            yLocation = -41;
            sleepTime4 = 2250;
        } else {
            vmPosition = 2188;
            hmPosition = -1334;
            xLocation = -2;
            yLocation = -38;
            sleepTime4 = 2250;
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

        vm.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        vm.setTargetPosition(vmPosition);
        vm.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        vm.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        vm.setPower(.75);
        sleep(250);
        hm.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        hm.setTargetPosition(hmPosition);
        hm.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        hm.setPower(.75);

        TrajectorySequence trajectory = drive.trajectorySequenceBuilder(new Pose2d(14, -66, Math.toRadians(0)))
                .strafeTo(new Vector2d(xLocation, yLocation))
                .build();
        drive.followTrajectorySequence(trajectory);

        telemetry.addData("hm", hm.getCurrentPosition());
        telemetry.addData("vm", vm.getCurrentPosition());
        telemetry.update();
        intake.setPower(-.5);
        sleep(300);
        intake.setPower(0);
        hm.setTargetPosition(0);
        sleep(250);
        vm.setTargetPosition(80);

        // first time in
        intake.setPower(.5);
        trajectory = drive.trajectorySequenceBuilder(new Pose2d(xLocation, yLocation, Math.toRadians(0)))
                .strafeTo(new Vector2d(12, -65))
                .forward(34)
                .build();

        drive.followTrajectorySequence(trajectory);
        vm.setTargetPosition(-75);
        sleep(300);
        intake.setPower(0);
        vm.setTargetPosition(85);

        // the 42 comes from the 30 + 12
        trajectory = drive.trajectorySequenceBuilder(new Pose2d(46, -65, Math.toRadians(0)))
                .back(34)
                .strafeTo(new Vector2d(-2, -38))
                .build();

        vm.setTargetPosition(2288);
        sleep(250);
        hm.setTargetPosition(-1334);

        drive.followTrajectorySequence(trajectory);

        intake.setPower(-.5);
        sleep(300);
        intake.setPower(0);
        hm.setTargetPosition(0);
        sleep(250);
        vm.setTargetPosition(85);
        intake.setPower(.5);

        // the -2, -38 comes from the strafeto position
        trajectory = drive.trajectorySequenceBuilder(new Pose2d(-2, -38, Math.toRadians(0)))
                .strafeTo(new Vector2d(12, -65))
                .forward(36)
                .build();

        drive.followTrajectorySequence(trajectory);
        vm.setTargetPosition(-75);
        sleep(300);
        intake.setPower(0);
        vm.setTargetPosition(85);

        // the 42 comes from the 30 + 12
        trajectory = drive.trajectorySequenceBuilder(new Pose2d(48, -65, Math.toRadians(0)))
                .back(36)
                .strafeTo(new Vector2d(-2, -38))
                .build();

        vm.setTargetPosition(2288);
        sleep(250);
        hm.setTargetPosition(-1334);

        drive.followTrajectorySequence(trajectory);

        intake.setPower(-.5);
        sleep(300);
        intake.setPower(0);
        hm.setTargetPosition(0);
        sleep(250);
        vm.setTargetPosition(100);

        trajectory = drive.trajectorySequenceBuilder(new Pose2d(-2, -38, Math.toRadians(0)))
                .strafeTo(new Vector2d(14, -65))
                .forward(25)
                .strafeLeft(15)
                .build();

        drive.followTrajectorySequence(trajectory);
        vm.setTargetPosition(0);
        sleep(1000);
    }
}
