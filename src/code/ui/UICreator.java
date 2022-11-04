package code.ui;

import code.core.Core;

import code.math.Vector2;

import code.ui.elements.*;
import code.ui.interactables.*;

import java.awt.Color;
import java.awt.Font;

public class UICreator {
  // private static final UIElement VIRTUAL_KEYBOARD = new ElemKeyboard();
  
  /**
  * Creates the UI pane for the main menu.
  */
  public static UIPane createMain(Core c, UIController ui) {
    UIPane mainMenu = new UIPane();

    UIElement title = new ElemTitle(
    new Vector2(0, 0),
    new Vector2(0.28, 0.14),
    "Bananas",
    Font.BOLD,
    75,
    ColourPacks.DEFAULT_COLOUR_PACK,
    new boolean[]{true, false, true, false}
    );

    UIElement outPanel = new ElemButtons(
    new Vector2(0, 0.28),
    new Vector2(0.12, 0.4536),
    42.48,
    15,
    ColourPacks.DEFAULT_COLOUR_PACK,
    new UIInteractable[]{
      new UIButton("Play"           , () -> ui.setMode(UIState.NEW_GAME)),
      new UIButton("Options"        , () -> ui.setMode(UIState.OPTIONS) ),
      new UIButton("Quit to Desktop", c::quitToDesk                     ),
    },
    new boolean[]{false, false, true, false}
    );

    UIElement playModes = new ElemButtons(
    new Vector2(0, 0.28),
    new Vector2(0.12, 0.4536),
    42.48,
    15,
    ColourPacks.DEFAULT_COLOUR_PACK,
    new UIInteractable[]{
      new UIButton("Host Game"   , () -> ui.setMode(UIState.HOST_SETUP)  ),
      new UIButton("Join Game"   , () -> ui.setMode(UIState.CLIENT_SETUP)),
      new UIButton("Back"        , ui::back                              ),
    },
    new boolean[]{false, false, true, false}
    );

    UIElement hostSetup = new ElemButtons(
    new Vector2(0.44, 0.28),
    new Vector2(0.56, 0.5068),
    42.48,
    15,
    ColourPacks.DEFAULT_COLOUR_PACK,
    new UIInteractable[]{
      new UITextfield(24, 1, null, ui),
      new UIButton("Test2", null),
      new UIButton("Test3", null),
      new UIButton("Test4", null),
    },
    new boolean[]{false, false, true, false}
    );

    UIElement clientSetup = new ElemButtons(
    new Vector2(0.44, 0.28),
    new Vector2(0.56, 0.5068),
    42.48,
    15,
    ColourPacks.DEFAULT_COLOUR_PACK,
    new UIInteractable[]{
      new UITextfield(24, 1, null, ui),
      new UIButton("Test2", null),
      new UIButton("Test3", null),
      new UIButton("Connect!", null),
    },
    new boolean[]{false, false, true, false}
    );

    UIElement options = new ElemButtons(
    new Vector2(0, 0.28),
    new Vector2(0.12, 0.5068),
    42.48,
    15,
    ColourPacks.DEFAULT_COLOUR_PACK,
    new UIInteractable[]{
      new UIButton("Video"   , () -> ui.setMode(UIState.VIDEO)   ),
      new UIButton("Audio"   , () -> ui.setMode(UIState.AUDIO)   ),
      new UIButton("Gameplay", () -> ui.setMode(UIState.GAMEPLAY)),
      new UIButton("Back"    , ui::back                          ),
    },
    new boolean[]{false, false, true, false}
    );

    UIElement optvid = new ElemButtons(
    new Vector2(0.44, 0.28),
    new Vector2(0.56, 0.5068),
    42.48,
    15,
    ColourPacks.DEFAULT_COLOUR_PACK,
    new UIInteractable[]{
      new UIButton("Test1", null),
      new UIButton("Test2", null),
      new UIButton("Test3", null),
      new UIButton("Test4", null),
    },
    new boolean[]{false, false, true, false}
    );

    UIElement optaud = new ElemButtons(
    new Vector2(0.44, 0.28),
    new Vector2(0.56, 0.625),
    42.48,
    15,
    ColourPacks.DEFAULT_COLOUR_PACK,
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
    
    return mainMenu;
  }
  
  /**
  * Creates the HUD for use during gameplay.
  */
  public static UIPane createHUD(Core c, UIController ui) {
    UIPane HUD = new UIPane();

    UIElement greyed = new ElemPlain(
    new Vector2(0,0),
    new Vector2(1, 1),
    ColourPacks.DEFAULT_COLOUR_PACK,
    new boolean[]{false, false, false, false}
    );

    UIElement outPause = new ElemButtons(
    new Vector2(0.4415, 0.332),
    new Vector2(0.5585, 0.668),
    42.48,
    15,
    ColourPacks.DEFAULT_COLOUR_PACK,
    new UIInteractable[]{
      new UIButton("Resume"         , ui::back                         ),
      new UIButton("Save Game"      , null                             ),
      new UIButton("Load Game"      , null                             ),
      new UIButton("Options"        , () -> ui.setMode(UIState.OPTIONS)),
      new UIButton("Quit to Title"  , c::quitToMenu                    ),
      new UIButton("Quit to Desktop", c::quitToDesk                    ),
    },
    new boolean[]{false, true, true, true}
    );

    UIElement options = new ElemButtons(
    new Vector2(0.4415, 0.388),
    new Vector2(0.5585, 0.612),
    42.48,
    15,
    ColourPacks.DEFAULT_COLOUR_PACK,
    new UIInteractable[]{
      new UIButton("Video"   , () -> ui.setMode(UIState.VIDEO)   ),
      new UIButton("Audio"   , () -> ui.setMode(UIState.AUDIO)   ),
      new UIButton("Gameplay", () -> ui.setMode(UIState.GAMEPLAY)),
      new UIButton("Back"    , ui::back                          ),
    },
    new boolean[]{false, true, true, true}
    );

    UIElement optvid = new ElemButtons(
    new Vector2(0.4415, 0.388),
    new Vector2(0.5585, 0.612),
    42.48,
    15,
    ColourPacks.DEFAULT_COLOUR_PACK,
    new UIInteractable[]{
      new UIButton("AH"          , null    ),
      new UIButton("MAKE IT STOP", null    ),
      new UIButton("PLEASE"      , null    ),
      new UIButton("Back"        , ui::back),
    },
    new boolean[]{false, true, true, true}
    );

    UIElement optaud = new ElemButtons(
    new Vector2(0.4415, 0.301),
    new Vector2(0.5585, 0.699),
    42.48,
    15,
    ColourPacks.DEFAULT_COLOUR_PACK,
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
    
    return HUD;
  }
}

class ColourPacks {
  public static final Color DEFAULT_BACKGROUND = new Color(100, 100, 100, 127);
  public static final Color DEFAULT_SCREEN_TINT = new Color(50, 50, 50, 127);
  public static final Color DEFAULT_BUTTON_OUT_ACC = new Color(200, 200, 200);
  public static final Color DEFAULT_BUTTON_BACKGROUND = new Color(160, 160, 160, 160);
  public static final Color DEFAULT_BUTTON_IN_ACC = new Color(0, 255, 255);
  public static final Color DEFAULT_BUTTON_LOCKED = new Color(180, 180, 180);
  public static final Color DEFAULT_BUTTON_HOVER = new Color(0, 180, 180);
  public static final Color[] DEFAULT_COLOUR_PACK = {
    DEFAULT_BACKGROUND, DEFAULT_SCREEN_TINT, DEFAULT_BUTTON_OUT_ACC, DEFAULT_BUTTON_BACKGROUND, DEFAULT_BUTTON_IN_ACC, DEFAULT_BUTTON_LOCKED, DEFAULT_BUTTON_HOVER
  };
}
