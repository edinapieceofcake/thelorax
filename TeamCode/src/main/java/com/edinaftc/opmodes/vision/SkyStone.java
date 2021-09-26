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
        left, middle, right, line
    }

    @Override
    public void init() {
        /*
         * Instantiate an OpenCvCamera object for the camera we'll be using.
         * In this sample, we're using a webcam. Note that you will need to
         * make sure you have added the webcam to your configuration file and
         * adjusted the name here to match what you named it in said config file.
         *
         * We pass it the view that we wish to use for camera monitor (on
         * the RC phone). If no camera monitor is desired, use the alternate
         * single-parameter constructor instead (commented out below)
         */
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

        /*
         * Open the connection to the camera device. New in v1.4.0 is the ability
         * to open the camera asynchronously, and this is now the recommended way
         * to do it. The benefits of opening async include faster init time, and
         * better behavior when pressing stop during init (i.e. less of a chance
         * of tripping the stuck watchdog)
         *
         * If you really want to open synchronously, the old method is still available.
         */
        webcam.setMillisecondsPermissionTimeout(2500); // Timeout for obtaining permission is configurable. Set before opening.
        webcam.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener()
        {
            @Override
            public void onOpened()
            {
                /*
                 * Tell the webcam to start streaming images to us! Note that you must make sure
                 * the resolution you specify is supported by the camera. If it is not, an exception
                 * will be thrown.
                 *
                 * Keep in mind that the SDK's UVC driver (what OpenCvWebcam uses under the hood) only
                 * supports streaming from the webcam in the uncompressed YUV image format. This means
                 * that the maximum resolution you can stream at and still get up to 30FPS is 480p (640x480).
                 * Streaming at e.g. 720p will limit you to up to 10FPS and so on and so forth.
                 *
                 * Also, we specify the rotation that the webcam is used in. This is so that the image
                 * from the camera sensor can be rotated such that it is always displayed with the image upright.
                 * For a front facing camera, rotation is defined assuming the user is looking at the screen.
                 * For a rear facing camera or a webcam, rotation is defined assuming the camera is facing
                 * away from the user.
                 */
                webcam.startStreaming(960, 720, OpenCvCameraRotation.UPRIGHT);
            }

            @Override
            public void onError(int errorCode)
            {
                /*
                 * This will be called if the camera could not be opened
                 */
            }
        });

        telemetry.addLine("Waiting for start");
        telemetry.update();
        _gamepad1 = new Stickygamepad(gamepad1);
    }

    @Override
    public void loop() {
        /*
         * Send some stats to the telemetry
         */
        telemetry.addData("Frame Count", webcam.getFrameCount());
        telemetry.addData("FPS", String.format("%.2f", webcam.getFps()));
        telemetry.addData("Total frame time ms", webcam.getTotalFrameTimeMs());
        telemetry.addData("Pipeline time ms", webcam.getPipelineTimeMs());
        telemetry.addData("Overhead time ms", webcam.getOverheadTimeMs());
        telemetry.addData("Theoretical max FPS", webcam.getCurrentPipelineMaxFps());
        telemetry.update();

        _gamepad1.update();
        if (_gamepad1.x) {
            if (location == Location.line) {
                skyStoneDetector.lineX -= 10;
            } else if (location == Location.left) {
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
            if (location == Location.line) {
                skyStoneDetector.lineX += 10;
            } else if (location == Location.left) {
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
            } else if (location == Location.left){
                location = Location.line;
            } else {
                location = Location.line;
            }
        }

        if (_gamepad1.right_bumper) {
            if (location == Location.line) {
                location = Location.left;
            } else if (location == Location.left) {
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
        telemetry.addData("line x", "%f", skyStoneDetector.lineX);
        telemetry.addData("dot location", location);
        telemetry.addData("location ", skyStoneDetector.getLocation());
        telemetry.update();
    }

    @Override
    public void stop() {
        //camera.close();
    }
}