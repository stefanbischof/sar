package at.stefanbischof.sar;

public class QLPathMacrosOMA extends QLPathMacrosOMSF {
  public static final String C_SC = "cache:subClassOf?";
  public static final String C_SP = "cache:subPropertyOf?";
  public static final String C_SPI = "cache:subInvPropertyOf";
  
  public QLPathMacrosOMA(PredicateSwitch ps, VariableGenerator vargen) {
    super(ps, vargen);
    
  }
  
  @Override
  public String subClassOf() {
    return C_SC;
  }
  
  @Override
  public String subPropertyOf() {
    return C_SP;
  }
  
  @Override
  public String subPropertyOfInv() {
    return C_SPI;
  }
}
