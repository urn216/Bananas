package code.core;

import code.error.ConnectionException;

import code.math.Vector2;

import code.server.Server;

import code.ui.UIAction;
import code.ui.UIColours;
import code.ui.UIController;
import code.ui.UIHelp;
import code.ui.UIPane;
import code.ui.UIState;
import code.ui.components.UIComponent;
import code.ui.components.UIInteractable;
import code.ui.components.UIText;
import code.ui.components.interactables.*;
import code.ui.elements.*;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

class UICreator {
  // private static final UIElement VIRTUAL_KEYBOARD = new ElemKeyboard();
  
  private static final double COMPON_HEIGHT = 0.075;
  private static final double BUFFER_HEIGHT = 0.015;
  
  private static final ElemConfirmation settingsChanged = new ElemConfirmation(
  new Vector2(0.35, 0.5-UIHelp.calculateListHeight(BUFFER_HEIGHT, COMPON_HEIGHT/2, COMPON_HEIGHT)/2),
  new Vector2(0.65, 0.5+UIHelp.calculateListHeight(BUFFER_HEIGHT, COMPON_HEIGHT/2, COMPON_HEIGHT)/2), 
  BUFFER_HEIGHT, 
  new boolean[]{false, false, false, false}, 
  () -> {Core.GLOBAL_SETTINGS.saveChanges();   UIController.retState();},
  () -> {Core.GLOBAL_SETTINGS.revertChanges(); UIController.retState();},
  "Save Changes?"
  );
  
  /**
  * A lambda function which, in place of transitioning back a step,
  * checks if the global settings have been changed and if so, 
  * brings up a confirmation dialogue to handle the changes before transitioning back.
  */
  public static final UIAction checkSettings = () -> {
    if (Core.GLOBAL_SETTINGS.hasChanged()) UIController.displayTempElement(settingsChanged);
    else UIController.retState();
  };
  
