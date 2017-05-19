package at.stefanbischof.resource;

import java.util.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import at.stefanbischof.sar.OntologyAnalyzer;
import at.stefanbischof.sar.PredicateSwitch;

/**
 * REST Service to analyze an ontology for properties to ignore
 * @author z003354t
 *
 */
@Path("/analyze")
public class AnalyzeResource {
  private static final Logger LOGGER = Logger.getLogger(AnalyzeResource.class.getName());

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Response analyzeOntology(@QueryParam("ontourl") String uri) {
    if(uri==null || uri.isEmpty()) {
      return Response.status(404).build();
    }
    OntologyAnalyzer oa = new OntologyAnalyzer();
    LOGGER.info("Analyzing URL: " + uri);
    oa.loadModelFromUri(uri);
    PredicateSwitch sw = oa.analyze();
    
    String ret = "{";
    // TODO use a string joiner for this
    ret += generateField("dom", sw.getDom()); 
    ret += generateField("ec", sw.getEqc());
    ret += generateField("ep", sw.getEqp());
    ret += generateField("list", sw.getFirst());// use this instead of first/rest
    //ret += generateField("rest", sw.getRest());
    ret += generateField("int", sw.getInt());
    ret += generateField("inv", sw.getInv());
    ret += generateField("onp", sw.getOnp());
    ret += generateField("rng", sw.getRng());
    ret += generateField("sc", sw.getSc());
    ret += generateField("sp", sw.getSp());
    ret += generateField("type", sw.getType());
    // remove the last comma and add the closing }
    ret = ret.substring(0, ret.length()-1) + "}";
    
    LOGGER.info("Returning: " + ret);
    return Response.ok(ret).build();
    
  }

  /**
   * Generate a JSON field with the name (first parameter) and a boolean value being <code>true</code> if the property does not occur in the graph
   * 
   * @param name
   * @param property
   * @return
   */
  private String generateField(String name, String property) {
    return quote(name) + ":" + (property == null ? "true" : "false") + ",";
  }
  
  /**
   * Put a pair of double quotes around input String
   * 
   * @param input
   * @return
   */
  private String quote(String input) {
    return "\"" + input + "\"";
  }
}
