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
  SETUP_HOST,
  SETUP_CLIENT,
  LOBBY,
  LOBBY_HOST,

  //gameplay specific
  PAUSED,
}
