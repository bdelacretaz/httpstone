package ch.codeconsult.httpstone.url;

import java.net.URL;

public class ConstantUrlGenerator implements UrlGenerator {
  private final URL url;
  
  public ConstantUrlGenerator(URL url) {
    this.url = url;
  }
  
  public URL getUrl() throws Exception {
    return url;
  }
}
