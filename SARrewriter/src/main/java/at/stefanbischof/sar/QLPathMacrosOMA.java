package at.stefanbischof.sar;

public class QLPathMacrosOMA extends QLPathMacrosOMSF {
  public static final String C_SPI = "cache:spi";
  
  public QLPathMacrosOMA(PropertySwitch ps, VariableGenerator vargen) {
    super(ps, vargen);
  }
  
  @Override
  public String subPropertyOfInv() {
    return C_SPI;
  }
}
