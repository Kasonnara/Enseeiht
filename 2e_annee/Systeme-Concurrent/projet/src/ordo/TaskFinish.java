package ordo;

public interface TaskFinish {
    // précondition : ne jamais appeler waitFinish() plus d'une fois, même par deux thread.
    void waitFinish() throws InterruptedException;
    boolean isProcessing();
    boolean isFailed();
}
