	/**
 * 
 */
package at.stefanbischof.sar;

import static at.stefanbischof.sar.PathBuilder.*;
import static at.stefanbischof.sar.QLPathRewriterHelper.*;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.NodeFactory;
import com.hp.hpl.jena.sparql.algebra.Op;
import com.hp.hpl.jena.sparql.algebra.op.OpUnion;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.expr.nodevalue.NodeValueNode;
import com.hp.hpl.jena.vocabulary.OWL2;

/**
 * The functions of this class will compute the different macros
 * 
 * The resulting paths depend on cached or non-occurring properties
 * 
 * @author Stefan Bischof
 *
 */
public class QLPathMacros {

//  private static final String C_STAR_UC = null;
//  private static final String C_STAR_TM = null;
//  private static final String C_STAR_EC1 = null;
//  private static final String C_STAR_EC2 = null;
//  private static final String C_STAR_EP = null;
//  private static final String C_STAR_UP = null;
  /**
   * Materialize star patterns (...)*
   * 
   * This is incompatible/orthogonal with matSC, matSP, matINV
   */
  static boolean matStar = false;
  public static final String C_STAR_SC = "cache:starSC?";
  public static final String C_STAR_SP = "cache:starSP?";
  public static final String C_STAR_SPI = "cache:starSPI?";
  private PredicateSwitch ps;
  private VariableGenerator vargen;
  
  @SuppressWarnings("unused")
  private QLPathMacros() {
    // hide default constructor
  }

  public QLPathMacros(PredicateSwitch ps, VariableGenerator vargen) {
    this.ps = ps;
    this.vargen = vargen;
  }
  
  /**
   * @return
   */
  public String subClassOf() {
    return matStar ? C_STAR_SC :
        star(
          disj(
              ps.getSc(), 
              ps.getEqc(), 
              invPath(ps.getEqc()), 
              intListMember(), 
              someprop(), 
              somepropinv()));
  }

  /**
   * @return
   */
  public String subPropertyOf() {
    return matStar ? C_STAR_SP :
        star(
          disj(
              spoeqp(),
              seq(
                  inverse(), 
                  star(spoeqp()), 
                  inverse())));
  }
  
  /**
   * this is only a part of the subPropertyInv path!!!
   * most of the rest is covered by the subPropertyOf
   * 
   * we also turn around the order of the sequence so that the already bound variable is in subject position
   * @return
   */
  public String subPropertyOfInv() {
    return seq(
        inverse(), subPropertyOfInvStar());
  }
  
  public String subPropertyOfInvStar() {
    return matStar ? C_STAR_SPI : 
      star(
        disj(
            invPath(ps.getSp()), 
            ps.getEqp(), 
            invPath(ps.getEqp())));
  }
  
  
  String inverse() {
    final String ret = disj(ps.getInv(), invPath(ps.getInv()));
    return ret;
  }
  
  String spoeqp() {
    return disj(ps.getSp(), ps.getEqp(), invPath(ps.getEqp()));
  }

  /**
   * @return
   */
  String intListMember() {
    // this function is only used from within a star, so no more star materialization needed
    return seq(ps.getInt(), star(ps.getRest()), ps.getFirst());
  }
  
  String someprop() {
    return seq(ps.getOnp(), subPropertyOf(), disj(invPath(ps.getOnp()), ps.getDom()));
  }
  
  String somepropinv() {
    // this function is only used from within a star, so no more star materialization needed
    return seq(ps.getOnp(), subPropertyOf(), inverse(), star(spoeqp()), ps.getRng());
  }

  String disjointClassses() {
    return disj(ps.getDisj(),invPath(ps.getDisj()),ps.getComp(),invPath(ps.getComp()));
  }

  /**
   * Implements the macro univProperty[x] defined as
   * 
   * { owl:topObjectProperty (SpoEqp | Inv)∗  x}
   *  
   * @param x
   * @return
   */
  public Op univProperty(Node x) {
    Op r = star(disj(spoeqp(), inverse())).equals("*") ?
    		null :
    		pathToOpPath(OWL2.topObjectProperty.asNode(),
        /*matStar ? C_STAR_UP : */
    				star(disj(spoeqp(), inverse())),
        x);
        
    return r;
  }

  /**
   * Implements the macro univClass[x] defined as
   * { owl:Thing subClassOf x} 
   * UNION
   * { owl:topObjectProperty ((SpoEqp | Inv)∗  / (ˆowl:onProperty | rdfs:domain | rdfs:range ) / subClassOf) x} 
   * 
   * @param qlPathRewriter TODO
   * @param x
   * @return
   */
  public Op univClass(Node x) {
    // {\owlThing}\pesco x\}
  
    
    Op op1 = subClassOf().equals("*") ? null : pathToOpPath(OWL2.Thing.asNode(), subClassOf(), x);
  
    Op op2 = pathToOpPath(OWL2.topObjectProperty.asNode(), 
        seq(
          /*matStar ? C_STAR_UC : */ star(
            disj(spoeqp(),inverse())
          ),
          disj(
            invPath(ps.getOnp()),
            ps.getDom(),
            ps.getRng()
          ),
          subClassOf()
        ), 
        x);
    
    Op r = OpUnion.create(op1, op2);
    return r;
  }
  
