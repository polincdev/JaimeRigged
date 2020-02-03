/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jaime;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.AnimEventListener;
import com.jme3.animation.LoopMode;
import com.jme3.app.SimpleApplication;
import com.jme3.bounding.BoundingBox;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
 
import com.jme3.input.KeyInput;
 
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.AmbientLight;
 
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
 
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
 

/** Sample 8 - how to let the user pick (select) objects in the scene
 * using the mouse or key presses. Can be used for shooting, opening doors, etc. */
public class JaimeRiggedTest extends SimpleApplication  implements AnimEventListener {

  public static void main(String[] args) {
    JaimeRiggedTest app = new JaimeRiggedTest();
    app.start();
  }
  private Node shootables;
  private AnimChannel channel;
  private AnimControl control;
  Node player;

  BitmapText hintText;  
  BitmapText debugText; 
  
  
  int currentType=0;
  boolean currentLoopMode=false;

  String[] animTypes=new String[]
           {
            "standing",
            "walking",
             "running",
             "jumpingStart",
            "jumpingMid",
            "jumpingEnd",
            "monkeyStuff1",
            "monkeyStuff2",
            "monkeyStuff3",
            "punchingStart",
            "punchingEnd",
            "kickingStart",
            "kickingEnd",
            "shooting",
            "slappingStart",
            "slappingEnd",
            "guarding",
            "throwing",
             "grabing",
            "dying",
           };
  

  @Override
  public void simpleInitApp() {
      this.setDisplayStatView(false);
     flyCam.setMoveSpeed(5f);
     cam.setLocation(new Vector3f(0,5,22));
     initKeys();       // load custom key mappings
   
    /** create four colored boxes and a floor to shoot at: */
    shootables = new Node("Shootables");
    rootNode.attachChild(shootables);
 
    shootables.attachChild(makeFloor());
    shootables.attachChild(makeCharacter());
    
    //Text
    BitmapFont font =  getAssetManager().loadFont("Interface/Fonts/Default.fnt");
    //Hint
    hintText = new BitmapText(font);
    hintText.setSize(font.getCharSet().getRenderedSize()*1.5f);
    hintText.setColor(ColorRGBA.Red);
    hintText.setText("AnimType:1/2 LoopMode:3/4");
    hintText.setLocalTranslation(0, this.getCamera().getHeight()-10, 1.0f);
    hintText.updateGeometricState();
    guiNode.attachChild(hintText);
    //Info
    debugText=hintText.clone();
    debugText.setColor(ColorRGBA.White);
    debugText.setText("AnimType:"+animTypes[currentType]+" LoopMode:"+currentLoopMode );
    debugText.setLocalTranslation(0, hintText.getLocalTranslation().y-30, 1.0f);
    debugText.updateGeometricState();
    guiNode.attachChild(debugText);
    
  }

  /** Custom Keybinding: Map named actions to inputs. */
  private void initKeys() {
    inputManager.addMapping("Walk", new KeyTrigger(KeyInput.KEY_SPACE));
    inputManager.addListener(actionListener, "Walk");
    
    
      //Keys
        inputManager.addMapping("StrDec", new KeyTrigger(KeyInput.KEY_1));
        inputManager.addMapping("StrInc", new KeyTrigger(KeyInput.KEY_2));
         inputManager.addMapping("BrDec", new KeyTrigger(KeyInput.KEY_3));
        inputManager.addMapping("BrInc", new KeyTrigger(KeyInput.KEY_4));
        inputManager.addListener(actionListener, new String[]{"StrInc"});
        inputManager.addListener(actionListener, new String[]{"StrDec"});
        inputManager.addListener(actionListener, new String[]{"BrInc"});
        inputManager.addListener(actionListener, new String[]{"BrDec"});
         
        
  }
  private ActionListener actionListener = new ActionListener() {
    public void onAction(String name, boolean keyPressed, float tpf) {
      if (name.equals("Walk") && !keyPressed) {
        if (!channel.getAnimationName().equals("Walk")) {
          channel.setAnim("walking", 0.50f);
          channel.setLoopMode(LoopMode.Loop);
        }
      }
      
        if(!keyPressed)
            return;
       
        if(name.equals("StrInc"))
        {
           currentType+=1;   
           if(currentType>animTypes.length-1)
               currentType=animTypes.length-1;
           refreshDisplay();
	    //
           
        }
        else  if(name.equals("StrDec"))
        {
           currentType-=1;   
           if(currentType<0)
              currentType=0;
           refreshDisplay();
	    //
          
        }
        if(name.equals("BrInc"))
        {
           currentLoopMode=!currentLoopMode;   
            refreshDisplay();
	     
        }
        else  if(name.equals("BrDec"))
        {
          currentLoopMode=!currentLoopMode;   
         refreshDisplay();
	 
        }
      
      
    }
  };

  /** A floor to show that the "shot" can go through several objects. */
  protected Geometry makeFloor() {
    Box box = new Box(15, .2f, 15);
    Geometry floor = new Geometry("the Floor", box);
    floor.setLocalTranslation(0, 0, 0);
    Material mat1 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    mat1.setColor("Color", ColorRGBA.Gray);
    floor.setMaterial(mat1);
    return floor;
  }
 
 

  protected   Spatial   makeCharacter() {
    // load a character  
     Spatial scene= assetManager.loadModel("Models/JaimeRigged/JaimeRigged.j3o");
     Node sceneAsNode=((Node)scene);
     Spatial jamie =  ((Node)scene).getChild("JaimeRigged");
     jamie.scale(0.3f);
    //Move by 50% asssuming that the root bone is half of its body. 
    //Since Jaime's proportions are not humanoid these numbers are different
    //BoundingBox box = (BoundingBox)jamie.getWorldBound();
    jamie.setLocalTranslation(-.0f,2.1f , -0f);
    
    //Anim
    player = (Node)jamie;
    control = player.getControl(AnimControl.class);
    control.addListener(this);
    channel = control.createChannel();
    channel.setAnim(animTypes[currentType]);
      
    for(int a=0;a<  control.getAnimationNames().size();a++)
       System.out.println("\""+control.getAnimationNames().toArray()[a]+"\",");
 
    // We must add a light to make the model visible
     //Light
    DirectionalLight sun = new DirectionalLight();
    sun.setColor(ColorRGBA.White.mult(1.5f));
    sun.setDirection(new Vector3f( -.5f, -.5f, -.5f).normalizeLocal());
    rootNode.addLight(sun);

    AmbientLight al = new AmbientLight();
    al.setColor(ColorRGBA.White.mult(1.0f));
    rootNode.addLight(al);
        
    return jamie;
  }
  
  
  void refreshAnim()
  {
       channel.setAnim(animTypes[currentType], 1.0f);
      channel.setLoopMode(currentLoopMode ? LoopMode.Loop: LoopMode.DontLoop);
      channel.setSpeed(1f);
  }
  public void onAnimCycleDone(AnimControl control, AnimChannel channel, String animName) {
    if (animName.equals("Walk")) {
      channel.setAnim("stand", 0.50f);
      channel.setLoopMode(LoopMode.DontLoop);
      channel.setSpeed(1f);
    }
  }

  public void onAnimChange(AnimControl control, AnimChannel channel, String animName) {
    // unused
  }
void refreshDisplay()
  {
   debugText.setText("AnimType:"+animTypes[currentType]+" LoopMode:"+currentLoopMode );
   refreshAnim();	  
  }    
    
}