  /**
  * Creates the UI pane for the main menu.
  */
  public static UIPane createMain() {
    UIPane mainMenu = new UIPane();
    
    UIElement title = new UIElement(
    new Vector2(0   , 0),
    new Vector2(0.28, 0.14),
    new boolean[]{true, false, true, false}
    ){
      protected void init() {components = new UIComponent[]{new UIText("Bananas", 0.6, Font.BOLD)};}
      protected void draw(Graphics2D g, int screenSizeY, Vector2 tL, Vector2 bR, Color[] c, UIInteractable highlighted) {
        components[0].draw(g, (float)tL.x, (float)tL.y, (float)(bR.x-tL.x), (float)(bR.y-tL.y), c[UIColours.TEXT]);
      }
    };
    
    UIElement outPanel = new ElemList(
    new Vector2(0   , 0.28),
    new Vector2(0.24, 0.28+UIHelp.calculateListHeightDefault(3, BUFFER_HEIGHT, COMPON_HEIGHT)),
    COMPON_HEIGHT,
    BUFFER_HEIGHT,
    new UIInteractable[]{
      new UIButton("Play"           , () -> UIController.setState(UIState.NEW_GAME)),
      new UIButton("Options"        , () -> UIController.setState(UIState.OPTIONS) ),
      new UIButton("Quit to Desktop", Core::quitToDesk                            ),
    },
    new boolean[]{false, false, true, false}
    );
    
    UIElement playModes = new ElemList(
    new Vector2(0   , 0.28),
    new Vector2(0.24, 0.28+UIHelp.calculateListHeightDefault(3, BUFFER_HEIGHT, COMPON_HEIGHT)),
    COMPON_HEIGHT,
    BUFFER_HEIGHT,
    new UIInteractable[]{
      new UIButton("Host Game", () -> UIController.setState(UIState.SETUP_HOST)  ),
      new UIButton("Join Game", () -> UIController.setState(UIState.SETUP_CLIENT)),
      new UIButton("Back"     , UIController::back                              ),
    },
    new boolean[]{false, false, true, false}
    );
    
    UIElement hostSetup = hostSetup();
    
    UIElement clientSetup = clientSetup();
    
    UIElement options = new ElemList(
    new Vector2(0   , 0.28),
    new Vector2(0.24, 0.28+UIHelp.calculateListHeightDefault(5, BUFFER_HEIGHT, COMPON_HEIGHT)),
    COMPON_HEIGHT,
    BUFFER_HEIGHT,
    new UIInteractable[]{
      new UITextfield("Nickname"  , 16, 1, (s) -> Core.GLOBAL_SETTINGS.setStringSetting("nickname", s), () -> Core.GLOBAL_SETTINGS.getStringSetting("nickname")),
      new UIToggle   ("Fullscreen", Core.WINDOW::isFullScreen, (b) -> {Core.GLOBAL_SETTINGS.setBoolSetting("fullScreen", b); Core.WINDOW.setFullscreen(b);}    ),
      new UIButton   ("Audio"     , () -> UIController.setState(UIState.AUDIO)                                                                                  ),
      new UIButton   ("Gameplay"  , () -> UIController.setState(UIState.GAMEPLAY)                                                                               ),
      new UIButton   ("Back"      , UIController::back                                                                                                         ),
    },
    new boolean[]{false, false, true, false}
    );
    
    UIElement optaud = new ElemList(
    new Vector2(0.38, 0.28),
    new Vector2(0.62, 0.28+UIHelp.calculateListHeight(BUFFER_HEIGHT, COMPON_HEIGHT*2, COMPON_HEIGHT*2, COMPON_HEIGHT*2, COMPON_HEIGHT)),
    COMPON_HEIGHT,
    BUFFER_HEIGHT,
    new UIInteractable[]{
      new UISlider("Master: %d"  , () -> Core.GLOBAL_SETTINGS.getIntSetting ("soundMaster"), (v) -> Core.GLOBAL_SETTINGS.setIntSetting ("soundMaster", v), 0, 100),
      new UISlider("Sound FX: %d", () -> Core.GLOBAL_SETTINGS.getIntSetting ("soundFX")    , (v) -> Core.GLOBAL_SETTINGS.setIntSetting ("soundFX"    , v), 0, 100),
      new UISlider("Music: %d"   , () -> Core.GLOBAL_SETTINGS.getIntSetting ("soundMusic") , (v) -> Core.GLOBAL_SETTINGS.setIntSetting ("soundMusic" , v), 0, 100),
      new UIToggle("Subtitles"   , () -> Core.GLOBAL_SETTINGS.getBoolSetting("subtitles")  , (v) -> Core.GLOBAL_SETTINGS.setBoolSetting("subtitles"  , v)        ),
    },
    new boolean[]{false, false, true, false}
    );
    
    UIElement optgme = new ElemList(
    new Vector2(0.38, 0.28),
    new Vector2(0.62, 0.28+UIHelp.calculateListHeight(BUFFER_HEIGHT, COMPON_HEIGHT*2)),
    COMPON_HEIGHT,
    BUFFER_HEIGHT,
    new UIInteractable[]{
      new UISlider("Scroll Sensitivity: %d", () -> Core.GLOBAL_SETTINGS.getIntSetting("scrollSensitivity"), (v) -> {Controls.updateScrollSensitivity(v);Core.GLOBAL_SETTINGS.setIntSetting("scrollSensitivity", v);}, 1, 10),
    },
    new boolean[]{false, false, true, false}
    );
    
    UIComponent[] lobbyList = new UIComponent[Server.MAX_PLAYERS];
    
    for (int i = 0; i < lobbyList.length; i++) {
      final int ind = i;
      lobbyList[ind] = new UIInteractable() {
        private byte[] player = null;
        
        private UIToggle toggle = new UIToggle(
        "", 
        () -> {return player == null ? false : player[1]==49;}, 
        b  -> {if (player != null && Client.getPlayerNum() == player[0]-48) Client.sendReadyToggle();}
        );
        
        public void setIn() {
          in = true; 
          if (player != null && Client.getPlayerNum() == player[0]-48) toggle.setIn();
        }
        
        public void setOut() {in = false; toggle.setOut();}
        
        public void primeAct() {toggle.primeAct();}
        
        public void draw(Graphics2D g, Color... colours) {
          player = Client.getPlayer(ind);
          if (player == null) return;
          toggle.setText(new String(player, 2, player.length-2));
          toggle.draw(g, x, y, width, height, Client.getPlayerNum() == player[0]-48 ? colours[0] : colours[2], colours[1], colours[2], colours[3], colours[4]);
        }
      };
    }
    
    double lobbyListHeight = UIHelp.calculateListHeight(0, UIHelp.calculateComponentHeights(COMPON_HEIGHT, lobbyList));
    
    UIElement lobbyClientList = new ElemList(
    new Vector2(0.38, 0.5-lobbyListHeight/2),
    new Vector2(0.62, 0.5+lobbyListHeight/2),
    COMPON_HEIGHT,
    0,
    lobbyList,
    new boolean[]{true, true, false, false}
    );
    
    UIElement lobbyHostStart = new ElemList(
    new Vector2(0.38, 1-UIHelp.calculateListHeightDefault(1, BUFFER_HEIGHT, COMPON_HEIGHT)),
    new Vector2(0.62, 1),
    COMPON_HEIGHT,
    BUFFER_HEIGHT,
    new UIInteractable[]{
      new UIButton("Start", Server::beginMatch)
    },
    new boolean[]{false, true, false, false}
    );
    
    ((UIButton)(lobbyHostStart.getComponents()[0])).setLockCheck(() -> !Server.allReady());
    
    mainMenu.addState(UIState.DEFAULT     , title          );
    mainMenu.addState(UIState.DEFAULT     , outPanel       );
    mainMenu.addState(UIState.NEW_GAME    , title           , UIState.DEFAULT );
    mainMenu.addState(UIState.NEW_GAME    , playModes      );
    mainMenu.addState(UIState.SETUP_HOST  , title           , UIState.NEW_GAME);
    mainMenu.addState(UIState.SETUP_HOST  , playModes      );
    mainMenu.addState(UIState.SETUP_HOST  , hostSetup      );
    mainMenu.addState(UIState.SETUP_CLIENT, title           , UIState.NEW_GAME);
    mainMenu.addState(UIState.SETUP_CLIENT, playModes      );
    mainMenu.addState(UIState.SETUP_CLIENT, clientSetup    );
    mainMenu.addState(UIState.OPTIONS     , title           , UIState.DEFAULT  , checkSettings);
    mainMenu.addState(UIState.OPTIONS     , options        );
    mainMenu.addState(UIState.AUDIO       , title           , UIState.OPTIONS  , checkSettings);
    mainMenu.addState(UIState.AUDIO       , options        );
    mainMenu.addState(UIState.AUDIO       , optaud         );
    mainMenu.addState(UIState.GAMEPLAY    , title           , UIState.OPTIONS  , checkSettings);
    mainMenu.addState(UIState.GAMEPLAY    , options        );
    mainMenu.addState(UIState.GAMEPLAY    , optgme         );
    mainMenu.addState(UIState.LOBBY       , lobbyClientList , UIState.DEFAULT  , Core::toMenu );
    mainMenu.addState(UIState.LOBBY_HOST  , lobbyClientList , UIState.DEFAULT  , Core::toMenu );
    mainMenu.addState(UIState.LOBBY_HOST  , lobbyHostStart );
    
    mainMenu.clear();
    
    return mainMenu;
  }
  
