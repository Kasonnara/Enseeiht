package fr.n7.stl.block.ast.expression;

import fr.n7.stl.block.ast.scope.Declaration;
import fr.n7.stl.block.ast.scope.HierarchicalScope;
import fr.n7.stl.block.ast.type.AtomicType;
import fr.n7.stl.block.ast.type.RecordType;
import fr.n7.stl.block.ast.type.Type;
import fr.n7.stl.block.ast.type.declaration.FieldDeclaration;
import fr.n7.stl.util.Logger;

import static fr.n7.stl.block.ast.type.NamedType.replaceNamedType;

/**
 * Common elements between left (Assignable) and right (Expression) end sides of
 * assignments. These elements share attributes, toString and getType methods.
 * 
 * @author Marc Pantel
 *
 */
public abstract class AbstractField implements Expression {

	protected Expression record;
	protected RecordType rt;
	protected String name;
	protected FieldDeclaration field;

	/**
	 * Construction for the implementation of a record field access expression
	 * Abstract Syntax Tree node.
	 * 
	 * @param _record
	 *            Abstract Syntax Tree for the record part in a record field
	 *            access expression.
	 * @param _name
	 *            Name of the field in the record field access expression.
	 */
	public AbstractField(Expression _record, String _name) {
		this.record = _record;
		this.name = _name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.record + "." + this.name;
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
		boolean valide = this.record.resolve(_scope);
		if (valide) {
			// Advance type check to ensure to not fail fields check
			Type type_record = replaceNamedType(this.record.getType());
			if (type_record instanceof RecordType) {
				rt = (RecordType) type_record;
				if (rt.contains(this.name)){
					this.field = rt.get(this.name);
				}else {
					valide = false;	
					Logger.error("Resolve Error: Given field isn't declared in the record type");
				}
			} else {
				valide = false;
				Logger.error("Resolve Error: Given expression isn't a record ("+type_record.toString()+" found).");
			}
		}
		return valide;
	}

	/**
	 * Synthesized Semantics attribute to compute the type of an expression.
	 * 
	 * @return Synthesized Type of the expression.
	 */
	public Type getType() {
		return this.field.getType();
	}

}