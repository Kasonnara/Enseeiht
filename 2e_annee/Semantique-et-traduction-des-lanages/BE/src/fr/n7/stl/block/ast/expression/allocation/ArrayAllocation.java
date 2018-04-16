/**
 * 
 */
package fr.n7.stl.block.ast.expression.allocation;

import fr.n7.stl.block.ast.SemanticsUndefinedException;
import fr.n7.stl.block.ast.expression.Expression;
import fr.n7.stl.block.ast.scope.Declaration;
import fr.n7.stl.block.ast.scope.HierarchicalScope;
import fr.n7.stl.block.ast.type.ArrayType;
import fr.n7.stl.block.ast.type.AtomicType;
import fr.n7.stl.block.ast.type.Type;
import fr.n7.stl.tam.ast.Fragment;
import fr.n7.stl.tam.ast.Library;
import fr.n7.stl.tam.ast.TAMFactory;
import fr.n7.stl.util.Logger;

/**
 * @author Marc Pantel
 *
 */
public class ArrayAllocation implements Expression {

	protected Type element;
	protected Expression size;

	public ArrayAllocation(Type _element, Expression _size) {
		this.element = _element;
		this.size = _size;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "new " + this.element + "[ " + this.size + " ]"; 
	}
	
	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.expression.Expression#resolve(fr.n7.stl.block.ast.scope.Scope)
	 */
	@Override
	public boolean resolve(HierarchicalScope<Declaration> _scope) {
		boolean success = this.element.resolve(_scope);
		return this.size.resolve(_scope) && success;
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Expression#getType()
	 */
	@Override
	public Type getType() {
		Type index_type = this.size.getType();
		boolean success_index = index_type.compatibleWith(AtomicType.IntegerType);
		if (!success_index) {
			Logger.error("Type Check Error: integer type array size expected found " + index_type.toString());
			return AtomicType.ErrorType;
		}
		return new ArrayType(this.element);
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Expression#getCode(fr.n7.stl.tam.ast.TAMFactory)
	 */
	@Override
	public Fragment getCode(TAMFactory _factory) {
		// Empiler la taille du tableau
		Fragment frag = this.size.getCode(_factory);
		// Calculer la taille totale en octet requise
		//   Empiler la taille du type
		frag.add(_factory.createLoadL(this.element.length()));
		//   Multiplier
		frag.add(Library.IMul);
		// Allouer cette espace dans le tas
		frag.add(Library.MAlloc);

		return frag;
	}
}
