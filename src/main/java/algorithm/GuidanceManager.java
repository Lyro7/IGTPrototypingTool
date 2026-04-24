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

    public void retrievePosition() {
        List<TrackingTool> tools = trackingService.getDataService().loadNextData(1);

        for (TrackingTool data : tools) {
            List<TrackingData> measurement = data.getMeasurement();
            for (TrackingData trackingData : measurement) {

                Vector3D vec = calculateTranslationVector(trackingData.getPos());
                guidanceHandler.getTargetCross().setTranslateX(vec.getX());
                guidanceHandler.getTargetCross().setTranslateY(vec.getY());
            }
        }
    }

    public Vector3D calculateTranslationVector(Vector3D worldPosition3D) {
        // Center point
        Vector3D entryPoint = guidanceHandler.getTargetList().getFirst();

        // Translation from entry point to position
        Vector3D translationVector = worldPosition3D.sub(entryPoint);

        double scale = 2.0; // MM -> Pixel

        double uiX = translationVector.getX() * scale;
        double uiY = translationVector.getY() * scale;

        return new Vector3D(uiX, uiY, 0);
    }

    private void clampTargetCross() {
        // TODO:
    }

    private void depthInformation() {
        // TODO:
    }

}
