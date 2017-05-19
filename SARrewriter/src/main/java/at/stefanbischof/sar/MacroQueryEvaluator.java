/**
 * 
 */
package at.stefanbischof.sar;

import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.sparql.algebra.Algebra;
import com.hp.hpl.jena.sparql.algebra.Op;
import com.hp.hpl.jena.sparql.algebra.OpAsQuery;
import com.hp.hpl.jena.sparql.algebra.Transformer;

/**
 * Main class for rewriting
 * 
 * @author z003354t
 *
 */
public class MacroQueryEvaluator {

  private boolean caching = false;
  private boolean starcaching = false;
  private String tbox;
  private PredicateSwitch ps;
  
  public MacroQueryEvaluator() {
    this(new PredicateSwitch());
  }

  public MacroQueryEvaluator(PredicateSwitch ps) {
    this.ps = ps;
  }
  
  /**
   * @param query
   * @return
   */
  public String rewriteQuery(String queryString) {
    Query query = QueryFactory.create(queryString);
    
    QLPathRewriterFactory f = new QLPathRewriterFactory();
    f.setOma(caching);
    f.setOmsf(starcaching);
    f.setTbox(tbox);
    f.setPs(ps);
    QLPathRewriter qlPathRewriter = f.getInstance();
    
    final Query newq = transform(query, qlPathRewriter);
    
    newq.setPrefixMapping(query.getPrefixMapping().withDefaultMappings(QLPathRewriter.getPrefixMapping()));
    return newq.serialize();
  }

  /**
   * @param query
   * @param qlPathRewriter
   * @return
   */
  protected Query transform(Query query, QLPathRewriter qlPathRewriter) {
    Op newop = Transformer.transform(qlPathRewriter, Algebra.compile(query));
    
    final Query newq = OpAsQuery.asQuery(newop); // Convert to a query
    return newq;
  }
  
  public static ResultSet evaluateQuery(Model model, String queryString) {
    Query query = QueryFactory.create(queryString) ;
    try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
      ResultSet results = qexec.execSelect() ;
      results = ResultSetFactory.copyResults(results) ;
      
      return results;
    }
  }

  /**
   * @param b
   */
  public void setCaching(boolean b) {
    this.caching = b; 
  }
  
  public void setStarCaching(boolean b) {
    this.starcaching = b;
  }

  /**
   * Uses this named graph for querying the TBox
   * 
   * @param tbox
   */
  public void setTboxURI(String tbox) {
    if(tbox != null && !tbox.isEmpty()) {
      this.tbox = tbox;
    }
  }

}
