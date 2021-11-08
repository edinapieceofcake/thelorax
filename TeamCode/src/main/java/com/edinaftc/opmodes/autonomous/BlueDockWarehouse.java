package com.edinaftc.opmodes.autonomous;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.edinaftc.library.roadrunner.drive.SampleMecanumDrive;
import com.edinaftc.library.vision.freightfrenzy.FrightFrenzy;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

/*
 * This is an example of a more complex path to really test the tuning.
 */
@Autonomous(group = "drive")
public class BlueDockWarehouse extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        SampleMecanumDrive drive = new SampleMecanumDrive(hardwareMap);
        FrightFrenzy frightFrenzy = new FrightFrenzy(hardwareMap);

        while (!isStarted()){
            telemetry.addData("location ", frightFrenzy.freightFrenzyDetector.getLocation());
            telemetry.update();
        }

        int liftLocation = frightFrenzy.freightFrenzyDetector.getLiftHeight();

        if (isStopRequested()) return;

        Pose2d startPose = new Pose2d(0, 0, Math.toRadians(0));

        Trajectory traj1 = drive.trajectoryBuilder(startPose, true)
                .strafeTo(new Vector2d(-20, 26)) // -5, 10 went to the up and right
                .build();

        Trajectory traj2 = drive.trajectoryBuilder(traj1.end())
                .splineTo(new Vector2d(0, 0), Math.toRadians(270))
                .forward(30)
                .build();

        drive.followTrajectory(traj1);
        sleep(2000);
        drive.followTrajectory(traj2);
    }
}
