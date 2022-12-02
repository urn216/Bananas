package code.ui;

import code.core.Core;

import code.math.Vector2;
import code.ui.components.UIComponent;
import code.ui.components.UIInteractable;
import code.ui.components.UIText;
import code.ui.components.interactables.*;
import code.ui.elements.*;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

public class UICreator {
  // private static final UIElement VIRTUAL_KEYBOARD = new ElemKeyboard();
  
  private static final double BUTTON_HEIGHT = 0.075;
  private static final double BUFFER_HEIGHT = 0.015;
  
  /**
  * Creates the UI pane for the main menu.
  */
  public static UIPane createMain(Core c, UIController ui) {
    UIPane mainMenu = new UIPane();
    
    UIElement title = new UIElement(
    new Vector2(0, 0),
    new Vector2(0.28, 0.14),
    new boolean[]{true, false, true, false}
    ){
      protected void init() {components = new UIComponent[]{new UIText("Bananas", 0.6, Font.BOLD)};}
      protected void draw(Graphics2D g, int screenSizeY, Vector2 tL, Vector2 bR, Color[] c, UIInteractable highlighted) {
        components[0].draw(g, (float)tL.x, (float)tL.y, (float)(bR.x-tL.x), (float)(bR.y-tL.y), c[UIColours.TEXT]);
      }
    };
    
    UIElement outPanel = new ElemList(
    new Vector2(0, 0.28),
    new Vector2(0.24, 0.565),
    BUTTON_HEIGHT,
    BUFFER_HEIGHT,
    new UIInteractable[]{
      new UIButton("Play"           , () -> ui.setMode(UIState.NEW_GAME)),
      new UIButton("Options"        , () -> ui.setMode(UIState.OPTIONS) ),
      new UIButton("Quit to Desktop", c::quitToDesk                     ),
    },
    new boolean[]{false, false, true, false}
    );
    
    UIElement playModes = new ElemList(
    new Vector2(0, 0.28),
    new Vector2(0.24, 0.565),
    BUTTON_HEIGHT,
    BUFFER_HEIGHT,
    new UIInteractable[]{
      new UIButton("Host Game"   , () -> ui.setMode(UIState.HOST_SETUP)  ),
      new UIButton("Join Game"   , () -> ui.setMode(UIState.CLIENT_SETUP)),
      new UIButton("Back"        , ui::back                              ),
    },
    new boolean[]{false, false, true, false}
    );
    
    ElemInfo portErr = new ElemInfo(
    new Vector2(0.38, 0.395),
    new Vector2(0.62, 0.605), 
    BUFFER_HEIGHT, 
    new boolean[]{false, false, false, false},
    "Port must be a number",
    "less than 65536"
    );
    
    ElemInfo ipErr = new ElemInfo(
    new Vector2(0.38, 0.42125),
    new Vector2(0.62, 0.57875), 
    BUFFER_HEIGHT, 
    new boolean[]{false, false, false, false},
    "Address required"
    );
    
    ElemInfo connectErr = new ElemInfo(
    new Vector2(0.34, 0.395),
    new Vector2(0.66, 0.605),  
    BUFFER_HEIGHT, 
    new boolean[]{false, false, false, false},
    "Connection failed!",
    "Please check details are correct"
    );
    
    UITextfield hostport = new UITextfield("Port Number", 5, 1, null, ui){
      public boolean isValid() {
        try {return totind > 0 && Integer.parseInt(getText()) >= 0 && Integer.parseInt(getText()) <= 0xFFFF;} 
        catch (NumberFormatException e) {return false;}
      }
    };
    
    UIElement hostSetup = new ElemList(
    new Vector2(0.38, 0.28),
    new Vector2(0.62, 0.475),
    BUTTON_HEIGHT,
    BUFFER_HEIGHT,
    new UIInteractable[]{
      hostport,
      new UIButton("Host New Game", () -> {
        if (hostport.isValid()) {
          c.hostGame(Integer.parseInt(hostport.getText()));
          connectErr.transIn();
        } else portErr.transIn();
      }),
    },
    new boolean[]{false, false, true, false}
    );
    
    UITextfield ipaddr = new UITextfield("Server Address", 15, 1, null, ui){public boolean isValid() {return totind > 0;}};
    
    UITextfield joinport = new UITextfield("Port Number", 5, 1, null, ui){
      public boolean isValid() {
        try {return totind > 0 && Integer.parseInt(getText()) >= 0 && Integer.parseInt(getText()) <= 0xFFFF;} 
        catch (NumberFormatException e) {return false;}
      }
    };
    
    UIElement clientSetup = new ElemList(
    new Vector2(0.38, 0.28),
    new Vector2(0.62, 0.565),
    BUTTON_HEIGHT,
    BUFFER_HEIGHT,
    new UIInteractable[]{
      ipaddr,
      joinport,
      new UIButton("Connect!", () -> {
        if (ipaddr.isValid()) {
          if (joinport.isValid()) {
            c.joinGame(ipaddr.getText(), Integer.parseInt(joinport.getText()));
            connectErr.transIn();
          } else portErr.transIn();
        } else ipErr.transIn();
      }),
    },
    new boolean[]{false, false, true, false}
    );
    
    UIElement options = new ElemList(
    new Vector2(0, 0.28),
    new Vector2(0.24, 0.655),
    BUTTON_HEIGHT,
    BUFFER_HEIGHT,
    new UIInteractable[]{
      new UIButton("Video"   , () -> ui.setMode(UIState.VIDEO)   ),
      new UIButton("Audio"   , () -> ui.setMode(UIState.AUDIO)   ),
      new UIButton("Gameplay", () -> ui.setMode(UIState.GAMEPLAY)),
      new UIButton("Back"    , ui::back                          ),
    },
    new boolean[]{false, false, true, false}
    );
    
    UIElement optvid = new ElemList(
    new Vector2(0.38, 0.28),
    new Vector2(0.62, 0.655),
    BUTTON_HEIGHT,
    BUFFER_HEIGHT,
    new UIInteractable[]{
      new UIButton("Test1", null),
      new UIButton("Test2", null),
      new UIButton("Test3", null),
      new UIButton("Test4", null),
    },
    new boolean[]{false, false, true, false}
    );
    
    UIElement optaud = new ElemList(
    new Vector2(0.38, 0.28),
    new Vector2(0.62, 0.88),
    BUTTON_HEIGHT,
    BUFFER_HEIGHT,
    new UIInteractable[]{
      new UISlider("Master: %d"   , () -> c.globalSettings.getSetting    ("soundMaster"), (v) -> c.globalSettings.setSetting    ("soundMaster", v), 0, 100),
      new UISlider("Sound FX: %d" , () -> c.globalSettings.getSetting    ("soundFX")    , (v) -> c.globalSettings.setSetting    ("soundFX"    , v), 0, 100),
      new UISlider("Music: %d"    , () -> c.globalSettings.getSetting    ("soundMusic") , (v) -> c.globalSettings.setSetting    ("soundMusic" , v), 0, 100),
      new UIToggle("Subtitles"    , () -> c.globalSettings.getBoolSetting("subtitles")  , (v) -> c.globalSettings.setBoolSetting("subtitles"  , v)),
    },
    new boolean[]{false, false, true, false}
    );
    
    mainMenu.addElement(title);
    mainMenu.addElement(outPanel);
    mainMenu.addElement(playModes);
    mainMenu.addElement(hostSetup);
    mainMenu.addElement(clientSetup);
    mainMenu.addElement(options);
    mainMenu.addElement(optvid);
    mainMenu.addElement(optaud);
    mainMenu.addElement(portErr);
    mainMenu.addElement(ipErr);
    mainMenu.addElement(connectErr);
    mainMenu.addMode(UIState.DEFAULT, title);
    mainMenu.addMode(UIState.DEFAULT, outPanel);
    mainMenu.addMode(UIState.NEW_GAME, title, UIState.DEFAULT);
    mainMenu.addMode(UIState.NEW_GAME, playModes);
    mainMenu.addMode(UIState.HOST_SETUP, title, UIState.NEW_GAME);
    mainMenu.addMode(UIState.HOST_SETUP, playModes);
    mainMenu.addMode(UIState.HOST_SETUP, hostSetup);
    mainMenu.addMode(UIState.CLIENT_SETUP, title, UIState.NEW_GAME);
    mainMenu.addMode(UIState.CLIENT_SETUP, playModes);
    mainMenu.addMode(UIState.CLIENT_SETUP, clientSetup);
    mainMenu.addMode(UIState.OPTIONS, title, UIState.DEFAULT);
    mainMenu.addMode(UIState.OPTIONS, options);
    mainMenu.addMode(UIState.VIDEO, title, UIState.OPTIONS);
    mainMenu.addMode(UIState.VIDEO, options);
    mainMenu.addMode(UIState.VIDEO, optvid);
    mainMenu.addMode(UIState.AUDIO, title, UIState.OPTIONS);
    mainMenu.addMode(UIState.AUDIO, options);
    mainMenu.addMode(UIState.AUDIO, optaud);
    
    mainMenu.clear();
    
    return mainMenu;
  }
  
