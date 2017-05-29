package at.stefanbischof.sar;

import static at.stefanbischof.sar.PathBuilder.*;
import static at.stefanbischof.sar.QLPathRewriterHelper.*;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.NodeFactory;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.shared.PrefixMapping;
import com.hp.hpl.jena.sparql.algebra.Op;
import com.hp.hpl.jena.sparql.algebra.TransformCopy;
import com.hp.hpl.jena.sparql.algebra.op.*;

/**
 *
 */
public class QLPathRewriter extends TransformCopy {
  public static final String CACHE_PREFIX = "http://stefanbischof.at/sar-cache#";
  private static final String PREFIX_BLANKVAR = "_b";
  protected final VariableGenerator vargen = new VariableGenerator("_v");
  
  /**
   * Reorder triple patterns by pushing rdf:type patterns to the very end
   */
  public static final boolean REORDER = false;
  
  /**
   * Prepare queries to use TBox in a separate named graph
   */
  private boolean useGraphPattern = false;
  private Node tboxURI = NodeFactory.createURI("http://benchmark.com/tbox");

  /**
   * prefixes used by the queries
   */
  public static final PrefixMapping prefixMapping = PrefixMapping.Factory.create().setNsPrefixes(PrefixMapping.Standard);
  private static final String RDFS = "http://www.w3.org/2000/01/rdf-schema#";
  private static final String RDF = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
  
  /**
   * Operator sequence/conjunction (AND) used for assembling the final result of the rewriting
   */
  private OpSequence opType;

  private PropertySwitch ps;
  private QLPathMacros macro;
  
  static {
    prefixMapping.setNsPrefix("cache", CACHE_PREFIX);
  }
  

  public QLPathRewriter(PropertySwitch ps) {
    // TODO Auto-generated constructor stub
    this.ps = ps;
    setMacro(new QLPathMacros(ps, vargen));
  }
  
  /**
   * @return
   */
  protected static PrefixMapping getPrefixMapping () {
    return prefixMapping;
  }

