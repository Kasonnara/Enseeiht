/**
 * 
 */
package fr.n7.stl.block.ast.expression.accessible;

import fr.n7.stl.block.ast.SemanticsUndefinedException;
import fr.n7.stl.block.ast.expression.AbstractArray;
import fr.n7.stl.block.ast.expression.Expression;
import fr.n7.stl.tam.ast.Fragment;
import fr.n7.stl.tam.ast.Library;
import fr.n7.stl.tam.ast.TAMFactory;

/**
 * Implementation of the Abstract Syntax Tree node for accessing an array element.
 * @author Marc Pantel
 *
 */
public class ArrayAccess extends AbstractArray implements AccessibleExpression {

	/**
	 * Construction for the implementation of an array element access expression Abstract Syntax Tree node.
	 * @param _array Abstract Syntax Tree for the array part in an array element access expression.
	 * @param _index Abstract Syntax Tree for the index part in an array element access expression.
	 */
	public ArrayAccess(Expression _array, Expression _index) {
		super(_array,_index);
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Expression#getCode(fr.n7.stl.tam.ast.TAMFactory)
	 */
	@Override
	public Fragment getCode(TAMFactory _factory) {
		//Empiler la valeur de l'adresse du tableau (c.a.d le contenu de la variable)
		Fragment frag = this.array.getCode(_factory);

		if (false) {
			// Methode bourinne NON OPTIMISÉ
			// Empiler le tableau
			// Calculer le nombre d'octet a supprimer avant
			// 		Empiler l'indice
			// 		Empiler la taille des éléments
			// 		Multiplier
			// Calculer le nombre d'octet a suprimer après
			// 		Empiler l'indice en partant de la fin
			//			Empiler la taille du tableau
			// 			Empiler l'indice
			//			Soustraire
			//		Empiler la taille des éléments
			//		Multiplier
			// Pop les éléments innutiles
			// 		Pop les éléments après
			// 			Pop (0) (taille elements après)
			// 		Pop les éléments avant
			//			Pop (taille d'un element) taille elements avant
		} else {
			// Methode optimisé, ne charger que l'élément souhaité
			// Empiler la taille d'un élément
			frag.add(_factory.createLoadL(this.getType().length())); // TODO mémoriser cette taille lors du TypeCheck plutôt que de réappeler getType
			// Empiler l'indice
			frag.append(this.index.getCode(_factory));
			// TODO vérifier la taille du tableau (pour contrer les bufferoverflow a l'execution)
			// Multiplier taille des éléments et indice
			frag.add(Library.IMul);
			// Ajouter l'adresse du début du tableau
			frag.add(Library.IAdd);
			// Charger le contenu de la case
			frag.add(_factory.createLoadI(this.getType().length())); // TODO mémoriser cette taille lors du TypeCheck plutôt que de réappeler getType
		}

		return frag;
	}

}
