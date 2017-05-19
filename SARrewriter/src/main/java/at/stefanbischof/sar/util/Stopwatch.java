package at.stefanbischof.sar.util;
/**
 * 
 */


/**
 * @author z003354t
 * 
 */
public class Stopwatch {

  private long start = 0;
  private long stop = 0;
  private boolean running = false;

  /**
   * 
   */
  public void start() {
    if(running) {
      throw new IllegalStateException("Stopwatch already running. Can not start stopwatch twice.");
    }
    start = System.nanoTime();
    running = true;
  }

  public void stop() {
    stop = System.nanoTime();
    running = false;
  }

  /**
   * Return elapsed time in milliseconds
   * 
   * @return
   */
  public long getTime() {
    if (running) {
      stop();
    }
    return (stop - start) / 1000000;
  }

  /**
   * Return elapsed time microseconds
   * 
   * @return
   */
  public long getUTime() {
    if (running) {
      stop();
    }
    return (stop - start) / 1000;
  }
}
