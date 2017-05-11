/**
 * 
 */
package at.stefanbischof.sar;

import java.util.logging.Logger;

/**
 * @author z003354t
 *
 */
public class PredicateSwitch {
  private static final Logger LOGGER = Logger.getLogger(PredicateSwitch.class.getName());

  private String dom = "rdfs:domain";
  private String rng = "rdfs:range";
  private String sp = "rdfs:subPropertyOf";
  private String sc = "rdfs:subClassOf";
  private String type = "rdf:type";
  private String onp = "owl:onProperty";
  private String inv = "owl:inverseOf";
  private String ec = "owl:equivalentClass";
  private String ep = "owl:equivalentProperty";
  private String first = "rdf:first";
  private String rest = "rdf:rest";
  private String inter = "owl:intersectionOf";
  private String disj = "owl:disjointWith";
  private String comp = "owl:complementOf";
  private String members = "owl:members";
  private String someValFrom = "owl:someValuesFrom";
  private String propDisj = "owl:propertyDisjointWith";
  
  /**
   * @return the dom
   */
  public String getDom() {
    return dom;
  }
  /**
   * @param dom the dom to set
   */
  public void setDom(String dom) {
    this.dom = dom;
  }
  /**
   * @return the rng
   */
  public String getRng() {
    return rng;
  }
  /**
   * @param rng the rng to set
   */
  public void setRng(String rng) {
    this.rng = rng;
  }
  /**
   * @return the sp
   */
  public String getSp() {
    return sp;
  }
  /**
   * @param sp the sp to set
   */
  public void setSp(String sp) {
    this.sp = sp;
  }
  /**
   * @return the sc
   */
  public String getSc() {
    return sc;
  }
  /**
   * @param sc the sc to set
   */
  public void setSc(String sc) {
    this.sc = sc;
  }
  /**
   * @return the type
   */
  public String getType() {
    return type;
  }
  /**
   * @param type the type to set
   */
  public void setType(String type) {
    this.type = type;
  }
  /**
   * @return the onp
   */
  public String getOnp() {
    return onp;
  }
  /**
   * @param onp the onp to set
   */
  public void setOnp(String onp) {
    this.onp = onp;
  }
  /**
   * @return the inv
   */
  public String getInv() {
    return inv;
  }
  /**
   * @param inv the inv to set
   */
  public void setInv(String inv) {
    this.inv = inv;
  }
  /**
   * @return the eqc
   */
  public String getEqc() {
    return ec;
  }
  /**
   * @param ec the ec to set
   */
  public void setEqc(String ec) {
    this.ec = ec;
  }
  /**
   * @return the eqp
   */
  public String getEqp() {
    return ep;
  }
  /**
   * @param ep the ep to set
   */
  public void setEqp(String ep) {
    this.ep = ep;
  }
  /**
   * @return the first
   */
  public String getFirst() {
    return first;
  }
  /**
   * @param first the first to set
   */
  public void setFirst(String first) {
    this.first = first;
  }
  /**
   * @return the rest
   */
  public String getRest() {
    return rest;
  }
  /**
   * @param rest the rest to set
   */
  public void setRest(String rest) {
    this.rest = rest;
  }
  /**
   * @return the int
   */
  public String getInt() {
    return inter;
  }
  /**
   * @param inter the inter to set
   */
  public void setInt(String inter) {
    this.inter = inter;
  }
  /**
   * @return the disj
   */
  public String getDisj() {
    return disj;
  }
  /**
   * @param disj the disj to set
   */
  public void setDisj(String disj) {
    this.disj = disj;
  }
  /**
   * @return the comp
   */
  public String getComp() {
    return comp;
  }
  /**
   * @param comp the comp to set
   */
  public void setComp(String comp) {
    this.comp = comp;
  }
  /**
   * @return the members
   */
  public String getMembers() {
    return members;
  }
  /**
   * @param members the members to set
   */
  public void setMembers(String members) {
    this.members = members;
  }
  /**
   * @return the someValFrom
   */
  public String getSomeValFrom() {
    return someValFrom;
  }
  /**
   * @param someValFrom the someValFrom to set
   */
  public void setSomeValFrom(String someValFrom) {
    this.someValFrom = someValFrom;
  }
  /**
   * @return the propDisj
   */
  public String getPropDisj() {
    return propDisj;
  }
  /**
   * @param propDisj the propDisj to set
   */
  public void setPropDisj(String propDisj) {
    this.propDisj = propDisj;
  }
  public void reset() {
//    PredicateSwitch.DOM = "rdfs:domain";
//    PredicateSwitch.RNG = "rdfs:range";
//    PredicateSwitch.SP = "rdfs:subPropertyOf";
//    PredicateSwitch.SC = "rdfs:subClassOf";
//    PredicateSwitch.TYPE = "rdf:type";
//    PredicateSwitch.ONP = "owl:onProperty";
//    PredicateSwitch.INV = "owl:inverseOf";
//    PredicateSwitch.EC = "owl:equivalentClass";
//    PredicateSwitch.EP = "owl:equivalentProperty";
//    PredicateSwitch.FIRST = "rdf:first";
//    PredicateSwitch.REST = "rdf:rest";
//    PredicateSwitch.INT = "owl:intersectionOf";
  }
  public void disable(String ignore) {
    switch (ignore) {
    case "dom":
      this.dom = null;
      break;
    case "rng":
      this.rng = null;
      break;
    case "sp":
      this.sp = null;
      break;
    case "sc":
      this.sc = null;
      break;
    case "onp":
      this.onp = null;
      break;
    case "inv":
      this.inv = null;
      break;
    case "ec":
      this.ec = null;
      break;
    case "ep":
      this.ep = null;
      break;
    case "first":
      this.first = null;
      break;
    case "rest":
      this.rest = null;
      break;
    case "int":
      this.inter = null;
      break;
    case "list":
      this.first = null;
      this.rest = null;
    default:
      LOGGER.warning("Invalid ignore: " + ignore);
    }
    
  }
  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "PredicateSwitch [" + (dom != null ? "dom=" + dom + ", " : "")
        + (rng != null ? "rng=" + rng + ", " : "")
        + (sp != null ? "sp=" + sp + ", " : "")
        + (sc != null ? "sc=" + sc + ", " : "")
        + (type != null ? "type=" + type + ", " : "")
        + (onp != null ? "onp=" + onp + ", " : "")
        + (inv != null ? "inv=" + inv + ", " : "")
        + (ec != null ? "ec=" + ec + ", " : "")
        + (ep != null ? "ep=" + ep + ", " : "")
        + (first != null ? "first=" + first + ", " : "")
        + (rest != null ? "rest=" + rest + ", " : "")
        + (inter != null ? "inter=" + inter + ", " : "")
        + (disj != null ? "disj=" + disj + ", " : "")
        + (comp != null ? "comp=" + comp + ", " : "")
        + (members != null ? "members=" + members + ", " : "")
        + (someValFrom != null ? "someValFrom=" + someValFrom + ", " : "")
        + (propDisj != null ? "propDisj=" + propDisj : "") + "]";
  }
}
