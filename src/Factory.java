

/**
 *
 * @author miguelferreira
 */
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Dome;
import com.jme3.scene.shape.Sphere;


public final class Factory {

    
    public static final float desliza_RADIUS = 0.3f;
    public static final float torre_RADIUS = 0.3f;
    public static final float torre_HEIGHT = 2.0f;

    public static final float[] GATLING = {0.0f, -1f, 6, 0f};
    public static final float[] FREEZE  = {-1f,  -1f, 3, 0f};
    public static final float[] NUKE    = {.5f,  -5f, 1, 2f};
    // assetmanager
    private AssetManager assetManager;
    // materials
    private Material desliza_mat;
    private Material floor_mat;
    private Material playerbase_mat;
    private Material torre_sel_mat;
    private Material torre_std_mat;

    public Factory(AssetManager as) {
        this.assetManager = as;
        initMaterials();
    }

    /**
     * ---------------------------------------------------------
     */
    public Node makeBaseJugador() {
        Node playerbase_node = new Node("PlayerBaseNode");
        // geometry de la base del jugador
        Box b2 = new Box(1.5f, .8f, 1f);
        Geometry playerbase_geo = new Geometry("Playerbase", b2);
        playerbase_geo.setMaterial(playerbase_mat);
        playerbase_geo.move(0, .8f, -1f);
        playerbase_node.attachChild(playerbase_geo);

        // floor geometry
        Node floor_node = new Node("Floor");
        Box b = new Box(33f, 0.1f, 33f);
        Geometry floor = new Geometry("Floor", b);
        floor.setMaterial(floor_mat);
        floor.setLocalTranslation(0, -8f, 0);
        floor_node.attachChild(floor);

        // agnade suelo y la base del jugador
        playerbase_node.attachChild(floor_node);
        return playerbase_node;
    }

    /**
     * Crea una torre en el origen
     */
    public Geometry makeTower(int index) {
        Box torre_shape = new Box(
                torre_RADIUS,
                torre_HEIGHT * .5f,
                torre_RADIUS);
        Geometry torre_geo = new Geometry("torre-" + index, torre_shape);
        assetManager.loadModel("Models/Sinbad/Sinbad.mesh.xml");
        torre_geo.setMaterial(torre_std_mat);
        torre_geo.setUserData("torreHeight", torre_HEIGHT);
        torre_geo.setUserData("selectedMaterial", torre_sel_mat);
        torre_geo.setUserData("standardMaterial", torre_std_mat);
        return torre_geo;
    }

    
    public Geometry makeCreep(Vector3f loc, int index) {
        Dome desliza_shape = new Dome(Vector3f.ZERO,
                10, 10, desliza_RADIUS, false);
        Geometry desliza_geo = new Geometry("Creep-" + index, desliza_shape);
        desliza_geo.setMaterial(desliza_mat);
        desliza_geo.setLocalTranslation(loc);
        return desliza_geo;
    }

    /**
     * --------------------------------------------------------------------
     */
    
    private Geometry getCargaGeometry(Material mat) {
        Sphere dot = new Sphere(10, 10, .1f);
        Geometry chargeMarker_geo = new Geometry("CargaMarker", dot);
        chargeMarker_geo.setMaterial(mat);
        return chargeMarker_geo;
    }
    
    /**
     * F: congela , ralentiza el desliza pero quita menos daño que el nuke
     */
    public Carga getCargaHelada() {
        Material beam_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        beam_mat.setColor("Color", ColorRGBA.Cyan);
        return new Carga(FREEZE, getCargaGeometry(beam_mat));
    }


    public Carga getGatlingCarga() {
        Material beam_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        beam_mat.setColor("Color", ColorRGBA.Yellow);
        return new Carga(GATLING, getCargaGeometry(beam_mat));
    }

    /**
     * 
     * Nuke hace mas daño pero tambien acelera al desliza
     */
    public Carga getNukeCarga() {
        Material beam_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        beam_mat.setColor("Color", ColorRGBA.Red);
        return new Carga(NUKE, getCargaGeometry(beam_mat));
    }

    /**
     * ---------------------------------------------------------
     */
    private void initMaterials() {
        // desliza material
        desliza_mat = new Material(assetManager,
                "Common/MatDefs/Light/Lighting.j3md");
        desliza_mat.setColor("Diffuse", ColorRGBA.Black);
        desliza_mat.setColor("Ambient", ColorRGBA.Black);
        desliza_mat.setBoolean("UseMaterialColors", true);
        // floor material
        floor_mat = new Material(assetManager,
                "Common/MatDefs/Light/Lighting.j3md");
        floor_mat.setColor("Diffuse", ColorRGBA.White);
        floor_mat.setColor("Ambient", ColorRGBA.White);
        floor_mat.setBoolean("UseMaterialColors", true);
        // player material
        playerbase_mat = new Material(assetManager,
                "Common/MatDefs/Light/Lighting.j3md");
        playerbase_mat.setColor("Diffuse", ColorRGBA.Yellow);
        playerbase_mat.setColor("Ambient", ColorRGBA.Yellow);
        playerbase_mat.setBoolean("UseMaterialColors", true);
        // torre Material
        torre_sel_mat = new Material(assetManager,
                    "Common/MatDefs/Light/Lighting.j3md");
        torre_sel_mat.setColor("Diffuse", ColorRGBA.Green.mult(.75f));
        torre_sel_mat.setColor("Ambient", ColorRGBA.Green.mult(.75f));
        torre_sel_mat.setBoolean("UseMaterialColors", true);
        //torre standard Material
        torre_std_mat = new Material(assetManager,
                "Common/MatDefs/Light/Lighting.j3md");
        torre_std_mat.setColor("Diffuse", ColorRGBA.Green);
        torre_std_mat.setColor("Ambient", ColorRGBA.Green);
        torre_std_mat.setBoolean("UseMaterialColors", true);
    }
}