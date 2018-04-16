// TODO Normalement innutile après modif du sujet 
package fr.n7.stl.block.ast.expression;

import fr.n7.stl.block.ast.instruction.declaration.VariableDeclaration;
import fr.n7.stl.block.ast.scope.Declaration;
import fr.n7.stl.block.ast.scope.HierarchicalScope;
import fr.n7.stl.block.ast.type.AtomicType;
import fr.n7.stl.block.ast.type.Type;
import fr.n7.stl.util.Logger;

/**
 * Common elements between left (Assignable) and right (Expression) end sides of assignments. These elements
 * share attributes, toString and getType methods.
 * @author Marc Pantel
 *
 */
public abstract class AbstractVariable implements Expression {

	/**
	 * Declaration of the variable.
	 */
	protected VariableDeclaration declaration;
	
	/**
	 * Name of the variable.
	 */
	protected String name;
	
	/**
	 * Creates a variable related expression Abstract Syntax Tree node.
	 * @param _name Name of the variable.
	 */
	public AbstractVariable(String _name) {
		this.name = _name;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		if (this.declaration == null) {
			return (this.name);
		} else {
			return ("@{" + this.declaration.getName() + ((this.declaration.getType() != null)?(":" + this.declaration.getType()):"") + ((this.declaration.getRegister() != null)?(" in " + this.declaration.getOffset() + "[" + this.declaration.getRegister() + "]"):"") + "}");
		}
	}
	
	/**
	 * Resolve the definition used inside an instruction.
	 * @param _scope Inherited scope used to resolve the identifiers used in the instruction.
	 */
	@Override
	public boolean resolve(HierarchicalScope<Declaration> _scope) {
		Logger.error("Maybe never used");
		if (((HierarchicalScope<Declaration>)_scope).knows(this.name)) {
			Declaration _declaration = _scope.get(this.name);
			if (_declaration instanceof VariableDeclaration) {
				this.declaration = ((VariableDeclaration) _declaration);
				return true;
			} else {
				Logger.error("The declaration for " + this.name + " is of the wrong kind.");
				return false;
			}
		} else {
			Logger.error("The identifier " + this.name + " has not been found.");
			return false;	
		}
	}


	/**
	 * Synthesized Semantics attribute to compute the type of an expression.
	 * @return Synthesized Type of the expression.
	 */
	public Type getType() {
		if (declaration != null) {
			return declaration.getType();
		} else {
			Logger.error("The identifier " + this.name + " has not been found and has been assigned the Error type.");
			return AtomicType.ErrorType;
		}
	}

}