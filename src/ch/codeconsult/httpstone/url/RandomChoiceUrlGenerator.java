package ch.codeconsult.httpstone.url;

import java.io.BufferedReader;
import java.io.FileReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/** randomly select an URL in a list read from a text file */
public class RandomChoiceUrlGenerator implements UrlGenerator {
  public static final String URL_FILE_PROPERTY = "RandomChoiceUrlGenerator.file";
  private static List urls = null;

  public URL getUrl() throws Exception {
    maybeReadFile();
    final int index = (int)(Math.random() * urls.size() - 1);
    return new URL((String) urls.get(index));
  }
  
  private static void maybeReadFile() throws Exception {
    synchronized (RandomChoiceUrlGenerator.class) {
      if(urls!=null) return;
      urls = new ArrayList();
      
      final String filename = System.getProperty(URL_FILE_PROPERTY);
      if(filename==null) {
        throw new Exception("RandomChoiceUrlGenerator filename not set, please define the " + URL_FILE_PROPERTY + " system property before running this");
      }
      
      BufferedReader br = null;
      try {
        br = new BufferedReader(new FileReader(filename));
        String line = null;
        while( ( line = br.readLine() ) != null) {
          urls.add(line.trim());
        }
      } finally {
        if(br!=null) br.close();
      }
    }
  }

}
