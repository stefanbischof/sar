/**
 * 
 */
package at.stefanbischof.sar.cli;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import at.stefanbischof.sar.MacroQueryEvaluator;
import at.stefanbischof.sar.MacroQueryEvaluatorBenchmark;
import at.stefanbischof.sar.OntologyAnalyzer;
import at.stefanbischof.sar.PredicateSwitch;
import at.stefanbischof.sar.util.BenchmarkStopWatches;
import at.stefanbischof.sar.util.FileUtil;
import at.stefanbischof.sar.util.Stopwatch;

/**
 * @author z003354t
 *
 */
public class Benchmark {
  
  private static String ontologyFilename = "resources/test/lubm/univ-bench.owl";
  private static PredicateSwitch oips;
  private String filename;
  private BenchmarkStopWatches bench;
  
  public static void main (String[] args) throws IOException {
    if(args.length < 2) {
      System.err.println("USAGE: Benchmark ontologyfilename queryfilename+");
      System.exit(1);
    }
    
    ontologyFilename = args[0];
    List<String> queries = new LinkedList<String>();
    for(int i = 1; i < args.length; i++) {
      queries.add(args[i]);
    }
    
    Benchmark b = new Benchmark();
    b.bench = new BenchmarkStopWatches(
        Arrays.asList("SAR","PR","OI","OMSF","OMA"),
        Arrays.asList(queries.toArray(new String[]{})),
        Arrays.asList("rewrite","all"));
    
    // analyze the ontology for properties to ignore
    // we have to do this only once since all queries use the same ontology
    OntologyAnalyzer oa = new OntologyAnalyzer();
    oa.loadModel(ontologyFilename);
    oips  = oa.analyze();
    
    //warmup
    for(String filename : queries) {
      b.rewriteQuery(filename);
    }
    
    // for real
    for(String filename : queries) {
      b.rewriteQuery(filename);
    }
    
    System.out.println(b.bench.csv());
  }

  /**
   * @param filename
   * @throws IOException 
   */
  private void rewriteQuery(String filename) throws IOException {
    this.filename = filename;

    this.bench.setTest(filename);
    
    final String queryString = FileUtil.readFile(filename);
    
    this.bench.setSystem("SAR");
    Stopwatch swall = this.bench.getStopwatch("all");
    swall.start();
    rewriteSAR(queryString);
    swall.stop();
    
    this.bench.setSystem("PR");
//    rewritePR(queryString);
    
    this.bench.setSystem("OI");
    swall = this.bench.getStopwatch("all");
    swall.start();
    rewriteOI(queryString);
    swall.stop();
    
    this.bench.setSystem("OMSF");
    swall = this.bench.getStopwatch("all");
    swall.start();
    rewriteOMSF(queryString);
    swall.stop();
    
    this.bench.setSystem("OMA");
    swall = this.bench.getStopwatch("all");
    swall.start();
    rewriteOMA(queryString);
    swall.stop();
  }

  /**
   * @param queryString
 * @throws IOException 
   */
  private void rewriteSAR(String queryString) throws IOException {
    Stopwatch sw = this.bench.getStopwatch("rewrite");
    MacroQueryEvaluator mqe = new MacroQueryEvaluatorBenchmark(sw);
    String result = mqe.rewriteQuery(queryString);
    
    FileUtil.writeFile(FileUtil.removeFileExtension(filename)+".SAR", result);
  }

  /**
   * @param queryString
   */
  @SuppressWarnings("unused")
  private void rewritePR(String queryString) {
    // TODO Auto-generated method stub
    
  }

  /**
   * @param queryString
 * @throws IOException 
   */
  private void rewriteOI(String queryString) throws IOException {
    Stopwatch sw = this.bench.getStopwatch("rewrite");
    MacroQueryEvaluator mqe = new MacroQueryEvaluatorBenchmark(oips, sw);
    String result = mqe.rewriteQuery(queryString);
    
    FileUtil.writeFile(FileUtil.removeFileExtension(filename)+".OI", result);
  }

  /**
   * @param queryString
 * @throws IOException 
   */
  private void rewriteOMSF(String queryString) throws IOException {
    Stopwatch sw = this.bench.getStopwatch("rewrite");
    MacroQueryEvaluator mqe = new MacroQueryEvaluatorBenchmark(sw);
    mqe.setStarCaching(true);
    String result = mqe.rewriteQuery(queryString);
    
    FileUtil.writeFile(FileUtil.removeFileExtension(filename)+".OMSF", result);
  }

  /**
   * @param queryString
 * @throws IOException 
   */
  private void rewriteOMA(String queryString) throws IOException {
    Stopwatch sw = this.bench.getStopwatch("rewrite");
    MacroQueryEvaluator mqe = new MacroQueryEvaluatorBenchmark(sw);
    mqe.setCaching(true);
    String result = mqe.rewriteQuery(queryString);
    
    FileUtil.writeFile(FileUtil.removeFileExtension(filename)+".OMA", result);
  }
}
