package code.core;

import code.math.IOHelp;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

class Settings {
  private static final String LOCATION = "../settings.txt";

  private final SortedMap<String, Integer> settings = new TreeMap<String, Integer>();

  private String nickname = "";

  private boolean changed = false;

  public String getNickname() {return nickname;}

  public void setNickname(String nickname) {
    if (this.nickname.equals(nickname)) return;
    this.nickname = nickname;
    changed = true;
  }

  public Settings() {
    revertChanges();
  }

  public Integer getSetting(String name) {
    return settings.get(name);
  }

  public void setSetting(String name, Integer value) {
    settings.replace(name, value);
    changed = true;
  }

  public Boolean getBoolSetting(String name) {
    return settings.get(name) == 0 ? false : true;
  }

  public void setBoolSetting(String name, Boolean value) {
    settings.replace(name, value ? 1 : 0);
    changed = true;
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
    + "fullScreen " + 1 + "\n"
    + "soundMaster " + 100 + "\n"
    + "soundFX " + 100 + "\n"
    + "soundMusic " + 100 + "\n"
    + "subtitles " + 0 + "\n"
    + "nickname " + "" + "\n"
    );
    load();
  }

  private void load() {
    List<String> lines = IOHelp.readAllLines(LOCATION, false);

    settings.clear();

    for (String line : lines) {
      String[] entry = line.split(" ", 2);

      if (entry[0].equals("nickname")) {
        nickname = entry.length > 1 ? entry[1] : "";
        for (int i = 2; i < entry.length; i++) nickname += " " + entry[i];
        continue;
      }
      if (entry[0].equals("fullScreen")) Core.setFullscreen(entry[1].equals("1"));

      settings.put(entry[0], Integer.valueOf(entry[1]));
      // System.out.println(entry[0] + ", " + Integer.valueOf(entry[1]));
    }
    changed = false;
  }

  public String toString() {
    String res = "";
    for (SortedMap.Entry<String, Integer> e : settings.entrySet()) {
      res += e.getKey() + " " + e.getValue() + "\n";
    }
    res += "nickname " + nickname;
    return res;
  }
}
