package exceptions;

public class JobNotSetup extends HidoopMissUsed {
    public JobNotSetup(String parameterName){
        super("Le paramètre '" + parameterName + "' de Job n'est pas initialisé, la tâche ne peut pas démarrer.");
    }
}
