package com.edinaftc.opmodes.teleop;

import com.edinaftc.library.subsystems.Intake;
import com.edinaftc.library.subsystems.Turret;
import com.edinaftc.library.subsystems.MecanumDrive;
import com.edinaftc.library.subsystems.SpinServo;
import com.edinaftc.library.subsystems.Subsystem;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ThreadPool;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class Robot {
    private ExecutorService subsystemUpdateExecutor;
    private boolean started;

    public MecanumDrive drive;

    public Intake intake;

    public SpinServo servo;

    public Turret turret;

    private List<Subsystem> subsystems;

    private Telemetry telemetry;

    private Runnable subsystemUpdateRunnable = () -> {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                for (Subsystem subsystem : subsystems) {
                    if (subsystem == null) continue;
                    try {
                        subsystem.update();
                    } catch (Throwable t) {
                        this.telemetry.addData("Exception running thread 1", "");
                        this.telemetry.update();
                    }
                }
            } catch (Throwable t) {
                this.telemetry.addData("Exception running thread 2", "");
                this.telemetry.update();
            }
        }
    };

    public Robot(OpMode opMode, Telemetry telemetry) {
        this.telemetry = telemetry;

        subsystems = new ArrayList<>();

        try {
            drive = new MecanumDrive(opMode.hardwareMap);
            subsystems.add(drive);
        } catch (IllegalArgumentException e) {

        }

        try {
            intake = new Intake(opMode.hardwareMap);
            subsystems.add(intake);
        } catch (IllegalArgumentException e) {

        }

        try {
            servo = new SpinServo(opMode.hardwareMap);
            subsystems.add(servo);
        } catch (IllegalArgumentException e) {

        }

        try {
            turret = new Turret(opMode.hardwareMap);
            subsystems.add(turret);
        } catch (IllegalArgumentException e) {

        }

        subsystemUpdateExecutor = ThreadPool.newSingleThreadExecutor("subsystem update");
    }

    public void start() {
        if (!started) {
            subsystemUpdateExecutor.submit(subsystemUpdateRunnable);
            started = true;
        }
    }

    public void stop() {
        if (subsystemUpdateExecutor != null) {
            subsystemUpdateExecutor.shutdownNow();
            subsystemUpdateExecutor = null;
            started = false;
        }
    }
}
