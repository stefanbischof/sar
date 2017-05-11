package at.stefanbischof.sar;
/**
 * 
 */

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.util.FileManager;

import at.stefanbischof.sar.util.Stopwatch;

public class Ontology {

  private static final Logger LOGGER = Logger.getLogger(Ontology.class.getName()); 
  public final static String RDFS = "http://www.w3.org/2000/01/rdf-schema#";
  public final static String RDF = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";

  private final Map<String,Set<String>> subclassaxioms = new HashMap<String,Set<String>>();
  private final Map<String,Set<String>> domainaxioms = new HashMap<String,Set<String>>();
  private final Map<String,Set<String>> rangeaxioms = new HashMap<String,Set<String>>();
  private final Map<String,Set<String>> subpropertyaxioms = new HashMap<String,Set<String>>();
  
  private final Model data;
  
  public Ontology(Model model) {
    this.data = model;
    init();
  }
  
  public Ontology(String filename) {
    data = FileManager.get().loadModel(filename);
    init();
  }
  
  public Ontology(String ontologyString, String lang) throws UnsupportedEncodingException {
    lang = lang == null ? lang = "RDF/XML" : lang;
    data = ModelFactory.createDefaultModel();
    data.read(new ByteArrayInputStream(ontologyString.getBytes("UTF-8")), null, lang);
    init();
  }

  public void init() {
    Stopwatch sw = new Stopwatch();
    sw.start();
    int numStatements = 0;
    StmtIterator si = data.listStatements(null, null, (RDFNode) null);
    while(si.hasNext()) {
      numStatements++;
      Statement s = si.next();
      Property predicate = s.getPredicate();

      if(predicate.hasURI(RDFS + "subClassOf")) {
        addAxiom(s,subclassaxioms);
      } else if(predicate.hasURI(RDFS + "range")) {
        addAxiom(s,rangeaxioms);
      } else if(predicate.hasURI(RDFS + "domain")) {
        addAxiom(s,domainaxioms);
      } else if(predicate.hasURI(RDFS + "subPropertyOf")) {
        addAxiom(s,subpropertyaxioms);
      } else {
        LOGGER.fine("Ignore ontology triple: " + s);
      }
    }
    sw.stop();
    
    LOGGER.info(sw.getTime() + "ms for parsing " + numStatements + " ontology statements");

    LOGGER.info(subclassaxioms.size() + " subclass axioms parsed");
    LOGGER.info(rangeaxioms.size() + " range axioms parsed");
    LOGGER.info(domainaxioms.size() + " domain axioms parsed");
    LOGGER.info(subpropertyaxioms.size() + " subproperty axioms parsed");
  }

  /**
   * @return the subclassaxioms
   */
  public Map<String, Set<String>> getSubclassaxioms() {
    return subclassaxioms;
  }

  /**
   * @return the domainaxioms
   */
  public Map<String, Set<String>> getDomainaxioms() {
    return domainaxioms;
  }

  /**
   * @return the rangeaxioms
   */
  public Map<String, Set<String>> getRangeaxioms() {
    return rangeaxioms;
  }

  /**
   * @return the subpropertyaxioms
   */
  public Map<String, Set<String>> getSubpropertyaxioms() {
    return subpropertyaxioms;
  }

  /**
   * 
   * @param s
   * @param axioms 
   */
  private static void addAxiom(Statement s, Map<String, Set<String>> axioms) {
    // for an axiom lhs \sqsubseteq rhs:
    // right hand side of the inclusion
    final String rhs = s.getObject().asResource().getURI();
    // left hand side of the inclusion
    final String lhs = s.getSubject().getURI();
    // a set of subclasses or subroles
    Set<String> subs = axioms.get(rhs);
    
    if(subs == null) {
      subs = new HashSet<String>();
      axioms.put(rhs, subs);
    }
    subs.add(lhs);
  }

  
}