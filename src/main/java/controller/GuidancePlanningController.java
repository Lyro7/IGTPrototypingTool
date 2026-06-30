package controller;

import algorithm.DataService;
import algorithm.TrackingService;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;
import util.PointSet;

import java.net.URL;
import java.util.ResourceBundle;

public class GuidancePlanningController implements GuidanceController {

    private GuidanceHandler guidanceHandler;

    @FXML
    public Button startVisualization;
    @FXML
    public ToggleButton xyPlane;
    @FXML
    public ToggleButton zxPlane;
    @FXML
    public ToggleButton yzPlane;
    @FXML
    public ComboBox<PointSet> pathComboBox;

    @Override
    public void registerController() {
        GuidanceController.super.registerController();
    }

    @Override
    public void unregisterController() {
        GuidanceController.super.unregisterController();
    }

    @Override
    public void injectStatusLabel(Label statusLabel) {
        GuidanceController.super.injectStatusLabel(statusLabel);
    }

    @Override
    public void close() {
        unregisterController();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        registerController();

        pathComboBox.setItems(DataService.getInstance().getPointSet());

        pathComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(PointSet pointSet) {
                return (pointSet != null) ? pointSet.getName() : "";
            }

            @Override
            public PointSet fromString(String s) {
                return null;
            }
        });

        pathComboBox.setOnAction(event -> {
            PointSet pointSet = pathComboBox.getValue();

            if (pointSet != null) {
                guidanceHandler.updatePlannedPoints(pointSet.getV1(), pointSet.getV2());
                guidanceHandler.setActivePointSet(pointSet);
            }
        });

        // If a new point set is being added, always select the first one in the combo box
        DataService.getInstance().getPointSet().addListener((ListChangeListener<PointSet>) change -> {
            if (!DataService.getInstance().getPointSet().isEmpty()) {
                pathComboBox.setValue(DataService.getInstance().getPointSet().getFirst());
            }
        });

        ToggleGroup planeGroup = new ToggleGroup();

        xyPlane.setToggleGroup(planeGroup);
        zxPlane.setToggleGroup(planeGroup);
        yzPlane.setToggleGroup(planeGroup);
    }

    @Override
    public void setGuidanceHandler(GuidanceHandler guidanceHandler) {
        this.guidanceHandler = guidanceHandler;
        guidanceHandler.addGuidanceController(this);

        if (guidanceHandler.getPlaneSelected() != null) {
            selectPlane(guidanceHandler.getPlaneSelected());
        }

        if (guidanceHandler.getActivePointSet() != null) {
            setActivePointSet(guidanceHandler.getActivePointSet());
        }

    }

    private void setActivePointSet(PointSet pointSet) {
        if (pathComboBox.getValue() == null && pointSet != null) {
            pathComboBox.setValue(pointSet);
        }
    }

    private void selectPlane(GuidanceHandler.Plane plane) {
        switch (plane) {
            case XY -> xyPlane.setSelected(true);
            case ZX -> zxPlane.setSelected(true);
            case YZ -> yzPlane.setSelected(true);
        }
    }

    public void onStartVisualizationClicked() {
        if (requirementsFailed()) {
            return;
        }

        guidanceHandler.switchContentOfTab("GuidanceAlignmentView");
        guidanceHandler.prepareTargetsAndMeshes();
        guidanceHandler.disableDepthViewCrosshair();

        guidanceHandler.startGuidanceLoop();
    }

    private boolean requirementsFailed() {
        if (TrackingService.getInstance().getDataService() == null) {
            showAlert("Please select a tracking source!");
            return true;
        }
        if (DataService.getInstance().getPointSet().isEmpty()) {
            showAlert("Please load a puncture path!");
            return true;
        }

        if (!xyPlane.isSelected() && !zxPlane.isSelected() && !yzPlane.isSelected()) {
            guidanceHandler.setPlaneSelected(null);
        }

        if (guidanceHandler.getPlaneSelected() == null) {
            showAlert("Please select a plane!");
            return true;
        }

        return false;
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void onXYClicked() {
        guidanceHandler.setPlaneSelected(GuidanceHandler.Plane.XY);
    }

    public void onZXClicked() {
        guidanceHandler.setPlaneSelected(GuidanceHandler.Plane.ZX);
    }

    public void onYZClicked() {
        guidanceHandler.setPlaneSelected(GuidanceHandler.Plane.YZ);
    }

}
