/**
 * 
 */
package fr.n7.stl.block.ast.instruction;

import fr.n7.stl.block.ast.Block;
import fr.n7.stl.block.ast.expression.Expression;
import fr.n7.stl.block.ast.scope.Declaration;
import fr.n7.stl.block.ast.scope.HierarchicalScope;
import fr.n7.stl.block.ast.type.AtomicType;
import fr.n7.stl.tam.ast.Fragment;
import fr.n7.stl.tam.ast.Register;
import fr.n7.stl.tam.ast.TAMFactory;
import fr.n7.stl.util.BlockMetaData;
import fr.n7.stl.util.Logger;

/**
 * Implementation of the Abstract Syntax Tree node for a conditional instruction.
 * @author Marc Pantel
 *
 */
public class Iteration implements Instruction {

	protected Expression condition;
	protected Block body;

	public Iteration(Expression _condition, Block _body) {
		this.condition = _condition;
		this.body = _body;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "while (" + this.condition + " )" + this.body;
	}
	
	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.instruction.Instruction#resolve(fr.n7.stl.block.ast.scope.Scope)
	 */
	@Override
	public boolean resolve(HierarchicalScope<Declaration> _scope) {
		boolean _cond = this.condition.resolve(_scope);
		boolean _corps = this.body.resolve(_scope);
		System.out.println("Repetition resolve condition: "+(_cond?"OK":"FAIL")+", coprs:"+(_corps?"OK":"FAIL"));
		return _cond && _corps;
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Instruction#checkType()
	 */
	@Override
	public BlockMetaData checkType() {
		// Vérifier que le condition est un boolean
		boolean condition_success = this.condition.getType().compatibleWith(AtomicType.BooleanType);
		if (! condition_success){
			Logger.error("A boolean condition is expected but a " + this.condition.getType().toString() + " was found.");
		}

		// vérifier le contenu du bloc
		return this.body.checkType().mergeParrallel(new BlockMetaData(condition_success));
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Instruction#allocateMemory(fr.n7.stl.tam.ast.Register, int)
	 */
	@Override
	public int allocateMemory(Register _register, int _offset, int current_parameter_alloc) {
		// propager l'appel récursif
		this.body.allocateMemory(_register, _offset, current_parameter_alloc);
		return 0;
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Instruction#getCode(fr.n7.stl.tam.ast.TAMFactory)
	 */
	@Override
	public Fragment getCode(TAMFactory _factory) {
		String id = Integer.toString(_factory.createLabelNumber());

		// Add conditionnal code
		Fragment frag = this.condition.getCode(_factory);
		frag.addPrefix("WHILE_"+id+":");
		frag.addComment("WHILE"+id+" condition");

		// JumpIf to end
		frag.add(_factory.createJumpIf("END_WHILE_"+id,0));
		// Add body code
		Fragment body_frag = this.body.getCode(_factory);
		body_frag.addComment("WHILE"+id+" body");
		frag.append(body_frag);
		// Add Jump to start
	frag.add(_factory.createJump("WHILE_"+id));
		// Add end label
		frag.addSuffix("END_WHILE_"+id+":");

		return frag;
	}

}
