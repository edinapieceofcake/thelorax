/* Copyright (c) 2017 FIRST. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of FIRST nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.edinaftc.opmodes.vision;

import com.edinaftc.library.Stickygamepad;
import com.edinaftc.skystone.vision.SkyStoneDetector;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvWebcam;

//@Disabled
@TeleOp
public class SkyStone extends OpMode {
    OpenCvWebcam webcam;
    private SkyStoneDetector skyStoneDetector = new SkyStoneDetector();
    private Stickygamepad _gamepad1;
    private Location location = Location.left;
    private enum Location {
        left, middle, right
    }

    @Override
    public void init() {
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        webcam = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName.class, "Webcam 1"), cameraMonitorViewId);

        // OR...  Do Not Activate the Camera Monitor View
        //webcam = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName.class, "Webcam 1"));

        /*
         * Specify the image processing pipeline we wish to invoke upon receipt
         * of a frame from the camera. Note that switching pipelines on-the-fly
         * (while a streaming session is in flight) *IS* supported.
         */
        webcam.setPipeline(skyStoneDetector);

        webcam.setMillisecondsPermissionTimeout(2500); // Timeout for obtaining permission is configurable. Set before opening.
        webcam.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener()
        {
            @Override
            public void onOpened()
            {
                webcam.startStreaming(960, 720, OpenCvCameraRotation.UPRIGHT);
            }

            @Override
            public void onError(int errorCode) {}
        });

        telemetry.addLine("Waiting for start");
        telemetry.update();
        _gamepad1 = new Stickygamepad(gamepad1);
    }

    @Override
    public void loop() {
        _gamepad1.update();
        if (_gamepad1.x) {
            if (location == Location.left) {
                skyStoneDetector.cx0 += 10;
            } else if (location == Location.middle) {
                skyStoneDetector.cx1 += 10;
            } else if (location == Location.right){
                skyStoneDetector.cx2 += 10;
            }
        }

        if (_gamepad1.y) {
            if (location == Location.left) {
                skyStoneDetector.cx0 -= 10;
            } else if (location == Location.middle) {
                skyStoneDetector.cx1 -= 10;
            } else if (location == Location.right){
                skyStoneDetector.cx2 -= 10;
            }
        }

        if (_gamepad1.b) {
            if (location == Location.left) {
                skyStoneDetector.cy0 += 10;
            } else if (location == Location.middle) {
                skyStoneDetector.cy1 += 10;
            } else if (location == Location.right) {
                skyStoneDetector.cy2 += 10;
            }
        }

        if (_gamepad1.a) {
            if (location == Location.left) {
                skyStoneDetector.cy0 -= 10;
            } else if (location == Location.middle) {
                skyStoneDetector.cy1 -= 10;
            } else if (location == Location.right){
                skyStoneDetector.cy2 -= 10;
            }
        }

        if (_gamepad1.left_bumper) {
            if (location == Location.middle) {
                location = Location.middle.left;
            } else if (location == Location.right) {
                location = Location.middle.middle;
            } else if (location == Location.left) {
                location = Location.left;
            } else {
                location = Location.left;
            }
        }

        if (_gamepad1.right_bumper) {
            if (location == Location.left) {
                location = Location.middle;
            } else if (location == Location.middle) {
                location = Location.right;
            } else {
                location = Location.right;
            }
        }

        telemetry.addData("left (x, y)", "%f %f", skyStoneDetector.cx0, skyStoneDetector.cy0);
        telemetry.addData("middle (x, y)", "%f %f", skyStoneDetector.cx1, skyStoneDetector.cy1);
        telemetry.addData("right (x, y)", "%f %f", skyStoneDetector.cx2, skyStoneDetector.cy2);
        telemetry.addData("dot location", location);
        telemetry.addData("location ", skyStoneDetector.getLocation());
        telemetry.update();
    }

    @Override
    public void stop() {
        //camera.close();
    }
}