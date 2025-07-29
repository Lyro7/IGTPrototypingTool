package algorithm;

import javafx.geometry.Point3D;
import util.Quaternion;

import java.util.List;

/**
 * This class provides the methods for processing the measurements. (
 */
public class TrackingDataProcessor {

    /**
     * This method computes the distance of two average points. Then the
     * expected distance is subtracted from the distance of the points.
     *
     * @param expectedDistance          - value of type double
     * @param firstAverageMeasurement  - value of type AverageMeasurement
     * @param secondAverageMeasurement - value of type AverageMeasurement
     * @return accurate - the distance
     */
    public static double getAccuracy(double expectedDistance,
            TrackingData firstTrackingData,
            TrackingData secondTrackingData) {

        /*
         * calculates the distance between the points of
         * firstAverageMeasurement and secondAverageMeasurement
         */
        return firstTrackingData.getPos().distTo(secondTrackingData.getPos()) - expectedDistance;
    }

    /**
     * This method gets a quaternion (four double values) and two
     * measurements. With the method getRotation the quaternion of
     * firstMeasurement is fetched. From this value, the second
     * quaternion is subtracted from the second measurement.
     * Then the expected quaternion is subtracted.
     *
     * @param expectedRotation  - of type quaternion
     * @param firstTrackingData  - of type Measurement
     * @param secondTrackingData - of type Measurement
     * @return result - of type quaternion
     */

    public static Quaternion getAccuracyRotation(Quaternion expectedRotation,
                                                 TrackingData firstTrackingData,
                                                 TrackingData secondTrackingData) {
        return secondTrackingData.getRotation()
                .subtract(firstTrackingData.getRotation())
                .subtract(expectedRotation);
    }



    /* toDoubleArray converts a list into an array */
    private static double[] toDoubleArray(List<Double> list) {
        double[] ret = new double[list.size()];
        int i = 0;
        for (Double e : list) {
            ret[i++] = e.intValue();
        }
        return ret;
    }

    /**
     * This method calculates the distance of two points and uses the method
     * distance from the class Point3D
     *
     * @param firstPoint  - of type Point3D
     * @param secondPoint - of type Point3D
     * @return distance - of type double
     */

    private static double getDistance(Point3D firstPoint, Point3D secondPoint) {
        return firstPoint.distance(secondPoint);
    }
}
