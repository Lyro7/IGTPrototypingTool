package controller;

import algorithm.GuidanceManager;
import javafx.animation.AnimationTimer;
import javafx.scene.Group;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
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

    private MainController mainController;

    private final GuidanceManager guidanceManager = new GuidanceManager(this);

    private final LinkedList<Vector3D> targetList = new LinkedList<>();

    /* List, which contains the current active controller */
    private final List<GuidanceController> guidanceControllers = new ArrayList<>();

    private AnimationTimer animator;

    public GuidanceHandler() {
        navigationLoop();
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public void switchToTab(String fileName) {
        mainController.switchContentOfTab(fileName);
    }

    public void navigationLoop() {
        animator = new AnimationTimer() {
            @Override
            public void handle(long now) {
                // Align
                guidanceManager.translateDeviationAndRenderCross();
            }
        };
    }

    public void startNavigationLoop() {
        animator.start();
    }

    public void stopNavigationLoop() {
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

    public Group getTargetCross() {
        return ((GuidanceNavigationController) guidanceControllers.getFirst()).getTargetCross();
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


}
