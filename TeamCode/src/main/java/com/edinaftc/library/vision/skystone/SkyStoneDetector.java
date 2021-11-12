package com.edinaftc.library.vision.skystone;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.openftc.easyopencv.OpenCvPipeline;

public class SkyStoneDetector extends OpenCvPipeline {
    private Mat mat0;
    private Mat mat1;
    private Mat mat2;

    private Mat mask0;
    private Mat mask1;
    private Mat mask2;

    private boolean madeMats = false;

    private Scalar BLACK = new Scalar(0,0,0);
    private Scalar WHITE = new Scalar(255,255,255);
    private Scalar RED = new Scalar(255, 0, 0);

    public double cx0 = 50;
    public double cy0 = 460;
    public double cx1 = 440;
    public double cy1 = 460;
    public double cx2 = 890;
    public double cy2 = 460;

    // lowest 950
    // middle 1450
    // high 2060

    public double left = 0;
    public double middle = 0;
    public double right = 0;

    private int r = 30;

    private SkystoneLocation location = SkystoneLocation.right;

    @Override
    public Mat processFrame(Mat frame) {
        Scalar s0 = WHITE;
        Scalar s1 = WHITE;
        Scalar s2 = WHITE;
        double r0 = 10;
        double r1 = 10;
        double r2 = 10;

        int h = frame.height();
        int w = frame.width();

        int type = frame.type();
        if (!madeMats) {
            mask0 = new Mat(h, w, type);
            mask1 = new Mat(h, w, type);
            mask2 = new Mat(h, w, type);
            mat0 = new Mat();
            mat1 = new Mat();
            mat2 = new Mat();
            madeMats = true;
        }

        mask0.setTo(BLACK);
        mask1.setTo(BLACK);
        mask2.setTo(BLACK);

        Imgproc.circle(mask0, new Point(cx0, cy0), r, WHITE, Core.FILLED);
        Imgproc.circle(mask1, new Point(cx1, cy1), r, WHITE, Core.FILLED);
        Imgproc.circle(mask2, new Point(cx2, cy2), r, WHITE, Core.FILLED);

        Core.bitwise_and(mask0, frame, mat0);
        Core.bitwise_and(mask1, frame, mat1);
        Core.bitwise_and(mask2, frame, mat2);

        left = Core.sumElems(mat0).val[0] + Core.sumElems(mat0).val[1] +
                Core.sumElems(mat0).val[2];
        middle = Core.sumElems(mat1).val[0] + Core.sumElems(mat1).val[1] +
                Core.sumElems(mat1).val[2];
        right = Core.sumElems(mat2).val[0] + Core.sumElems(mat2).val[1] +
                Core.sumElems(mat2).val[2];

        if (left > middle && left > right) {
            location = SkystoneLocation.left;
        } else if (middle > left && middle > right) {
            location = SkystoneLocation.middle;
        } else {
            location = SkystoneLocation.right;
        }

        if (location == SkystoneLocation.right) {
            Imgproc.circle(frame, new Point(cx0, cy0), r/2, WHITE, Imgproc.FILLED);
            Imgproc.circle(frame, new Point(cx1, cy1), r/2, WHITE, Imgproc.FILLED);
            Imgproc.circle(frame, new Point(cx2, cy2), r, RED, Imgproc.FILLED);
        } else if (location == SkystoneLocation.left) {
            Imgproc.circle(frame, new Point(cx0, cy0), r, RED, Imgproc.FILLED);
            Imgproc.circle(frame, new Point(cx1, cy1), r/2, WHITE, Imgproc.FILLED);
            Imgproc.circle(frame, new Point(cx2, cy2), r/2, WHITE, Imgproc.FILLED);
        } else {
            Imgproc.circle(frame, new Point(cx0, cy0), r/2, WHITE, Imgproc.FILLED);
            Imgproc.circle(frame, new Point(cx1, cy1), r, RED, Imgproc.FILLED);
            Imgproc.circle(frame, new Point(cx2, cy2), r/2, WHITE, Imgproc.FILLED);
        }

        return frame;
    }

    public SkystoneLocation getLocation() {
        return location;
    }

    public int getLiftHeight(){
        if (location == SkystoneLocation.left) {
            return 950;
        } else if (location == SkystoneLocation.middle){
            return 1450;
        } else {
            return 2060;
        }
    }
}
