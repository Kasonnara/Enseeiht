package fr.n7.stl.util;

import fr.n7.stl.block.ast.type.AtomicType;
import fr.n7.stl.block.ast.type.Type;

public class BlockMetaData {
    protected boolean ValideType = true;                      // Stock le resultat inital de CheckType
    protected Type returnType = AtomicType.VoidType;   // Stock le type des return du block.
    protected boolean returnAlwaysSet = false;         // Stock si le block fini toujours par tomber sur un return


    public BlockMetaData(boolean valideCheckType, Type returnType){
        // utilisépar 'return' pour un block retournant une valeur
        this(valideCheckType);
        this.returnType = returnType;
        this.returnAlwaysSet = true;
    }

    public BlockMetaData(boolean valideCheckType) {
        // Utilisé par 'if' et 'while' pour des blocks pas toujours correct
        this();
        ValideType = valideCheckType;
    }

    public BlockMetaData(){
        // Utilisé pour des init (block par defaut considéré correct exemple un block vide)
        returnType = AtomicType.VoidType;
        returnAlwaysSet = false;
        ValideType = true;
    }

    public boolean isValideType() {
        return ValideType;
    }

    public void setValideType(boolean valideType) {
        ValideType = valideType;
    }

    public Type getReturnType() {
        return returnType;
    }

    public void setReturnType(Type returnType) {
        this.returnType = returnType;
    }

    public boolean isReturnAlwaysSet() {
        return returnAlwaysSet;
    }

    public void setReturnAlwaysSet(boolean returnAlwaysSet) {
        this.returnAlwaysSet = returnAlwaysSet;
    }


    /**
     * Réuni les BlockMetaData de deux block executé sequentiellement (d'abord other puis this)
     */
    public BlockMetaData mergeSequence(BlockMetaData _other){
        if (_other.isReturnAlwaysSet()){
            // Ona  trouvé une instruction innutile // TODO la supprimer
            Logger.warning("Instruction is not reachable");
        }
        BlockMetaData result = new BlockMetaData();
        result.setValideType( this.isValideType() && _other.isValideType());
        result.setReturnAlwaysSet(this.isReturnAlwaysSet() || _other.isReturnAlwaysSet());
        result.setReturnType(MergeWithVoid(this.getReturnType(), _other.getReturnType()));

        return result;
    }
    /**
     * Réuni les BlockMetaData de deux block executé parallèlement (if then else par exemple)
     */
    public BlockMetaData mergeParrallel(BlockMetaData _other){
        BlockMetaData result = new BlockMetaData();
        result.setValideType( this.isValideType() && _other.isValideType());
        result.setReturnAlwaysSet(this.isReturnAlwaysSet() && _other.isReturnAlwaysSet());
        result.setReturnType(MergeWithVoid(this.getReturnType(),_other.getReturnType()));

        return result;
    }


    /**
     * Effectue un merge entre les deux type sauf si l'un d'eux est Void auquel cas il ne compte pas
     * @param t1 Type
     * @param t2 Type
     * @return type mergé
     */
    public static Type MergeWithVoid(Type t1, Type t2){
        if (t1 == AtomicType.VoidType){
            return t2;
        } else if(t2 == AtomicType.VoidType){
            return t1;
        } else {
            return t1.merge(t2);
        }
    }

}
