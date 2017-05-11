package at.stefanbischof.sar.cli;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.hp.hpl.jena.graph.NodeFactory;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.shared.PrefixMapping;
import com.hp.hpl.jena.sparql.algebra.Op;
import com.hp.hpl.jena.sparql.algebra.OpAsQuery;
import com.hp.hpl.jena.update.UpdateFactory;

import at.stefanbischof.sar.OntologyAnalyzer;
import at.stefanbischof.sar.PredicateSwitch;
import at.stefanbischof.sar.QLPathMacrosOMA;
import at.stefanbischof.sar.QLPathRewriter;
import at.stefanbischof.sar.QLPathRewriterFactory;
import at.stefanbischof.sar.QLPathRewriterOMA;

/**
 * CLI to generate update queries
 * 
 * @author Stefan Bischof
 *
 */
public class UpdateOMA {
  
  
  private static String prefixes;
  static {
	  prefixes = "PREFIX cache: <"+ QLPathRewriter.CACHE_PREFIX + ">";
	    for(String prefix: PrefixMapping.Standard.getNsPrefixMap().keySet()) {
	      prefixes += "PREFIX " + prefix + ": <" +  PrefixMapping.Standard.getNsPrefixMap().get(prefix) + ">\n";
	    }
  }

  public static void main(String[] args) {
    Option oif = CLI.getOIF();
    Option oi = CLI.getOI();
    Option tbox = CLI.getTBOX();
    Option help = CLI.getHelp();
    
    Options options = new Options();
    options.addOption(tbox);
    options.addOptionGroup(new OptionGroup().addOption(oi).addOption(oif));
    options.addOption(help);
    
    CommandLine cmd = null;
    try {
      CommandLineParser parser = new DefaultParser();
      cmd = parser.parse(options, args);
    } catch (ParseException e) {
      System.err.println("Illegal commandline parameters: " + e.getMessage());
      showUsage(options);
    }
    
    if(cmd.hasOption(help.getOpt())) {
      showUsage(options);
    }
    
    QLPathRewriterFactory f = new QLPathRewriterFactory();
    
    if(cmd.hasOption(oif.getOpt())) {
      String ontologyfilename = cmd.getOptionValue(oif.getOpt());
      
      OntologyAnalyzer oa = new OntologyAnalyzer();
      oa.loadModel(ontologyfilename);
      PredicateSwitch ps  = oa.analyze();
      f.setPs(ps);
    }
    
    if(cmd.hasOption(tbox.getOpt())) {
      f.setTbox(cmd.getOptionValue(tbox.getOpt()));
    }
    
    QLPathRewriter q = f.getInstance();
    
    String r = "";
    r += embedCacheQuery(QLPathRewriterOMA.C_DOM, q.typePathDom()) + ";\n\n";
    r += embedCacheQuery(QLPathRewriterOMA.C_RNG , q.typePathRng()) + ";\n\n";
    r += embedCacheQuery("cache:subClassOf", starToPlus(q.getMacro().subClassOf())) + ";\n\n"; // TODO fix this somehow
    r += embedCacheQuery("cache:subPropertyOf", starToPlus(q.getMacro().subPropertyOf())) + ";\n\n";
    r += embedCacheQuery(QLPathMacrosOMA.C_SPI, q.getMacro().subPropertyOfInv()) + ";\n\n";
    r += cacheUpdateQuery("?C a <" +  QLPathMacrosOMA.C_UC + ">", q.getMacro().univClass(NodeFactory.createVariable("C"))) + ";\n\n";
    r += cacheUpdateQuery("?C a <" +  QLPathMacrosOMA.C_UP + ">", q.getMacro().univProperty(NodeFactory.createVariable("C")));
//    r += cacheUpdateQuery("?C a <" +  QLPathMacrosOMA.C_EC + ">", q.getMacro().emptyClass(NodeFactory.createVariable("C"))) + ";\n\n";
//    r += cacheUpdateQuery("?C a <" +  QLPathMacrosOMA.C_EP + ">", q.getMacro().emptyProperty(NodeFactory.createVariable("C")));
    
    System.out.println(r);
    
    // this just tests that the query is a valid SPARQL update query
    UpdateFactory.create(r);
  }

  protected static String embedCacheQuery(String head, String body) {
    final String subjectVar = "?S ";
    final String objectvar = " ?O";
    
    return cacheQuery(subjectVar + head + objectvar, subjectVar + body + objectvar);
  }
  
  protected static String cacheUpdateQuery(String head, Op op) {
	Query q = OpAsQuery.asQuery(op);
	q.setPrefixMapping(PrefixMapping.Standard);
	
    
    String t = q.serialize();
    return cacheQuery(head, t.substring(t.indexOf("{") + 1, t.lastIndexOf("}")));
  }

  private static String cacheQuery(String head, String body) {
    return prefixes + "DELETE WHERE { " + head + " }; \n"
        + "INSERT { " + head +  " } \nWHERE { \n"
            + body + "\n}";
  }
  
  /**
   * @param options
   */
  private static void showUsage(Options options) {
    HelpFormatter formatter = new HelpFormatter();
    formatter.printHelp(UpdateOMA.class.getSimpleName(), options, true);
    System.exit(1);
  }
  
  /**
   * replaces the last star operator with a plus operator
   * 
   * @param path
   * @return
   */
  private static String starToPlus(String path) {
    int i = path.lastIndexOf("*");
    return path.substring(0, i) + "+" + path.substring(i+1);
  }
}