  /**
  * Creates the HUD for use during gameplay.
  */
  public static UIPane createHUD() {
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
    new Vector2(0.38, 0.5-UIHelp.calculateListHeightDefault(4, BUFFER_HEIGHT, COMPON_HEIGHT)/2),
    new Vector2(0.62, 0.5+UIHelp.calculateListHeightDefault(4, BUFFER_HEIGHT, COMPON_HEIGHT)/2),
    COMPON_HEIGHT,
    BUFFER_HEIGHT,
    new UIInteractable[]{
      new UIButton("Resume"         , UIController::back                         ),
      new UIButton("Options"        , () -> UIController.setState(UIState.OPTIONS)),
      new UIButton("Quit to Title"  , Core::toMenu                               ),
      new UIButton("Quit to Desktop", Core::quitToDesk                           ),
    },
    new boolean[]{false, true, true, true}
    );
    
    UIElement options = new ElemList(
    new Vector2(0.38, 0.5-UIHelp.calculateListHeightDefault(4, BUFFER_HEIGHT, COMPON_HEIGHT)/2),
    new Vector2(0.62, 0.5+UIHelp.calculateListHeightDefault(4, BUFFER_HEIGHT, COMPON_HEIGHT)/2),
    COMPON_HEIGHT,
    BUFFER_HEIGHT,
    new UIInteractable[]{
      new UIToggle("Fullscreen", Core.WINDOW::isFullScreen, (b) -> {Core.GLOBAL_SETTINGS.setBoolSetting("fullScreen", b); Core.WINDOW.setFullscreen(b);}),
      new UIButton("Audio"     , () -> UIController.setState(UIState.AUDIO)                                                                              ),
      new UIButton("Gameplay"  , () -> UIController.setState(UIState.GAMEPLAY)                                                                           ),
      new UIButton("Back"      , UIController::back                                                                                                     ),
    },
    new boolean[]{false, true, true, true}
    );
    
