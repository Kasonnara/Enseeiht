package fr.n7.stl.block.ast.expression;

import fr.n7.stl.block.ast.scope.Declaration;
import fr.n7.stl.block.ast.scope.HierarchicalScope;
import fr.n7.stl.block.ast.type.AtomicType;
import fr.n7.stl.block.ast.type.ArrayType;
import fr.n7.stl.block.ast.type.Type;
import fr.n7.stl.util.Logger;

import static fr.n7.stl.block.ast.type.NamedType.replaceNamedType;

/**
 * Common elements between left (Assignable) and right (Expression) end sides of assignments. These elements
 * share attributes, toString and getType methods.
 * @author Marc Pantel
 *
 */
public abstract class AbstractArray implements Expression {

	/**
	 * AST node that represents the expression whose result is an array.
	 */
	protected Expression array;
	
	/**
	 * AST node that represents the expression whose result is an integer value used to index the array.
	 */
	protected Expression index;
	
	/**
	 * Construction for the implementation of an array element access expression Abstract Syntax Tree node.
	 * @param _array Abstract Syntax Tree for the array part in an array element access expression.
	 * @param _index Abstract Syntax Tree for the index part in an array element access expression.
	 */
	public AbstractArray(Expression _array, Expression _index) {
		this.array = _array;
		this.index = _index;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return (this.array + "[ " + this.index + " ]");
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.expression.Expression#resolve(fr.n7.stl.block.ast.scope.HierarchicalScope)
	 */
	@Override
	public boolean resolve(HierarchicalScope<Declaration> _scope) {
		return this.array.resolve(_scope) && this.index.resolve(_scope);
	}
	
	/**
	 * Synthesized Semantics attribute to compute the type of an expression.
	 * @return Synthesized Type of the expression.
	 */
	public Type getType() {
		//System.out.println("AbstractArray (ArrayAssignment)");
		Type element_type = AtomicType.ErrorType;
		Type arrType =  replaceNamedType(this.array.getType());
		if (!(arrType instanceof ArrayType)){
			Logger.error("Type Check Error: ArrayType expected found " + arrType.toString() + ", this type isn't indexable.");
		}else{
			// Renvoyer le type des éléments du tableau
			element_type = ((ArrayType) arrType).getType();
		}
		Type indexType = this.index.getType();
		if (!(indexType.compatibleWith(AtomicType.IntegerType ))){
			Logger.error("Type Check Error: integer index expected found " + indexType.toString());
			element_type = AtomicType.ErrorType;
		}
		return element_type;
	}

}