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

    public void translateDeviationAndRenderCross() {
        List<TrackingTool> tools = trackingService.getDataService().loadNextData(1);

        for (TrackingTool data : tools) {
            List<TrackingData> measurement = data.getMeasurement();
            for (TrackingData trackingData : measurement) {

                Vector3D translationVector = calculateTranslationVector(trackingData.getPos());

                calculateDepthInformation(translationVector);

                Vector3D scaledTranslationVector = calculateScaledTranslationVector(translationVector);

                translateTargetCross(scaledTranslationVector.getY(), scaledTranslationVector.getZ());
            }
        }
    }

    private void translateTargetCross(double value1, double value2) {
        guidanceHandler.getTargetCross().setTranslateX(value1);
        guidanceHandler.getTargetCross().setTranslateY(value2);
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

    private void calculateDepthInformation(Vector3D translationVector) {
        // TODO:
    }

    // Maybe not needed?
    private void clampTargetCross() {
        // TODO:
    }

}
