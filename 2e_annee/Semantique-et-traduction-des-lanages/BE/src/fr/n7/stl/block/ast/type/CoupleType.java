/**
 * 
 */
package fr.n7.stl.block.ast.type;

import fr.n7.stl.block.ast.scope.Declaration;
import fr.n7.stl.block.ast.scope.HierarchicalScope;
import fr.n7.stl.tam.ast.Fragment;
import fr.n7.stl.tam.ast.Library;
import fr.n7.stl.tam.ast.Register;
import fr.n7.stl.tam.ast.TAMFactory;

/**
 * Implementation of the Abstract Syntax Tree node for a couple type.
 * @author Marc Pantel
 *
 */
public class CoupleType implements Type {

	private Type first;
	private Type second;

	/**
	 * Builds a couple type.
	 * @param _first Type of the first part of the couple.
	 * @param _second Type of the second part of the couple.
	 */
	public CoupleType(Type _first, Type _second) {
		this.first = _first;
		this.second = _second;
	}

	/* (non-Javadoc)
	 * @see fr.n7.block.ast.Type#equalsTo(fr.n7.block.ast.Type)
	 */
	@Override
	public boolean equalsTo(Type _other) {
		if (_other instanceof CoupleType) {
			return this.first.equalsTo(((CoupleType) _other).first)
					&& this.second.equalsTo(((CoupleType) _other).second);
		} else {
			return false;
		}
	}

	/* (non-Javadoc)
	 * @see fr.n7.block.ast.Type#compatibleWith(fr.n7.block.ast.Type)
	 */
	@Override
	public boolean compatibleWith(Type _other) {
		if (_other instanceof CoupleType) {
			return this.first.compatibleWith(((CoupleType) _other).first)
					&& this.second.compatibleWith(((CoupleType) _other).second);
		} else {
			return false;
		}
	}

	/* (non-Javadoc)
	 * @see fr.n7.block.ast.Type#merge(fr.n7.block.ast.Type)
	 */
	@Override
	public Type merge(Type _other) {
		if (_other instanceof CoupleType) {
			return new CoupleType( 
					this.first.merge(((CoupleType) _other).first),
					this.second.merge(((CoupleType) _other).second));
		} else {
			return AtomicType.ErrorType;
		}
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Type#length()
	 */
	@Override
	public int length() {
		return this.first.length() + this.second.length();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "< " + this.first + ", " + this.second + ">";
	}

	/**
	 * Provide the type of the first element in the couple.
	 * @return Type of the first element.
	 */
	public Type getFirst() {
		return this.first;
	}

	/**
	 * Provide the type of the second element in the couple.
	 * @return Type of the second element.
	 */
	public Type getSecond() {
		return this.second;
	}
	
	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.type.Type#resolve(fr.n7.stl.block.ast.scope.Scope)
	 */
	@Override
	public boolean resolve(HierarchicalScope<Declaration> _scope) {
		boolean _first = this.first.resolve(_scope);
		boolean _second = this.second.resolve(_scope);
		return _first && _second;
	}

	@Override
	public Fragment getPrintCode(TAMFactory _factory) {
		Fragment frag = _factory.createFragment();
		frag.append(AtomicType.getCodeStringMake(_factory, "<"));
		frag.add(Library.SOut);

		frag.add(_factory.createLoad(Register.ST, -this.length(), this.first.length()));
		frag.append(this.first.getPrintCode(_factory));
		frag.add(_factory.createPop(this.second.length(), this.first.length()));

		frag.append(AtomicType.getCodeStringMake(_factory, ","));
		frag.add(Library.SOut);

		frag.append(this.second.getPrintCode(_factory));

		frag.append(AtomicType.getCodeStringMake(_factory, ">"));
		frag.add(Library.SOut);

		return frag;
	}

	@Override
	public boolean canPrintCode() {
		return this.first.canPrintCode() && this.second.canPrintCode();
	}


}
