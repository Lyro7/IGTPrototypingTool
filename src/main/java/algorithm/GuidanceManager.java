package algorithm;

import controller.GuidanceHandler;
import util.Vector3D;

import java.util.List;

public class GuidanceManager {

    private final GuidanceHandler guidanceHandler;

    private final TrackingService trackingService = TrackingService.getInstance();

    public GuidanceManager(GuidanceHandler guidanceHandler) {
        this.guidanceHandler = guidanceHandler;
    }

    public void alignmentLogic() {
        List<TrackingTool> tools = trackingService.getDataService().loadNextData(1);

        for (TrackingTool data : tools) {
            List<TrackingData> measurement = data.getMeasurement();
            for (TrackingData trackingData : measurement) {

                Vector3D translationVector = calculateTranslationVector(trackingData.getPos());

                double depth = calculateDepth(trackingData.getPos());

                renderDepthLabel(depth);

                double progress = calculateDepthProgress(depth);

                adjustDepthRectangle(progress);

                Vector3D scaledTranslationVector = calculateScaledTranslationVector(translationVector);

                translateTargetCross(scaledTranslationVector.getY(), scaledTranslationVector.getZ());
            }
        }
    }

    // TODO: Fix mm <-> pixel relation
    private void adjustDepthRectangle(double progress) {
        double height;

        // Behind the entry point
        if (progress < 0.0) {
            height = (1- Math.min(Math.abs(progress), 1.0)) * 100;
        // Exactly on the entry point
        } else if (progress == 0.0) {
            height = 100;
        // Above the target point
        } else if (progress > 1.0) {
            // TODO: Show warning!
            height = Math.clamp(progress, 0.0, 1.0);
        // Inside the body, not on target point yet
        } else {
            height = Math.min(Math.abs(progress), 1.0) * 300;
        }

        guidanceHandler.getDepthRectangle().setHeight(height);

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

    private void translateTargetCross(double value1, double value2) {
        guidanceHandler.getTargetCross().setTranslateX(value1);
        guidanceHandler.getTargetCross().setTranslateY(value2);
    }

    private void renderDepthLabel(double depth) {
        String formattedDepth = String.format("%.2f", depth);
        guidanceHandler.getDepthLabel().setText("Depth: " + formattedDepth);
    }

}
