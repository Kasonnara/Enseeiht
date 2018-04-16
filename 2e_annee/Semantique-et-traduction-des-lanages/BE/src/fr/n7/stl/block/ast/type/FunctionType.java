/**
 * 
 */
package fr.n7.stl.block.ast.type;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import fr.n7.stl.block.ast.SemanticsUndefinedException;
import fr.n7.stl.block.ast.scope.Declaration;
import fr.n7.stl.block.ast.scope.HierarchicalScope;
import fr.n7.stl.block.ast.type.declaration.FieldDeclaration;
import fr.n7.stl.tam.ast.Fragment;
import fr.n7.stl.tam.ast.TAMFactory;
import fr.n7.stl.util.Logger;

import static fr.n7.stl.block.ast.type.NamedType.replaceNamedType;

/**
 * Implementation of the Abstract Syntax Tree node for a function type.
 * @author Marc Pantel
 *
 */
public class FunctionType implements Type {

	private Type result;
	private List<Type> parameters;

	public FunctionType(Type _result, Iterable<Type> _parameters) {
		this.result = _result;
		this.parameters = new LinkedList<Type>();
		for (Type _type : _parameters) {
			this.parameters.add(_type);
		}
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Type#equalsTo(fr.n7.stl.block.ast.Type)
	 */
	@Override
	public boolean equalsTo(Type _other) {
		boolean result = true;
		Type other_base_type = replaceNamedType(_other);
		if (!(other_base_type instanceof FunctionType)){
			Logger.error("Expect a function got "+ _other.toString());
			result = false;
		} else {
			FunctionType other_function_type = (FunctionType)_other;
			result = result && this.parameters.size() == other_function_type.parameters.size();
			Iterator<Type> _iter = this.parameters.iterator();
			Iterator<Type> other_iter = other_function_type.parameters.iterator();
			while (_iter.hasNext() && other_iter.hasNext() && result){
				result = result && (_iter.next().equalsTo(other_iter.next()));
			}
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Type#compatibleWith(fr.n7.stl.block.ast.Type)
	 */
	@Override
	public boolean compatibleWith(Type _other) {
		boolean result = true;
		Type other_base_type = replaceNamedType(_other);
		if (!(other_base_type instanceof FunctionType)){
			Logger.error("Expect a function got "+ _other.toString());
			result = false;
		} else {
			FunctionType other_function_type = (FunctionType)_other;
			result = result && this.parameters.size() == other_function_type.parameters.size();
			Iterator<Type> _iter = this.parameters.iterator();
			Iterator<Type> other_iter = other_function_type.parameters.iterator();
			while (_iter.hasNext() && other_iter.hasNext() && result){
				result = result && (_iter.next().compatibleWith(other_iter.next()));
			}
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Type#merge(fr.n7.stl.block.ast.Type)
	 */
	@Override
	public Type merge(Type _other) {
		if (this.compatibleWith(_other)){
			return _other;
		} else if (_other.compatibleWith(this)){
			return this;
		} else {
			return AtomicType.ErrorType;
		}
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Type#length(int)
	 */
	@Override
	public int length() {
		return 0;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String _result = "(";
		Iterator<Type> _iter = this.parameters.iterator();
		if (_iter.hasNext()) {
			_result += _iter.next();
			while (_iter.hasNext()) {
				_result += " ," + _iter.next();
			}
		}
		return _result + ") -> " + this.result;
	}
	
	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.type.Type#resolve(fr.n7.stl.block.ast.scope.Scope)
	 */
	@Override
	public boolean resolve(HierarchicalScope<Declaration> _scope) {
		// TODO :  Never use?
		boolean result = this.result.resolve(_scope);
		for (Type t : this.parameters){
			result = t.resolve(_scope) && result;
		}

		return result;

	}

	@Override
	public Fragment getPrintCode(TAMFactory _factory) {
		return null;
	}

	@Override
	public boolean canPrintCode() {
		return false;
	}

}
