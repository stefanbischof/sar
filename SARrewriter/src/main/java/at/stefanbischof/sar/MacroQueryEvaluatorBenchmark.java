/**
 * 
 */
package at.stefanbischof.sar;

import com.hp.hpl.jena.query.Query;

import at.stefanbischof.sar.util.Stopwatch;

/**
 * @author Stefan Bischof
 *
 */
public class MacroQueryEvaluatorBenchmark extends MacroQueryEvaluator {
  private Stopwatch sw;
  
  public MacroQueryEvaluatorBenchmark(Stopwatch sw) {
    this.sw = sw; 
  }
  
  public MacroQueryEvaluatorBenchmark(PredicateSwitch ps, Stopwatch sw) {
    super(ps);
    this.sw = sw; 
  }
  
  @Override
  protected Query transform(Query query, QLPathRewriter qlPathRewriter) {
    sw.start();
    Query q = super.transform(query, qlPathRewriter);
    sw.stop();
    return q;
  }
}
