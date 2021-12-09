package com.edinaftc.opmodes.autonomous;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.edinaftc.library.roadrunner.drive.SampleMecanumDrive;
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
    @Override
    public void runOpMode() throws InterruptedException {
        SampleMecanumDrive drive = new SampleMecanumDrive(hardwareMap);
        FrightFrenzy frightFrenzy = new FrightFrenzy(hardwareMap);
        Servo bucket = hardwareMap.servo.get("bucket");
        DcMotorEx lift = hardwareMap.get(DcMotorEx.class, "lift");

        bucket.setPosition(0.3);
        frightFrenzy.freightFrenzyDetector.cx2 = 840;
        lift.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        while (!isStarted()){
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

        Pose2d startPose = new Pose2d(0, 0, Math.toRadians(0));

        Trajectory traj1 = drive.trajectoryBuilder(startPose, true)
                .strafeTo(new Vector2d(-23, -25)) // -5, 10 went to the up and right
                .build();

        Trajectory traj2 = drive.trajectoryBuilder(traj1.end())
                .splineTo(new Vector2d(6, 0), Math.toRadians(90))
                .forward(30)
                .build();

        drive.followTrajectory(traj1);

        lift.setTargetPosition(liftLocation);
        lift.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        lift.setPower(1);
        while (lift.isBusy()){
            ;
        }

        sleep(2000);
        bucket.setPosition(1);
        sleep(2000);
        bucket.setPosition(0);
        lift.setTargetPosition(0);
        lift.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        lift.setPower(1);
        drive.followTrajectory(traj2);
    }
}
