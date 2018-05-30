

/**
 *
 * @author miguelferreira
 */
import com.jme3.export.Savable;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;

/**
 * Esta clase controla la salud de los disparos y los ataques
 *
 * @author Miguel Ferreira
 */
public class CreepControl extends AbstractControl implements Savable, Cloneable {

    private final float speed_min = .5f;
    private GamePlayAppState game;

    public CreepControl(GamePlayAppState game) {
        this.game = game;
    }

    @Override
    protected void controlUpdate(float tpf) {
        if (isAlive()) {
            // avanza en el eje z
            Vector3f newloc = new Vector3f(
                    spatial.getLocalTranslation().getX(),
                    spatial.getLocalTranslation().getY(),
                    spatial.getLocalTranslation().getZ()
                    - (getSpeed() * tpf * FastMath.rand.nextFloat()));
            if (newloc.z > 0) {
  
                //Si no ha alcanzado la base sigue andando
                setLoc(newloc);
            } else {
                // Ataca al jugador
                kamikaze();
            }
        } else {
            // Creep got killed by tower
            game.addBudgetMod(1); // increase player budget as reward.
            remove();             // remove me (this creep)
        }
    }

    /**
     * ----------------------------------------------
     */
    public void setsalud(float h) {
        spatial.setUserData("salud", h);
    }

    public float getsalud() {
        return (Float) spatial.getUserData("salud");
    }

    public Boolean isAlive() {
        return getsalud() > 0f;
    }


    public void addsalud(float mod) {
        setsalud(getsalud() + mod);
    }
    
    public void kamikaze() {
        game.addsaludMod(getsalud() / -10);
        setsalud(0f);
        remove();
    }

    public void addSpeed(float mod) {
        spatial.setUserData("speed", getSpeed() + mod);
        if (getSpeed() < speed_min) {
            spatial.setUserData("speed", speed_min);
        }
    }

    public float getSpeed() {
        return (Float) spatial.getUserData("velocidad");
    }

    /**
     * ----------------------------------------------
     */
    public void setLoc(Vector3f loc) {
        spatial.setLocalTranslation(loc);
    }

    public Vector3f getLoc() {
        return spatial.getLocalTranslation();
    }

    /**
     * ----------------------------------------------
     */
    public int getIndex() {
        return (Integer) spatial.getUserData("index");
    }

    public void remove() {
        spatial.removeFromParent();
        spatial.removeControl(this);
    }

    /**
     * ----------------------------------------------
     */
    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }

    @Override
    public Control cloneForSpatial(Spatial spatial) {
        return this;
    }
}