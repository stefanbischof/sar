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
import com.hp.hpl.jena.update.UpdateFactory;

import at.stefanbischof.sar.OntologyAnalyzer;
import at.stefanbischof.sar.PropertySwitch;
import at.stefanbischof.sar.QLPathMacrosOMSF;
import at.stefanbischof.sar.QLPathRewriter;
import at.stefanbischof.sar.QLPathRewriterFactory;

/**
 * CLI to generate update queries
 * 
 * @author Stefan Bischof
 *
 */
public class UpdateOMSF {
  
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
      PropertySwitch ps  = oa.analyze();
      f.setPs(ps);
    }
    
    if(cmd.hasOption(tbox.getOpt())) {
      f.setTbox(cmd.getOptionValue(tbox.getOpt()));
    }
    
    QLPathRewriter q = f.getInstance();
    
    String r = "";
    r += UpdateOMA.embedCacheQuery(QLPathMacrosOMSF.C_SC, UpdateOMA.starToPlus(q.getMacro().subClassOf())) + ";\n\n"; // TODO fix this somehow
    r += UpdateOMA.embedCacheQuery(QLPathMacrosOMSF.C_SP, UpdateOMA.starToPlus(q.getMacro().subPropertyOf())) + ";\n\n";
    r += UpdateOMA.embedCacheQuery(QLPathMacrosOMSF.C_STAR_SPO, UpdateOMA.starToPlus(q.getMacro().subPropertyOfInvStar())) + ";";
    r += UpdateOMA.cacheUpdateQuery("?C a <" +  QLPathMacrosOMSF.C_UC + ">", q.getMacro().univClass(NodeFactory.createVariable("C"))) + ";";
    r += UpdateOMA.cacheUpdateQuery("?C a <" +  QLPathMacrosOMSF.C_UP + ">", q.getMacro().univProperty(NodeFactory.createVariable("C")));
//    r += UpdateOMA.cacheUpdateQuery("?C a <" +  QLPathMacrosOMA.C_EC + ">", q.getMacro().emptyClass(NodeFactory.createVariable("C"))) + ";";
//    r += UpdateOMA.cacheUpdateQuery("?C a <" +  QLPathMacrosOMA.C_EP + ">", q.getMacro().emptyProperty(NodeFactory.createVariable("C")));
    
    System.out.println(r);
    
    // this just tests that the query is a valid SPARQL update query
    UpdateFactory.create(r);
  }
  
  /**
   * @param options
   */
  private static void showUsage(Options options) {
    HelpFormatter formatter = new HelpFormatter();
    formatter.printHelp(UpdateOMSF.class.getSimpleName(), options, true);
    System.exit(1);
  }
}
