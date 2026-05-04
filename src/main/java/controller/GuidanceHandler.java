package controller;

import algorithm.GuidanceManager;
import javafx.animation.AnimationTimer;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import util.GuidanceKeyHandler;
import util.Vector3D;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class GuidanceHandler {

    public enum Phase { ALIGNMENT, ANGLE, DEPTH }

    private Phase currentPhase = Phase.ALIGNMENT;

    private MainController mainController;

    private final GuidanceManager guidanceManager = new GuidanceManager(this);

    private final GuidanceKeyHandler guidanceKeyHandler = new GuidanceKeyHandler(this);

    private final LinkedList<Vector3D> targetList = new LinkedList<>();

    /* List, which contains the current active controller */
    private final List<GuidanceController> guidanceControllers = new ArrayList<>();

    private AnimationTimer animator;

    public GuidanceHandler() {
        guidanceLoop();
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public void switchToTab(String fileName) {
        mainController.switchContentOfTab(fileName);
    }

    public void guidanceLoop() {
        animator = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (currentPhase.equals(Phase.ALIGNMENT)) {
                    guidanceManager.alignmentLogic();
                } else if (currentPhase.equals(Phase.ANGLE)) {
                    guidanceManager.angulationLogic();
                }
            }
        };
    }

    public void startGuidanceLoop() {
        animator.start();
    }

    public void stopGuidanceLoop() {
        animator.stop();
    }

    public void resetControllers() {
        for (GuidanceController controller : guidanceControllers) {
            controller.close();
        }
        guidanceControllers.clear();
    }

    public void addGuidanceController(GuidanceController controller) {
        this.guidanceControllers.add(controller);
    }

    /**
     * This function is based on the implementation from the visualization section.
     * <p>
     * Select a .mps file created from MITK to add two targets to the guidance.
     * One is the entry point and the other represents the target point.
     * </p>
     */
    public void loadPuncturePath() {
        targetList.clear();

        FileChooser fc = new FileChooser();
        fc.setTitle("Load puncture path");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("MPS Files", "*.mps"));
        File file = fc.showOpenDialog(new Stage());
        if (file != null) {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            try {
                dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
                DocumentBuilder db = dbf.newDocumentBuilder();
                Document doc = db.parse(file);
                doc.getDocumentElement().normalize();
                NodeList list = doc.getElementsByTagName("point");
                for (int temp = 0; temp < list.getLength(); temp++) {
                    org.w3c.dom.Node node = list.item(temp);
                    if (node.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                        Element element = (Element) node;
                        double x = Double.parseDouble(element.getElementsByTagName("x").item(0).getTextContent());
                        double y = Double.parseDouble(element.getElementsByTagName("y").item(0).getTextContent());
                        double z = Double.parseDouble(element.getElementsByTagName("z").item(0).getTextContent());

                        targetList.add(new Vector3D(x, y, z));
                    }
                }
            } catch (ParserConfigurationException | SAXException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    public LinkedList<Vector3D> getTargetList() {
        return targetList;
    }

    public Group getTargetCross() {
        return ((GuidanceAlignmentController) guidanceControllers.getFirst()).getTargetCross();
    }

    public Circle getTargetCircle() {
        return ((GuidanceAngleController) guidanceControllers.getFirst()).getTargetCircle();
    }

    public Rectangle getDepthRectangle() {
        return ((GuidanceAlignmentController) guidanceControllers.getFirst()).getDepthRectangle();
    }

    public Label getDepthLabel() {
        return ((GuidanceAlignmentController) guidanceControllers.getFirst()).getDepthLabel();
    }

    public void updateKeyHandler(Tab guidanceTab) {
        guidanceKeyHandler.setContentNode(guidanceTab);
    }

    public void registerKeyHandler(Scene scene) {
        guidanceKeyHandler.handleKeyPressed(scene);
    }

    public Phase getCurrentPhase() {
        return currentPhase;
    }

    public void updateCurrentPhase(Phase currentPhase) {
        this.currentPhase = currentPhase;
        phaseToSwitch();
    }

    private void phaseToSwitch() {
        if (currentPhase.equals(Phase.ALIGNMENT)) {
            switchToTab("GuidanceAlignmentView");
        } else if (currentPhase.equals(Phase.ANGLE)) {
            switchToTab("GuidanceAngleView");
        } else if (currentPhase.equals(Phase.DEPTH)) {
            // Soon...
        }
    }

}
