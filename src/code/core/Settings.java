package code.core;

import code.math.IOHelp;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

class Settings {
  private static final String LOCATION = "../settings.txt";

  private static final String[] DEFAULT_SETTINGS = {
    "fullScreen"        , " " + "true"  + "\n",
    "nickname"          , " " + ""      + "\n",
    "scrollSensitivity" , " " + 5       + "\n",
    "soundFX"           , " " + 100     + "\n",
    "soundMaster"       , " " + 100     + "\n",
    "soundMusic"        , " " + 100     + "\n",
    "subtitles"         , " " + "false" + "\n",
  };

  private final SortedMap<String, String> settings = new TreeMap<String, String>();

  private boolean changed = false;

  /**
   * Initialises the user-defined settings for the program
   */
  public Settings() {
    revertChanges();
  }

  /**
   * Gets the current {@code String} value associated with the desired setting
   * 
   * @param name the name of the setting to retrieve the value of
   * 
   * @return the value associated with the desired setting
   */
  public String getStringSetting(String name) {
    return settings.get(name);
  }

  /**
   * Sets a given {@code String} setting to the desired value
   * 
   * @param name the name of the setting to alter
   * @param value the value to set the setting to
   */
  public void setStringSetting(String name, String value) {
    if (!value.equals(settings.replace(name, "" + value))) changed = true;
  }

  /**
   * Gets the current {@code int} value associated with the desired setting
   * 
   * @param name the name of the setting to retrieve the value of
   * 
   * @return the value associated with the desired setting
   */
  public int getIntSetting(String name) {
    return Integer.parseInt(settings.get(name));
  }

  /**
   * Sets a given {@code int} setting to the desired value
   * 
   * @param name the name of the setting to alter
   * @param value the value to set the setting to
   */
  public void setIntSetting(String name, int value) {
    if (value != Integer.parseInt(settings.replace(name, "" + value))) changed = true;
  }

  /**
   * Gets the current {@code double} value associated with the desired setting
   * 
   * @param name the name of the setting to retrieve the value of
   * 
   * @return the value associated with the desired setting
   */
  public double getDoubleSetting(String name) {
    return Double.parseDouble(settings.get(name));
  }

  /**
   * Sets a given {@code double} setting to the desired value
   * 
   * @param name the name of the setting to alter
   * @param value the value to set the setting to
   */
  public void setDoubleSetting(String name, double value) {
    if (value != Double.parseDouble(settings.replace(name, "" + value))) changed = true;
  }

  /**
   * Gets the current {@code boolean} value associated with the desired setting
   * 
   * @param name the name of the setting to retrieve the value of
   * 
   * @return the value associated with the desired setting
   */
  public boolean getBoolSetting(String name) {
    return Boolean.parseBoolean(settings.get(name));
  }

  /**
   * Sets a given {@code boolean} setting to the desired value
   * 
   * @param name the name of the setting to alter
   * @param value the value to set the setting to
   */
  public void setBoolSetting(String name, boolean value) {
    if (value != Boolean.parseBoolean(settings.replace(name, value ? "true" : "false"))) changed = true;
  }

  /**
   * Checks whether or not the settings have been changed from their last saved state
   * 
   * @return {@code true} if any settings have been changed
   */
  public boolean hasChanged() {return changed;}

  /**
   * Saves the current settings to the user's device storage
   */
  public void saveChanges() {
    IOHelp.saveToFile(LOCATION, toString());
    changed = false;
  }

  /**
   * Reverts the settings to the last saved state, or to the default state if no saved state is found
   */
  public void revertChanges() {
    if (!IOHelp.exists(LOCATION)) resetToDefault();
    else load();
  }

  /**
   * Resets the settings to their default state, and saves this default to file
   */
  public void resetToDefault() {
    IOHelp.saveToFile(LOCATION, DEFAULT_SETTINGS);
    load();
  }

  /**
   * Loads the currently saved settings file into the program.
   * <p>
   * if a setting is missing wihtin the file as it is read in, the file will be deleted and replaced with the default settings
   */
  private void load() {
    List<String> lines = IOHelp.readAllLines(LOCATION, false);

    settings.clear();

    for (int i = 0; i < lines.size(); i++) {
      String[] entry = lines.get(i).split(" ", 2);

      if (!entry[0].equals(DEFAULT_SETTINGS[i*2])) {
        resetToDefault();
        return;
      }

      if (entry[0].equals("fullScreen")) Core.WINDOW.setFullscreen(entry[1].equals("true"));//Don't like this very much. Change it at some point

      settings.put(entry[0], entry[1]);
      // System.out.println(entry[0] + ", " + entry[1]);
    }
    changed = false;
  }

  /**
   * Converts the current settings into a readable {@code String} to be saved to a file
   * 
   * @return a {@code String} representation of the current {@code Settings} object
   */
  public String toString() {
    String res = "";
    for (SortedMap.Entry<String, String> e : settings.entrySet()) {
      res += e.getKey() + " " + e.getValue() + "\n";
    }
    return res;
  }
}
