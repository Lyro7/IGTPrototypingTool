package controller;

import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

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
    public Label depth;
    @FXML
    public Group targetCross;
    @FXML
    public Rectangle fillRect;
    @FXML
    public Circle targetCircle;
    @FXML
    public Text title;

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
        guidanceHandler.stopGuidanceLoop();
    }

    public Group getTargetCross() {
        return targetCross;
    }

    public Circle getTargetCircle() {
        return targetCircle;
    }

    public Rectangle getDepthRectangle() {
        return fillRect;
    }

    public Label getDepthLabel() {
        return depth;
    }

}
