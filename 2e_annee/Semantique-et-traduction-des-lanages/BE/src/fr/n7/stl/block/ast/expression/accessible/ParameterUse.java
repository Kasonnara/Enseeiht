/**
 * 
 */
package fr.n7.stl.block.ast.expression.accessible;

import fr.n7.stl.block.ast.SemanticsUndefinedException;
import fr.n7.stl.block.ast.expression.AbstractUse;
import fr.n7.stl.block.ast.instruction.declaration.ParameterDeclaration;
import fr.n7.stl.block.ast.scope.Declaration;
import fr.n7.stl.tam.ast.Fragment;
import fr.n7.stl.tam.ast.Register;
import fr.n7.stl.tam.ast.TAMFactory;

/**
 * Implementation of the Abstract Syntax Tree node for a variable use expression.
 * @author Marc Pantel
 */
public class ParameterUse extends AbstractUse {
	
	protected ParameterDeclaration declaration;
	
	/**
	 * Creates a variable use expression Abstract Syntax Tree node.
	 */
	public ParameterUse(ParameterDeclaration _declaration) {
		this.declaration = _declaration;
	}
	
	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.expression.AbstractUse#getDeclaration()
	 */
	public Declaration getDeclaration() {
		return this.declaration;
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.expression.AbstractUse#getCode(fr.n7.stl.tam.ast.TAMFactory)
	 */
	public Fragment getCode(TAMFactory _factory) {
		// Empiler la valeur du paramètre
		Fragment _result = _factory.createFragment();
		_result.add(_factory.createLoad(
				Register.LB,
				this.declaration.getOffset(),
				this.declaration.getType().length()));
		_result.addComment("parameter use "+this.declaration.getName());
		return _result;
	}

}
