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
public class BlueDockSpinPark extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        SampleMecanumDrive drive = new SampleMecanumDrive(hardwareMap);
        FrightFrenzy frightFrenzy = new FrightFrenzy(hardwareMap);
        CRServo spinner = hardwareMap.crservo.get("spinner");
        Servo bucket = hardwareMap.servo.get("bucket");
        DcMotorEx lift = hardwareMap.get(DcMotorEx.class, "lift");

        bucket.setPosition(0.3);
        lift.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        while (!isStarted()){
            telemetry.addData("location ", frightFrenzy.freightFrenzyDetector.getLocation());
            telemetry.update();
        }

        int liftLocation = frightFrenzy.freightFrenzyDetector.getLiftHeight();

        waitForStart();

        if (isStopRequested()) return;

        Pose2d startPose = new Pose2d(0, 0, Math.toRadians(0));

        Trajectory traj1 = drive.trajectoryBuilder(startPose, true)
                .strafeTo(new Vector2d(-20, -26)) // -5, 10 went to the up and right
                .build();

        Trajectory traj2 = drive.trajectoryBuilder(traj1.end())
                .strafeTo(new Vector2d(-7, 24))
                .build();

        Trajectory traj3 = drive.trajectoryBuilder(traj1.end())
                .back(20)
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
        spinner.setPower(1);
        sleep(4000);
        spinner.setPower(0);
        drive.followTrajectory(traj3);    }
}
