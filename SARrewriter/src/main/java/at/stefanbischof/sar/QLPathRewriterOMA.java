package at.stefanbischof.sar;

public class QLPathRewriterOMA extends QLPathRewriter { 
  
  public static final String C_DOM = "cache:dom";
  public static final String C_RNG = "cache:rng";

  public QLPathRewriterOMA(PredicateSwitch ps) {
    super(ps);
    super.setMacro(new QLPathMacrosOMA(ps,vargen));
  }

  @Override
  public String typePathDom() {
    return C_DOM;
  }
  
  @Override 
  public String typePathRng() {
    return C_RNG;
  }
}