    UIElement optaud = new ElemList(
    new Vector2(0.38, 0.5-UIHelp.calculateListHeight(BUFFER_HEIGHT, COMPON_HEIGHT*2, COMPON_HEIGHT*2, COMPON_HEIGHT*2, COMPON_HEIGHT, COMPON_HEIGHT)/2),
    new Vector2(0.62, 0.5+UIHelp.calculateListHeight(BUFFER_HEIGHT, COMPON_HEIGHT*2, COMPON_HEIGHT*2, COMPON_HEIGHT*2, COMPON_HEIGHT, COMPON_HEIGHT)/2),
    COMPON_HEIGHT,
    BUFFER_HEIGHT,
    new UIInteractable[]{
      new UISlider("Master: %d"  , () -> Core.GLOBAL_SETTINGS.getIntSetting ("soundMaster"), (v) -> Core.GLOBAL_SETTINGS.setIntSetting ("soundMaster", v), 0, 100),
      new UISlider("Sound FX: %d", () -> Core.GLOBAL_SETTINGS.getIntSetting ("soundFX")    , (v) -> Core.GLOBAL_SETTINGS.setIntSetting ("soundFX"    , v), 0, 100),
      new UISlider("Music: %d"   , () -> Core.GLOBAL_SETTINGS.getIntSetting ("soundMusic") , (v) -> Core.GLOBAL_SETTINGS.setIntSetting ("soundMusic" , v), 0, 100),
      new UIToggle("Subtitles"   , () -> Core.GLOBAL_SETTINGS.getBoolSetting("subtitles")  , (v) -> Core.GLOBAL_SETTINGS.setBoolSetting("subtitles"  , v)        ),
      new UIButton("Back"        , UIController::back                                                                                                            ),
    },
    new boolean[]{false, true, true, true}
    );
    
    UIElement optgme = new ElemList(
    new Vector2(0.38, 0.5-UIHelp.calculateListHeight(BUFFER_HEIGHT, COMPON_HEIGHT*2, COMPON_HEIGHT)/2),
    new Vector2(0.62, 0.5+UIHelp.calculateListHeight(BUFFER_HEIGHT, COMPON_HEIGHT*2, COMPON_HEIGHT)/2),
    COMPON_HEIGHT,
    BUFFER_HEIGHT,
    new UIInteractable[]{
      new UISlider("Scroll Sensitivity: %d", () -> Core.GLOBAL_SETTINGS.getIntSetting("scrollSensitivity"), (v) -> {Controls.updateScrollSensitivity(v);Core.GLOBAL_SETTINGS.setIntSetting("scrollSensitivity", v);}, 1, 10),
      new UIButton("Back"                  , UIController::back),
    },
    new boolean[]{false, true, true, true}
    );
    
    HUD.setModeParent(UIState.DEFAULT, UIState.PAUSED);
    
    HUD.addState(UIState.PAUSED  , greyed   , UIState.DEFAULT);
    HUD.addState(UIState.PAUSED  , outPause);
    HUD.addState(UIState.OPTIONS , greyed   , UIState.PAUSED  , checkSettings);
    HUD.addState(UIState.OPTIONS , options );
    HUD.addState(UIState.AUDIO   , greyed   , UIState.OPTIONS , checkSettings);
    HUD.addState(UIState.AUDIO   , optaud  );
    HUD.addState(UIState.GAMEPLAY, greyed   , UIState.OPTIONS , checkSettings);
    HUD.addState(UIState.GAMEPLAY, optgme  );
    
