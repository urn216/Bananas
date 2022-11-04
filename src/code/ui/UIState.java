package code.ui;

/**
* Defines different ui states
*/
public enum UIState {
  //general
  DEFAULT,
  OPTIONS,
  VIDEO,
  AUDIO,
  GAMEPLAY,

  //menu specific
  NEW_GAME,
  HOST_SETUP,
  CLIENT_SETUP,

  //gameplay specific
  PAUSED,
}
