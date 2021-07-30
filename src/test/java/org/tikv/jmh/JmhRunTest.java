package org.tikv.jmh;

import java.io.IOException;
import org.junit.Test;

public class JmhRunTest {
  @Test
  public void runtTest() throws IOException {
    org.openjdk.jmh.Main.main(new String[] {"-prof", "gc", "-rf", "json"});
  }
}
