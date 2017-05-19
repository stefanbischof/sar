package at.stefanbischof.sar;

public class QLPathRewriterFactory {
  private PredicateSwitch ps = new PredicateSwitch();
  private boolean oma = false;
  private boolean omsf = false;
  private String tbox = null;
  
  /**
   * @param ps the ps to set
   */
  public void setPs(PredicateSwitch ps) {
    this.ps = ps;
  }

  /**
   * @param oma the oma to set
   */
  public void setOma(boolean oma) {
    this.oma = oma;
  }

  /**
   * @param omsf the omsf to set
   */
  public void setOmsf(boolean omsf) {
    this.omsf = omsf;
  }

  /**
   * @param tbox the tbox to set
   */
  public void setTbox(String tbox) {
    this.tbox = tbox;
  }

  public QLPathRewriter getInstance() {
    QLPathMacros.matStar = omsf;
    
    QLPathRewriter qlPathRewriter = new QLPathRewriter(ps);
    
    if(oma) {
      qlPathRewriter = new QLPathRewriterOMA(ps);
    }
    if(omsf) {
      qlPathRewriter.setMacro(new QLPathMacrosOMSF(ps, qlPathRewriter.vargen));
    }
    
    if(tbox != null) {
      qlPathRewriter.setTboxURI(tbox);
    }
    
    return qlPathRewriter;
  }
}
