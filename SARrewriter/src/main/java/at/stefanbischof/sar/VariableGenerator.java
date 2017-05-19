/**
 * 
 */
package at.stefanbischof.sar;

/**
 * An object of this class creates a new variable name each time the method <code>getNewVariable</code> is called
 * 
 * @author z003354t
 */
class VariableGenerator {
  private String varprefix = "genvar_";
  private int counter = 0;
  
  public VariableGenerator(String varprefix) {
    this.varprefix = varprefix;
  }
  
  public String getNewVariable() {
    return varprefix + counter++;
  }
}
