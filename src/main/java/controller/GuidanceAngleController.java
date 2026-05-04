package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.shape.Circle;

import java.net.URL;
import java.util.ResourceBundle;

public class GuidanceAngleController implements GuidanceController {

    @FXML
    public Circle targetCircle;

    private GuidanceHandler guidanceHandler;

    @Override
    public void setGuidanceHandler(GuidanceHandler guidanceHandler) {
        this.guidanceHandler = guidanceHandler;
        guidanceHandler.addGuidanceController(this);
    }

    @Override
    public void registerController() {
        GuidanceController.super.registerController();
    }

    @Override
    public void unregisterController() {
        GuidanceController.super.unregisterController();
    }

    @Override
    public void close() {
        unregisterController();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        registerController();
    }

    public void onStopVisualizationClicked(ActionEvent actionEvent) {
        guidanceHandler.switchToTab("GuidancePlanningView");
        guidanceHandler.stopGuidanceLoop();
    }

    public Circle getTargetCircle() {
        return targetCircle;
    }


}
