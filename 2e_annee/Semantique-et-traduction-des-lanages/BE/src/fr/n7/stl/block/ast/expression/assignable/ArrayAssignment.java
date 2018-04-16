/**
 * 
 */
package fr.n7.stl.block.ast.expression.assignable;

import fr.n7.stl.block.ast.SemanticsUndefinedException;
import fr.n7.stl.block.ast.expression.AbstractArray;
import fr.n7.stl.block.ast.expression.Expression;
import fr.n7.stl.tam.ast.Fragment;
import fr.n7.stl.tam.ast.Library;
import fr.n7.stl.tam.ast.TAMFactory;

/**
 * Abstract Syntax Tree node for an expression whose computation assigns a cell in an array.
 * @author Marc Pantel
 */
public class ArrayAssignment extends AbstractArray implements AssignableExpression {

	/**
	 * Construction for the implementation of an array element assignment expression Abstract Syntax Tree node.
	 * @param _array Abstract Syntax Tree for the array part in an array element assignment expression.
	 * @param _index Abstract Syntax Tree for the index part in an array element assignment expression.
	 */
	public ArrayAssignment(AssignableExpression _array, Expression _index) {
		super(_array, _index);
	}
	
	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.impl.ArrayAccessImpl#getCode(fr.n7.stl.tam.ast.TAMFactory)
	 */
	@Override
	public Fragment getCode(TAMFactory _factory) {

		// Empiler l'indice du tableau
		Fragment frag = this.index.getCode(_factory);
		// Empiler la taille du tableau
		frag.add(_factory.createLoadL(this.getType().length())); // TODO mémoriser cette taille lors du TypeCheck plutôt que de réappeler getType
		// TODO vérifier la taille du tableau (pour contrer les bufferoverflow a l'execution)
		// Calculer le décalage
		frag.add(Library.IMul);

		// Empiler l'adresse de la variable (pointant vers le tableau)
		frag.append(this.array.getCode(_factory));
		// extraire l'adresse du tableau
		frag.add(_factory.createLoadI(1));
		//System.out.println("marker 2");
		// Calculer l'adresse finale de l'élément
		frag.add(Library.IAdd);

		return frag;
	}

	
}
