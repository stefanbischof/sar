 /**
 * 
 */
package at.stefanbischof.sar.cli;

import java.io.IOException;
import java.util.logging.Logger;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.time.StopWatch;

import at.stefanbischof.sar.MacroQueryEvaluator;
import at.stefanbischof.sar.OntologyAnalyzer;
import at.stefanbischof.sar.PredicateSwitch;
import at.stefanbischof.sar.util.FileUtil;


/**
 * @author z003354t
 * 
 */
public class Rewrite {
  private static final Logger LOGGER = Logger.getLogger(Rewrite.class.getSimpleName());


  private PredicateSwitch ps = new PredicateSwitch();

  /**
   * @param args
   * @throws IOException
   */
  public static void main(String[] args) throws IOException {
    Rewrite cli = new Rewrite();
    
    Option oif = CLI.getOIF();
    Option oi = CLI.getOI();
    Option tbox = CLI.getTBOX();
    Option help = CLI.getHelp();
    Option oma = Option.builder("materializeall").desc("materialize all paths").build();
    Option omsf = Option.builder("materializestar").desc("materialize star fragments").build();
    OptionGroup matgroup = new OptionGroup();
    matgroup.addOption(oi);
    matgroup.addOption(oif);
    matgroup.addOption(oma);
    matgroup.addOption(omsf);
    
    Options options = new Options();

    options.addOptionGroup(matgroup);
    Option query = Option.builder("q").hasArgs().argName("filenames").required().desc("sparql query filenames").build();
    options.addOption(query);
    
    
    CommandLineParser parser = new DefaultParser();
    CommandLine cmd = null;
    try {
      cmd = parser.parse(options, args);
    } catch (ParseException e) {
      System.err.println( "Wrong commandline parameters: " + e.getMessage() );
      showUsage(options);
    }
    
    if(cmd.hasOption(help.getOpt())) {
      showUsage(options);
    }
        
    MacroQueryEvaluator mqe = new MacroQueryEvaluator(cli.ps);
        
    if(cmd.hasOption(oma.getOpt())) {
      LOGGER.fine("chose materialization all");
      mqe.setCaching(true);
    } else if(cmd.hasOption(omsf.getOpt())) {
      LOGGER.fine("chose materialization star");
      mqe.setStarCaching(true);
    } else if(cmd.hasOption(oi.getOpt())) {
      LOGGER.fine("chose ignore");
      String ignores = cmd.getOptionValue(oi.getOpt());
      for (String ignore : ignores.split(",")) {
        cli.ps.disable(ignore);
      }
    } else if (cmd.hasOption(oif.getOpt())) {
      LOGGER.fine("chose analyze");
      String ontologyfilename = cmd.getOptionValue(oif.getOpt());
      OntologyAnalyzer oa = new OntologyAnalyzer();
      oa.loadModel(ontologyfilename);
      cli.ps  = oa.analyze();
    }
    
    if(cmd.hasOption(tbox.getOpt())) {
      mqe.setTboxURI(cmd.getOptionValue(tbox.getOpt()));
    }
    
    StopWatch sw = new StopWatch();
    sw.start();
    String queryString = FileUtil.readFile(cmd.getOptionValue(query.getOpt()));
    String queryResult = mqe.rewriteQuery(queryString);
    sw.stop();

    System.out.println(queryResult);
    long first = sw.getTime();
    
    
    sw.reset();
    sw.start();
    queryString = FileUtil.readFile(cmd.getOptionValue(query.getOpt()));
    mqe.rewriteQuery(queryString);
    sw.stop();
    
    System.out.flush();
    
    LOGGER.info("1st run took " + first + " milliseconds");
    LOGGER.info("2nd run took " + sw.getTime() + " milliseconds");
  }

  /**
   * @param options
   */
  private static void showUsage(Options options) {
    HelpFormatter formatter = new HelpFormatter();
    formatter.printHelp(Rewrite.class.getSimpleName(), options, true);
    System.exit(1);
  }

}
