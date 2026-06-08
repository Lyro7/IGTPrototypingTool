package algorithm;

import controller.GuidanceHandler;
import javafx.geometry.Point3D;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import util.Matrix3D;
import util.Quaternion;
import util.Vector3D;

import java.util.List;

public class GuidanceManager {

    private final GuidanceHandler guidanceHandler;

    private final TrackingService trackingService = TrackingService.getInstance();

    private final Vector3D modelFeetPos = new Vector3D(0, 0, 100);

    private Vector3D entryPoint;

    private Vector3D targetPoint;

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
                    cameraTipAlignmentPhase(trackingData.getPos());

                    guidanceHandler.phaseSwitch = false;
                }

                // Angulation
                if (guidanceHandler.getCurrentPhase().equals(GuidanceHandler.Phase.ANGLE)) {
                    angulation(trackingData);
                    cameraAngulationPhase(trackingData.getPos(), trackingData.getRotation());

                    guidanceHandler.phaseSwitch = false;
                }

                // Scene-depth
                if (guidanceHandler.getCurrentPhase().equals(GuidanceHandler.Phase.DEPTH)) {
                    // First time
                    if (guidanceHandler.phaseSwitch) {
                        renderPlannedPath();
                        addTargetSphere();
                    }

                    cameraAngulationPhase(trackingData.getPos(), trackingData.getRotation());

                    //Vector3D predictedPoint = calculatePredictedPoint(trackingData.getPos(), trackingData.getRotation());
                    //double hitError = calculateErrorTerm(predictedPoint);

                    guidanceHandler.phaseSwitch = false;
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
        Vector3D pathDir = (targetPoint.sub(entryPoint)).normalize();

        Vector3D movement = position.sub(entryPoint);

        return movement.dot(pathDir);
    }

    private double calculateDepthProgress(double depth) {
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

    /* CAMERA-MECHANISMS FOR SCENE OVERLAY */

    private void cameraTipAlignmentPhase(Vector3D pos) {
        guidanceHandler.getCamera().setPos(pos);
    }

    private void cameraAngulationPhase(Vector3D pos, Quaternion quaternion) {
        guidanceHandler.getCamera().setPos(pos);

        // Check this
        Quaternion damped = new Quaternion(
                quaternion.getX() * 0.1,
                quaternion.getY() * 0.1,
                quaternion.getZ() * 0.1,
                quaternion.getW()
        );
        damped.norm();

        quaternion.norm();

        Matrix3D rotationMatrix = quaternion.toRotationMatrix();

        // Debug purposes
        /*

        Vector3D forward = calculateForwardVector(rotationMatrix);

        forward.multLocal(50.0);

        Vector3D cameraPos = entryPoint.add(forward);
        guidanceHandler.getCamera().setPos(cameraPos);
         */

        Affine rotation = new Affine(
                rotationMatrix.get(0, 0), rotationMatrix.get(0, 1), rotationMatrix.get(0, 2), 0,
                rotationMatrix.get(1, 0), rotationMatrix.get(1, 1), rotationMatrix.get(1, 2), 0,
                rotationMatrix.get(2, 0), rotationMatrix.get(2, 1), rotationMatrix.get(2, 2), 0);

        guidanceHandler.getCamera().getPerspectiveCamera().getTransforms().setAll(rotation);
    }

    private Vector3D calculateForwardVector(Matrix3D rotationMatrix) {
        Vector3D forward = new Vector3D(
                -rotationMatrix.get(0, 2),
                -rotationMatrix.get(1, 2),
                -rotationMatrix.get(2, 2));

        forward.normalizeLocal();

        return forward;
    }

    private void renderPlannedPath() {
        Cylinder plannedPath = createCylinder(new Point3D(entryPoint.getX(), entryPoint.getY(), entryPoint.getZ()),
                new Point3D(targetPoint.getX(), targetPoint.getY(), targetPoint.getZ()));

        guidanceHandler.getWorld().getChildren().add(plannedPath);
    }

    private Vector3D calculatePredictedPoint(Vector3D pos, Quaternion quaternion) {
        Matrix3D rotationMatrix = quaternion.toRotationMatrix();

        Vector3D forward = calculateForwardVector(rotationMatrix);

        double d = pos.distTo(targetPoint);

        forward.multLocal(d);

        return pos.add(forward);
    }

    private void addTargetSphere() {
        Sphere sphere = new Sphere();

        sphere.setTranslateX(targetPoint.getX());
        sphere.setTranslateY(targetPoint.getY());
        sphere.setTranslateZ(targetPoint.getZ());

        sphere.setRadius(0.5);

        sphere.setMaterial(new PhongMaterial(Color.BLACK));

        guidanceHandler.getWorld().getChildren().add(sphere);
    }

    private double calculateErrorTerm(Vector3D predictedPoint) {
        return Math.round(predictedPoint.distTo(targetPoint));
    }

    /* Source: https://stackoverflow.com/questions/56259785/how-to-draw-a-3d-line-in-javafx */
    private Cylinder createCylinder(Point3D start, Point3D end) {
        Point3D yAxis = new Point3D(0, 1, 0);

        Point3D seg = end.subtract(start);

        double height = seg.magnitude();

        Point3D midpoint = end.midpoint(start);

        Translate moveToMidpoint = new Translate(
                midpoint.getX(),
                midpoint.getY(),
                midpoint.getZ());

        Point3D axisOfRotation = seg.crossProduct(yAxis);

        double angle = Math.acos(
                seg.normalize().dotProduct(yAxis));

        Rotate rotateAroundCenter = new Rotate(
                -Math.toDegrees(angle),
                axisOfRotation);

        Cylinder line = new Cylinder(0.2, height);

        line.getTransforms().addAll(moveToMidpoint, rotateAroundCenter);

        return line;
    }

    public void updatePlannedPoints(Vector3D entryPoint, Vector3D targetPoint) {
        this.entryPoint = entryPoint;
        this.targetPoint = targetPoint;
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
