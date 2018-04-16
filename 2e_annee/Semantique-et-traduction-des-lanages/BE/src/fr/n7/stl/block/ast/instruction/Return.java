/**
 * 
 */
package fr.n7.stl.block.ast.instruction;

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

/**
 * Implementation of the Abstract Syntax Tree node for a return instruction.
 * @author Marc Pantel
 *
 */
public class Return implements Instruction {

	protected Expression value;
	protected int current_parameter_alloc; // Enregistre la taille des paramètre pour les effacer
	protected int alloc_offset; // Enregistre l'offset courant pour vider les varible locales a la place de bloc

	public Return(Expression _value) {
		this.value = _value;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "return " + this.value + ";\n";
	}
	
	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.instruction.Instruction#resolve(fr.n7.stl.block.ast.scope.Scope)
	 */
	@Override
	public boolean resolve(HierarchicalScope<Declaration> _scope) {
		return this.value.resolve(_scope);
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Instruction#checkType()
	 */
	@Override
	public BlockMetaData checkType() {
		// Ecrire le type renvoyé dans les meta donnée du block
		Type returned_type = this.value.getType();
		return new BlockMetaData(returned_type != AtomicType.ErrorType, returned_type);
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Instruction#allocateMemory(fr.n7.stl.tam.ast.Register, int)
	 */
	@Override
	public int allocateMemory(Register _register, int _offset, int current_parameter_alloc) {
		this.current_parameter_alloc = current_parameter_alloc;
		this.alloc_offset = _offset;

		return 0;
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Instruction#getCode(fr.n7.stl.tam.ast.TAMFactory)
	 */
	@Override
	public Fragment getCode(TAMFactory _factory) {
		// Empiler la valeur a return
		Fragment frag = this.value.getCode(_factory);
		// Effacer les variables locales (Remplace le pop normalement généré par Block)
		frag.add(_factory.createPop(this.value.getType().length(), this.alloc_offset-3)); // on laisse juste les 3 registre gérés par CALL et RETURN
		// Return
		frag.add(_factory.createReturn(this.value.getType().length(),this.current_parameter_alloc));

		return frag;
	}
}
