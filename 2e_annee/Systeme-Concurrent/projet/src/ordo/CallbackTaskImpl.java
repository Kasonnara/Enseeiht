package ordo;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.Semaphore;


public class CallbackTaskImpl extends UnicastRemoteObject implements CallBack, TaskFinish {
    protected TaskStatus status;
    protected Semaphore waitingSemaphore; // TODO remplacer par un seul sémaphore commun, ce qui permet de débloquer Job a la fin de chaque tache (pour verifié sa réussite par exemple)

    public CallbackTaskImpl() throws RemoteException {
        this.status = TaskStatus.WORKING;
        this.waitingSemaphore = new Semaphore(0);
    }

    enum TaskStatus {
        WORKING,
        DONE,
        FAILED;
    }

    public void setTaskDone() throws RemoteException{
        // TODO vérifier les problèmes d'accès concurrents avec isProcessing
        this.status = TaskStatus.DONE;
        this.waitingSemaphore.release();
    }

    public void waitFinish() throws InterruptedException {
            this.waitingSemaphore.acquire();
    }

    public boolean isProcessing(){
        // TODO vérifier les problèmes d'accès concurrents avec setTaskDone
        return this.status == TaskStatus.WORKING;
    }

    @Override
    public boolean isFailed() {
        // Pas encore utilisé
        return this.status == TaskStatus.FAILED;
    }
}
