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
 * Implementation of the Abstract Syntax Tree node  for an expression extracting the second component in a couple.
 * @author Marc Pantel
 *
 */
public class Second implements Expression {

	/**
	 * AST node for the expression whose value must whose second element is extracted by the expression.
	 */
	private Expression target;

	/**
	 * Remember the size of the type in the couple, used when generating code
	 */
	private int target_first_size;
	private int target_second_size;

	/**
	 * Builds an Abstract Syntax Tree node for an expression extracting the second component of a couple.
	 * @param _target : AST node for the expression whose value must whose second element is extracted by the expression.
	 */
	public Second(Expression _target) {
		this.target = _target;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "(snd" + this.target + ")";
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Expression#getType()
	 */
	@Override
	public Type getType() {
	Type secondType = null;

	Type type_couple = this.target.getType();
	if (type_couple instanceof CoupleType) {      // TODO Et pour les renommages du type couple?
		CoupleType ct = (CoupleType) type_couple;
		secondType = ct.getSecond();

		// Remember element size
		this.target_first_size = ct.getFirst().length();
		this.target_second_size = secondType.length();
	} else {
		secondType = AtomicType.ErrorType;
		Logger.error("Given expression isn't a couple " + type_couple.toString());
	}
		return secondType;
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.expression.Expression#resolve(fr.n7.stl.block.ast.scope.Scope)
	 */
	@Override
	public boolean resolve(HierarchicalScope<Declaration> _scope) {
		return this.target.resolve(_scope);
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
		Fragment frag_snd = _factory.createFragment();
		frag_snd.add(_factory.createPop(this.target_second_size, this.target_first_size));
		frag_snd.addComment("Remove first element");

		frag.append(frag_snd);
		return frag;
	}

}
