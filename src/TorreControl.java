
import com.jme3.export.Savable;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;
import com.jme3.scene.shape.Line;
import java.util.ArrayList;
import java.util.List;

public class TorreControl extends AbstractControl implements Savable, Cloneable {

    private List<Carga> charges = new ArrayList<Carga>();
    private float timer = 0f;
    private GamePlayAppState game;

    public TorreControl(GamePlayAppState game) {
        this.game = game;
    }

    @Override
    protected void controlUpdate(float tpf) {
        timer += tpf;
        if (getCargaNum() > 0) {

            Carga charge = popNextCarga();

            List<CreepControl> reachable = new ArrayList<CreepControl>();
            List<Spatial> creeps = game.getCreeps();
            for (Spatial creep_geo : creeps) {
                CreepControl creep = creep_geo.getControl(CreepControl.class);

                if (creep.isAlive()
                        && getTowerTop().distance(creep.getLoc()) < getHeight() * 2f) { // RANGE
                    reachable.add(creep);
                }
            }
            /* Si la torre Cargada puede alcanzar el creep ... */
            if (reachable.size() > 0 && charge.getAmmoNum() > 0) {
             
                for (CreepControl creep : reachable) {
                    if (timer > .01f) { 
                        Vector3f hit = creep.getLoc();
                        Line beam = new Line(
                                getTowerTop(),
                                new Vector3f(
                                hit.x + FastMath.rand.nextFloat() / 10f,
                                hit.y + FastMath.rand.nextFloat() / 10f,
                                hit.z + FastMath.rand.nextFloat() / 10f));
                        Geometry beam_geo = new Geometry("Beam", beam);
                        beam_geo.setMaterial(charge.getBeamMaterial());
                        game.addBeam(beam_geo);
                        // laser tiene efecto
                        applyDamage(creep, charge);
                        // Shooting quita 1 ucrga
                        charge.addAmmo(-1);
                        if (charge.getAmmoNum() <= 0) {
                            // this charge is out of ammo, discard the charge.
                            removeCarga(charge);
                            // pause to reload, that is, stop shooting until next turn.
                            break;
                        }
                        break;
                    }
                }
            timer = 0f;
            }
        }
    }

    /**
     * Todas las torres hacen algun daño,
     * N mas daño pero mas rapido tambien
     */
    
    private void applyDamage(CreepControl creep, Carga charge) {
        List<Spatial> creeps = game.getCreeps();
        creep.addSpeed(charge.getSpeedImpact());
        creep.addsalud(charge.getsaludImpact());
        // blast neighbours
        for (Spatial neighbour : creeps) {
            float dist = neighbour.getLocalTranslation().distance(creep.getLoc());
            if (dist < charge.getBlastRange() && dist > 0f) {
                neighbour.getControl(CreepControl.class).addsalud(charge.getsaludImpact() / 2f);
                neighbour.getControl(CreepControl.class).addSpeed(charge.getSpeedImpact() / 3);
            }
        }
    }

    public void addCarga(Carga charge) {
        charges.add(charge);
        Vector3f loc = spatial.getLocalTranslation();
        Spatial chargeMarker_geo = charge.getGeometry();
        int offset_x = (getIndex() % 2 == 0 ? 1 : -1);
        chargeMarker_geo.setLocalTranslation(
                loc.x - (offset_x * 0.33f),
                loc.y - ((charges.size() - 1) * .25f) + 1,
                loc.z);
        game.addCargaMarker(charge.getGeometry());
    }

    public void removeCarga(Carga charge) {
        charges.remove(charge);
        game.removeCargaMarker(charge.getGeometry());
    }

    public int getCargaNum() {
        return charges.size();
    }

    public Carga popNextCarga() {
        return charges.get(0);
    }

    /**
     * ---------------------------------------
     */
    public float getHeight() {
        return (Float) spatial.getUserData("torreHeight");
    }

    public int getIndex() {
        return (Integer) spatial.getUserData("index");
    }

    /**
     * Localizacion para calcular el rango
     *
     * Calcula la altura de la torre
     */
    public Vector3f getTowerTop() {
        Vector3f loc = getLoc();
        return new Vector3f(loc.x, loc.y + getHeight() / 2, loc.z);
    }

    public Vector3f getLoc() {
        return spatial.getLocalTranslation();
    }

    /**
     * ---------------------------------------
     */
    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }

    public Control cloneForSpatial(Spatial spatial) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}