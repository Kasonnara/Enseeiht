package exceptions;

public class FormatNotReferenced extends HidoopMissUsed{
    public FormatNotReferenced() {
        super("La classe Format demandée n'existe pas dans la liste des Formats possibles.");
    }
}
