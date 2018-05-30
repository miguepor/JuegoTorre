
import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import java.util.List;




public class GamePlayAppState extends AbstractAppState {

    private AppStateManager stateManager;
    private final Node rootNode;
    // nodes
    private Node playerBase;
    private final Node creepNode;
    private final Node torreNode;
    private final Node beamNode;
    private final Node cargaMarkerNode;
    Factory factory;
    private float timer_beam = 0f;
    private float timer_budget = 0f;
    private int nivel = 0;
    private int score = 0;
    private float salud = 0;
    private int budget = 0;
    private boolean lastGameWon = false;
    private int CREEP_INIT_NUM;
    private int torre_INIT_NUM;
    private float CREEP_INIT_salud;
    private float CREEP_INIT_velocidad;

    public GamePlayAppState(Node rootNode, Factory factory) {
        this.rootNode = rootNode;
        this.factory = factory;
        playerBase = factory.makeBaseJugador();
        creepNode = new Node("CreepNode");
        torreNode = new Node("TowerNode");
        beamNode = new Node("BeamNode");
        cargaMarkerNode = new Node("CargaMarkerNode");
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        this.stateManager = stateManager;
        // configurable factors depend on nivel
        this.budget = 5 + nivel * 2;
        this.salud = 2f + nivel;
        this.CREEP_INIT_NUM = 2 + nivel * 2;
        this.torre_INIT_NUM = 4 + nivel / 2;
        this.CREEP_INIT_salud = 20f + nivel * 2;
        this.CREEP_INIT_velocidad = 0.5f + nivel / 10;
        rootNode.attachChild(playerBase);
        rootNode.attachChild(creepNode);
        rootNode.attachChild(torreNode);
        rootNode.attachChild(beamNode);
        rootNode.attachChild(cargaMarkerNode);
        addCreeps();
        addTowers();
    }

    @Override
    public void cleanup() {
        creepNode.detachAllChildren();
        torreNode.detachAllChildren();
        beamNode.detachAllChildren();
        cargaMarkerNode.detachAllChildren();
        rootNode.detachChild(playerBase);
        rootNode.detachChild(creepNode);
        rootNode.detachChild(torreNode);
        rootNode.detachChild(beamNode);
        rootNode.detachChild(cargaMarkerNode);
        super.cleanup();
    }

 

    private void addTowers() {
        for (int index = 0; index < torre_INIT_NUM; index++) {
            int leftOrRight = (index % 2 == 0 ? 1 : -1); // -1 or +1
            float offset_x = leftOrRight * 2.5f;
            float offset_y = Factory.torre_HEIGHT * .5f;
            float offset_z = index + 2;
            Vector3f loc = new Vector3f(offset_x, offset_y, offset_z);
            // torre geo
            Geometry torre_geo = factory.makeTower(index);
            torre_geo.setLocalTranslation(loc);
            torre_geo.setUserData("index", index);
            torre_geo.setUserData("cargasNum", 0);
            torre_geo.addControl(new TorreControl(this));
            torreNode.attachChild(torre_geo);
        }
    }

    private void addCreeps() {
        for (int index = 0; index < CREEP_INIT_NUM; index++) {
            int leftOrRight = (index % 2 == 0 ? 1 : -1); // +1 or -1
            float offset_x = 1.75f * leftOrRight * FastMath.rand.nextFloat();
            float offset_y = 0;
            float offset_z = 2.5f * ((torre_INIT_NUM / 2f) + 6f);
            Vector3f spawnloc = new Vector3f(offset_x, offset_y, offset_z);
            // creep geometry
            Geometry creep_geo = factory.makeCreep(spawnloc, index);
            // data
            creep_geo.setUserData("index", index);
            creep_geo.setUserData("salud", CREEP_INIT_salud);
            creep_geo.setUserData("velocidad", CREEP_INIT_velocidad);
            creep_geo.addControl(new CreepControl(this));
            creepNode.attachChild(creep_geo);
        }
    }

    /**
     * --------------------------------------------------------------
     */
    public void setLevel(int nivel) {
        this.nivel = nivel;
    }

    public int getLevel() {
        return nivel;
    }

    public int getScore() {
        return score;
    }

    public boolean isLastGameWon() {
        return lastGameWon;
    }
    

    public void addScoreMod(int mod) {
        score += mod;
    }

    public float getsalud() {
        return Math.round(salud * 10) / 10; // drop the decimals
    }


    public void addsaludMod(float mod) {
        salud += mod;
    }

    public int getBudget() {
        return budget;
    }


    public void addBudgetMod(int mod) {
        budget += mod;
    }


    public int getCreepNum() {
        return creepNode.getChildren().size();
    }

    public List<Spatial> getCreeps() {
        return creepNode.getChildren();
    }

    public void addBeam(Geometry beam) {
        beamNode.attachChild(beam);
    }

    public void addCargaMarker(Spatial spat){
        cargaMarkerNode.attachChild(spat);
    }
    
    public void removeCargaMarker(Spatial spat){
        cargaMarkerNode.detachChild(spat);
    }
    

    private Boolean thereAreBeams() {
        return beamNode.descendantMatches("Beam").size() > 0;
    }


    private void clearAllBeams() {
        beamNode.detachAllChildren();
    }


    @Override
    public void update(float tpf) {
   
        timer_budget += tpf;
        if (timer_budget > getLevel() + 10) {
            addBudgetMod(getLevel());
            timer_budget = 0;
        }

        timer_beam += tpf;
        if (timer_beam > 1f) {
            if (thereAreBeams()) {
                clearAllBeams();
            }
            timer_beam = 0;
        }


        if (getsalud() <= 0) {
            lastGameWon = false;
            stateManager.detach(this);
        
        } else if ((getCreepNum() == 0) && getsalud() > 0) {
            lastGameWon = true;
            stateManager.detach(this);
        }
    }
}