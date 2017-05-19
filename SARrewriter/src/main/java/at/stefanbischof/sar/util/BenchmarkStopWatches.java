/**
 * 
 */
package at.stefanbischof.sar.util;

import java.util.List;

/**
 * @author Stefan Bischof
 *
 */
public class BenchmarkStopWatches {
  /**
   * 
   */
  private static final String SEP = ",";
  private final List<String> systemnames;
  private final List<String> testnames;
  private final List<String> timenames;
  private final Stopwatch[][][] stopwatches;
  private String systemname;
  private String testname;
  
  public BenchmarkStopWatches(List<String> systemnames, List<String> testnames, List<String> timenames) {
    this.systemnames = systemnames;
    this.testnames = testnames;
    this.timenames = timenames;
    
    stopwatches = new Stopwatch[systemnames.size()][testnames.size()][timenames.size()];
    
    // create all the stopwatches
    for(int i = 0; i < systemnames.size(); i++) {
      for(int j = 0; j < testnames.size(); j++) {
        for(int k = 0; k < timenames.size(); k++) {
          stopwatches[i][j][k] = new Stopwatch();
        }
      }
    }
  }
  
  public Stopwatch getStopwatch(String systemname, String testname, String timename) {
    int si = systemnames.indexOf(systemname);
    int testi = testnames.indexOf(testname);
    int timei = timenames.indexOf(timename);
    Stopwatch sw = stopwatches[si][testi][timei];
    
    return sw; 
  }
  
  public void setSystem(String systemname) {
    this.systemname = systemname;
  }
  
  public void setTest(String testname) {
    this.testname = testname;
  }
  
  public Stopwatch getStopwatch(String timename) {
    return getStopwatch(systemname,testname,timename);
  }

  /**
   * @return
   */
  public String csv() {
    String result = "test" + SEP + "system" + SEP + "time" + SEP + "value" + "\r\n";
    
    for(String testname : testnames) {
      for(String systemname : systemnames) {
        for(String timename : timenames) {
          final long uTime = getStopwatch(systemname, testname, timename).getUTime();
          if(uTime == 0) {
            continue;
          }
          result += testname + SEP + systemname + SEP + timename + SEP + uTime + "\r\n";
        }
      }
    }
    
    return result;
  }
  
}
