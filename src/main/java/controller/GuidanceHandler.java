package controller;

import algorithm.DataService;
import algorithm.GuidanceDepthVisualizer;
import algorithm.GuidanceSceneCoordinator;
import javafx.animation.AnimationTimer;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import shapes.CameraContainer;
import util.GuidanceKeyHandler;

import java.util.ArrayList;
import java.util.List;

public class GuidanceHandler {

    /** The 3 main phases after guidance started. */
    public enum Phase { ALIGNMENT, ANGLE, DEPTH }

    /** The standard planes depending on the position of the EM-Tracker */
    public enum Plane { XY, ZX, YZ }

    /** The phase the user is currently in. */
    private Phase currentPhase = Phase.ALIGNMENT;

    /** The current selected plane from the planing section. */
    public Plane planeSelected;

    /** The boolean if a phase switch occured. */
    public boolean phaseSwitch = true;

    /** List, which contains the current active controller */
    private final List<GuidanceController> guidanceControllers = new ArrayList<>();

    private MainController mainController;

    private final GuidanceSceneCoordinator guidanceSceneCoordinator = new GuidanceSceneCoordinator(this);

    private final GuidanceKeyHandler guidanceKeyHandler = new GuidanceKeyHandler(this);

    private final GuidanceDepthVisualizer guidanceDepthVisualizer = new GuidanceDepthVisualizer(this);

    private AnimationTimer animator;

    public GuidanceHandler() {
        guidanceLoop();
    }

    /**
     * Creates the alignment main loop.
     * */
    public void guidanceLoop() {
        animator = new AnimationTimer() {
            @Override
            public void handle(long now) {
                guidanceSceneCoordinator.alignment();
            }
        };
    }

    /**
     * This method updates the selected target points, loads and transforms the meshes selected
     * from the visualization section.
     */
    public void prepareTargetsAndMeshes() {
        guidanceSceneCoordinator.updatePlannedPoints(
                DataService.getInstance().getTargetList().getFirst(),
                DataService.getInstance().getTargetList().getLast());

        guidanceDepthVisualizer.initialize();
        guidanceDepthVisualizer.addActiveModelsToRoot();
    }

    /**
     * Starts the main loop.
     * */
    public void startGuidanceLoop() {
        animator.start();
    }

    /**
     * Stops the main loop.
     * */
    public void stopGuidanceLoop() {
        animator.stop();
    }

    /**
     * Delegates the tab switch from the guidance controllers to the main controller.
     * */
    public void switchContentOfTab(String fileName) {
        mainController.switchContentOfTab(fileName);
    }

    /**
     * This method is called by switching tabs. It closes and removes the last active controller.
     * */
    public void resetControllers() {
        for (GuidanceController controller : guidanceControllers) {
            controller.close();
        }
        guidanceControllers.clear();
    }

    /**
     * Adds the current active controller to the list.
     * */
    public void addGuidanceController(GuidanceController controller) {
        this.guidanceControllers.add(controller);
    }

    /**
     * This method is being called by the {@link GuidanceKeyHandler},
     * if a key has been pressed.
     *
     * @param currentPhase The phase the user is currently in.
     * */
    public void updateCurrentPhase(Phase currentPhase) {
        phaseSwitch = true;
        this.currentPhase = currentPhase;
        viewAdjustments();
    }

    /**
     * Calls related methods based of the current phase.
     * */
    private void viewAdjustments() {
        if (currentPhase.equals(Phase.ALIGNMENT)) {
            tipAlignmentViewAdjustments();
        } else if (currentPhase.equals(Phase.ANGLE)) {
            angulationViewAdjustments();
        } else if (currentPhase.equals(Phase.DEPTH)) {
            sceneDepthViewAdjustments();
        }
    }

    /**
     * Performs view changes by switching from angulation to tip alignment phase.
     * */
    private void tipAlignmentViewAdjustments() {
        GuidanceAlignmentController controller = ((GuidanceAlignmentController) guidanceControllers.getFirst());

        controller.title.setText("Phase 2: Tip Alignment");

        controller.targetCross.setVisible(true);
        controller.targetCircle.setVisible(false);

        controller.tLight1.setId("glowTrafficLight1");
        controller.tLight2.setId("trafficLight2");

        guidanceDepthVisualizer.toggleTorso(true);
    }

