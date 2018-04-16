package exceptions;

public class NameNodeUnreachable extends HidoopFail {
    public NameNodeUnreachable(String requete){
        super("Impossible de contacter le NameNode pour obtenir " + requete);
    }
}
