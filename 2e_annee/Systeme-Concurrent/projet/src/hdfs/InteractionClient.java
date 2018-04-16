package hdfs;

import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.net.Socket;

import formats.Format;
import formats.KV;
import formats.KVFormat;
import formats.LineFormat;

/*
 * cette class traite la communication entre le DataNode et le client
 */
public class InteractionClient implements Runnable {

	private Socket socket;
	private ObjectOutputStream out;
	private ObjectInputStream in;
	private String pathToData;
	private boolean verbose;

	// on récupére le flux d'entrée et de sortie
    public InteractionClient(Socket s, String pathToData){
        this(s, pathToData, false);
    }
	public InteractionClient(Socket s, String pathToData, boolean verbose) {
        this.verbose = verbose;
		this.pathToData = pathToData;
		this.socket = s;
		try {
			this.out = new ObjectOutputStream(socket.getOutputStream());
			this.in = new ObjectInputStream(socket.getInputStream());

		} catch (IOException e) {
			System.out.println("Error in creation of ObjectInputStream or ObjectOuputStream ");
			e.printStackTrace();
		}
	}

	/*
	 * le serveur reçoit un objet de type commande et selon le code de cette
	 * commande appel la méthode souhaitée
	 */
	@Override
	public void run() {
        if (verbose) System.out.print("Commande reçue ");
		Commande cmd = null;
		try {
			cmd = (Commande) in.readObject();
		} catch (ClassNotFoundException | IOException e1) {
			e1.printStackTrace();
		}
		switch (cmd.code) {
		case CMD_WRITE:
		    if (verbose) System.out.println("WRITE "+cmd.name + " ("+ cmd.getType().name()+")");
			writeFile(cmd.name, cmd.getType());
			break;
		case CMD_DELETE:
            if (verbose) System.out.println("DELETE "+cmd.name);
			deleteFile(cmd.name);
			break;
		case CMD_READ:
            if (verbose) System.out.println("READ "+cmd.name + " ("+ cmd.getType().name()+")");
			readFile(cmd.name, cmd.getType());
			break;
		default:
			throw new RuntimeException("Invalide commande code");
		}

		// fermeture de connexion
		try {
			this.out.close();
			this.in.close();
			this.socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*
	 * on écrit les kv qu'on reçoit dans un fichier de format kv de name fname
	 * lorsque on reçoit la valeur null c-à-d le client a envoyé toutes les données
	 * alors on envoie un boolean pour synchroniser la fermeture de sockete coté
	 * client
	 */
	public void writeFile(String fname, Format.Type type) {
		Format fWriter = null;
		switch (type) {
		case LINE:
			fWriter = new LineFormat();
			break;
		case KV:
			fWriter = new KVFormat();
			break;
		default:
			throw new RuntimeException("Invalide type Format");
		}

		fWriter.setFname(pathToData + fname);
		fWriter.open(Format.OpenMode.W);
		KV kvReceived = null;
		try{
			do {
				kvReceived = (KV) this.in.readObject();
				if (kvReceived != null) {
					fWriter.write(kvReceived);
				}
			} while (kvReceived != null);
            out.writeBoolean(true);
            out.flush();
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
            try {
                out.writeBoolean(false);
                out.flush();
            } catch (IOException e2) {}
		}
		fWriter.close();
	}

	/*
	 * on essaie de supprimer le fichier de nome fname et on envoie un boolean au
	 * client selon le cas: true si on a réussi a le supprimé false sinon ce msg
	 * permet aussi de synchroniser la fermeture de sockete coté client avec coté
	 * serveur
	 */

	private void deleteFile(String fname) {
		try {

			// file.delete retourne true si le fichier est supprimé; false sinon.
			out.writeBoolean(new File(pathToData + fname).delete());
		} catch (Exception e) {

			e.printStackTrace();

		}

	}

	/*
	 * on envoie les kv qu'on lit a partir de chunk de nom chunkName au client on
	 * envoie la valeur null quand on termine l'envoie de chunk et on attend la
	 * réception d'un boolean pour synchroniser la fermeture de sockete
	 */
	private void readFile(String chunkName, Format.Type type) {
		Format fReader = null;
		switch (type) {
		case LINE:
			fReader = new LineFormat();
			break;
		case KV:
			fReader = new KVFormat();
			break;
		default:
			throw new RuntimeException("Invalide type Format");
		}

		fReader.setFname(pathToData + chunkName);
		fReader.open(Format.OpenMode.R);
		KV kvToSend;
		while ((kvToSend = fReader.read()) != null) {
			
			try {
				this.out.writeObject(kvToSend);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// informer le client de la fin d'envoie
		try {
			this.out.writeObject(null);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			in.readBoolean();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		fReader.close();
	}

}