  /**
   * Implements the macro univClass[x] defined as
   * { owl:Thing subClassOf x} 
   * UNION
   * { owl:topObjectProperty ((SpoEqp | Inv)∗  / (ˆowl:onProperty | rdfs:domain | rdfs:range ) / subClassOf) x} 
   * 
   * @param qlPathRewriter TODO
   * @param x class 
   * @param tempVar temporary Variable for 
   * @return
   */
  public Op univClass(Node x, Node tempVar) {
    // {\owlThing}\pesco x\}

    Op op1 = com.hp.hpl.jena.sparql.algebra.op.OpExtend.extend(com.hp.hpl.jena.sparql.algebra.op.OpTable.unit(), Var.alloc(tempVar), new NodeValueNode(OWL2.Thing.asNode()));
  
    Op op2 = pathToOpPath(OWL2.topObjectProperty.asNode(), 
        seq(
          /*matStar ? C_STAR_UC : */ star(
            disj(spoeqp(),inverse())
          ),
          disj(
            invPath(ps.getOnp()),
            ps.getDom(),
            ps.getRng()
          )
        ), 
        tempVar);
    
    Op r = OpUnion.create(op1, op2);
    return r;
  }

  Op twoMembers(Node x, Node y, Node z) {
    Node varW = newVariable(vargen);
    
    return join(
        pathToOpPath(x, seq(ps.getMembers(), /*matStar ? C_STAR_TM :*/ star(ps.getRest())), varW),
        pathToOpPath(varW, ps.getFirst(), y),
        pathToOpPath(varW, seq(ps.getRest()+"+", ps.getFirst()), z)
        );
  }

  /**
   * x ( sCO | eqC | ˆ eqC | intListMember | owl:someValuesFrom | 
   *       ( owl:onProperty / 
   *           (Inv | SpoEqp)∗  / 
   *           (ˆ owl:onProperty | rdfs:domain | rdfs:range )
   *       )
   *   ) ∗  ?C . 
   * {
   *   { ?C subClassOf owl:Nothing } UNION
   *   { ?C subClassOf ?D1 .
   *     {
   *       { ?C subClassOf ?D2 } UNION 
   *       univClass[ ?D2 ]
   *     } .
   *     {
   *       { ?D1 disjointClasses ?D2 } UNION
   *       { ?V rdf:type owl:AllDisjointClasses . twoMembers[ ?V , ?D1 , ?D2 ]}
   *     }
   *   } UNION
   *   { ?C ( owl:onProperty / (Inv | SpoEqp) ∗ ) ?P . 
   *     {
   *       { ?P subPropertyOf owl:bottomObjectProperty } UNION
   *       { ?P subPropertyOf ?Q1 
   *         {
   *           { ?P subPropertyOf ?Q2 } UNION 
   *           univProperty[ ?Q2 ]
   *         } .
   *         {
   *           { ?Q1 ( owl:propertyDisjointWith | ˆ owl:propertyDisjointWith ) ?Q2 } UNION
   *           { ?V rdf:type owl:AllDisjointProperties . twoMembers[ ?V , ?Q1 , ?Q2 ] }
   *         }
   *       }
   *     }
   *   }
   * }
   * @param x TODO
   */
  public Op emptyClass(Node x) {
    Node varC  = NodeFactory.createVariable(vargen.getNewVariable());
    Node varD1 = NodeFactory.createVariable(vargen.getNewVariable());
    Node varD2 = NodeFactory.createVariable(vargen.getNewVariable());
    Node varV  = NodeFactory.createVariable(vargen.getNewVariable());
    Node varP  = NodeFactory.createVariable(vargen.getNewVariable());
    Node varQ1 = NodeFactory.createVariable(vargen.getNewVariable());
    Node varQ2 = NodeFactory.createVariable(vargen.getNewVariable());
    
    return join(
        pathToOpPath(x, 
          /*matStar ? C_STAR_EC1 :*/ star(
            disj(
              ps.getSc(),
              ps.getEqc(),
              invPath(ps.getEqc()),
              intListMember(),
              ps.getSomeValFrom(),
              seq(
                ps.getOnp(),
                star(disj(inverse(), spoeqp())),
                disj(invPath(ps.getOnp()), ps.getDom(), ps.getRng())
              )
            )
          ), varC
        ),
        union(
          pathToOpPath(varC, subClassOf(), OWL2.Nothing.asNode()),
          join(
            pathToOpPath(varC, subClassOf(), varD1),
            union(
              pathToOpPath(varC, subClassOf(), varD2),
              univClass(varD2)
            ),
            union(
              pathToOpPath(varD1, disjointClassses(), varD2),
              join(
                pathToOpPath(varV, ps.getType(), OWL2.AllDisjointClasses.asNode()),
                twoMembers(varV, varD1, varD2)
              )
            )
          ),
          join(
            pathToOpPath(varC, seq(ps.getOnp(), /*matStar ? C_STAR_EC2 :*/ star(disj(inverse(), spoeqp()))), varP),
            union(
              pathToOpPath(varP, subPropertyOf(), OWL2.bottomObjectProperty.asNode()),
              join(
                pathToOpPath(varP, subPropertyOf(), varQ1),
                union(
                  pathToOpPath(varP, subPropertyOf(), varQ2),  
                  univProperty(varQ2)
                ),
                union(
                  join(
                    pathToOpPath(varQ1, disj(ps.getPropDisj(), invPath(ps.getPropDisj())), varQ2),
                    twoMembers(varV, varQ1, varQ2)
                  )
                )
              )
            )
          )
        )
      );
  }

