package com.edinaftc.skystone.vision;

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

    public double cx0 = 240;
    public double cy0 = 640;
    public double cx1 = 480;
    public double cy1 = 640;
    public double cx2 = 720;
    public double cy2 = 640;

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

        double val0 = Core.sumElems(mat0).val[0] + Core.sumElems(mat0).val[1] +
                Core.sumElems(mat0).val[2];
        double val1 = Core.sumElems(mat1).val[0] + Core.sumElems(mat1).val[1] +
                Core.sumElems(mat1).val[2];
        double val2 = Core.sumElems(mat2).val[0] + Core.sumElems(mat2).val[1] +
                Core.sumElems(mat2).val[2];

        if (val0 < val1 && val0 < val2) {
            location = SkystoneLocation.left;
        } else if (val1 < val0 && val1 < val2) {
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
}
