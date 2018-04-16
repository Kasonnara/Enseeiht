/**
 * 
 */
package fr.n7.stl.block.ast.type;


import fr.n7.stl.block.ast.scope.Declaration;
import fr.n7.stl.block.ast.scope.HierarchicalScope;
import fr.n7.stl.tam.ast.Fragment;
import fr.n7.stl.tam.ast.Library;
import fr.n7.stl.tam.ast.Register;
import fr.n7.stl.tam.ast.TAMFactory;
import fr.n7.stl.util.Logger;

/**
 * Elementary types in the Bloc language.
 * @author Marc Pantel
 *
 */
public enum AtomicType implements Type {
	BooleanType,
	CharacterType,
	FloatingType,
	IntegerType,
	StringType,
	VoidType,
	NullType,
	ErrorType
	;

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Type#equalsTo(fr.n7.stl.block.ast.Type)
	 */
	@Override
	public boolean equalsTo(Type _other) {
		return this == _other;
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Type#compatibleWith(fr.n7.stl.block.ast.Type)
	 */
	@Override
	public boolean compatibleWith(Type _other) {
		if (this.equalsTo(_other)) {
			return true;
		} else {
			switch (this) {
			case NullType : return ((_other != ErrorType) && (_other != VoidType));
			case IntegerType: return (_other == FloatingType);
			default: return false;
			}
		}
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Type#merge(fr.n7.stl.block.ast.Type)
	 */
	@Override
	public Type merge(Type _other) {
		if (this.compatibleWith(_other)) {
			return _other;
		} else {
			if (_other.compatibleWith(this)) {
				return this;
			} else {
				return ErrorType;
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Type#length()
	 */
	@Override
	public int length() {
		switch (this) {
			case NullType : 
			case BooleanType :
			case CharacterType :
			case FloatingType :
			case IntegerType :
			case StringType : return 1;
			case VoidType : return 0;
			default : throw new IllegalArgumentException( "Must not call length on the Error type.");
		}
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Enum#toString()
	 */
	@Override
	public String toString() {
		switch (this) {
		case BooleanType: return "boolean";
		case CharacterType: return "char";
		case ErrorType: return "error";
		case FloatingType: return "float";
		case IntegerType: return "int";
		case StringType: return "String";
		case VoidType: return "void";
		case NullType: return "unit";
		default: throw new IllegalArgumentException( "The default case should never be triggered.");
		}
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.type.Type#resolve(fr.n7.stl.block.ast.scope.Scope)
	 */
	@Override
	public boolean resolve(HierarchicalScope<Declaration> _scope) {
		return true;
	}


	public Fragment getPrintCode(TAMFactory _factory){
		Fragment frag = _factory.createFragment();
		switch (this) {
			case BooleanType:
				frag.add(Library.B2S);
				frag.add(Library.SOut);
				/*String id = Integer.toString(_factory.createLabelNumber());
				// JumpIf to else
				frag.add(_factory.createJumpIf("PRINT_FALSE_"+id,0));
				// Add then code
				Fragment then_frag = getCodeStringMake(_factory, "true");
				then_frag.addComment("print "+id+" true");
				frag.append(then_frag);
				// Jump to end label
				frag.add(_factory.createJump("END_PRINT_"+id));
				frag.addSuffix("PRINT_FALSE_"+id+":");
				// Add Else code
				Fragment else_frag = getCodeStringMake(_factory, "true");
				else_frag.addComment("print "+id+" false");
				frag.append(else_frag);

				// Add end label
				frag.addSuffix("END_PRINT_"+id+":");
				frag.add(Library.SOut);*/
			case CharacterType:
				/*
				// Allouer l'espace
				frag.add(_factory.createLoadL(1));
				frag.add(Library.SAlloc);
				// copier la valeure
				frag.add(_factory.createLoad(Register.ST,-2, 1));
				// copier l'adresse alloué au String
				frag.add(_factory.createLoad(Register.ST,-2, 1));
				frag.add(_factory.createStoreI(1));
				frag.add(Library.SOut);
				*/
				frag.add(Library.C2S);
				frag.add(Library.SOut);
				break;

			case ErrorType:
				Logger.error("Error type can't be printed");
				break;
			case FloatingType:;
				Logger.error("float type can't be printed");
				break;
			case IntegerType:
				frag.add(Library.IOut);
				break;
			case StringType:
				// TODO
				frag.add(Library.SOut);
				break;
			case VoidType:
				frag.append(getCodeStringMake(_factory, "void"));
				frag.add(Library.SOut);
				break;
			case NullType:
				frag.append(getCodeStringMake(_factory, "null"));
				frag.add(Library.SOut);
				break;
			default: throw new IllegalArgumentException( "The default case should never be triggered.");
		}
		return frag;
	}

	public static Fragment getCodeStringMake(TAMFactory _factory, String tomake){
		Fragment frag = _factory.createFragment();
		// Allouer l'espace
		frag.add(_factory.createLoadL(tomake.length()));
		frag.add(Library.SAlloc);
		// Ranger les valeurs
		for (char c : tomake.toCharArray()){
			frag.add(_factory.createLoadL(Character.getNumericValue(c)));
		}
		// copier l'adresse alloué au String
		frag.add(_factory.createLoad(Register.ST,-1-tomake.length(), 1));
		frag.add(_factory.createStoreI(tomake.length()));

		return frag;
	}

	public boolean canPrintCode(){
		switch (this) {
			case BooleanType: return false;
			case CharacterType: return false;
			case ErrorType: return false;
			case FloatingType: return false;
			case IntegerType: return true;
			case StringType: return  false;
			case VoidType: return false;
			case NullType: return false;
			default: throw new IllegalArgumentException( "The default case should never be triggered.");
		}
	}
}
