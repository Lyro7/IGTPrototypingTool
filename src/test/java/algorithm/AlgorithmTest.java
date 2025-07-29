package algorithm;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import util.Quaternion;
import util.Vector3D;

import static java.lang.Math.PI;
import static java.lang.Math.sin;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AlgorithmTest {

    TrackingData firstTrackingData;
    TrackingData secondTrackingData;
    private DataService dataService;
    private TrackingTool testTrackingTool;

    @Test
    /**
     */
    public void getRotationJitterIsCorrect() {
        setUpData();

        Quaternion result = testTrackingTool.getAverageRotation();

        assertEquals(result, new Quaternion(0.0f, 0.0f, 0.0f, 1.0f));

    }


    @Test
    /**
     * {@link DataService} {@link DataService#getToolByName(String)}
     */
    public void getToolByNameCorrect() {

        dataService = new DataService();
        setUpData();

        TrackingTool result = null;

        try {
            result = dataService.getToolByName("TestTool");

        } catch (Exception e) {

            assertEquals("Tool not found: TestTool", e.getMessage());
        }

    }

    private void setUpDataAccuracy() {
        firstTrackingData = new TrackingData();
        secondTrackingData = new TrackingData();

        Vector3D p1 = new Vector3D(1, 1, 1);
        Vector3D p2 = new Vector3D(2, 2, 2);
        Quaternion quaternion1 = new Quaternion(0.0f, 0.0f, 0.0f, 1.0f);
        Quaternion quaternion2 = new Quaternion(0.0f, (float) sin(PI / 4), 0, (float) sin(PI / 4));
        firstTrackingData.setRotation(quaternion1);
        secondTrackingData.setRotation(quaternion2);

    }

    private void setUpData() {
        testTrackingTool = new TrackingTool("TestTool");
        List<TrackingData> trackingData = new ArrayList<>();

        TrackingData trackingData1 = new TrackingData();
        TrackingData trackingData2 = new TrackingData();
        TrackingData trackingData3 = new TrackingData();

        Vector3D p1 = new Vector3D(1, 1, 1);
        Vector3D p2 = new Vector3D(2, 2, 2);
        Vector3D p3 = new Vector3D(3, 3, 3);

        trackingData1.setPos(p1);
        trackingData1.setRotation(new Quaternion(0.0f, 0.0f, 0.0f, 1.0f));
        trackingData2.setPos(p2);
        trackingData2.setRotation(new Quaternion(0.0f, 0.0f, 0.0f, 1.0f));
        trackingData3.setPos(p3);
        trackingData3.setRotation(new Quaternion(0.0f, 0.0f, 0.0f, 1.0f));


        trackingData.add(trackingData1);
        trackingData.add(trackingData2);
        trackingData.add(trackingData3);

        testTrackingTool.addMeasurement(trackingData1);
        testTrackingTool.addMeasurement(trackingData2);
        testTrackingTool.addMeasurement(trackingData3);

    }

}
