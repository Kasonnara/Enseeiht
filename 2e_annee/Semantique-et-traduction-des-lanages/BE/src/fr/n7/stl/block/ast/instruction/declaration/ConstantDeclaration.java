/**
 * 
 */
package fr.n7.stl.block.ast.instruction.declaration;

import fr.n7.stl.block.ast.expression.Expression;
import fr.n7.stl.block.ast.instruction.Instruction;
import fr.n7.stl.block.ast.scope.Declaration;
import fr.n7.stl.block.ast.scope.HierarchicalScope;
import fr.n7.stl.block.ast.type.Type;
import fr.n7.stl.tam.ast.Fragment;
import fr.n7.stl.tam.ast.Register;
import fr.n7.stl.tam.ast.TAMFactory;
import fr.n7.stl.util.BlockMetaData;
import fr.n7.stl.util.Logger;

/**
 * Implementation of the Abstract Syntax Tree node for a constant declaration instruction.
 * @author Marc Pantel
 *
 */
public class ConstantDeclaration implements Instruction, Declaration {

	/**
	 * Name of the constant
	 */
	protected String name;
	
	/**
	 * AST node for the type of the constant
	 */
	protected Type type;
	
	/**
	 * AST node for the expression that computes the value of the constant
	 */
	protected Expression value;
	
	// TODO check if needed for constant
	protected Register register;
	protected int offset;

	/**
	 * Builds an AST node for a constant declaration
	 * @param _name : Name of the constant
	 * @param _type : AST node for the type of the constant
	 * @param _value : AST node for the expression that computes the value of the constant
	 */
	public ConstantDeclaration(String _name, Type _type, Expression _value) {
		this.name = _name;
		this.type = _type;
		this.value = _value;
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Declaration#getName()
	 */
	@Override
	public String getName() {
		return this.name;
	}
	
	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Declaration#getType()
	 */
	@Override
	public Type getType() {
		return this.type;
	}
	
	/**
	 * Provide the value associated to a name in constant declaration.
	 * @return Value from the declaration.
	 */
	public Expression getValue() {
		return this.value;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "const " + this.type + " " + this.name + " = " + this.value + ";\n";
	}
	
	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.instruction.Instruction#resolve(fr.n7.stl.block.ast.scope.Scope)
	 */
	@Override
	public boolean resolve(HierarchicalScope<Declaration> _scope) {
		boolean result = this.value.resolve(_scope);
		result = this.type.resolve(_scope) && result;
		if (_scope.accepts(this)) {
			_scope.register(this);
			System.out.println("Register cte "+this.name);
		}else{
			result= false;
			Logger.error("Symbol "+this.name+" already registered.");
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Instruction#checkType()
	 */
	@Override
	public BlockMetaData checkType() {
		boolean success = this.value.getType().compatibleWith(this.type);
		if (!success) {
			Logger.error("Checktype error: constant type ("+this.type.toString()+") and expression type ("+this.value.getType().toString()+") missmatch.");
		}
		return new BlockMetaData(success);
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Instruction#allocateMemory(fr.n7.stl.tam.ast.Register, int)
	 */
	@Override
	public int allocateMemory(Register _register, int _offset, int current_parameter_alloc) {
		// On ne devrait rien allouer mais, on ne peux pas
		// même comportement que les variables
		// enregister les information d'allocation
		this.register = _register;
		this.offset = _offset;
		// renvoyer la taille de la constante
		return this.getType().length();
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Instruction#getCode(fr.n7.stl.tam.ast.TAMFactory)
	 */
	@Override
	public Fragment getCode(TAMFactory _factory) {
		// TODO Optimisation possible pour les déclaration, empiler directement la valeur plutôt que de push puis store
		// PUSH de la taille requise
		int size = this.getType().length();
		Fragment frag = _factory.createFragment();
		frag.add(_factory.createPush(size));
		frag.addComment("New variable : "+this.name);
		// Code de l'expression
		frag.append(this.value.getCode(_factory));
		// STORE de la valeur
		//System.out.println("register "+(this.register==null?"null":this.register.toString()));
		frag.add(_factory.createStore(this.register, this.offset, size));

		return frag;
	}

}
