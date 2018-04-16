/**
 * 
 */
package fr.n7.stl.block.ast.expression;

import fr.n7.stl.block.ast.SemanticsUndefinedException;
import fr.n7.stl.block.ast.scope.Declaration;
import fr.n7.stl.block.ast.scope.HierarchicalScope;
import fr.n7.stl.block.ast.type.AtomicType;
import fr.n7.stl.block.ast.type.CoupleType;
import fr.n7.stl.block.ast.type.Type;
import fr.n7.stl.tam.ast.Fragment;
import fr.n7.stl.tam.ast.TAMFactory;
import fr.n7.stl.util.Logger;

/**
 * Abstract Syntax Tree node for an expression extracting the first component in a couple.
 * @author Marc Pantel
 *
 */
public class First implements Expression {

	/**
	 * AST node for the expression whose value must whose first element is extracted by the expression.
	 */
	protected Expression target;

	/**
	 * Remember the size of the type in the couple, used when generating code
	 */
	private int target_first_size;
	private int target_second_size;

	/**
	 * Builds an Abstract Syntax Tree node for an expression extracting the first component of a couple.
	 * @param _target : AST node for the expression whose value must whose first element is extracted by the expression.
	 */
	public First(Expression _target) {
		this.target = _target;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "(fst" + this.target + ")";
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.expression.Expression#resolve(fr.n7.stl.block.ast.scope.HierarchicalScope)
	 */
	@Override
	public boolean resolve(HierarchicalScope<Declaration> _scope) {
		return this.target.resolve(_scope);
	}
	
	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Expression#getType()
	 */
	@Override
	public Type getType() {
		Type firstType = null;

		Type type_couple = this.target.getType();
		if (type_couple instanceof CoupleType) {      // TODO Et pour les renommages du type couple?
			CoupleType ct = (CoupleType) type_couple;
			firstType = ct.getFirst();

			this.target_first_size = firstType.length();
			this.target_second_size = ct.getSecond().length();
		} else {
			firstType = AtomicType.ErrorType;
			Logger.error("Given expression isn't a couple " + type_couple.toString());
		}
		return firstType;
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Expression#getCode(fr.n7.stl.tam.ast.TAMFactory)
	 */
	@Override
	public Fragment getCode(TAMFactory _factory) {
		// TODO optimisation possible ne charger que le necessaire
		// Charger tout le couple
		Fragment frag = this.target.getCode(_factory);
		frag.addComment("Load couple");

		// Eliminer le premier element
		Fragment frag_fst = _factory.createFragment();
		frag_fst.add(_factory.createPop(0, this.target_second_size));
		frag_fst.addComment("Remove second element");

		frag.append(frag_fst);
		return frag;

	}

}
