/**
 * 
 */
package fr.n7.stl.block.ast.expression;

import fr.n7.stl.block.ast.SemanticsUndefinedException;
import fr.n7.stl.block.ast.scope.Declaration;
import fr.n7.stl.block.ast.scope.HierarchicalScope;
import fr.n7.stl.block.ast.type.AtomicType;
import fr.n7.stl.block.ast.type.Type;
import fr.n7.stl.tam.ast.Fragment;
import fr.n7.stl.tam.ast.TAMFactory;
import fr.n7.stl.util.Logger;

/**
 * Abstract Syntax Tree node for a conditional expression.
 * @author Marc Pantel
 *
 */
public class ConditionalExpression implements Expression {

	/**
	 * AST node for the expression whose value is the condition for the conditional expression.
	 */
	protected Expression condition;
	
	/**
	 * AST node for the expression whose value is the then parameter for the conditional expression.
	 */
	protected Expression thenExpression;
	
	/**
	 * AST node for the expression whose value is the else parameter for the conditional expression.
	 */
	protected Expression elseExpression;
	
	/**
	 * Builds a binary expression Abstract Syntax Tree node from the left and right sub-expressions
	 * and the binary operation.
	 * @param _left : Expression for the left parameter.
	 * @param _operator : Binary Operator.
	 * @param _right : Expression for the right parameter.
	 */
	public ConditionalExpression(Expression _condition, Expression _then, Expression _else) {
		this.condition = _condition;
		this.thenExpression = _then;
		this.elseExpression = _else;
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.expression.Expression#resolve(fr.n7.stl.block.ast.scope.Scope)
	 */
	@Override
	public boolean resolve(HierarchicalScope<Declaration> _scope) {
		boolean _cond = this.condition.resolve(_scope);
		boolean _then = this.thenExpression.resolve(_scope);
		boolean _else = this.elseExpression.resolve(_scope);
		//System.out.println("IF THEN ELSE RESOLVE :" + (_cond?"OK":"FAIL")+","+(_then?"OK":"FAIL")+","+(_else?"OK":"FAIL"));
		return _cond && _then && _else;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "(" + this.condition + " ? " + this.thenExpression + " : " + this.elseExpression + ")";
	}
	
	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Expression#getType()
	 */
	@Override
	public Type getType() {
		Type condType = this.condition.getType();
		Type thenType = this.thenExpression.getType();
		Type elseType = this.elseExpression.getType();
		if (! condType.compatibleWith(AtomicType.BooleanType)){
			Logger.error("A boolean condition is expected but a " + condType.toString() + " was found.");
		}
		if (thenType.compatibleWith(elseType)){
			return elseType;
		}else if (elseType.compatibleWith(thenType)){
			return thenType;
		}else{
			Logger.error("Then block and Else block type missmatch (then:" + thenType.toString() + " != else:" + elseType.toString() + ")");
			return AtomicType.ErrorType;
		}
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Expression#getCode(fr.n7.stl.tam.ast.TAMFactory)
	 */
	@Override
	public Fragment getCode(TAMFactory _factory) {
		String id = Integer.toString(_factory.createLabelNumber());
		Fragment frag = _factory.createFragment();

		// Add conditionnal code
		Fragment condition_frag = this.condition.getCode(_factory);
		condition_frag.addComment("IF"+id+" condition");
		frag.append(condition_frag);
		// JumpIf to else
		frag.add(_factory.createJumpIf("ELSE_"+id,0));
		// Add then code
		Fragment then_frag = this.thenExpression.getCode(_factory);
		then_frag.addComment("IF"+id+" then");
		frag.append(then_frag);
		// Jump to end label
		frag.add(_factory.createJump("END_IF_"+id));
		frag.addSuffix("ELSE_"+id+":");
		// Add Else code
		Fragment else_frag = this.elseExpression.getCode(_factory);
		else_frag.addComment("IF"+id+" else");
		frag.append(else_frag);
		// Add end label
		frag.addSuffix("END_IF_"+id+":");

		return frag;
	}

}