  /**
   * Implements the emptyProperty[x] macro
   * 
   * x ( Inv | 
   *     SpoEqp | 
   *     ( ˆowl:onProperty / 
   *       ( sCO | eqC | ˆ eqC | intListMember | owl:someValuesFrom ) ∗ / 
   *       owl:onProperty 
   *     )
   *   ) ∗ ?P . 
   * {
   *   { ?P subPropertyOf owl:bottomObjectProperty } UNION
   *   { ?P subPropertyOf ?Q1 .
   *     {
   *       { ?P subPropertyOf ?Q2 } UNION 
   *       univProperty[ ?Q2 ]
   *     } . 
   *     {
   *       { ?Q1 ( owl:propertyDisjointWith | ˆ owl:propertyDisjointWith ) ?Q2 } UNION 
   *       { ?V rdf:type owl:AllDisjointProperties . twoMembers[ ?V , ?Q1 , ?Q2 ]}
   *     }
   *   } UNION 
   *   { ?P ((ˆ owl:onProperty | rdfs:domain | rdfs:range ) / subClassOf) ?C . 
   *     {
   *       { ?C subClassOf owl:Nothing } UNION
   *       { ?C subClassOf ?D1 .
   *         {
   *           { ?C subClassOf ?D2 } UNION 
   *           univClass[ ?D2 ]
   *         } . 
   *         {
   *           { ?D1 disjointClasses ?D2 } UNION
   *           { ?V rdf:type owl:AllDisjointClasses . twoMembers[ ?V , ?D1 , ?D2 ]}
   *         }
   *       }
   *     }
   *   }
   * }
   * @param x
   * 
   * @return
   */
  public Op emptyProperty(Node x) {
    Node varC  = NodeFactory.createVariable(vargen.getNewVariable());
    Node varD1 = NodeFactory.createVariable(vargen.getNewVariable());
    Node varD2 = NodeFactory.createVariable(vargen.getNewVariable());
    Node varV  = NodeFactory.createVariable(vargen.getNewVariable());
    Node varP  = NodeFactory.createVariable(vargen.getNewVariable());
    Node varQ1 = NodeFactory.createVariable(vargen.getNewVariable());
    Node varQ2 = NodeFactory.createVariable(vargen.getNewVariable());
    
    return join(
        pathToOpPath(x, 
          /*matStar ? C_STAR_EP : */star(disj(
            inverse(),
            spoeqp(),
            seq(
              ps.getOnp(),
              star(disj(ps.getSc(), ps.getEqc(), invPath(ps.getEqc()), intListMember(), ps.getSomeValFrom())),
              ps.getOnp()))), varP),
        union(
          pathToOpPath(varP, subPropertyOf(), OWL2.bottomObjectProperty.asNode()),
          join(
            pathToOpPath(varP, subPropertyOf(), varQ1),
            union(
                pathToOpPath(varP, subPropertyOf(), varQ2),
              univProperty(varQ2)
            ),
            union(
              pathToOpPath(varQ1, disj(ps.getPropDisj(), invPath(ps.getPropDisj())), varQ2),
              join(
                bgp(varV, ps.getType(), OWL2.AllDisjointProperties.asNode()),
                twoMembers(varV, varQ1, varQ2)
              )
            )
          ),
          join(
            pathToOpPath(varP, seq(disj(invPath(ps.getOnp()), ps.getDom(), ps.getRng()), subClassOf()), varC),
            union(
              pathToOpPath(varC, subClassOf(), OWL2.Nothing.asNode()),
              join(
                pathToOpPath(varC, subClassOf(), varD1),
                union(
                  pathToOpPath(varC, subClassOf(), varD2),
                  univClass(varD2)
                ),
                union(
                  pathToOpPath(varD1, disjointClassses(), varD2),
                  join(
                    bgp(varV, ps.getType(), OWL2.AllDisjointClasses.asNode()),
                    twoMembers(varV, varD1, varD2)
                  )
                )
              )
            )
          )
        )
      ); 
  }
  
  
}