    /**
     * Performs view changes by switching to angulation phase.
     * */
    private void angulationViewAdjustments() {
        GuidanceAlignmentController controller = ((GuidanceAlignmentController) guidanceControllers.getFirst());

        controller.title.setText("Phase 3: Angulation");

        controller.targetCross.setVisible(false);
        controller.targetCircle.setVisible(true);

        controller.tLight1.setId("trafficLight1");
        controller.tLight2.setId("glowTrafficLight2");
        controller.tLight3.setId("trafficLight3");

        controller.guidanceCircle.setVisible(true);

        controller.getHitErrorLabel().setText("Hit Error: ? mm");

        guidanceDepthVisualizer.toggleTorso(false);
    }

    /**
     * Performs view changes by switching from angulation to scene depth phase.
     * */
    private void sceneDepthViewAdjustments() {
        GuidanceAlignmentController controller = ((GuidanceAlignmentController) guidanceControllers.getFirst());

        controller.title.setText("Phase 4: Depth");

        controller.tLight2.setId("trafficLight2");
        controller.tLight3.setId("glowTrafficLight3");

        controller.targetCross.setVisible(false);
        controller.targetCircle.setVisible(false);

        controller.guidanceCircle.setVisible(false);
    }

    /**
     * Gives access to the {@link GuidanceAlignmentController}.
     * <p>
     * This method will throw an {@link IllegalStateException} if the controller is not active.
     * It should only be called from certain places where the guidance view is active.
     * </p>
     * @return The {@link GuidanceAlignmentController}.
     * */
    private GuidanceAlignmentController getGuidanceAlignmentControllerIfActive() {
        if (guidanceControllers.getFirst() instanceof GuidanceAlignmentController) {
            return ((GuidanceAlignmentController) guidanceControllers.getFirst());
        }
        throw new IllegalStateException("GuidanceAlignmentController is not active");
    }

    /**
     * Gives access to the {@link GuidancePlanningController}.
     * <p>
     * This method will throw an {@link IllegalStateException} if the controller is not active.
     * It should only be called from certain places where the planning view is active.
     * </p>
     * @return The {@link GuidancePlanningController}.
     * */
    private GuidancePlanningController getGuidancePlanningControllerIfActive() {
        if (guidanceControllers.getFirst() instanceof GuidancePlanningController) {
            return ((GuidancePlanningController) guidanceControllers.getFirst());
        }
        throw new IllegalStateException("GuidancePlanningController is not active");
    }

    /**
     * Provides access to the current guidance tab and passes it to the {@link GuidanceKeyHandler}.
     *
     * @param guidanceTab The current guidance tab.
     * */
    public void updateKeyHandler(Tab guidanceTab) {
        guidanceKeyHandler.setContentNode(guidanceTab);
    }

    /**
     * Provides access to the scene and passes it to the {@link GuidanceKeyHandler}
     *
     * @param scene The scene which will be used to gain access of the keys pressed.
     * */
    public void registerKeyHandler(Scene scene) {
        guidanceKeyHandler.handleKeyPressed(scene);
    }

    public Plane getPlaneSelected() {
        return planeSelected;
    }

    public Phase getCurrentPhase() {
        return currentPhase;
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public void setPlaneSelected(Plane planeSelected) {
        this.planeSelected = planeSelected;
    }

    /*----------------------------------------DELEGATES----------------------------------------*/

    public Group getWorld() {
        return guidanceDepthVisualizer.getWorld();
    }

    public CameraContainer getCamera() {
        return guidanceDepthVisualizer.getCamera();
    }

    public Group getTargetCross() {
        return getGuidanceAlignmentControllerIfActive().getTargetCross();
    }

    public Circle getTargetCircle() {
        return getGuidanceAlignmentControllerIfActive().getTargetCircle();
    }

    public Rectangle getDepthRectangle() {
        return getGuidanceAlignmentControllerIfActive().getDepthRectangle();
    }

    public Label getDepthLabel() {
        return getGuidanceAlignmentControllerIfActive().getDepthLabel();
    }

    public Label getDistanceLabel() {
        return getGuidanceAlignmentControllerIfActive().getDistanceLabel();
    }

    public Label getHitErrorLabel() {
        return getGuidanceAlignmentControllerIfActive().getHitErrorLabel();
    }

    public StackPane getSubScene() {
        return getGuidanceAlignmentControllerIfActive().getSubScene();
    }

}
