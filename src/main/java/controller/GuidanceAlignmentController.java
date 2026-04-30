package controller;

import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;

import java.net.URL;
import java.util.ResourceBundle;

public class GuidanceAlignmentController implements GuidanceController {

    @FXML
    public Circle tLight1;
    @FXML
    public Circle tLight2;
    @FXML
    public Circle tLight3;
    @FXML
    public VBox depthBox;
    @FXML
    public Label depth;
    @FXML
    public Group targetCross;

    private GuidanceHandler guidanceHandler;

    @FXML
    public Button stopVisualization;

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
        guidanceHandler.stopGuidanceLoop();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        registerController();
    }

    @Override
    public void setGuidanceHandler(GuidanceHandler guidanceHandler) {
        this.guidanceHandler = guidanceHandler;
        guidanceHandler.addGuidanceController(this);
    }

    public void onStopVisualizationClicked() {
        guidanceHandler.switchToTab("GuidancePlanningView");
    }

    public Group getTargetCross() {
        return targetCross;
    }

}
