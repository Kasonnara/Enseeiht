/**
 * 
 */
package fr.n7.stl.block.ast.instruction.declaration;

import java.util.Iterator;
import java.util.List;

import fr.n7.stl.block.ast.Block;
import fr.n7.stl.block.ast.SemanticsUndefinedException;
import fr.n7.stl.block.ast.instruction.Instruction;
import fr.n7.stl.block.ast.scope.Declaration;
import fr.n7.stl.block.ast.scope.HierarchicalScope;
import fr.n7.stl.block.ast.scope.SymbolTable;
import fr.n7.stl.block.ast.type.AtomicType;
import fr.n7.stl.block.ast.type.Type;
import fr.n7.stl.tam.ast.Fragment;
import fr.n7.stl.tam.ast.Register;
import fr.n7.stl.tam.ast.TAMFactory;
import fr.n7.stl.util.BlockMetaData;
import fr.n7.stl.util.Logger;

/**
 * Abstract Syntax Tree node for a function declaration.
 * @author Marc Pantel
 */
public class FunctionDeclaration implements Instruction, Declaration {

	/**
	 * Name of the function
	 */
	protected String name;
	protected String id;
	
	/**
	 * AST node for the returned type of the function
	 */
	protected Type type;
	
	/**
	 * List of AST nodes for the formal parameters of the function
	 */
	protected List<ParameterDeclaration> parameters;
	
	/**
	 * @return the parameters
	 */
	public List<ParameterDeclaration> getParameters() {
		return parameters;
	}

	/**
	 * AST node for the body of the function
	 */
	protected Block body;

	protected int allocation_length;
	/**
	 * Builds an AST node for a function declaration
	 * @param _name : Name of the function
	 * @param _type : AST node for the returned type of the function
	 * @param _parameters : List of AST nodes for the formal parameters of the function
	 * @param _body : AST node for the body of the function
	 */
	public FunctionDeclaration(String _name, Type _type, List<ParameterDeclaration> _parameters, Block _body) {
		this.name = _name;
		this.type = _type;
		this.parameters = _parameters;
		this.body = _body;
		this.id = null;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String _result = this.type + " " + this.name + "( ";
		Iterator<ParameterDeclaration> _iter = this.parameters.iterator();
		if (_iter.hasNext()) {
			_result += _iter.next();
			while (_iter.hasNext()) {
				_result += " ," + _iter.next();
			}
		}
		return _result + " )" + this.body;
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
	
	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.instruction.Instruction#resolve(fr.n7.stl.block.ast.scope.Scope)
	 */
	@Override
	public boolean resolve(HierarchicalScope<Declaration> _scope) {
		boolean success = true;
		if (_scope.accepts(this)) {
			_scope.register(this);
			System.out.println("Register fct "+this.name);
		} else {
			success = false;
			Logger.error("Symbol " + this.name + " already registered.");
		}
		// Resolve parameter dans une table des symbole a part
		HierarchicalScope<Declaration> parameter_scope = new SymbolTable((SymbolTable) _scope);

		// Enregistrer les paramètres
		for (ParameterDeclaration p : this.parameters){
			success = p.type.resolve(parameter_scope) && success;
			// Vérifier si l'identifiant est disponnible
			if (parameter_scope.accepts(p)) {
				parameter_scope.register(p);
				System.out.println("Register parameter "+p.getName());
			} else {
				Logger.error("Symbol "+p.getName()+" already registered.");
				success = false;
			}
		}

		// Resolve body (en enregistrant aussi les paramètres)
		success = this.body.resolve(parameter_scope) && success;
		return success;
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.instruction.Instruction#checkType()
	 */
	@Override
	public BlockMetaData checkType() {
		BlockMetaData body_bmd = this.body.checkType();
		if (!body_bmd.isReturnAlwaysSet() && body_bmd.getReturnType() != AtomicType.VoidType){
			// Attention les branches d'execution du code de la fonction ne mènent pas toutes a un return
			Logger.warning("Warning : Some function branch doesn't contains return");
		}


		BlockMetaData result_bmd = new BlockMetaData();
		// Vérifier que le type de la fonction correspond a celui des return
		if (!body_bmd.getReturnType().compatibleWith(this.type)){
			result_bmd.setValideType(false);
			Logger.error("Return type ("+body_bmd.getReturnType().toString()+") doesn't match specified function type ("+this.type.toString()+")");
		}

		return  result_bmd;
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.instruction.Instruction#allocateMemory(fr.n7.stl.tam.ast.Register, int)
	 */
	@Override
	public int allocateMemory(Register _register, int _offset, int current_parameter_alloc) {
		// Calculer les offset des paramtre (en négatif car avant LB)
		this.allocation_length = 0;
		for (int k = this.parameters.size()-1; k>=0; k--) {
			ParameterDeclaration p =  this.parameters.get(k);
			this.allocation_length += p.getType().length();
			p.setOffset(- allocation_length);

		}
		this.body.allocateMemory(Register.LB, 3, this.allocation_length); // offset par defaut 3 pour laisser la place aux donnees de CALL et RETURN
		return 0;
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.instruction.Instruction#getCode(fr.n7.stl.tam.ast.TAMFactory)
	 */
	@Override
	public Fragment getCode(TAMFactory _factory) {
		this.id = Integer.toString(_factory.createLabelNumber());
		Fragment frag = _factory.createFragment();

		// Placer le code de la fonction (en indiquant la taille du type pour que le block ne l'efface pas
		frag.append(this.body.getCode(_factory, this.type.length()));
		// Placer un return s'il n'y en a pas (type void seulement)
		if (this.type == AtomicType.VoidType){
			frag.add(_factory.createReturn(0, allocation_length));
		}
		// Placer les etiquettes
		frag.addPrefix(this.getStartLabel()+":");
		frag.addSuffix(this.getEndLabel()+":");
		// Placer un jump au tout début pour eviter la fonction
		Fragment frag_jump = _factory.createFragment();
		frag_jump.add(_factory.createJump(this.getEndLabel()));

		// POP des paramètre (déjà fait par Return)

		frag_jump.append(frag);
		return frag_jump;
	}

	public String getStartLabel(){
		if (this.id == null){
			Logger.error("Function label was call before function is declared");
		}

		return "FUNC_"+this.name+"_"+this.id+"_START";
	}
	public String getEndLabel(){
		if (this.id == null){
			Logger.error("Function label was call before function is declared");
		}
		return "FUNC_"+this.name+"_"+this.id+"_END";
	}

}
