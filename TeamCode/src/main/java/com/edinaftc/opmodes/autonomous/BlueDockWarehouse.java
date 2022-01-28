package com.edinaftc.opmodes.autonomous;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.edinaftc.library.roadrunner.drive.SampleMecanumDrive;
import com.edinaftc.library.roadrunner.trajectorysequence.TrajectorySequence;
import com.edinaftc.library.roadrunner.trajectorysequence.TrajectorySequenceBuilder;
import com.edinaftc.library.util.Stickygamepad;
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

        int liftLocation = frightFrenzy.freightFrenzyDetector.getLiftHeight();

        if (isStopRequested()) return;

        sleep(sleepTime2);
/*
        vm.setTargetPosition(2500);
        vm.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        vm.setPower(.5);
        sleep(500);
        hm.setTargetPosition(-700);
        hm.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        hm.setPower(.75);
*/
        // We want to start the bot at x: 10, y: -8, heading: 90 degrees
        vm.setTargetPosition(2500);
        vm.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        hm.setTargetPosition(700);
        hm.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        Pose2d startPose = new Pose2d(14, 66, Math.toRadians(0));

        drive.setPoseEstimate(startPose);

        TrajectorySequence traj1 = drive.trajectorySequenceBuilder(startPose)
                .addTemporalMarker(() -> {
                    vm.setPower(.5);
                })
                .strafeTo(new Vector2d(0, 50))
                .addTemporalMarker(.5, () -> {
                    hm.setPower(.75);
                })
                .build();

        drive.followTrajectorySequence(traj1);
/*
        Trajectory traj1 = drive.trajectoryBuilder(startPose)
                .strafeTo(new Vector2d(0, 50))
                .build();

        drive.followTrajectory(traj1);
*/
        // drop element
        intake.setPower(-.5);
        sleep(200);
        intake.setPower(0);
        /*
        hm.setTargetPosition(0);
        sleep(500);
        vm.setTargetPosition(100);

        Trajectory traj2 = drive.trajectoryBuilder(drive.getPoseEstimate())
                .splineTo(new Vector2d(34, 67), Math.toRadians(0))
                .forward(10)
                .build();

        drive.followTrajectory(traj2);
        */
        TrajectorySequence traj2 = drive.trajectorySequenceBuilder(startPose)
                .addTemporalMarker(() -> {
                    hm.setTargetPosition(0);
                })
                .strafeTo(new Vector2d(14, 66))
                .addTemporalMarker(.5, () -> {
                    vm.setTargetPosition(100);
                })
                .forward(20)
                .build();

        drive.followTrajectorySequence(traj2);

        vm.setTargetPosition(0);
    }
}
