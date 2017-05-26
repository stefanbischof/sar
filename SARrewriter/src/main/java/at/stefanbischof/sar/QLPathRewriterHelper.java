/**
 * 
 */
package at.stefanbischof.sar;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.NodeFactory;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.shared.PrefixMapping;
import com.hp.hpl.jena.sparql.algebra.Op;
import com.hp.hpl.jena.sparql.algebra.op.OpBGP;
import com.hp.hpl.jena.sparql.algebra.op.OpJoin;
import com.hp.hpl.jena.sparql.algebra.op.OpPath;
import com.hp.hpl.jena.sparql.algebra.op.OpSequence;
import com.hp.hpl.jena.sparql.algebra.op.OpUnion;
import com.hp.hpl.jena.sparql.core.BasicPattern;
import com.hp.hpl.jena.sparql.core.TriplePath;
import com.hp.hpl.jena.sparql.path.PathParser;

/**
 *
 */
public class QLPathRewriterHelper {
  
  protected static Node newVariable(VariableGenerator vargen) {
    return NodeFactory.createVariable(vargen.getNewVariable());
  }

  /**
   * Adds an arbitrary operator to the result sequence
   * 
   * @param operator
   */
  protected static void addToOpList(Op operator, OpSequence opSeq) {
    opSeq.add(operator);
  }

  /**
   * Adds an arbitrary triple pattern to the results sequence (in form of a BGP).
   * If the last operator in the result sequence is a BGP, the triple is added to this BGP.
   * 
   * @param triple
   * @param opSeq
   */
  protected static void addToOpList(Triple triple, OpSequence opSeq) {
  
    Op lastOp = opSeq.size() > 0 ? opSeq.get(opSeq.size() - 1) : null;
  
    if (lastOp instanceof OpBGP) {
      OpBGP bgp = (OpBGP) lastOp;
      BasicPattern bp = bgp.getPattern();
      bp.add(triple);
    } else {
      opSeq.add(QLPathRewriterHelper.tripleToBGP(triple));
    }
  }

  /**
   * Create a new OpBGP for a triple <code>tr</code>
   * 
   * @param tr
   * @return
   */
  protected static OpBGP tripleToBGP(Triple tr) {
    BasicPattern bp = new BasicPattern();
    bp.add(tr);
    return new OpBGP(bp);
  }

  /**
   * @param subject
   * @param path
   * @param object
   * @return
   */
  protected static OpPath pathToOpPath(Node subject, String path, Node object) {
    return subject == null || path == null || object == null ? null : new OpPath(new TriplePath(subject, PathParser.parse(path, QLPathRewriter.prefixMapping), object));
  }

  protected static Op bgp(Node subject, Node predicate, Node object) {
    BasicPattern bp = new BasicPattern();
    bp.add(new Triple(subject, predicate, object));
    
    return new OpBGP(bp);
  }

  protected static Op union(Op ... ops) {
    Op r = null;
    
    for(Op op : ops) {
      if(r == null) { // both cases: op == null || op != null
        r = op;
      } else if (op != null) { // also r != null
        r = OpUnion.create(r, op);
      //} else { // op == null
        // do nothing
      }
    }
    
    return r;
  }

  /**
   * If one of the operators is null, then the whole operator is null
   * 
   * @param ops
   * @return
   */
  protected static Op join(Op ... ops) {
    Op r = null;
    
    for(Op op : ops) {
      if(op == null) {
        return null;
      } else {
        r = OpJoin.create(r, op);
      }
    }
    
    return r;
  }

  private static Node curie2Node(String curie) {
    return NodeFactory.createURI(PrefixMapping.Standard.expandPrefix(curie));
  }

  protected static Op bgp(Node subject, String predicate, Node object) {
    return bgp(subject, curie2Node(predicate), object);
  }

}
