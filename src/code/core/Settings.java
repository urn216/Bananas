package code.core;

import code.math.IOHelp;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

class Settings {
  private static final String LOCATION = "../settings.txt";

  private static final String[] DEFAULT_SETTINGS = {
    "fullScreen"        , " " + "true"  + "\n",
    "soundMaster"       , " " + 100     + "\n",
    "soundFX"           , " " + 100     + "\n",
    "soundMusic"        , " " + 100     + "\n",
    "subtitles"         , " " + "false" + "\n",
    "scrollSensitivity" , " " + 5       + "\n",
    "nickname"          , " " + ""      + "\n",
  };

  private final SortedMap<String, String> settings = new TreeMap<String, String>();

  private boolean changed = false;

  public Settings() {
    revertChanges();
  }

  public String getStringSetting(String name) {
    return settings.get(name);
  }

  public void setStringSetting(String name, String value) {
    if (!value.equals(settings.replace(name, "" + value))) changed = true;
  }

  public int getIntSetting(String name) {
    return Integer.parseInt(settings.get(name));
  }

  public void setIntSetting(String name, int value) {
    if (value != Integer.parseInt(settings.replace(name, "" + value))) changed = true;
  }

  public double getDoubleSetting(String name) {
    return Double.parseDouble(settings.get(name));
  }

  public void setDoubleSetting(String name, double value) {
    if (value != Double.parseDouble(settings.replace(name, "" + value))) changed = true;
  }

  public boolean getBoolSetting(String name) {
    return Boolean.parseBoolean(settings.get(name));
  }

  public void setBoolSetting(String name, boolean value) {
    if (value != Boolean.parseBoolean(settings.replace(name, value ? "true" : "false"))) changed = true;
  }

  public boolean hasChanged() {return changed;}

  public void saveChanges() {
    IOHelp.saveToFile(LOCATION, toString());
    changed = false;
  }

  public void revertChanges() {
    if (!IOHelp.exists(LOCATION)) resetToDefault();
    else load();
  }

  public void resetToDefault() {
    IOHelp.saveToFile(LOCATION, DEFAULT_SETTINGS);
    load();
  }

  private void load() {
    List<String> lines = IOHelp.readAllLines(LOCATION, false);

    settings.clear();

    for (int i = 0; i < lines.size(); i++) {
      String[] entry = lines.get(i).split(" ", 2);

      if (!entry[0].equals(DEFAULT_SETTINGS[i*2])) {
        resetToDefault();
        return;
      }

      if (entry[0].equals("fullScreen")) Core.setFullscreen(entry[1].equals("true"));

      settings.put(entry[0], entry[1]);
      // System.out.println(entry[0] + ", " + Integer.valueOf(entry[1]));
    }
    changed = false;
  }

  public String toString() {
    String res = "";
    for (SortedMap.Entry<String, String> e : settings.entrySet()) {
      res += e.getKey() + " " + e.getValue() + "\n";
    }
    return res;
  }
}
