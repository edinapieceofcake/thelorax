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
public class BlueDockSpinPark extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        SampleMecanumDrive drive = new SampleMecanumDrive(hardwareMap);
        FrightFrenzy frightFrenzy = new FrightFrenzy(hardwareMap, "rightwebcam");
        CRServo spinner = hardwareMap.crservo.get("spinner");
        DcMotorEx vm = hardwareMap.get(DcMotorEx.class, "vm");
        DcMotorEx hm = hardwareMap.get(DcMotorEx.class, "hm");
        DcMotorEx intake = hardwareMap.get(DcMotorEx.class, "intake");
        Stickygamepad g1 = new Stickygamepad(gamepad1);
        long sleepTime = 0;
        int vmPosition = 0;
        int hmPosition = 0;
        double xLocation = 0;
        double yLocation = 0;
        frightFrenzy.freightFrenzyDetector.cx0 = 110;
        frightFrenzy.freightFrenzyDetector.cx1 = 410;
        frightFrenzy.freightFrenzyDetector.cx2 = 730;

        while (!isStarted()){
            g1.update();
            if (g1.y)
                sleepTime = 3000;
            else if (g1.b)
                sleepTime = 5000;
            else if (g1.a)
                sleepTime = 7000;
            else if (g1.x)
                sleepTime = 10000;
            else if (g1.dpad_down)
                sleepTime = 0;

            if (g1.left_bumper) {
                frightFrenzy.freightFrenzyDetector.cx0 -= 10;
                frightFrenzy.freightFrenzyDetector.cx1 -= 10;
                frightFrenzy.freightFrenzyDetector.cx2 -= 10;
            } else if (g1.right_bumper) {
                frightFrenzy.freightFrenzyDetector.cx0 += 10;
                frightFrenzy.freightFrenzyDetector.cx1 += 10;
                frightFrenzy.freightFrenzyDetector.cx2 += 10;
            }

            telemetry.addData("current sleep time", "%d", sleepTime);
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

        sleep(sleepTime);
        if (location == FreightFrenzyLocation.left) {
            vmPosition = 1064;
            hmPosition = 662;
            xLocation = -19;
            yLocation = 50;
            sleepTime = 1000;
        } else if (location == FreightFrenzyLocation.middle){
            vmPosition = 1882;
            hmPosition = 700;
            xLocation = -18;
            yLocation = 43;
            sleepTime = 1000;
        } else {
            vmPosition = 2400;
            hmPosition = 740;
            xLocation = -18;
            yLocation = 40;
            sleepTime = 2250;
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

        TrajectorySequence traj1 = drive.trajectorySequenceBuilder(new Pose2d(-34, 66, Math.toRadians(0)))
                .strafeTo(new Vector2d(xLocation, yLocation))
                .build();
        drive.followTrajectorySequence(traj1);

        telemetry.addData("hm", hm.getCurrentPosition());
        telemetry.addData("vm", vm.getCurrentPosition());
        telemetry.update();
        intake.setPower(-.75);
        sleep(300);
        intake.setPower(0);
        hm.setTargetPosition(0);
        sleep(500);
        vm.setTargetPosition(400);

        TrajectorySequence traj2 = drive.trajectorySequenceBuilder(new Pose2d(xLocation, yLocation, Math.toRadians(0)))
                .strafeTo(new Vector2d(-62, 62))
                .build();
        drive.followTrajectorySequence(traj2);

        spinner.setPower(.2);
        sleep(5000);
        spinner.setPower(0);
        TrajectorySequence traj3 = drive.trajectorySequenceBuilder(new Pose2d(-62, 62, Math.toRadians(0)))
                .strafeTo(new Vector2d(-63, 38))
                .build();
        drive.followTrajectorySequence(traj3);
        vm.setTargetPosition(0);
        sleep(500);
    }
}
