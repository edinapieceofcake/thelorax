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

        vm.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        vm.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        hm.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        hm.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

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

            if (g1.left_bumper) {
                frightFrenzy.freightFrenzyDetector.cx0 -= 10;
                frightFrenzy.freightFrenzyDetector.cx1 -= 10;
                frightFrenzy.freightFrenzyDetector.cx2 -= 10;
            } else if (g1.right_bumper) {
                frightFrenzy.freightFrenzyDetector.cx0 += 10;
                frightFrenzy.freightFrenzyDetector.cx1 += 10;
                frightFrenzy.freightFrenzyDetector.cx2 += 10;
            }

            telemetry.addData("current sleep time", "%d", sleepTime3);
            telemetry.addData("press y for 3 second delay", "");
            telemetry.addData("press b for 5 second delay","");
            telemetry.addData("press a for 7 second delay","");
            telemetry.addData("press x for 10 second delay","");
            telemetry.addData("press dpad down to reset delay","");
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

        sleep(sleepTime3);

        if (location == FreightFrenzyLocation.left) {
            vmPosition = 1130;
            hmPosition = -448;
            xLocation = -27;
            yLocation = -39.5;
            sleepTime3 = 1200;
            spinY = -63;
        } else if (location == FreightFrenzyLocation.middle){
            vmPosition = 1882;
            hmPosition = -700;
            xLocation = -18;
            yLocation = -44;
            sleepTime3 = 1200;
            spinY = -63;
        } else {
            vmPosition = 2400;
            hmPosition = -740;
            xLocation = -18;
            yLocation = -40;
            sleepTime3 = 2250;
            spinY = -63;
        }

        vm.setTargetPosition(vmPosition);
        vm.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        vm.setPower(.75);
        sleep(250);
        hm.setTargetPosition(hmPosition);
        hm.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        hm.setPower(.75);

        TrajectorySequence traj1 = drive.trajectorySequenceBuilder(new Pose2d(-34, -66, Math.toRadians(0)))
                .strafeTo(new Vector2d(xLocation, yLocation))
                .build();
        drive.followTrajectorySequence(traj1);

        telemetry.addData("hm", hm.getCurrentPosition());
        telemetry.addData("vm", vm.getCurrentPosition());
        telemetry.update();
        intake.setPower(-.5);
        sleep(300);
        intake.setPower(0);
        hm.setTargetPosition(0);
        sleep(500);
        vm.setTargetPosition(400);

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
        vm.setTargetPosition(150);
        sleep(500);
    }
}
