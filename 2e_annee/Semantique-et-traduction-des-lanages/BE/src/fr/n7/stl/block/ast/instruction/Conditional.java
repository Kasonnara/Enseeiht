/**
 * 
 */
package fr.n7.stl.block.ast.instruction;

import java.util.Optional;

import fr.n7.stl.block.ast.Block;
import fr.n7.stl.block.ast.SemanticsUndefinedException;
import fr.n7.stl.block.ast.expression.Expression;
import fr.n7.stl.block.ast.scope.Declaration;
import fr.n7.stl.block.ast.scope.HierarchicalScope;
import fr.n7.stl.block.ast.type.AtomicType;
import fr.n7.stl.block.ast.type.Type;
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
public class Conditional implements Instruction {

	protected Expression condition;
	protected Block thenBranch;
	protected Optional<Block> elseBranch;

	public Conditional(Expression _condition, Block _then, Block _else) {
		this.condition = _condition;
		this.thenBranch = _then;
		this.elseBranch = Optional.of(_else);
	}

	public Conditional(Expression _condition, Block _then) {
		this.condition = _condition;
		this.thenBranch = _then;
		this.elseBranch = Optional.empty();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "if (" + this.condition + " )" + this.thenBranch + ((this.elseBranch.isPresent())?(" else " + this.elseBranch.get()):"");
	}
	
	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.instruction.Instruction#resolve(fr.n7.stl.block.ast.scope.Scope)
	 */
	@Override
	public boolean resolve(HierarchicalScope<Declaration> _scope) {
		boolean _cond = this.condition.resolve(_scope);
		boolean _then = this.thenBranch.resolve(_scope);
		boolean _else = ((this.elseBranch.isPresent())?(this.elseBranch.get().resolve(_scope)):true);
		
		// System.out.println("IF THEN ELSE RESOLVE :" + (_cond?"OK":"FAIL")+","+(_then?"OK":"FAIL")+","+(_else?"OK":"FAIL"));
		return _cond && _then && _else;
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Instruction#checkType()
	 */
	@Override
	public BlockMetaData checkType() {
		// check then block
		BlockMetaData then_bmd = this.thenBranch.checkType();

		// check else block if it exist
		BlockMetaData else_bmd = this.elseBranch.isPresent() ? this.elseBranch.get().checkType() : new BlockMetaData();
		//Logger.warning("condition : "+(cond_success?"ok":"ko")+" then : "+(then_bmd?"ok":"ko")+" else : "+(else_bmd?"ok":"ko"));

		BlockMetaData result_bmd = then_bmd.mergeParrallel(else_bmd);


		// check condition is boolean
		boolean cond_success = this.condition.getType().compatibleWith(AtomicType.BooleanType);
		if (! cond_success){
			result_bmd.setValideType(false);
			Logger.error("Condition must be boolean, found "+this.condition.getType().toString());
		}

		return result_bmd;
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Instruction#allocateMemory(fr.n7.stl.tam.ast.Register, int)
	 */
	@Override
	public int allocateMemory(Register _register, int _offset, int current_parameter_alloc) {
		// propager l'appel récursif
		int offset = _offset;
		offset += this.thenBranch.allocateMemory(_register, offset, current_parameter_alloc);
		if (this.elseBranch.isPresent()) {
			offset += this.elseBranch.get().allocateMemory(_register, offset, current_parameter_alloc);
		}
		return 0;
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Instruction#getCode(fr.n7.stl.tam.ast.TAMFactory)
	 */
	@Override
	public Fragment getCode(TAMFactory _factory) {
		String id = Integer.toString(_factory.createLabelNumber());
		Fragment frag = _factory.createFragment();

		// Add conditionnal code
		Fragment condition_frag = this.condition.getCode(_factory);
		condition_frag.addComment("IF "+id+" condition");
		frag.append(condition_frag);
		// JumpIf to else
		frag.add(_factory.createJumpIf("ELSE_"+id,0));
		// Add then code
		Fragment then_frag = this.thenBranch.getCode(_factory);
		then_frag.addComment("IF "+id+" then");
		frag.append(then_frag);
		// Jump to end label
		frag.add(_factory.createJump("END_IF_"+id));
		frag.addSuffix("ELSE_"+id+":");
		// Add Else code
		if (this.elseBranch.isPresent()) { // TODO optimisation possible si le else n'est pas présent | inverser les labels else et end si possible)
			Fragment else_frag = this.elseBranch.get().getCode(_factory);
			else_frag.addComment("IF "+id+" else");
			frag.append(else_frag);
		}
		// Add end label
		frag.addSuffix("END_IF_"+id+":");

		return frag;
	}

}
