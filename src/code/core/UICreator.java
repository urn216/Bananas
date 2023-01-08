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
  () -> {Core.globalSettings.saveChanges();   UIController.retMode();},
  () -> {Core.globalSettings.revertChanges(); UIController.retMode();},
  "Save Changes?"
  );
  
  /**
   * A lambda function which, in place of transitioning back a step,
   * checks if the global settings have been changed and if so, 
   * brings up a confirmation dialogue to handle the changes before transitioning back.
   */
  public static final UIAction checkSettings = () -> {
    if (Core.globalSettings.hasChanged()) settingsChanged.transIn();
    else UIController.retMode();
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
      new UIButton("Play"           , () -> UIController.setMode(UIState.NEW_GAME)),
      new UIButton("Options"        , () -> UIController.setMode(UIState.OPTIONS) ),
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
      new UIButton("Host Game"   , () -> UIController.setMode(UIState.SETUP_HOST)  ),
      new UIButton("Join Game"   , () -> UIController.setMode(UIState.SETUP_CLIENT)),
      new UIButton("Back"        , UIController::back                              ),
    },
    new boolean[]{false, false, true, false}
    );
    
    ElemInfo portErr = new ElemInfo(
    new Vector2(0.38, 0.5-UIHelp.calculateListHeight(BUFFER_HEIGHT, COMPON_HEIGHT/2, COMPON_HEIGHT/2, COMPON_HEIGHT)/2),
    new Vector2(0.62, 0.5+UIHelp.calculateListHeight(BUFFER_HEIGHT, COMPON_HEIGHT/2, COMPON_HEIGHT/2, COMPON_HEIGHT)/2), 
    BUFFER_HEIGHT, 
    new boolean[]{false, false, false, false},
    "Port must be a number",
    "less than 65536"
    );
    
    ElemInfo ipErr = new ElemInfo(
    new Vector2(0.38, 0.5-UIHelp.calculateListHeight(BUFFER_HEIGHT, COMPON_HEIGHT/2, COMPON_HEIGHT)/2),
    new Vector2(0.62, 0.5+UIHelp.calculateListHeight(BUFFER_HEIGHT, COMPON_HEIGHT/2, COMPON_HEIGHT)/2), 
    BUFFER_HEIGHT, 
    new boolean[]{false, false, false, false},
    "Address required"
    );
    
    ElemInfo connectErr = new ElemInfo(
    new Vector2(0.34, 0.5-UIHelp.calculateListHeight(BUFFER_HEIGHT, COMPON_HEIGHT/2, COMPON_HEIGHT/2, COMPON_HEIGHT)/2), 
    new Vector2(0.66, 0.5+UIHelp.calculateListHeight(BUFFER_HEIGHT, COMPON_HEIGHT/2, COMPON_HEIGHT/2, COMPON_HEIGHT)/2), 
    BUFFER_HEIGHT, 
    new boolean[]{false, false, false, false},
    "Connection failed!",
    "Please check details are correct"
    );
    
    UITextfield hostport = new UITextfield("Port Number", 5, 1){
      public boolean isValid() {
        try {return totind > 0 && Integer.parseInt(getText()) > 0 && Integer.parseInt(getText()) <= 0xFFFF;} 
        catch (NumberFormatException e) {return false;}
      }
    };
    
    UIElement hostSetup = new ElemList(
    new Vector2(0.38, 0.28),
    new Vector2(0.62, 0.28+UIHelp.calculateListHeightDefault(2, BUFFER_HEIGHT, COMPON_HEIGHT)),
    COMPON_HEIGHT,
    BUFFER_HEIGHT,
    new UIInteractable[]{
      hostport,
      new UIButton("Host New Game", () -> {
        if (!hostport.isValid()) { portErr.transIn(); return;}
        
        try {
          Core.hostGame(Integer.parseInt(hostport.getText()));
          UIController.setMode(UIState.LOBBY_HOST);
        } catch (ConnectionException e) {connectErr.transIn();}
      }),
    },
    new boolean[]{false, false, true, false}
    );
    
    UITextfield ipaddr = new UITextfield("Server Address", 15, 1){public boolean isValid() {return totind > 0;}};
    
    UITextfield joinport = new UITextfield("Port Number", 5, 1){
      public boolean isValid() {
        try {return totind > 0 && Integer.parseInt(getText()) > 0 && Integer.parseInt(getText()) <= 0xFFFF;} 
        catch (NumberFormatException e) {return false;}
      }
    };
    
    UIElement clientSetup = new ElemList(
    new Vector2(0.38, 0.28),
    new Vector2(0.62, 0.28+UIHelp.calculateListHeightDefault(3, BUFFER_HEIGHT, COMPON_HEIGHT)),
    COMPON_HEIGHT,
    BUFFER_HEIGHT,
    new UIInteractable[]{
      ipaddr,
      joinport,
      new UIButton("Connect!", () -> {
        if (!ipaddr.isValid()) {ipErr.transIn(); return;}
        if (!joinport.isValid()) {portErr.transIn(); return;}
        
        try {
          Core.joinGame(ipaddr.getText(), Integer.parseInt(joinport.getText()));
          UIController.setMode(UIState.LOBBY);
        } catch (ConnectionException e) {connectErr.transIn();}
      }),
    },
    new boolean[]{false, false, true, false}
    );
    
    UIElement options = new ElemList(
    new Vector2(0   , 0.28),
    new Vector2(0.24, 0.28+UIHelp.calculateListHeightDefault(5, BUFFER_HEIGHT, COMPON_HEIGHT)),
    COMPON_HEIGHT,
    BUFFER_HEIGHT,
    new UIInteractable[]{
      new UITextfield("Nickname"   , 16, 1, Core.globalSettings::setNickname, Core.globalSettings::getNickname                               ),
      new UIToggle   ("Fullscreen", Core::isFullScreen, (b) -> {Core.globalSettings.setBoolSetting("fullScreen", b); Core.setFullscreen(b);}),
      new UIButton   ("Audio"      , () -> UIController.setMode(UIState.AUDIO)                                                               ),
      new UIButton   ("Gameplay"   , () -> UIController.setMode(UIState.GAMEPLAY)                                                            ),
      new UIButton   ("Back"       , UIController::back                                                                                      ),
    },
    new boolean[]{false, false, true, false}
    );
    
    UIElement optaud = new ElemList(
    new Vector2(0.38, 0.28),
    new Vector2(0.62, 0.28+UIHelp.calculateListHeight(BUFFER_HEIGHT, COMPON_HEIGHT*2, COMPON_HEIGHT*2, COMPON_HEIGHT*2, COMPON_HEIGHT)),
    COMPON_HEIGHT,
    BUFFER_HEIGHT,
    new UIInteractable[]{
      new UISlider("Master: %d"   , () -> Core.globalSettings.getSetting    ("soundMaster"), (v) -> Core.globalSettings.setSetting    ("soundMaster", v), 0, 100),
      new UISlider("Sound FX: %d" , () -> Core.globalSettings.getSetting    ("soundFX")    , (v) -> Core.globalSettings.setSetting    ("soundFX"    , v), 0, 100),
      new UISlider("Music: %d"    , () -> Core.globalSettings.getSetting    ("soundMusic") , (v) -> Core.globalSettings.setSetting    ("soundMusic" , v), 0, 100),
      new UIToggle("Subtitles"    , () -> Core.globalSettings.getBoolSetting("subtitles")  , (v) -> Core.globalSettings.setBoolSetting("subtitles"  , v)        ),
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
    
    mainMenu.addMode(UIState.DEFAULT, title);
    mainMenu.addMode(UIState.DEFAULT, outPanel);
    mainMenu.addMode(UIState.NEW_GAME, title, UIState.DEFAULT);
    mainMenu.addMode(UIState.NEW_GAME, playModes);
    mainMenu.addMode(UIState.SETUP_HOST, title, UIState.NEW_GAME);
    mainMenu.addMode(UIState.SETUP_HOST, playModes);
    mainMenu.addMode(UIState.SETUP_HOST, hostSetup);
    mainMenu.addMode(UIState.SETUP_CLIENT, title, UIState.NEW_GAME);
    mainMenu.addMode(UIState.SETUP_CLIENT, playModes);
    mainMenu.addMode(UIState.SETUP_CLIENT, clientSetup);
    mainMenu.addMode(UIState.OPTIONS, title, UIState.DEFAULT, checkSettings);
    mainMenu.addMode(UIState.OPTIONS, options);
    mainMenu.addMode(UIState.AUDIO, title, UIState.OPTIONS, checkSettings);
    mainMenu.addMode(UIState.AUDIO, options);
    mainMenu.addMode(UIState.AUDIO, optaud);
    mainMenu.addMode(UIState.LOBBY, lobbyClientList, UIState.DEFAULT, Core::toMenu);
    mainMenu.addMode(UIState.LOBBY_HOST, lobbyClientList, UIState.DEFAULT, Core::toMenu);
    mainMenu.addMode(UIState.LOBBY_HOST, lobbyHostStart);
    
    mainMenu.addElement(portErr);
    mainMenu.addElement(ipErr);
    mainMenu.addElement(connectErr);
    mainMenu.addElement(settingsChanged);
    
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
      new UIButton("Options"        , () -> UIController.setMode(UIState.OPTIONS)),
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
      new UIToggle("Fullscreen", Core::isFullScreen, (b) -> {Core.globalSettings.setBoolSetting("fullScreen", b); Core.setFullscreen(b);}),
      new UIButton("Audio"   , () -> UIController.setMode(UIState.AUDIO)                                                                  ),
      new UIButton("Gameplay", () -> UIController.setMode(UIState.GAMEPLAY)                                                               ),
      new UIButton("Back"    , UIController::back                                                                                         ),
    },
    new boolean[]{false, true, true, true}
    );
    
    UIElement optaud = new ElemList(
    new Vector2(0.38, 0.5-UIHelp.calculateListHeight(BUFFER_HEIGHT, COMPON_HEIGHT*2, COMPON_HEIGHT*2, COMPON_HEIGHT*2, COMPON_HEIGHT, COMPON_HEIGHT)/2),
    new Vector2(0.62, 0.5+UIHelp.calculateListHeight(BUFFER_HEIGHT, COMPON_HEIGHT*2, COMPON_HEIGHT*2, COMPON_HEIGHT*2, COMPON_HEIGHT, COMPON_HEIGHT)/2),
    COMPON_HEIGHT,
    BUFFER_HEIGHT,
    new UIInteractable[]{
      new UISlider("Master: %d"   , () -> Core.globalSettings.getSetting    ("soundMaster"), (v) -> Core.globalSettings.setSetting    ("soundMaster", v), 0, 100),
      new UISlider("Sound FX: %d" , () -> Core.globalSettings.getSetting    ("soundFX")    , (v) -> Core.globalSettings.setSetting    ("soundFX"    , v), 0, 100),
      new UISlider("Music: %d"    , () -> Core.globalSettings.getSetting    ("soundMusic") , (v) -> Core.globalSettings.setSetting    ("soundMusic" , v), 0, 100),
      new UIToggle("Subtitles"    , () -> Core.globalSettings.getBoolSetting("subtitles")  , (v) -> Core.globalSettings.setBoolSetting("subtitles"  , v)        ),
      new UIButton("Back"         , UIController::back                                                                                                          ),
    },
    new boolean[]{false, true, true, true}
    );
    HUD.setModeParent(UIState.DEFAULT, UIState.PAUSED);
    HUD.addMode(UIState.PAUSED, greyed, UIState.DEFAULT);
    HUD.addMode(UIState.PAUSED, outPause);
    HUD.addMode(UIState.OPTIONS, greyed, UIState.PAUSED, checkSettings);
    HUD.addMode(UIState.OPTIONS, options);
    HUD.addMode(UIState.AUDIO, greyed, UIState.OPTIONS, checkSettings);
    HUD.addMode(UIState.AUDIO, optaud);
    
    HUD.addElement(settingsChanged);
    
    HUD.clear();
    
    return HUD;
  }
}