  public void setTboxURI(String tbox) {
    useGraphPattern = true;
    tboxURI = NodeFactory.createURI(tbox);
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.hp.hpl.jena.sparql.algebra.TransformCopy#transform(com.hp.hpl.jena.sparql.algebra.op.OpBGP)
   */
  @Override
  public Op transform(OpBGP opBGP) {
    // return if pattern is empty
    if (opBGP.getPattern().getList().size() < 1)
      return opBGP;

    OpSequence opSeq = OpSequence.create();
    if(REORDER) {
      opType = OpSequence.create();
    } else {
      opType = opSeq;
    }

    for (Triple triple : opBGP.getPattern().getList()) {
      transformTriple(triple, opSeq);
    }

    if(REORDER) {
      for(Op op : opType.getElements()) {
        opSeq.add(op);
      }
    }
    
    return join(opSeq.getElements().toArray(new Op[]{}));
  }

  /**
   * Transform a single triple <code>tr</code> and add result to <code>os</code>
   * 
   * @param tr Input triple
   * @param os Container for results
   */
  private void transformTriple(Triple tr, OpSequence os) {
    Node pr = tr.getPredicate();
    
    // replace blank nodes in triple patterns by similarly named variable
    tr = blankSubjToVar(tr);
    tr = blankObjToVar(tr);
    
    
    if (pr.isURI()) {

      switch (pr.getURI()) {
      case RDFS + "domain":
      case "rdfs:domain":
        addToOpList(tr, os);
        break;
      case RDFS + "range":
      case "rdfs:range":
        addToOpList(tr, os);
        break;
      case RDFS + "subClassOf":
      case "rdfs:subClassOf":
        addToOpList(rewriteSC(tr), os);
        break;
      case RDFS + "subPropertyOf":
      case "rdfs:subPropertyOf":
        addToOpList(rewriteSP(tr), os);
        break;
      case RDF + "type":
      case "rdf:type":
        addToOpList(rewriteType(tr), opType);
        break;
      default:
        addToOpList(rewriteArbitraryPredicate(tr, pr), os);
      }
    } else if (pr.isVariable()) {
      //addToOpList(rewriteVariable(tr, pr), os);
      // can't do at the moment
      throw new UnsupportedOperationException("Variables on predicate position are currently not allowed");
    } else {
      throw new IllegalStateException("Invalid predicate, must be URI or variable but is: '" + pr.toString());
      // Severe error if there is a predicate which is neither a uri nor a variable
      // This is not allowed by the sparql spec and we therefore give up
    }
  }

  /**
   * Check if object of triple pattern <code>tr</code> is a blank node and replace by variable
   * 
   * @param tr
   * @return
   */
  private static Triple blankSubjToVar(Triple tr) {
    Node subject = tr.getSubject();
    
    return subject.isBlank() || (subject.isVariable() && subject.getName().startsWith("?"))
        ? new Triple(
            blankToVar(subject), 
            tr.getPredicate(), 
            tr.getObject())
        : tr;
  }

  /**
   * Check if object of triple pattern <code>tr</code> is a blank node and replace by variable
   * 
   * @param tr 
   * @return
   */
  private static Triple blankObjToVar(Triple tr) {
    Node object = tr.getObject();
    
    // apparently Jena makes blank nodes to variables of the form "??0"
    // so we check for blank nodes and for variable names starting with "?"
    // (note the first ? is already parsed away by Jena)
    return object.isBlank() || (object.isVariable() && object.getName().startsWith("?"))
        ? new Triple(
            tr.getSubject(), 
            tr.getPredicate(), 
            blankToVar(object))
        : tr;
  }

  /**
   * Creates a new variable node from a blank node
   * 
   * @param blankNode
   * @return
   */
  private static Node blankToVar(Node blankNode) {
    return NodeFactory.createVariable(PREFIX_BLANKVAR + 
       (blankNode.isBlank()
        ? blankNode.getBlankNodeLabel()
        : blankNode.getName().substring(1)));
  }

  /**
   * Rewrite triple something rdf:type Class
   * 
   * @param tr
   * @return
   */
  private Op rewriteType(Triple triple) {
    
    Node skVar1 = newVariable(vargen);
    Node skVar2 = newVariable(vargen);
    Node skVar3 = newVariable(vargen); // added a third variable to avoid reusing variable 2 when it is not necessary
    
    String sc = getMacro().subClassOf();
    
    if("*".equals(sc)) { // (nearly) empty tbox: replace the variable with the class uri (which is the object)
      sc = null;
      skVar1 = triple.getObject();
    }
    
    Op op0 = bgp(triple.getSubject(), com.hp.hpl.jena.vocabulary.RDF.type.asNode(), skVar1);
    
    Op path1 = pathToOpPath(skVar2, typePathDom(), skVar1);
    Op op1 = join(condGraphPattern(path1), bgp(triple.getSubject(), skVar2, NodeFactory.createAnon()));
    
    Op path2 = pathToOpPath(skVar3, typePathRng(), skVar1);
    Op op2 = join(condGraphPattern(path2), bgp(NodeFactory.createAnon(), skVar3, triple.getSubject()));
    
    
    Op path = pathToOpPath(skVar1,  sc, triple.getObject());
    
    Op op3 = sc != null ? 
    		getMacro().univClass(triple.getObject(), skVar1) :
    		null;
    		
    
    Op r = path != null ?
    	join(
            condGraphPattern(path), // still works if this is null (in the empty tbox case)
            union(op0, op1, op2, op3)
          ):
        union(op0, op1, op2, op3);
    
    return r;
  }

  public String typePathRng() {
    return seq(getMacro().subPropertyOf(), ps.getRng());
  }

  public String typePathDom() {
    return seq(getMacro().subPropertyOf(), disj(invPath(ps.getOnp()), ps.getDom()));
  }
  
  /**
   * @param tr
   * @return
   */
  private Op rewriteSC(Triple tr) {
    return union(
        pathToOpPath(tr.getSubject(), getMacro().subClassOf(), tr.getObject()),
        getMacro().univClass(tr.getObject()),
        getMacro().emptyClass(tr.getSubject()));
  }

  /**
   * @param tr
   * @return
   */
  private Op rewriteSP(Triple tr) {
    return union(
        pathToOpPath(tr.getSubject(), getMacro().subPropertyOf(), tr.getObject()),
        getMacro().univProperty(tr.getObject()),
        getMacro().emptyProperty(tr.getSubject()));
  }

  /**
   * @param triple
   * @param predicate - just for convenience
   * @return
   */
  private Op rewriteArbitraryPredicate(Triple triple, Node predicate) {
    Node skVar1 = newVariable(vargen);
    Node skVar2 = newVariable(vargen);
    
    String sp = getMacro().subPropertyOf();
    if("*".equals(sp)) {  // (nearly) empty tbox: return triple unchanged
      sp = null;
      skVar1 = triple.getPredicate();
    }
    
    OpPath path1 = pathToOpPath(skVar1, sp, predicate);
    
    OpPath path2 = pathToOpPath(skVar1, getMacro().subPropertyOfInv(), skVar2);
    Op op2 = join(condGraphPattern(path2), bgp(triple.getObject(), skVar2, triple.getSubject()));
      
    Op union = union(bgp(triple.getSubject(), skVar1, triple.getObject()), op2);
    
    Op univProp = getMacro().univProperty(predicate);
      
    Op r = union(path1 != null ? join(condGraphPattern(path1), union) : union, univProp);
    
    return r;
  }

//  /**
//   * @param tr
//   * @param predicate
//   * @return
//   */
//  private OpUnion rewriteVariable(Triple tr, Node predicate) {
//    if(!predicate.isVariable()) {
//      throw new IllegalStateException("Predicate must be a variable but is " + predicate.toString());
//    }
//    
//    final Node freshVariable = NodeFactory.createVariable(vargen.getNewVariable());
//
//    final Op op1 = opBindVarTo(rewriteType(tr), predicate, RDF_TYPE);
//    final Op op2 = opBindVarTo(rewriteSC(tr), predicate, RDFS_SC);
//    final Op op3 = opBindVarTo(rewriteSP(tr), predicate, RDFS_SP);
//    // TODO cases for dom and range are not clear
//    // this doesn't work
//    final Op op4 = opBindVarTo(tripleToBGP(tr), predicate, RDFS_DOM);
//    final Op op5 = opBindVarTo(tripleToBGP(tr), predicate, RDFS_RNG);
//
//    final BasicPattern bgp1 = new BasicPattern();
//    bgp1.add(new Triple(tr.getSubject(), freshVariable, tr.getObject()));
//    final OpPath path1 = pathToOpPath(freshVariable, RDFS_SP + "*", predicate);
//
//    return new OpUnion(
//        new OpUnion(
//            new OpUnion(op1, op2),
//            op3), 
//        OpSequence.create(path1, new OpBGP(bgp1)));
//  }  


  /**
   * Embeds the operator <code>pattern</code> in a graph pattern if configured to do so
   * Otherwise just return the pattern.
   * 
   * @param pattern
   * @return
   */
  private Op condGraphPattern(Op pattern) {
    if(pattern == null) {
      return null;
    }
    
    return useGraphPattern
           ? new OpGraph(tboxURI, pattern)
           : pattern;
  }

  public QLPathMacros getMacro() {
    return macro;
  }

  public void setMacro(QLPathMacros macro) {
    this.macro = macro;
  }
  
  
}
