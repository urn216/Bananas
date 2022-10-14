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
    boolean[] Tties = {true, false, true, false};
    UIElement title = new ElemTitle(
    new Vector2(0, 0),
    new Vector2(0.28, 0.14),
    "Bananas",
    Font.BOLD,
    75,
    ColourPacks.DEFAULT_COLOUR_PACK,
    Tties
    );
    boolean[] Bties = {false, false, true, false};
    UIInteractable[] topButtons = {
      new UIButton("New Game"       , c::newGame                       ),
      new UIButton("Options"        , () -> ui.setMode(UIState.OPTIONS)),
      new UIButton("Quit to Desktop", c::quitToDesk                    ),
    };
    UIElement outPanel = new ElemButtons(
    new Vector2(0, 0.28),
    new Vector2(0.12, 0.56),
    42.48,
    15,
    ColourPacks.DEFAULT_COLOUR_PACK,
    topButtons,
    Bties
    );
    UIInteractable[] OptButtons = {
      new UIButton("Video"   , () -> ui.setMode(UIState.VIDEO)   ),
      new UIButton("Audio"   , () -> ui.setMode(UIState.AUDIO)   ),
      new UIButton("Gameplay", () -> ui.setMode(UIState.GAMEPLAY)),
      new UIButton("Back"    , ui::back                          ),
    };
    UIElement options = new ElemButtons(
    new Vector2(0, 0.28),
    new Vector2(0.12, 0.5068),
    42.48,
    15,
    ColourPacks.DEFAULT_COLOUR_PACK,
    OptButtons,
    Bties
    );
    UIInteractable[] OViButtons = {
      new UIButton("Test1", null),
      new UIButton("Test2", null),
      new UIButton("Test3", null),
      new UIButton("Test4", null),
    };
    UIElement optvid = new ElemButtons(
    new Vector2(0.44, 0.28),
    new Vector2(0.56, 0.5068),
    42.48,
    15,
    ColourPacks.DEFAULT_COLOUR_PACK,
    OViButtons,
    Bties
    );
    UIInteractable[] OAuButtons = {
      new UISlider("Master: %d"   , () -> c.globalSettings.getSetting    ("soundMaster"), (v) -> c.globalSettings.setSetting    ("soundMaster", v), 0, 100),
      new UISlider("Sound FX: %d" , () -> c.globalSettings.getSetting    ("soundFX")    , (v) -> c.globalSettings.setSetting    ("soundFX"    , v), 0, 100),
      new UISlider("Music: %d"    , () -> c.globalSettings.getSetting    ("soundMusic") , (v) -> c.globalSettings.setSetting    ("soundMusic" , v), 0, 100),
      new UIToggle("Subtitles"    , () -> c.globalSettings.getBoolSetting("subtitles")  , (v) -> c.globalSettings.setBoolSetting("subtitles"  , v)),
    };
    UIElement optaud = new ElemButtons(
    new Vector2(0.44, 0.28),
    new Vector2(0.56, 0.625),
    42.48,
    15,
    ColourPacks.DEFAULT_COLOUR_PACK,
    OAuButtons,
    Bties
    );
    mainMenu.addElement(title);
    mainMenu.addElement(outPanel);
    mainMenu.addElement(options);
    mainMenu.addElement(optvid);
    mainMenu.addElement(optaud);
    mainMenu.addMode(UIState.DEFAULT, title);
    mainMenu.addMode(UIState.DEFAULT, outPanel);
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
    boolean[] Tties = {false, false, false, false};
    UIElement greyed = new ElemPlain(
    new Vector2(0,0),
    new Vector2(1, 1),
    ColourPacks.DEFAULT_COLOUR_PACK,
    Tties
    );
    // boolean[] hTies = {true, false, true, false};
    // int[] hI = {0, 3, 4, 5};
    // UIElement health = new ElemInfo(
    // new Vector2(0, 0),
    // new Vector2(0.125, 0.2),
    // 0,
    // hI,
    // Font.BOLD,
    // 30,
    // ColourPacks.DEFAULT_COLOUR_PACK,
    // hTies
    // );
    boolean[] Bties = {false, true, true, true};
    UIInteractable[] topButtons = {
      new UIButton("Resume"         , ui::back                         ),
      new UIButton("Save Game"      , null                             ),
      new UIButton("Load Game"      , null                             ),
      new UIButton("Options"        , () -> ui.setMode(UIState.OPTIONS)),
      new UIButton("Quit to Title"  , c::quitToMenu                    ),
      new UIButton("Quit to Desktop", c::quitToDesk                    ),
    };
    UIElement outPause = new ElemButtons(
    new Vector2(0.4415, 0.332),
    new Vector2(0.5585, 0.668),
    42.48,
    15,
    ColourPacks.DEFAULT_COLOUR_PACK,
    topButtons,
    Bties
    );
    UIInteractable[] OptButtons = {
      new UIButton("Video"   , () -> ui.setMode(UIState.VIDEO)   ),
      new UIButton("Audio"   , () -> ui.setMode(UIState.AUDIO)   ),
      new UIButton("Gameplay", () -> ui.setMode(UIState.GAMEPLAY)),
      new UIButton("Back"    , ui::back                          ),
    };
    UIElement options = new ElemButtons(
    new Vector2(0.4415, 0.388),
    new Vector2(0.5585, 0.612),
    42.48,
    15,
    ColourPacks.DEFAULT_COLOUR_PACK,
    OptButtons,
    Bties
    );
    UIInteractable[] OViButtons = {
      new UIButton("AH"          , null    ),
      new UIButton("MAKE IT STOP", null    ),
      new UIButton("PLEASE"      , null    ),
      new UIButton("Back"        , ui::back),
    };
    UIElement optvid = new ElemButtons(
    new Vector2(0.4415, 0.388),
    new Vector2(0.5585, 0.612),
    42.48,
    15,
    ColourPacks.DEFAULT_COLOUR_PACK,
    OViButtons,
    Bties
    );
    UIInteractable[] OAuButtons = {
      new UISlider("Master: %d"   , () -> c.globalSettings.getSetting    ("soundMaster"), (v) -> c.globalSettings.setSetting    ("soundMaster", v), 0, 100),
      new UISlider("Sound FX: %d" , () -> c.globalSettings.getSetting    ("soundFX")    , (v) -> c.globalSettings.setSetting    ("soundFX"    , v), 0, 100),
      new UISlider("Music: %d"    , () -> c.globalSettings.getSetting    ("soundMusic") , (v) -> c.globalSettings.setSetting    ("soundMusic" , v), 0, 100),
      new UIToggle("Subtitles"    , () -> c.globalSettings.getBoolSetting("subtitles")  , (v) -> c.globalSettings.setBoolSetting("subtitles"  , v)),
      new UIButton("Back"         , ui::back),
    };//0.398
    UIElement optaud = new ElemButtons(
    new Vector2(0.4415, 0.301),
    new Vector2(0.5585, 0.699),
    42.48,
    15,
    ColourPacks.DEFAULT_COLOUR_PACK,
    OAuButtons,
    Bties
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
