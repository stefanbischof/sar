package at.stefanbischof.sar.cli;

import java.lang.reflect.Method;

import org.apache.commons.cli.Option;

/**
 * Main command line interface class
 * Calls the other CLI main methods
 * 
 * Inspired by https://github.com/aidhog/blabel/blob/master/src/cl/uchile/dcc/skolem/cli/Main.java
 * 
 * @author z003354t
 *
 */
public class CLI {
  /**
   * Main method
   * @param args Command line args, first of which is the utility to run
   */
  public static void main(String[] args) {
    
    if(args.length < 1) {
      String usage = "Schema Agnostic Rewring Implementation";
      usage += "Use one of the following commands as first argument:\n";
      usage += Rewrite.class.getSimpleName() + ": Rewrite SPARQL query\n";
      usage += UpdateOMA.class.getSimpleName() + ": Get SPARQL Update materialization query for OMA\n";
      usage += UpdateOMSF.class.getSimpleName() + ": Get SPARQL Update materialization query for OMSF\n";
      usage += Benchmark.class.getSimpleName() + ": Perform rewriting benchmark\n";
      
      
      System.err.println(usage);
      System.exit(1);
    }
    
    
    
    try {
      Class<? extends Object> cls = Class.forName(CLI.class.getPackage().getName() + "." + args[0]);

      Method mainMethod = cls.getMethod("main", new Class[] { String[].class });

      String[] mainArgs = new String[args.length - 1];
      System.arraycopy(args, 1, mainArgs, 0, mainArgs.length);
      
      mainMethod.invoke(null, new Object[] { mainArgs });


    } catch (Throwable e) {
      e.printStackTrace();
    }
  }

  /**
   * @return
   * @throws IllegalArgumentException
   */
  static Option getHelp() throws IllegalArgumentException {
    return new Option("h", "print help");
  }

  /**
   * @return
   */
  static Option getTBOX() {
    return Option.builder("tbox").hasArg().argName("tboxgraph")
        .desc( "graph name of the TBox")
        .build();
  }

  /**
   * @return
   */
  static Option getOI() {
    return Option.builder("oi").hasArg().argName("properties")
        .desc("comma separated properties to ignore: dom,rng,sp,sc,onp,inv,ec,ep,first,rest,int")
        .build();
  }

  /**
   * @return
   */
  static Option getOIF() {
    return Option.builder("oif").hasArg().argName( "filename" )
        .desc( "ontology file to analyze for properties to ignore")
        .build();
  }
}
