package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class GuidancePlanningController implements GuidanceController {

    private GuidanceHandler guidanceHandler;

    @FXML
    public Button startVisualization;

    @FXML
    public Button loadTestScene;

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
    }

    @Override
    public void setGuidanceHandler(GuidanceHandler guidanceHandler) {
        this.guidanceHandler = guidanceHandler;
        guidanceHandler.addGuidanceController(this);
    }

    public void onLoadTestSceneClicked() {
        // TODO
    }

    public void onStartVisualizationClicked(javafx.event.ActionEvent actionEvent) {
        guidanceHandler.switchToTab("GuidanceNavigationView");
    }



}
