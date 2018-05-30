/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author miguelferreira
 */
import com.jme3.app.SimpleApplication;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import java.util.logging.Level;


public class Main extends SimpleApplication {

   
    Factory factory;

    private int selected = -1;   // Mira que torre ha seleccionado
    private GamePlayAppState game;
    private UIAppState ui;


    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }


    @Override
    public void simpleInitApp() {

        java.util.logging.Logger.getLogger("").setLevel(Level.WARNING);
        setDisplayStatView(false); // don't show debugger
        viewPort.setBackgroundColor(ColorRGBA.White);
        cam.setLocation(new Vector3f(0, 8, 18f));
        cam.lookAt(new Vector3f(0, 0, 6f), Vector3f.UNIT_Y);
        flyCam.setEnabled(false);

        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(0.8f, -0.7f, -1));
        sun.setColor(ColorRGBA.White.mult(1.5f));
        rootNode.addLight(sun);

        selected = -1;

        factory = new Factory(assetManager);
        ui = new UIAppState(guiNode, guiFont);
        game = new GamePlayAppState(rootNode, factory);
        stateManager.attach(ui);

        initInputs();
        startGame(1); // s
    }

    public void simpleUpdate(float tpf) { }
    

    /**
     * --------------------------------------------------------------
     */
    private void initInputs() {

        inputManager.setCursorVisible(true);
        // configurar input mappings
        inputManager.addMapping("Restart", new KeyTrigger(KeyInput.KEY_RETURN));
        inputManager.addMapping("Quit", new KeyTrigger(KeyInput.KEY_ESCAPE));
        inputManager.addMapping("Select", new MouseButtonTrigger(0)); // click
        inputManager.addMapping("LoadCargaHelada", new KeyTrigger(KeyInput.KEY_F));
        inputManager.addMapping("LoadNukeCarga", new KeyTrigger(KeyInput.KEY_N));
        inputManager.addMapping("LoadGatlingCarga", new KeyTrigger(KeyInput.KEY_G));

        inputManager.addListener(actionListener,
                "Restart", "Select", "Quit",
                "LoadGatlingCarga", "LoadNukeCarga", "LoadCargaHelada");
    }
   
    private ActionListener actionListener = new ActionListener() {

        @Override
        public void onAction(String mapping, boolean keyDown, float tpf) {
            if (stateManager.hasState(game)) {
                // Un jugador selecciona una torre.
                if (mapping.equals("Select") && !keyDown) {
                    // SI el usuario cambia de torre
                    if (selected != -1) {
                        Spatial prevTower = (rootNode.getChild("torre-" + selected));
                        prevTower.setMaterial((Material) prevTower.getUserData("standardMaterial"));
                    }
                    // Con el rayo determina en que torre ha clickado el user
                    CollisionResults results = new CollisionResults();
                    Vector2f click2d = inputManager.getCursorPosition();
                    Vector3f click3d = cam.getWorldCoordinates(
                            new Vector2f(click2d.x, click2d.y), 0f).clone();
                    Vector3f dir = cam.getWorldCoordinates(
                            new Vector2f(click2d.x, click2d.y), 1f).subtractLocal(click3d);
             
                    Ray ray = new Ray(click3d, dir);
                    rootNode.getChild("TowerNode").collideWith(ray, results);
                    // determina en que torre ha clickado el user
                    if (results.size() > 0) {
                        // Rayo colisiona con una torre, el user ha seleccionado una torre
                        CollisionResult closest = results.getClosestCollision();
                        selected = closest.getGeometry().getControl(TorreControl.class).getIndex();
                        Spatial seleccionada = rootNode.getChild("torre-" + selected);
                        seleccionada.setMaterial((Material) seleccionada.getUserData("selectedMaterial"));
                    } else {
                        // El usuario no ha seleccionado nada
                        selected = -1;
                    }
                }
                // SI se ha seleccionado la torre y el usuario ha presionado una tecla se carga
                //solo si hay municion disponible
                // Add new Carga only if player has budget and if torre is not full yet 
                // 
                if (selected != -1 && game.getBudget() > 0 && !keyDown) {
                    TorreControl seleccionada =
                            rootNode.getChild("torre-" + selected).getControl(TorreControl.class);
                    if (seleccionada.getCargaNum() <= game.getLevel()) {
                        // Se le puede añadir mas municion a la torre
                        if (mapping.equals("LoadCargaHelada")) {
                            seleccionada.addCarga(factory.getCargaHelada());
                            game.addBudgetMod(-1);
                        } else if (mapping.equals("LoadNukeCarga")) {
                            seleccionada.addCarga(factory.getNukeCarga());
                            game.addBudgetMod(-1);
                        } else if (mapping.equals("LoadGatlingCarga")) {
                            seleccionada.addCarga(factory.getGatlingCarga());
                            game.addBudgetMod(-1);
                        }
                    }
                }
            } else {
                // El juego esta pausado
                if (mapping.equals("Restart") && !keyDown) {
                    if (game.isLastGameWon()) {
                        // if last game won, then next level
                        startGame(game.getLevel() + 1);
                    } else {
                        // if last game lost, then restart from level 1
                        startGame(1);
                    }
                }
            }
            if (mapping.equals("Quit") && !keyDown) {
                endGame();
                stop();
            }
        }
    };

    /**
     * --------------------------------------------------------------
     */
  
    private void startGame(int level) {
        game.setLevel(level);
        stateManager.attach(game);
        selected = -1;
    }


    private void endGame() {
        stateManager.detach(game);
    }

}