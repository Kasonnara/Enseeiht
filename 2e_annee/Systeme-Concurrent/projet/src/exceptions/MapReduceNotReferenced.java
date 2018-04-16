package exceptions;

public class MapReduceNotReferenced extends HidoopMissUsed{
    public MapReduceNotReferenced() {
        super("La classe MapReduce demandée n'existe pas dans la liste des MapReduce possibles.");
    }
}
