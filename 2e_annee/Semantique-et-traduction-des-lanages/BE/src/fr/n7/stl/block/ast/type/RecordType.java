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
import fr.n7.stl.block.ast.scope.Scope;
import fr.n7.stl.block.ast.type.declaration.FieldDeclaration;
import fr.n7.stl.tam.ast.Fragment;
import fr.n7.stl.tam.ast.Library;
import fr.n7.stl.tam.ast.TAMFactory;
import fr.n7.stl.util.Logger;

/**
 * Implementation of the Abstract Syntax Tree node for a record type.
 * This one is a scope to allow an easy access to the fields.
 * @author Marc Pantel
 *
 */
public class RecordType implements Type, Declaration, Scope<FieldDeclaration> {

	private List<FieldDeclaration> fields;
	private String name;

	/**
	 * Constructor for a record type including fields.
	 * @param _name Name of the record type.
	 * @param _fields Sequence of fields to initialize the content of the record type.
	 */
	public RecordType(String _name, Iterable<FieldDeclaration> _fields) {
		this.name = _name;
		this.fields = new LinkedList<FieldDeclaration>();
		for (FieldDeclaration _field : _fields) {
			this.fields.add(_field);
		}
	}

	/**
	 * Constructor for an empty record type (i.e. without fields).
	 * @param _name Name of the record type.
	 */
	public RecordType(String _name) {
		this.name = _name;
		this.fields = new LinkedList<FieldDeclaration>();
	}

	/**
	 * Add a field to a record type.
	 * @param _field The added field.
	 */
	public void add(FieldDeclaration _field) {
		this.fields.add(_field);
	}

