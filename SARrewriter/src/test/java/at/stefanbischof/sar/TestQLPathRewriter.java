package at.stefanbischof.sar;
/**
 * 
 */


import java.io.*;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import at.stefanbischof.sar.MacroQueryEvaluator;
import at.stefanbischof.sar.util.FileUtil;

/**
 *
 */
public class TestQLPathRewriter {
  public static final String SEP = ";";
  private static final String LUBMPATH = "resources/test/lubm/";
  
  private static List<String> queries = new LinkedList<String>();
  
  static {
      for(int i = 1; i < 15; i++) {
        String filename = LUBMPATH + "queries/query." + (i < 10 ? "0" + i : i) + ".BL"; 
        queries.add(filename);
      }
//      FilenameFilter filter = new FilenameFilter() {
//        @Override
//        public boolean accept(File directory, String fileName) {
//            return fileName.endsWith(".txt");
//        }
//        };
//      for(String file : new File("resources-test/fishmark/queries/").list(filter)) {
//        if(file.contains("query2_speciespage.txt") || file.contains("query9_collaboratorpage.txt")) {
//          continue;
//        }
//        queries.add("resources-test/fishmark/queries/" + file);
//      }
  }
  
  public TestQLPathRewriter() {
    System.out.println("filename;read file; parse query; compile query; transform query");
  }
  
  @Test
  public void testQuery() throws IOException {
    // use one query to warm up: load all classes
//    rewriteQuery("resources-test/artificial-test-queries/q1.rq");
    
    new MacroQueryEvaluator().rewriteQuery(FileUtil.readFile("resources/test/fishmark/q1.rq"));
//    System.out.println(new MacroQueryEvaluator().rewriteQuery(FileUtil.readFile(LUBMPATH + "queries/query.01.BL")));
    for(String filename : queries) {
      new MacroQueryEvaluator().rewriteQuery(FileUtil.readFile(filename));
    }
  }
}
