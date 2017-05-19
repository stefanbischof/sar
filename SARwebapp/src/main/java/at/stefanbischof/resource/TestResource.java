/**
 * 
 */
package at.stefanbischof.resource;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.logging.Logger;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import at.stefanbischof.sar.MacroQueryEvaluator;
import at.stefanbischof.sar.PredicateSwitch;

/**
 * @author z003354t
 *
 */
@Path("/rewrite")
public class TestResource {
  private static final Logger LOGGER = Logger.getLogger(TestResource.class.getName());
  
  private boolean mat = false;
  private boolean starmat = false;
  private String tbox;
  private PredicateSwitch ps = new PredicateSwitch();
  private MacroQueryEvaluator eval = new MacroQueryEvaluator(ps);
  
  @POST
  @Produces("application/x-sparql-query")
  @Consumes(MediaType.APPLICATION_JSON)
  public String rewritePost(@QueryParam("query") String query, String message) {
    LOGGER.info("QUERY: <" + query.substring(10) + "...>");
    LOGGER.info("MESSAGE: <" + message + ">");
    
    ps = new PredicateSwitch();
    eval = new MacroQueryEvaluator(ps);
    
    if(message != null & !message.isEmpty()) {
      processOptions(message);
    }
    
    return eval.rewriteQuery(query);
  }

  private void processOptions(String message) {
    String optimization = null;
    String ignoreoptions = "";
    String tboxuri = null;
    
    for (String arg : message.split("&")) {
      if(arg.endsWith("=")) {
        continue;
      }
      String thesplit[] = arg.split("=");
      String name = thesplit[0];
      String value = thesplit[1];
      
      switch(name) {
      case "optimization":
        
        optimization = value.equals("none") ? null : value;
        break;
      case "tbox":
        tboxuri = value;
      default:
        // this includes all the oi things
        if(value.equals("true")) {
          ignoreoptions += name + ",";
        }
      }
    }
    
    String cmdoptions = "";
    if(optimization != null) {
      cmdoptions = "-" + optimization;
      
      if (optimization.equals("ignore")) {
        cmdoptions += ":" + ignoreoptions;
      }
      
      LOGGER.info("Calling with this optimization: " + cmdoptions);
    }
    
    if(tboxuri != null && !tboxuri.isEmpty()) {
      LOGGER.info("TBox URI: " + tboxuri);
      processOption("-tbox:" + tboxuri);
    }
    
    
    
    processOption(cmdoptions);
    eval.setCaching(mat);
    eval.setStarCaching(starmat);
    eval.setTboxURI(tbox);
    
  }
  
  @GET
  @Produces("application/x-sparql-query")
  public String rewriteGet(@QueryParam("query") String query) {
    
    
    return eval.rewriteQuery(query);
  }

  /**
   * @param option
   */
  private void processOption(final String option) {
    if (option.equals("-materializeall")) {
      LOGGER.fine("chose materialization all");
      mat = true;
    } else if (option.equals("-materializestar")) {
      LOGGER.fine("chose materialization star");
      starmat = true;
    } else if(option.startsWith("-ignore:")) {
      for (String ignore : option.substring(option.indexOf(":") + 1).split(",")) {
        ps.disable(ignore);
        
      }
    } else if(option.startsWith("-analyze:")) {
// TODO
//        LOGGER.fine("chose analyze");
//       String ontologyfilename = option.substring(option.indexOf(":") + 1);
//       OntologyAnalyzer oa = new OntologyAnalyzer();
//       oa.loadModel(ontologyfilename);
//       processOption("-ignore:" + oa.analyze());
    } else if(option.startsWith("-tbox:")) {
      try {
        this.tbox = URLDecoder.decode(option.substring(option.indexOf(":") + 1), "UTF-8");
      } catch (UnsupportedEncodingException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    } else {
      LOGGER.warning("Invalid option: " + option);
    }
  }
}
