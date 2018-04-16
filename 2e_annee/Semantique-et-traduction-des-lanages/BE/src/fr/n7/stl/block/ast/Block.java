/**
 * 
 */
package fr.n7.stl.block.ast;

import java.util.List;

import fr.n7.stl.block.ast.instruction.Instruction;
import fr.n7.stl.block.ast.instruction.declaration.ParameterDeclaration;
import fr.n7.stl.block.ast.scope.Declaration;
import fr.n7.stl.block.ast.scope.HierarchicalScope;
import fr.n7.stl.block.ast.scope.Scope;
import fr.n7.stl.block.ast.scope.SymbolTable;
import fr.n7.stl.tam.ast.Fragment;
import fr.n7.stl.tam.ast.Register;
import fr.n7.stl.tam.ast.TAMFactory;
import fr.n7.stl.util.BlockMetaData;
import fr.n7.stl.util.Logger;

/**
 * Represents a Block node in the Abstract Syntax Tree node for the Bloc language.
 * Declares the various semantics attributes for the node.
 * 
 * A block contains declarations. It is thus a Scope even if a separate SymbolTable is used in
 * the attributed semantics in order to manage declarations.
 * 
 * @author Marc Pantel
 *
 */
public class Block {

	/**
	 * Sequence of instructions contained in a block.
	 */
	protected List<Instruction> instructions;

	/**
	 * Hierarchical structure of blocks.
	 * Link to the container block.
	 * 
	 */
	protected Block context;

	/**
	 * Remember total amount of memory allocated in order to free it at the endof the block
	 */
	protected int allocation_length;

	/**
	 * Constructor for a block contained in a _context block.
	 * @param _context Englobing block.
	 */
	public Block(Block _context, List<Instruction> _instructions) {
		this.instructions = _instructions;
		this.context = _context;
	}
	
	/**
	 * Constructor for a block root of the block hierarchy.
	 */
	public Block(List<Instruction> _instructions) {
		this( null, _instructions);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String _local = "";
		for (Instruction _instruction : this.instructions) {
			_local += _instruction;
		}
		return "{\n" + _local + "}\n" ;
	}
	
	/**
	 * Inherited Semantics attribute to check that all identifiers have been defined and
	 * associate all identifiers uses with their definitions.
	 * @param _scope Inherited Scope attribute that contains the defined identifiers.
	 * @return Synthesized Semantics attribute that indicates if the identifier used in the
	 * block have been previously defined.
	 */

	public boolean resolve(HierarchicalScope<Declaration> _scope) {
		HierarchicalScope<Declaration> local_scope = new SymbolTable((SymbolTable) _scope);
		boolean result = true;
		System.out.println("new bloc");
		for (Instruction instr : this.instructions) {
			boolean resultInstr = instr.resolve(local_scope);
			System.out.println("instruction [in bloc] : " +(resultInstr ? "OK" : "fail"));
			result = result && resultInstr;
		}
		System.out.println("end bloc");
		return result;
	}
	/**
	 * Synthesized Semantics attribute to check that an instruction if well typed.
	 * @return Synthesized BlockMetaData which store if the instrution is well typed and it's optionnal return type
	 */	
	public BlockMetaData checkType() {
		BlockMetaData bmd = new BlockMetaData();
		for (Instruction instr : this.instructions) {
			bmd = bmd.mergeSequence(instr.checkType());
		}
		return bmd;
	}

	/**
	 * Inherited Semantics attribute to allocate memory for the variables declared in the instruction.
	 * Synthesized Semantics attribute that compute the size of the allocated memory. 
	 * @param _register Inherited Register associated to the address of the variables.
	 * @param _offset Inherited Current offset for the address of the variables.
	 */	
	public int allocateMemory(Register _register, int _offset, int current_parameter_alloc) {
		//System.out.println("allocating Block");
		//System.out.println("register "+(_register==null?"null":_register.toString()));
		// propager récursivement en mémorisant l'offset
		this.allocation_length = 0;
		for (Instruction instr : this.instructions) {
			this.allocation_length += instr.allocateMemory(_register, _offset + this.allocation_length, current_parameter_alloc);
		}
		return 0;
	}

	/*  TODO OPTIMISATION  : permet d'economiser un POP (block retire lui même les paramètres)
	public int allocateMemory(Register _register, int _offset, List<ParameterDeclaration> block_parametres , int current_parameter_alloc) {
		// propager récursivement en mémorisant l'offset
		// Dans les paramètres
		this.allocation_length = 0;
		for (ParameterDeclaration p : block_parametres) {
			p.setOffset(allocation_length);
			this.allocation_length += p.getType().length();
		}
		// Dans les instructions
		for (Instruction instr : this.instructions) {
			this.allocation_length += instr.allocateMemory(_register, _offset + this.allocation_length, current_parameter_alloc);
		}
		return 0;
	}
	*/

	/**
	 * Inherited Semantics attribute to build the nodes of the abstract syntax tree for the generated TAM code.
	 * Synthesized Semantics attribute that provide the generated TAM code.
	 * @param _factory Inherited Factory to build AST nodes for TAM code.
	 * @return Synthesized AST for the generated TAM code.
	 */


	public Fragment getCode(TAMFactory _factory) {
		return this.getCode(_factory, 0);
	}
	public Fragment getCode(TAMFactory _factory, int returntype_size) {
		// propager récursivement
		Fragment frag = _factory.createFragment();
		for (Instruction instr : this.instructions) {
			frag.append(instr.getCode(_factory));
		}
		// POP des variables locales // TODO optimisation possible : ne rien faire si rien a supprimer
		Fragment frag_pop = _factory.createFragment();
		frag_pop.add(_factory.createPop(returntype_size, this.allocation_length));
		frag_pop.addComment("Clearing bloc context");

		// TODO free les éventuels tableaux

		frag.append(frag_pop);
		return frag;
	}


}