	/**
	 * Add a sequence of fields to a record type.
	 * @param _fields : Sequence of fields to be added.
	 */
	public void addAll(Iterable<FieldDeclaration> _fields) {
		for (FieldDeclaration _field : _fields) {
			this.fields.add(_field);
		}
	}
	
	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Type#equalsTo(fr.n7.stl.block.ast.Type)
	 */
	@Override
	public boolean equalsTo(Type _other) {
		/* vérifier qu'on a les même champs dans le même ordre */
		if (_other instanceof RecordType) {
			RecordType otherRecord = (RecordType)_other;
			// Vérifier qu'il ont le même nom? est ce que c'est possible/souhaité
			// boolean equal = this.name.equals(otherRecord.name) ;
			boolean equal = this.fields.size() >= otherRecord.fields.size();

			Iterator<FieldDeclaration> _iter = this.fields.iterator();
			Iterator<FieldDeclaration> _iterOther = this.fields.iterator();
			FieldDeclaration _current = null;
			FieldDeclaration other_current = null;
			while (_iterOther.hasNext() && (equal)) {
				if (_iter.hasNext()) {
					_current = _iter.next();
					other_current = _iterOther.next();
					// Rechercher si le champ existe a l'identique dans l'autre record
					equal = equal && _current.getType().equalsTo(other_current.getType());
				} else {
					equal = false;
				}
			}

			return equal;

		} else {
			return false;
		}
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Type#compatibleWith(fr.n7.stl.block.ast.Type)
	 */
	@Override
	public boolean compatibleWith(Type _other) {

		/* vérifier qu'on a les même champs dans le même ordre */
		if (_other instanceof RecordType) {
			RecordType otherRecord = (RecordType)_other;
			// Vérifier qu'il ont le même nom? est ce que c'est possible/souhaité
			// boolean equal = this.name.equals(otherRecord.name) ;
			boolean equal = this.fields.size() >= otherRecord.fields.size();

			Iterator<FieldDeclaration> _iter = this.fields.iterator();
			Iterator<FieldDeclaration> _iterOther = this.fields.iterator();
			FieldDeclaration _current = null;
			FieldDeclaration other_current = null;
			while (_iterOther.hasNext() && (equal)) {
				if (_iter.hasNext()) {
					_current = _iter.next();
					other_current = _iterOther.next();
					// Rechercher si le champ existe a l'identique dans l'autre record
					equal = equal && _current.getType().compatibleWith(other_current.getType());
				} else {
					equal = false;
				}
			}

			return equal;

		} else {
			return false;
		}
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Type#merge(fr.n7.stl.block.ast.Type)
	 */
	@Override
	public Type merge(Type _other) {
		if (this.compatibleWith(_other)) {
			return this;
		} if (_other.compatibleWith(this)) {
			return _other;
		} else {
			return AtomicType.ErrorType;
		}
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Scope#get(java.lang.String)
	 */
	@Override
	public FieldDeclaration get(String _name) {
		boolean _found = false;
		Iterator<FieldDeclaration> _iter = this.fields.iterator();
		FieldDeclaration _current = null;
		while (_iter.hasNext() && (! _found)) {
			_current = _iter.next();
			_found = _found || _current.getName().contentEquals(_name);
		}
		if (_found) {
			return _current;
		} else {
			return null;
		}
	}

	public int getFieldOffset(String _name) {
		// TODO calculer les offset a la création du record plutot que de les recalculer a chaque utilisation
		boolean _found = false;
		int offset = 0;
		Iterator<FieldDeclaration> _iter = this.fields.iterator();
		FieldDeclaration _current = null;
		while (_iter.hasNext() && (! _found)) {
			_current = _iter.next();
			_found = _found || _current.getName().contentEquals(_name);
			if (!_found){
				offset += _current.getType().length();
			}
		}
		if (_found) {
			return offset;
		} else {
			Logger.warning("Trying to get offset of a field that doesn't exists.");
			return -1;
		}
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Scope#contains(java.lang.String)
	 */
	@Override
	public boolean contains(String _name) {
		boolean _result = false;
		Iterator<FieldDeclaration> _iter = this.fields.iterator();
		while (_iter.hasNext() && (! _result)) {
			_result = _result || _iter.next().getName().contentEquals(_name);
		}
		return _result;
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Scope#accepts(fr.n7.stl.block.ast.Declaration)
	 */
	@Override
	public boolean accepts(FieldDeclaration _declaration) {
		return ! this.contains(_declaration.getName());
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Scope#register(fr.n7.stl.block.ast.Declaration)
	 */
	@Override
	public void register(FieldDeclaration _declaration) {
		if (this.accepts(_declaration)) {
			this.fields.add(_declaration);
		} else {
			throw new IllegalArgumentException();
		}
	}
	
	/**
	 * Build a sequence type by erasing the names of the fields.
	 * @return Sequence type extracted from record fields.
	 */
	public SequenceType erase() {
		SequenceType _local = new SequenceType();
		for (FieldDeclaration _field : this.fields) {
			_local.add(_field.getType());
		}
		return _local;
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Type#length()
	 */
	@Override
	public int length() {
		int _length = 0;
		for (FieldDeclaration f : this.fields) {
			_length += f.getType().length();
		}
		return _length;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String _result = "struct " + this.name + " { ";
		Iterator<FieldDeclaration> _iter = this.fields.iterator();
		if (_iter.hasNext()) {
			_result += _iter.next();
			while (_iter.hasNext()) {
				_result += " " + _iter.next();
			}
		}
		return _result + "}";
	}
	
	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.type.Type#resolve(fr.n7.stl.block.ast.scope.Scope)
	 */
	@Override
	public boolean resolve(HierarchicalScope<Declaration> _scope) {
		//TODO : Définition récursive

		boolean _result = true;
		for (FieldDeclaration f : this.fields) {
			_result = _result && f.getType().resolve(_scope);
		}
		return _result;
	}

	@Override
	public Fragment getPrintCode(TAMFactory _factory) {
		Fragment frag = AtomicType.getCodeStringMake(_factory, this.name+"{");
		frag.add(Library.SOut);
		for (int k = this.fields.size()-1; k >=0; k-- ){
			FieldDeclaration f = this.fields.get(k);
			frag.append( f.getType().getPrintCode(_factory));
		}
		frag.append(AtomicType.getCodeStringMake(_factory, "}"));
		frag.add(Library.SOut);

		return frag;
	}

	@Override
	public boolean canPrintCode() {
		boolean result = true;
		for (FieldDeclaration f : this.fields){
			result = result && f.getType().canPrintCode();
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.scope.Declaration#getName()
	 */
	@Override
	public String getName() {
		return this.name;
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.scope.Declaration#getType()
	 */
	@Override
	public Type getType() {
		return this;
	}
}