    HUD.clear();
    
    return HUD;
  }
  
  //-------------------------------------------------------------------------------------
  //                            CLIENT-SERVER CONNECTION
  //-------------------------------------------------------------------------------------
  
  private static UIElement hostSetup() {
    UITextfield hostport = new UITextfield("Port Number", 5, 1){
      public boolean isValid() {
        try {return totind > 0 && Integer.parseInt(getText()) > 1023 && Integer.parseInt(getText()) <= 0xFFFF;} 
        catch (NumberFormatException e) {return false;}
      }
    };
    
    return new ElemList(
    new Vector2(0.38, 0.28),
    new Vector2(0.62, 0.28+UIHelp.calculateListHeightDefault(2, BUFFER_HEIGHT, COMPON_HEIGHT)),
    COMPON_HEIGHT,
    BUFFER_HEIGHT,
    new UIInteractable[]{
      hostport,
      new UIButton("Host New Game", () -> {
        if (!hostport.isValid()) { UIController.displayWarning(BUFFER_HEIGHT, COMPON_HEIGHT, "Port must be a number", "between 1024 and 65535"); return;}
        
        try {
          Core.hostGame(Integer.parseInt(hostport.getText()));
          if (Client.isConnected()) UIController.setState(UIState.LOBBY_HOST);
        } catch (ConnectionException e) {UIController.displayWarning(BUFFER_HEIGHT, COMPON_HEIGHT, "Server Creation Failed");}
      }),
    },
    new boolean[]{false, false, true, false}
    );
  }
  
  private static UIElement clientSetup() {
    UITextfield ipaddr = new UITextfield("Server Address", 15, 1){public boolean isValid() {return totind > 0;}};
    
    UITextfield joinport = new UITextfield("Port Number", 5, 1){
      public boolean isValid() {
        try {return totind > 0 && Integer.parseInt(getText()) > 1023 && Integer.parseInt(getText()) <= 0xFFFF;} 
        catch (NumberFormatException e) {return false;}
      }
    };

    ElemList connecting = new ElemList(
    new Vector2(0.33, 0.5-UIHelp.calculateListHeight(BUFFER_HEIGHT, COMPON_HEIGHT/2, COMPON_HEIGHT)/2), 
    new Vector2(0.67, 0.5+UIHelp.calculateListHeight(BUFFER_HEIGHT, COMPON_HEIGHT/2, COMPON_HEIGHT)/2), 
    COMPON_HEIGHT,
    BUFFER_HEIGHT, 
    new UIComponent[]{
      new UIText("Attempting to Connect to Server...", 1, Font.PLAIN),
      null
    },
    new boolean[]{false, false, false, false}
    ) {protected void init() {solidBacking = true;}};
    
    return new ElemList(
    new Vector2(0.38, 0.28),
    new Vector2(0.62, 0.28+UIHelp.calculateListHeightDefault(3, BUFFER_HEIGHT, COMPON_HEIGHT)),
    COMPON_HEIGHT,
    BUFFER_HEIGHT,
    new UIInteractable[]{
      ipaddr,
      joinport,
      new UIButton("Connect!", () -> {
        if (!ipaddr.isValid()) {UIController.displayWarning(BUFFER_HEIGHT, COMPON_HEIGHT, "Address required"); return;}
        if (!joinport.isValid()) {UIController.displayWarning(BUFFER_HEIGHT, COMPON_HEIGHT, "Port must be a number", "between 1024 and 65535"); return;}
        
        Thread join = new Thread(() -> {
          try {
            Core.joinGame(ipaddr.getText(), Integer.parseInt(joinport.getText()));
            if (Client.isConnected()) {UIController.setState(UIState.LOBBY); UIController.clearTempElement();}
          } catch (ConnectionException e) {UIController.displayWarning(BUFFER_HEIGHT, COMPON_HEIGHT, "Connection failed!", "Please check details are correct");}
        });
        
        join.start();
        
        connecting.getComponents()[1] = new UIButton("Cancel", () -> {Client.disconnect(); connecting.transOut(UIController.DEFAULT_ANIMATION_TIME_MILLIS);});
        
        UIController.displayTempElement(connecting);
      }),
    },
    new boolean[]{false, false, true, false}
    );
  }
}
