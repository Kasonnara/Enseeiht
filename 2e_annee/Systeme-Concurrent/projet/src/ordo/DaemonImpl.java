package ordo;

import formats.Format;
import formats.KVFormat;
import map.Mapper;
import map.Reducer;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Properties;

public class DaemonImpl extends UnicastRemoteObject implements Daemon {

    public static boolean verbose;

    public DaemonImpl()throws RemoteException {}

    class TaskExecMap implements Runnable{
        Mapper m;
        Format reader;
        Format writer;
        CallBack cb;

        public TaskExecMap(Mapper m, Format reader, Format writer, CallBack cb){
            this.m = m;
            this.reader = reader;
            this.writer = writer;
            this.cb = cb;
        }

        @Override
        public void run() {
            this.reader.open(Format.OpenMode.R);
            this.writer.open(Format.OpenMode.W);
            // Executer le calcul
            this.m.map(this.reader, this.writer);
            try {

                // Indiquer a l'odonnanceur que la tâche est terminée
                this.cb.setTaskDone();
                System.out.println("Tache finie map");
            } catch(RemoteException e){
                // TODO Que faire si problème avec le callback?
                e.printStackTrace();
                System.out.println("Problème avec le callback de la tâche");
            } finally {
                this.reader.close();
                this.writer.close();
            }
        }
    }

    
    class TaskExecReduce implements Runnable{
        Reducer r;
        Format reader;
        Format writer;
        CallBack cb;

        public TaskExecReduce(Reducer r, Format reader, Format writer, CallBack cb){
            this.r = r;
            this.reader=reader;           
            this.writer = writer;
            this.cb = cb;
        }

        @Override
        public void run() {
            this.reader.open(Format.OpenMode.R);
            this.writer.open(Format.OpenMode.W);
            // Executer le calcul
            this.r.reduce(this.reader, this.writer);
            try {

                // Indiquer a l'odonnanceur que la tâche est terminée
                this.cb.setTaskDone();
                System.out.println("Tache finie reduce");
            } catch(RemoteException e){
                // TODO Que faire si problème avec le callback?
                e.printStackTrace();
                System.out.println("Problème avec le callback de la tâche");
            } finally {
                this.reader.close();
                this.writer.close();
            }
        }
    }
    
    
    @Override
    public void runMap(Mapper m, Format reader, Format writer, CallBack cb) throws RemoteException {
        System.out.println("Nouvelle tache map");
        // Générer un thread pour la nouvelle tâche
        Thread t = new Thread(new TaskExecMap(m, reader, writer, cb));
        // Lancer le thread
        t.start();
        // Redonner la main a l'ordonnaceur sans attendre la fin
    }
    
    @Override
    public void runReduce(Reducer r,Format reader, Format writer, CallBack cb) throws RemoteException {
       if(verbose) System.out.println("Nouvelle tache reduce");
        // Générer un thread pour la nouvelle tâche
        Thread t = new Thread(new TaskExecReduce(r, reader, writer, cb));
        // Lancer le thread
        t.start();
        // Redonner la main a l'ordonnaceur sans attendre la fin
    }

    public static void main(String argv[]) throws RemoteException, AlreadyBoundException, MalformedURLException {
        int port = 4242;
        String nameNodeAdressPort = "localhost:4141";
        if (argv.length > 0){
            Properties prop = new Properties();
            try {
                // load a properties file
                prop.load(new FileInputStream(argv[0]));
                // get the property value and print it out
                port = Integer.parseInt(prop.getProperty("hidoop_port"));
                //nameNodeAdressPort = prop.getProperty("nameNodeAdressPort");
                verbose = prop.getProperty("verbose").equals("true");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } else {
            // TODO print usage
            return;
        }
        if (verbose) System.out.println("Démarrage d'un démon sur le hdfsPort "+port);
        LocateRegistry.createRegistry(port);
        Naming.rebind("//localhost:"+port+"/hidoop_daemon/", new DaemonImpl());

    }
}
