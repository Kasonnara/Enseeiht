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

import static fr.n7.stl.block.ast.type.NamedType.replaceNamedType;

/**
 * Abstract Syntax Tree node for a variable declaration instruction.
 * @author Marc Pantel
 *
 */
public class VariableDeclaration implements Declaration, Instruction {

	/**
	 * Name of the declared variable.
	 */
	protected String name;
	
	/**
	 * AST node for the type of the declared variable.
	 */
	protected Type type;
	
	/**
	 * AST node for the initial value of the declared variable.
	 */
	protected Expression value;
	
	/**
	 * Address register that contains the base address used to store the declared variable.
	 */
	protected Register register;
	
	/**
	 * Offset from the base address used to store the declared variable
	 * i.e. the size of the memory allocated to the previous declared variables
	 */
	protected int offset;
	
	/**
	 * Creates a variable declaration instruction node for the Abstract Syntax Tree.
	 * @param _name Name of the declared variable.
	 * @param _type AST node for the type of the declared variable.
	 * @param _value AST node for the initial value of the declared variable.
	 */
	public VariableDeclaration(String _name, Type _type, Expression _value) {
		this.name = _name;
		this.type = _type;
		this.value = _value;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.type + " " + this.name + " = " + this.value + ";\n";
	}

	/**
	 * Synthesized semantics attribute for the type of the declared variable.
	 * @return Type of the declared variable.
	 */
	public Type getType() {
		return this.type;
	}

	/* (non-Javadoc)
	 * @see fr.n7.block.ast.VariableDeclaration#getName()
	 */
	@Override
	public String getName() {
		return this.name;
	}

	/**
	 * Synthesized semantics attribute for the register used to compute the address of the variable.
	 * @return Register used to compute the address where the declared variable will be stored.
	 */
	public Register getRegister() {
		return this.register;
	}
	
	/**
	 * Synthesized semantics attribute for the offset used to compute the address of the variable.
	 * @return Offset used to compute the address where the declared variable will be stored.
	 */
	public int getOffset() {
		return this.offset;
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.instruction.Instruction#resolve(fr.n7.stl.block.ast.scope.Scope)
	 */
	@Override
	public boolean resolve(HierarchicalScope<Declaration> _scope) {
		// Vérifier l'expression et le type
		boolean result = this.value.resolve(_scope);
		result = this.type.resolve(_scope) && result;
		// Vérifier si l'identifiant est disponnible
		if (_scope.accepts(this)) {
			_scope.register(this);
			System.out.println("Register var "+this.name);
		} else {
			Logger.error("Symbol "+this.name+" already registered.");
			result = false;
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Instruction#checkType()
	 */
	@Override
	public BlockMetaData checkType() {
		//System.out.println("VariableDeclaration "+this.getName());
		boolean success = replaceNamedType(this.value.getType()).compatibleWith(replaceNamedType(this.type));
		if (!success) {
			Logger.error("Checktype error: Variable type ("+this.type.toString()+") and expression type ("+this.value.getType().toString()+") missmatch.");
		}
		return new BlockMetaData(success);
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Instruction#allocateMemory(fr.n7.stl.tam.ast.Register, int)
	 */
	@Override
	public int allocateMemory(Register _register, int _offset, int current_parameter_alloc) {
		//System.out.println("allocating variableDeclaration");
		//System.out.println("register "+(_register==null?"null":_register.toString()));
		
		// enregister les information d'allocation
		this.register = _register;
		this.offset = _offset;
		// renvoyer la taille de la variable
		return this.getType().length();
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Instruction#getCode(fr.n7.stl.tam.ast.TAMFactory)
	 */
	@Override
	public Fragment getCode(TAMFactory _factory) {
		//System.out.println("VariableDeclaration");
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
