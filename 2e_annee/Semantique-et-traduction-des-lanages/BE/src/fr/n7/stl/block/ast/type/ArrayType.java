/**
 * 
 */
package fr.n7.stl.block.ast.type;

import fr.n7.stl.block.ast.SemanticsUndefinedException;
import fr.n7.stl.block.ast.scope.Declaration;
import fr.n7.stl.block.ast.scope.HierarchicalScope;
import fr.n7.stl.tam.ast.Fragment;
import fr.n7.stl.tam.ast.TAMFactory;

/**
 * @author Marc Pantel
 *
 */
public class ArrayType implements Type {

	protected Type element;

	public ArrayType(Type _element) {
		this.element = _element;
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Type#equalsTo(fr.n7.stl.block.ast.Type)
	 */
	@Override
	public boolean equalsTo(Type _other) {
		if (_other instanceof ArrayType) {
			return this.element.equalsTo(((ArrayType)_other).element);
		} else {
			return false;
		}
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Type#compatibleWith(fr.n7.stl.block.ast.Type)
	 */
	@Override
	public boolean compatibleWith(Type _other) {
		if (_other instanceof ArrayType) {
			return this.element.compatibleWith(((ArrayType)_other).element);
		} else {
			return false;
		}
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Type#merge(fr.n7.stl.block.ast.Type)
	 */
	@Override
	public Type merge(Type _other) {
		if (_other instanceof ArrayType) {
			return new ArrayType(this.element.merge(((ArrayType)_other).element));
		} else {
			return AtomicType.ErrorType;
		}
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Type#length(int)
	 */
	@Override
	public int length() {
		// on ne connait pas sa taille a la compilation, donc pointeur vers le tas.
		return 1;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "(" + this.element + " [])";
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.type.Type#resolve(fr.n7.stl.block.ast.scope.Scope)
	 */
	@Override
	public boolean resolve(HierarchicalScope<Declaration> _scope) {
		return this.element.resolve(_scope);
	}

	@Override
	public Fragment getPrintCode(TAMFactory _factory) {

		// Empiler tout le tableau // TODO on ne connait pas sa taille!
		// Ajouter le cde de print du type autant de fois que d'élements // TODO très peu optimisé
		return null;
	}

	@Override
	public boolean canPrintCode() {
		//this.element.canPrintCode();
		return false;
	}

	/**
	 * @return Type of the elements in the array.
	 */
	public Type getType() {

		//System.out.println("ArrayType type elt : "+this.element.toString());
		return this.element;
	}

}