  /**
  * Creates the HUD for use during gameplay.
  */
  public static UIPane createHUD(Core c, UIController ui) {
    UIPane HUD = new UIPane();
    
    UIElement greyed = new UIElement(
    new Vector2(0,0),
    new Vector2(1, 1),
    new boolean[]{false, false, false, false}
    ){
      protected void init() {this.backgroundColour = UIColours.SCREEN_TINT;}
      protected void draw(Graphics2D g, int screenSizeY, Vector2 tL, Vector2 bR, Color[] c, UIInteractable highlighted) {}
    };
    
    UIElement outPause = new ElemList(
    new Vector2(0.38, 0.2225),
    new Vector2(0.62, 0.7775),
    BUTTON_HEIGHT,
    BUFFER_HEIGHT,
    new UIInteractable[]{
      new UIButton("Resume"         , ui::back                         ),
      new UIButton("Save Game"      , null                             ),
      new UIButton("Load Game"      , null                             ),
      new UIButton("Options"        , () -> ui.setMode(UIState.OPTIONS)),
      new UIButton("Quit to Title"  , c::toMenu                        ),
      new UIButton("Quit to Desktop", c::quitToDesk                    ),
    },
    new boolean[]{false, true, true, true}
    );
    
    UIElement options = new ElemList(
    new Vector2(0.38, 0.3125),
    new Vector2(0.62, 0.6875),
    BUTTON_HEIGHT,
    BUFFER_HEIGHT,
    new UIInteractable[]{
      new UIButton("Video"   , () -> ui.setMode(UIState.VIDEO)   ),
      new UIButton("Audio"   , () -> ui.setMode(UIState.AUDIO)   ),
      new UIButton("Gameplay", () -> ui.setMode(UIState.GAMEPLAY)),
      new UIButton("Back"    , ui::back                          ),
    },
    new boolean[]{false, true, true, true}
    );
    
    UIElement optvid = new ElemList(
    new Vector2(0.38, 0.3125),
    new Vector2(0.62, 0.6875),
    BUTTON_HEIGHT,
    BUFFER_HEIGHT,
    new UIInteractable[]{
      new UIButton("AH"          , null    ),
      new UIButton("MAKE IT STOP", null    ),
      new UIButton("PLEASE"      , null    ),
      new UIButton("Back"        , ui::back),
    },
    new boolean[]{false, true, true, true}
    );
    
    UIElement optaud = new ElemList(
    new Vector2(0.38, 0.155),
    new Vector2(0.62, 0.845),
    BUTTON_HEIGHT,
    BUFFER_HEIGHT,
    new UIInteractable[]{
      new UISlider("Master: %d"   , () -> c.globalSettings.getSetting    ("soundMaster"), (v) -> c.globalSettings.setSetting    ("soundMaster", v), 0, 100),
      new UISlider("Sound FX: %d" , () -> c.globalSettings.getSetting    ("soundFX")    , (v) -> c.globalSettings.setSetting    ("soundFX"    , v), 0, 100),
      new UISlider("Music: %d"    , () -> c.globalSettings.getSetting    ("soundMusic") , (v) -> c.globalSettings.setSetting    ("soundMusic" , v), 0, 100),
      new UIToggle("Subtitles"    , () -> c.globalSettings.getBoolSetting("subtitles")  , (v) -> c.globalSettings.setBoolSetting("subtitles"  , v)),
      new UIButton("Back"         , ui::back),
    },
    new boolean[]{false, true, true, true}
    );
    // HUD.addElement(health);
    HUD.addElement(greyed);
    HUD.addElement(outPause);
    HUD.addElement(options);
    HUD.addElement(optvid);
    HUD.addElement(optaud);
    // HUD.addMode(UIState.DEFAULT, health);
    HUD.setModeParent(UIState.DEFAULT, UIState.PAUSED);
    HUD.addMode(UIState.PAUSED, greyed, UIState.DEFAULT);
    HUD.addMode(UIState.PAUSED, outPause);
    HUD.addMode(UIState.OPTIONS, greyed, UIState.PAUSED);
    HUD.addMode(UIState.OPTIONS, options);
    HUD.addMode(UIState.VIDEO, greyed, UIState.OPTIONS);
    HUD.addMode(UIState.VIDEO, optvid);
    HUD.addMode(UIState.AUDIO, greyed, UIState.OPTIONS);
    HUD.addMode(UIState.AUDIO, optaud);
    
    HUD.clear();
    
    return HUD;
  }
}
