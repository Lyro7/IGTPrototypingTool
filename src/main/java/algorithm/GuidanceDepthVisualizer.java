package algorithm;

import com.interactivemesh.jfx.importer.stl.StlMeshImporter;
import controller.GuidanceHandler;
import javafx.scene.Group;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;
import shapes.CameraContainer;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Objects;

public class GuidanceDepthVisualizer {

    private final GuidanceHandler guidanceHandler;

    private final CameraContainer cameraContainer = new CameraContainer(true);

    private final Group world = new Group();

    public GuidanceDepthVisualizer (GuidanceHandler guidanceHandler) {
        this.guidanceHandler = guidanceHandler;
    }

    public void initialize() {

        MeshView liver = loadMesh("/models/liver.stl");
        MeshView kidneyLeft = loadMesh("/models/kidney_left.stl");
        MeshView kidneyRight = loadMesh("/models/kidney_right.stl");
        MeshView lung = loadMesh("/models/lung.stl");
        MeshView bones = loadMesh("/models/bones.stl");
        MeshView vessels = loadMesh("/models/vessels.stl");

        world.getChildren().addAll(liver, kidneyLeft, kidneyRight, lung, bones, vessels);

        StackPane subScenePane = guidanceHandler.getSubScene();

        SubScene subScene = new SubScene(world, 1200, 1000, true, SceneAntialiasing.BALANCED);

        cameraContainer.getPerspectiveCamera().setTranslateZ(500);

        subScene.setRoot(world);
        subScene.setCamera(cameraContainer.getPerspectiveCamera());

        subScenePane.getChildren().add(subScene);
    }

    public CameraContainer getCamera() {
        return cameraContainer;
    }

    public Group getWorld() {
        return world;
    }

    private MeshView loadMesh(String path) {
        StlMeshImporter meshImporter = new StlMeshImporter();

        try {
            meshImporter.read(new File(Objects.requireNonNull(getClass().getResource(path)).toURI()));
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        TriangleMesh mesh = meshImporter.getImport();
        meshImporter.close();

        MeshView meshView = new MeshView(mesh);

        meshView.setScaleX(5);
        meshView.setScaleY(5);
        meshView.setScaleZ(5);

        return meshView;
    }



}
