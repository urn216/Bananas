package code.core;

import code.math.IOHelp;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

class Settings {
  private static final String LOCATION = "../settings.txt";

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
    if (settings.get(name) == null) resetToDefault(); //TODO find a better way to achieve something similar
    return Integer.parseInt(settings.get(name));
  }

  public void setIntSetting(String name, int value) {
    if (value != Integer.parseInt(settings.replace(name, "" + value))) changed = true;
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
    IOHelp.saveToFile(LOCATION, ""
    + "fullScreen "        + "true"  + "\n"
    + "soundMaster "       + 100     + "\n"
    + "soundFX "           + 100     + "\n"
    + "soundMusic "        + 100     + "\n"
    + "subtitles "         + "false" + "\n"
    + "scrollSensitivity " + 5       + "\n"
    + "nickname "          + ""      + "\n"
    );
    load();
  }

  private void load() {
    List<String> lines = IOHelp.readAllLines(LOCATION, false);

    settings.clear();

    for (String line : lines) {
      String[] entry = line.split(" ", 2);

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

  private static final String[] DEFAULT_SETTINGS = {//TODO finish
    "fullScreen " + 1 + "\n",
    "soundMaster " + 100 + "\n",
    "soundFX " + 100 + "\n",
    "soundMusic " + 100 + "\n",
    "subtitles " + 0 + "\n",
    "scrollSensitivity " + 5 + "\n",
    "nickname " + "" + "\n"
  };
}
