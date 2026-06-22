package algorithm;

import javafx.scene.shape.TriangleMesh;
import util.Vector3D;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Singleton class which holds meshes and target points globally, which are loaded from visualization.
 * */
public final class DataService {

    /** List which contains the entry and target point loaded from the .mps file. */
    private final LinkedList<Vector3D> targetList = new LinkedList<>();

    /** List which contains the meshes, loaded from the visualization section. */
    private final ArrayList<TriangleMesh> meshes = new ArrayList<>();

    /** List which contains the names of the meshes. */
    private final ArrayList<String> meshNames = new ArrayList<>();

    /** Instance of the {@link DataService} */
    private static DataService instance;

    private DataService() {}

    /**
     * This method gives access to the instance of the {@link DataService}.
     * <p>
     * Ensures that the same instance is being used by all classes in runtime.
     * The synchronized keyword ensures that only one thread can access this method at a time.
     * </p>
     * @return The instance of the {@link DataService}.
     * */
    public synchronized static DataService getInstance() {
       if (instance == null) {
           instance = new DataService();
       }
       return instance;
    }

    /**
     * This method adds a loaded mesh and associated name to the lists.
     *
     * @param mesh The loaded mesh.
     * @param name The mesh name.
     * */
    public void addMesh(TriangleMesh mesh, String name) {
        meshes.add(mesh);
        meshNames.add(name);
    }

    /**
     * This method adds a 3D vector to the target list.
     *
     * @param target The target.
     * */
    public void addTarget(Vector3D target) {
        targetList.add(target);
    }

    /**
     * This method clears the targets, meshes and mesh names.
     * */
    public void clearAll() {
        targetList.clear();
        meshes.clear();
        meshNames.clear();
    }

    /**
     * Clears the target list.
     * */
    public void clearTargets() {
        targetList.clear();
    }

    /**
     * Checks if a given mesh name appears in the list.
     * */
    public boolean meshNameContainsInList(String meshName) {
        return meshNames.contains(meshName);
    }

    public LinkedList<Vector3D> getTargetList() {
        return targetList;
    }

    public ArrayList<TriangleMesh> getMeshes() {
        return meshes;
    }

    public ArrayList<String> getMeshNames() {
        return meshNames;
    }

}
