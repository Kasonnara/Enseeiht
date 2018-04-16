/**
 * 
 */
package fr.n7.stl.block.ast.expression;

import fr.n7.stl.block.ast.SemanticsUndefinedException;
import fr.n7.stl.block.ast.instruction.declaration.TypeDeclaration;
import fr.n7.stl.block.ast.scope.Declaration;
import fr.n7.stl.block.ast.scope.HierarchicalScope;
import fr.n7.stl.block.ast.type.AtomicType;
import fr.n7.stl.block.ast.type.NamedType;
import fr.n7.stl.block.ast.type.Type;
import fr.n7.stl.tam.ast.Fragment;
import fr.n7.stl.tam.ast.TAMFactory;
import fr.n7.stl.util.Logger;

/**
 * Abstract Syntax Tree node for an expression extracting the first component in
 * a couple.
 * 
 * @author Marc Pantel
 *
 */
public class TypeCast implements Expression {

	protected String type;

	/**
	 * AST node for the expression whose value must whose first element is
	 * extracted by the expression.
	 */
	protected Expression target;

	protected NamedType type_resolu;

	/**
	 * Builds an Abstract Syntax Tree node for an expression extracting the
	 * first component of a couple.
	 * 
	 * @param _target
	 *            : AST node for the expression whose value must whose first
	 *            element is extracted by the expression.
	 */
	public TypeCast(Expression _target, String _type) {
		this.target = _target;
		this.type = _type;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "((" + this.type + ") " + this.target + ")";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * fr.n7.stl.block.ast.expression.Expression#resolve(fr.n7.stl.block.ast.
	 * scope.HierarchicalScope)
	 */
	@Override
	public boolean resolve(HierarchicalScope<Declaration> _scope) {
		boolean valide = this.target.resolve(_scope);
		if (valide) {
			Declaration named_type = _scope.get(this.type);
			if (named_type instanceof TypeDeclaration) {
				if (((TypeDeclaration)named_type).getType() instanceof NamedType) {
					// TODO a tester
					this.type_resolu = (NamedType)(((TypeDeclaration)named_type).getType());
				} else {
					valide = false;
					Logger.error("Given expression  doesn't resolve to a NamedType declaration, cannot cast"); // TODO faire une exception propre
				}
			} else {
				valide = false;
				Logger.error("Given identifier doesn't resolve to a TypeDeclaration, cannot cast"); // TODO faire une exception propre
			}
		}
		return valide;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.n7.stl.block.ast.Expression#getType()
	 */
	@Override
	public Type getType() {
		// Vérifier que le typeCible est bien un soustype de l'expression
		if (this.type_resolu.compatibleWith(this.target.getType())) {
			return this.type_resolu;
		} else {
			Logger.error("Expression type ("+this.target.getType().toString()+") is not a supertype of "+this.type_resolu.toString()+" cannot cast.");
			return AtomicType.ErrorType;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.n7.stl.block.ast.Expression#getCode(fr.n7.stl.tam.ast.TAMFactory)
	 */
	@Override
	public Fragment getCode(TAMFactory _factory) {
		// Rien a faire car on ne peut caster que des NamedType qui sont donc fondamentalement identique dans la mémoire
		return _factory.createFragment();
	}

}
