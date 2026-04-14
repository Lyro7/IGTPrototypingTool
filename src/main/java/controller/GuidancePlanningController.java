package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class GuidancePlanningController implements Controller {

    private GuidanceHandler guidanceHandler;

    @FXML
    public Button startVisualization;

    @FXML
    public Button loadTestScene;

    @Override
    public void registerController() {
        Controller.super.registerController();
    }

    @Override
    public void unregisterController() {
        Controller.super.unregisterController();
    }

    @Override
    public void injectStatusLabel(Label statusLabel) {
        Controller.super.injectStatusLabel(statusLabel);
    }

    @Override
    public void close() {
        unregisterController();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        registerController();
    }

    public void onLoadTestSceneClicked() {

    }

    public void onStartVisualizationClicked(javafx.event.ActionEvent actionEvent) {
        guidanceHandler.switchToTab("GuidanceNavigationView");
    }

    public void setGuidanceHandler(GuidanceHandler guidanceHandler) {
        this.guidanceHandler = guidanceHandler;
    }

}
