package controller;

import algorithm.DataService;
import algorithm.TrackingService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
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
        guidanceHandler.startGuidanceLoop();
    }

    private boolean requirementsFailed() {
        if (TrackingService.getInstance().getDataService() == null) {
            showAlert("Please select a tracking source!");
            return true;
        }
        if (DataService.getInstance().getTargetList().isEmpty()) {
            showAlert("Please load a puncture path!");
            return true;
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
