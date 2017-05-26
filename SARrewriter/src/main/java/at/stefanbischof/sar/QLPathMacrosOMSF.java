/**
 * 
 */
package at.stefanbischof.sar;

import static at.stefanbischof.sar.QLPathRewriterHelper.bgp;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.NodeFactory;
import com.hp.hpl.jena.sparql.algebra.Op;
import com.hp.hpl.jena.vocabulary.RDF;

/**
 * @author Stefan Bischof
 *
 */
public class QLPathMacrosOMSF extends QLPathMacros {

	public static final String C_SC = "cache:sc";
	public static final String C_SP = "cache:sp";
	public static final String C_SC_OPT = C_SC + "?";
	public static final String C_SP_OPT = C_SP + "?";
	public static final String C_EP = QLPathRewriter.CACHE_PREFIX + "EmpyProperty";
	public static final String C_UP = QLPathRewriter.CACHE_PREFIX + "UnivProperty";
	public static final String C_EC = QLPathRewriter.CACHE_PREFIX + "EmpyClass";
	public static final String C_UC = QLPathRewriter.CACHE_PREFIX + "UnivClass";
	public static final String C_STAR_SPO = "cache:spo";

	public static final String C_STAR_SPO_OPT = QLPathMacrosOMSF.C_STAR_SPO + "?";

	/**
	 * @param ps
	 * @param vargen
	 */
	public QLPathMacrosOMSF(PropertySwitch ps, VariableGenerator vargen) {
		super(ps, vargen);
	}

	@Override
	public String subClassOf() {
		return C_SC_OPT;
	}

	@Override
	public String subPropertyOf() {
		return C_SP_OPT;
	}
	
	@Override
	public String subPropertyOfInvStar() {
	    return C_STAR_SPO_OPT;
	}

	@Override
	public Op univClass(Node n) {
		return bgp(n, RDF.type.asNode(), NodeFactory.createURI(C_UC));
	}

	@Override
	public Op univClass(Node x, Node tempVar) {
		return univClass(x);
	}

	@Override
	public Op emptyClass(Node n) {
		return bgp(n, RDF.type.asNode(), NodeFactory.createURI(C_EC));
	}

	@Override
	public Op univProperty(Node n) {
		return bgp(n, RDF.type.asNode(), NodeFactory.createURI(C_UP));
	}

	@Override
	public Op emptyProperty(Node n) {
		return bgp(n, RDF.type.asNode(), NodeFactory.createURI(C_EP));
	}
}
