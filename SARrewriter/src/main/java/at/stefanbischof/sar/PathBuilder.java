/**
 * 
 */
package at.stefanbischof.sar;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

/**
 * functions to create paths
 * 
 * the result of a function can be:
 * 1. a path of length > 0: this is the normal case
 * 2. null: a collapsed path of length 0, this has different consequences
 * 3. *: an empty path with a star
 * 
 * @author z003354t
 *
 */
class PathBuilder {

  
  /**
   * (.../a/...)  -> ()
   * (.../a* /...) -> (.../...)
   * @param terms
   * @return
   */
  static String seq(String... terms) {
    List<String> filtered = new LinkedList<>();
    
    for(String term : terms) {
      if(term != null && !term.equals("*")) {
        filtered.add(term);
      //} else if(term != null && term.equals("*")) {
        // just leave out, do nothing
      } else { // term == null
        return null;
      }
    }
        
    // build output string
    if(filtered.size() == 0) {
      return null;
    } else {
      return "(" + StringUtils.join(filtered, "/") + ")";
    }
  }

  /**
   * (...|a|...)  -> (...|...)
   * (...|a*|...) -> (...|...)?
   * @param terms
   * @return
   */
  static String disj(String... terms) {
    List<String> filtered = new LinkedList<>();
    boolean optional = false;
    
    // filter out null terms
    // maybe set optional to true
    for(String term : terms) {
      if(term != null && !term.equals("*")) {
        filtered.add(term);
      } else if(term != null && term.equals("*")) {
        optional = true;
      //} else { // term == null
        // don't add to filtered
      }
    }
    
    // build output string
    if(filtered.size() == 0) {
      return null;
    } else {
      return "(" + StringUtils.join(filtered, "|") + ")"
    		  + (optional ? "?" : "");
    }
  }

  static String star(String term) {
    if(term == null) {
      return "*";
    } else {
      return "(" + term + "*)";
    }
  }

  static String opt(String term) {
    if(term == null) {
      return "*"; // TODO is this correct?
    } else {
      return "(" + term + "?)";
    }
  }

  static String invPath(String term) {
    if(term == null) {
      return null;
    } else {
      return "(^" + term + ")";
    }
  }
}
