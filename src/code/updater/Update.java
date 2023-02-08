package code.updater;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.awt.Desktop;

abstract class Update {
  public static void main(String[] args) throws Throwable{
    URL url = new URL("https://github.com/urn216/Bananas/blob/master/versions/Bananas.jar?raw=true");
    File file = new File("./versions/Bananas.jar");
    url.openStream().transferTo(new FileOutputStream(file));
    if (Desktop.isDesktopSupported()) Desktop.getDesktop().open(file);
  }
}
