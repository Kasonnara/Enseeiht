/**
 * 
 */
package fr.n7.stl.block.ast.instruction;

import fr.n7.stl.block.ast.SemanticsUndefinedException;
import fr.n7.stl.block.ast.expression.Expression;
import fr.n7.stl.block.ast.scope.Declaration;
import fr.n7.stl.block.ast.scope.HierarchicalScope;
import fr.n7.stl.block.ast.type.AtomicType;
import fr.n7.stl.block.ast.type.CoupleType;
import fr.n7.stl.block.ast.type.Type;
import fr.n7.stl.tam.ast.Fragment;
import fr.n7.stl.tam.ast.Library;
import fr.n7.stl.tam.ast.Register;
import fr.n7.stl.tam.ast.TAMFactory;
import fr.n7.stl.util.BlockMetaData;
import fr.n7.stl.util.Logger;

import static fr.n7.stl.block.ast.type.NamedType.replaceNamedType;

/**
 * Implementation of the Abstract Syntax Tree node for a printer instruction.
 * @author Marc Pantel
 *
 */
public class Printer implements Instruction {

	protected Expression parameter;

	public Printer(Expression _value) {
		this.parameter = _value;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "print " + this.parameter + ";\n";
	}
	
	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.instruction.Instruction#resolve(fr.n7.stl.block.ast.scope.Scope)
	 */
	@Override
	public boolean resolve(HierarchicalScope<Declaration> _scope) {
		boolean result = this.parameter.resolve(_scope);
		return result;
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Instruction#checkType()
	 */
	@Override
	public BlockMetaData checkType() {
		//System.out.println("Printer");

		Type printed_type= this.parameter.getType();
		boolean success = printed_type.canPrintCode();
		if (!success) {
			Logger.error("Type non pris en charge par Print");
		}

		return new BlockMetaData(success);
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Instruction#allocateMemory(fr.n7.stl.tam.ast.Register, int)
	 */
	@Override
	public int allocateMemory(Register _register, int _offset, int current_parameter_alloc) {
		return 0;
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Instruction#getCode(fr.n7.stl.tam.ast.TAMFactory)
	 */
	@Override
	public Fragment getCode(TAMFactory _factory) {

		/*
		Fragment frag = _factory.createFragment();
		frag.append(this.parameter.getCode(_factory));
		frag.addComment("Print (expression)");
		// Print adapté selon le type
		Type printed_type = this.parameter.getType();
		if (printed_type == AtomicType.StringType) { 
			frag.add(Library.SOut); // TODO Pas sur
		} else if (printed_type == AtomicType.IntegerType) {
			frag.add(Library.IOut); // TODO Pas sur
		}else if (replaceNamedType(printed_type) instanceof CoupleType) {
			// Print d'un couple
			//   print '<'
			frag.add(_factory.createLoadL(1));
			frag.add(Library.SAlloc);
			frag.add(_factory.createLoadL(60));
			frag.add(_factory.createLoad(Register.ST,-2, 1));
			frag.add(Library.SOut);
			//   print first

			//   print ','
			//   print second
			// 	 print '>'


		} else {
			throw new SemanticsUndefinedException("Type non pris en charge par Print"); // TODO ajouter des types
		}*/
		Fragment frag = _factory.createFragment();
		frag.append(this.parameter.getCode(_factory));
		frag.addComment("Print (expression)");
		frag.append(this.parameter.getType().getPrintCode(_factory));
		return frag;
	}

}
