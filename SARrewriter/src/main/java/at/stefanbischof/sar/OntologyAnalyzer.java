/**
 * 
 */
package at.stefanbischof.sar;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.util.FileManager;

/**
 * @author z003354t
 *
 */
public class OntologyAnalyzer {
  private Model model;
  
  public static void main(String[] args) {
    if(args.length != 1) {
      System.err.println("command line argument: path to the ontology file");
      System.exit(1);
    }
    
    OntologyAnalyzer oa = new OntologyAnalyzer();
    
    oa.loadModel(args[0]);
    
    Object result = oa.analyze();
    
    
    System.out.println(result);
  }

  public void loadModelFromUri(String url) {
    
    model =  ModelFactory.createDefaultModel();
    model.read(url);
  }

  /**
   * @param args
   */
  public void loadModel(String modelFilename) {
    model = FileManager.get().loadModel(modelFilename);
  }
  
  /**
   * @return
   */
  public PredicateSwitch analyze() {
    PredicateSwitch ps = new PredicateSwitch();
    PredicateSwitch r = new PredicateSwitch();
    
    if (!evalAskQuery(askQuery(ps.getDom()))) {
      r.setDom(null);
    }
    if (!evalAskQuery(askQuery(ps.getRng()))) {
      r.setRng(null);
    }
    if (!evalAskQuery(askQuery(ps.getSp()))) {
      r.setSp(null);
    }
    if (!evalAskQuery(askQuery(ps.getSc()))) {
      r.setSc(null);
    }
    if (!evalAskQuery(askQuery(ps.getOnp()))) {
      r.setOnp(null);
    }
    if (!evalAskQuery(askQuery(ps.getInv()))) {
      r.setInv(null);
    }
    if (!evalAskQuery(askQuery(ps.getEqc()))) {
      r.setEqc(null);
    }
    if (!evalAskQuery(askQuery(ps.getEqp()))) {
      r.setEqp(null);
    }
    if (!evalAskQuery(askQuery(ps.getFirst()))) {
      r.setFirst(null);
    }
    if (!evalAskQuery(askQuery(ps.getRest()))) {
      r.setRest(null);
    }
    if (!evalAskQuery(askQuery(ps.getInt()))) {
      r.setInt(null);
    }
    if (!evalAskQuery(askQuery(ps.getDisj()))) {
      r.setDisj(null);
    }
    if (!evalAskQuery(askQuery(ps.getComp()))) {
      r.setComp(null);
    }
    if (!evalAskQuery(askQuery(ps.getMembers()))) {
      r.setMembers(null);
    }
    if (!evalAskQuery(askQuery(ps.getSomeValFrom()))) {
      r.setSomeValFrom(null);
    }
    if (!evalAskQuery(askQuery(ps.getPropDisj()))) {
      r.setPropDisj(null);
    }
    
    return r;
  }
  
  /**
 * @param queryString
 * @return
 */
  private boolean evalAskQuery(String queryString) {
    Query query = QueryFactory.create(queryString) ;
    
    QueryExecution qexec = QueryExecutionFactory.create(query, model) ;
    boolean result = qexec.execAsk() ;
    qexec.close() ;
    
    return result;
  }
  
  private static String askQuery(String property) {
    return "PREFIX  cache: <http://stefanbischof.at/sar-cache#>\r\n" + 
        "PREFIX  rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\r\n" + 
        "PREFIX  owl:  <http://www.w3.org/2002/07/owl#>\r\n" + 
        "PREFIX  xsd:  <http://www.w3.org/2001/XMLSchema#>\r\n" + 
        "PREFIX  rdfs: <http://www.w3.org/2000/01/rdf-schema#>\r\n" + 
        "PREFIX  ub:   <http://swat.cse.lehigh.edu/onto/univ-bench.owl#>\r\n" + 
        "PREFIX  dc:   <http://purl.org/dc/elements/1.1/>\r\n" + 
        "\r\n" + 
        "ASK\r\n" + 
        "WHERE { \r\n" + 
        "  ?s " + property + " ?o\r\n" + 
        "}\r\n";
  }
}
