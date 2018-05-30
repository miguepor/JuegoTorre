package mygame.state;


import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;

public class Level01State extends AbstractAppState{


    private final Node rootNode;
    private final Node localRootNode = new Node("Level01");
    private AssetManager assetManager;
    
    
    public Level01State(SimpleApplication app){
        rootNode =app.getRootNode();
        assetManager = app.getAssetManager();
        }
   
    @Override
    public void initialize(AppStateManager stateManager, Application app){
        super.initialize(stateManager, app);
        rootNode.attachChild(localRootNode);
        
        Box b = new Box(1, 1, 1);
        Geometry geom = new Geometry("Box", b);

        Material mat = assetManager.loadMaterial("Materials/BlueBoat.j3m");
        geom.setMaterial(mat);

        rootNode.attachChild(geom);
    }
    
    @Override
    public void cleanup(){
        rootNode.detachChild(localRootNode); // borra todo lo del rootnode y pasa por ee
                                             // ejemplo al nodo 2
        super.cleanup();
    }
}
