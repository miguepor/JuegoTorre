import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.export.Savable;
import com.jme3.material.Material;
import com.jme3.scene.Geometry;
import java.io.IOException;


public class Carga implements Savable {

  private float velocidaddagno;
  private float saluddagno;
  private int   ammoNum;
  private float blast;
  private Material beam_mat;
  private Geometry geometry;


  public Carga(float s, float h, int a, float b, Geometry m) {
    this.velocidaddagno = s;
    this.saluddagno = h;
    this.ammoNum = a;
    this.blast = b;
    this.geometry = m;
    this.beam_mat = m.getMaterial();
  }


  public Carga(float[] v, Geometry m) {
    this.velocidaddagno = v[0];
    this.saluddagno = v[1];
    this.ammoNum = (int) v[2];
    this.blast = v[3];
    this.geometry = m;
    this.beam_mat = m.getMaterial();
  }

 
  public int getAmmoNum() {
    return ammoNum;
  }


  public void addAmmo(int mod) {
    ammoNum += mod;
  }

  
  public float getSpeedImpact() {
    return velocidaddagno;
  }


  public float getsaludImpact() {
    return saluddagno;
  }

  public float getBlastRange() {
    return blast;
  }

  public Material getBeamMaterial() {
    return beam_mat;
  }

  public Geometry getGeometry() {
    return geometry;
  }
  /** ----------------------------------------------------------- */
  
  public void write(JmeExporter ex) throws IOException {
    OutputCapsule capsule = ex.getCapsule(this);
    capsule.write(ammoNum,      "ammo", 1);
    capsule.write(saluddagno, "saluddagno", 1f);
    capsule.write(velocidaddagno,  "velocidaddagno", 1f);
    capsule.write(blast,        "blast", 1f);
    capsule.write(beam_mat,     "beam_mat", null);
    capsule.write(geometry,     "geometry", null);
  }

  public void read(JmeImporter im) throws IOException {
    InputCapsule capsule = im.getCapsule(this);
    ammoNum       = capsule.readInt("ammo", 1);
    saluddagno  = capsule.readFloat("saluddagno", 1);
    velocidaddagno   = capsule.readFloat("velocidaddagno", 1);
    blast         = capsule.readFloat("blast", 1);
    beam_mat      = (Material) capsule.readSavable("beam_mat", null);
    geometry       = (Geometry)  capsule.readSavable("geometry", null);
  }
}