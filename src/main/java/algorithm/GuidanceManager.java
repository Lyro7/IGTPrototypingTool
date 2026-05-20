package algorithm;

import controller.GuidanceHandler;
import util.Matrix3D;
import util.Quaternion;
import util.Vector3D;

import java.util.List;

public class GuidanceManager {

    private final GuidanceHandler guidanceHandler;

    private final TrackingService trackingService = TrackingService.getInstance();

    private final Vector3D modelFeetPos = new Vector3D(0, 0, 100);

    public GuidanceManager(GuidanceHandler guidanceHandler) {
        this.guidanceHandler = guidanceHandler;
    }

    public void alignment() {
        List<TrackingTool> tools = trackingService.getDataService().loadNextData(1);

        for (TrackingTool data : tools) {
            List<TrackingData> measurement = data.getMeasurement();
            for (TrackingData trackingData : measurement) {

                // Depth
                depth(trackingData.getPos());

                // Tip alignment
                if (guidanceHandler.getCurrentPhase().equals(GuidanceHandler.Phase.ALIGNMENT)) {
                    tipAlignment(trackingData.getPos());
                }

                // Angulation
                if (guidanceHandler.getCurrentPhase().equals(GuidanceHandler.Phase.ANGLE)) {
                    angulation(trackingData);
                }
            }
        }
    }

    private void depth(Vector3D tipPosition) {
        double depth = calculateDepth(tipPosition);

        renderDepthLabel(depth);

        double progress = calculateDepthProgress(depth);

        double height = adjustDepthRectangleHeight(progress);

        adjustDepthRectangle(height);
    }

    private void tipAlignment(Vector3D tipPosition) {
        Vector3D translationVector = calculateTranslationVector(tipPosition);

        Vector3D scaledTranslationVector = calculateScaledTranslationVector(translationVector);

        translateTargetCross(scaledTranslationVector.getY(), scaledTranslationVector.getZ());
    }

    public void angulation(TrackingData data) {
        Vector3D tipPos = data.getPos();

        Vector3D feetPos = getNeedleFeetPosition(data);

        Vector3D entryPoint  = guidanceHandler.getTargetList().getFirst();
        Vector3D targetPoint = guidanceHandler.getTargetList().getLast();

        Vector3D path = targetPoint.sub(entryPoint).normalize();

        Vector3D orientation = tipPos.sub(feetPos);

        double needleLength = orientation.getMag();

        path.multLocal(needleLength);

        // Calculate point, where feet pos should be positioned
        Vector3D wantedPoint = tipPos.sub(path);

        // Get the rotated right and up axis based off the path
        Vector3D right = path.cross(new Vector3D(0, 1, 0)).normalize();
        Vector3D up = right.cross(path).normalize();

        Vector3D current = feetPos.sub(tipPos).normalize();
        Vector3D should = wantedPoint.sub(tipPos).normalize();

        // Get the rotation axis
        Vector3D rotationAxis = current.cross(should);

        // Get the horizontal and vertical angular deviation
        double x = rotationAxis.dot(right) * 50;
        double y = -rotationAxis.dot(up) * 50;

        translateTargetCircle(x, y);
    }

    private Vector3D getNeedleFeetPosition(TrackingData trackingData) {
        Vector3D tipPosition = trackingData.getPos();

        Quaternion quaternion = trackingData.getRotation();
        Matrix3D rotationMatrix = quaternion.toRotationMatrix();

        return tipPosition.add(rotationMatrix.mult(modelFeetPos));
    }

    public Vector3D calculateTranslationVector(Vector3D worldPosition3D) {
        // Center point
        Vector3D entryPoint = guidanceHandler.getTargetList().getFirst();

        // Translation from entry point to position
        return worldPosition3D.sub(entryPoint);
    }

    // Is dependent on the plane
    private Vector3D calculateScaledTranslationVector(Vector3D translationVector) {
        double scale = 1.5; // MM -> Pixel

        double uiY = -translationVector.getY() * scale;
        double uiZ = -translationVector.getZ() * scale;

        return new Vector3D(0, uiY, uiZ);
    }

    /* DEPTH */

    private double calculateDepth(Vector3D position) {
        Vector3D entryPoint = guidanceHandler.getTargetList().getFirst();
        Vector3D targetPoint = guidanceHandler.getTargetList().getLast();

        Vector3D pathDir = (targetPoint.sub(entryPoint)).normalize();

        Vector3D movement = position.sub(entryPoint);

        return movement.dot(pathDir);
    }

    private double calculateDepthProgress(double depth) {
        Vector3D entryPoint = guidanceHandler.getTargetList().getFirst();
        Vector3D targetPoint = guidanceHandler.getTargetList().getLast();

        double maxDepth = targetPoint.sub(entryPoint).getMag();

        return depth / maxDepth;
    }

    private double adjustDepthRectangleHeight(double progress) {
        double height;

        // Behind the entry point
        if (progress < 0.0) {
            height = (1 - Math.min(Math.abs(progress), 1.0)) * 100;
            // Exactly on the entry point
        } else if (progress == 0.0) {
            height = 100;
            // Above the target point
        } else if (progress > 1.0) {
            height = Math.clamp(progress, 0.0, 1.0);
            // Inside the body, not on target point yet
        } else {
            height = Math.min(Math.abs(progress), 1.0) * 300;
        }

        return height;
    }

    /* UI-CALLS */

    private void adjustDepthRectangle(double height) {
        guidanceHandler.getDepthRectangle().setHeight(height);
    }

    private void translateTargetCross(double value1, double value2) {
        guidanceHandler.getTargetCross().setTranslateX(value1);
        guidanceHandler.getTargetCross().setTranslateY(value2);
    }

    private void translateTargetCircle(double value1, double value2) {
        guidanceHandler.getTargetCircle().setTranslateX(value1);
        guidanceHandler.getTargetCircle().setTranslateY(value2);
    }

    private void renderDepthLabel(double depth) {
        String formattedDepth = String.format("%.2f", depth);
        guidanceHandler.getDepthLabel().setText("Depth: " + formattedDepth);
    }

}
