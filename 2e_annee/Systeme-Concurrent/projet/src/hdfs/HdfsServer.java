package hdfs;

import exceptions.NameNodeUnreachable;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.*;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Properties;

/*
 * cette class doit être lancé sur chaque machine de cluster
 * le server se met à  l'écoute sur un hdfsPort
 * quand il reçoit une connexion d'un client il lance une Thread  InteractionClient qui traite cette connexion 
 * et il se met en attente d'un autre client
 */

public class HdfsServer {

	public static int hdfsPort;
	public static int hidoopPort;
	public static String daemonAdress;
	public static String nameNodeAdressPort;
	public static String pathToData;
	public static boolean verbose;

	public static void loadConfig(String pathFile) {
		Properties prop = new Properties();
		try {
			// load a properties file
			prop.load(new FileInputStream(pathFile));
			// get the property value and print it out
			hdfsPort = Integer.parseInt(prop.getProperty("hdfs_port"));
			hidoopPort = Integer.parseInt(prop.getProperty("hidoop_port"));
			daemonAdress = prop.getProperty("daemonAdress");
			pathToData = prop.getProperty("pathData");
            nameNodeAdressPort = prop.getProperty("nameNodeAdressPort");
            verbose = prop.getProperty("verbose").equals("true");
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	private static void usage() {
		System.out.println("Usage: java HdfsServer <path to config démon>");
	}

	public static void main(String[] args) {
		if (args.length != 1) {
			usage();
			return;
		}
		loadConfig(args[0]);

        // s'enregistrer auprès du NameNode
        DataNameNodeDaemon nnd;
        try {
            nnd = (DataNameNodeDaemon) Naming.lookup("//"+nameNodeAdressPort+"/DataNameNodeDaemon");
            //nnd.registerDaemon(new NodeIdentifier(Inet4Address.getLocalHost().getHostAddress(), hidoopPort ,hdfsPort, pathToData));
			//System.out.println(Inet4Address.getLocalHost().getHostName());
            nnd.registerDaemon(new NodeIdentifier(daemonAdress, hidoopPort ,hdfsPort, pathToData));
		} catch (RemoteException e) {
            throw new NameNodeUnreachable(" enregistrer le data node.");
        } catch (NotBoundException | MalformedURLException e) {
            e.printStackTrace();
        }

        ServerSocket server = null;
		try {
            server = new ServerSocket(hdfsPort);// Serveur en écoute
            System.out.println("server en écoute sur le hdfsPort " + hdfsPort);
            Socket client;

            while(true) {
                client = server.accept();
                // chaque interaction avec client est traité par une Thread InteractionClient
                InteractionClient ic = new InteractionClient(client, pathToData, verbose);
                new Thread(ic).start();
            }
        } catch (IOException e) {
			System.out.println("Error : connection failed");
			e.printStackTrace();
		} finally {
			if (server != null) {
				try {
					server.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// essayer d'informer le nameNode de la panne
				try {
					nnd = (DataNameNodeDaemon) Naming.lookup("//"+nameNodeAdressPort+"/DataNameNodeDaemon");
					nnd.removeDaemon(new NodeIdentifier(daemonAdress, hidoopPort ,hdfsPort, pathToData));
				} catch (RemoteException | NotBoundException | MalformedURLException e) {
					System.out.println("Impossible de se deconnecter du NameNode lors de l'arret");
				}
            }
		}

	}

}
