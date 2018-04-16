package formats;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import hdfs.Commande;
import hdfs.Commande.CommandeCode;

public class RemoteFormatImpl implements RemoteFormat{
	/**
	 * 
	 */
	protected boolean readActive;
	private static final long serialVersionUID = 1L;
	protected int index =0;
	protected Format.Type fmt;
	protected String adresseIp;
	protected int port;
	protected  ObjectOutputStream out = null;
	protected ObjectInputStream in = null;
	protected Commande cmd=null;
	
	
	
    public RemoteFormatImpl(Format.Type fmt, String adresseIp, int port) {
		super();

		this.cmd=new Commande();
		cmd.setType( fmt);
		this.adresseIp=adresseIp;
		this.port=port;

	}

	@Override
	
    public KV read() {
		KV kv=null;
		if(readActive) {
			try {
				kv = (KV) in.readObject();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			if(kv==null) {
				readActive=false;
			}
		}

        return kv;
    }

    @Override
    public void write(KV record) {
    	if(in==null || out==null ) {
    		throw new RuntimeException("Exception In methode write of class RMIFormat : you can not write in file without open it in mode write");
    	}
    	
			try {

				out.writeObject(record);
				out.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}


    	
    }
    
    


    @Override
    public void open(OpenMode mode) {
    	
    	
    
    	this.index =0;
    	if(this.cmd.getName()==null){
			throw new RuntimeException("Exception In methode open of class RemoteFormat : you can not open file because the fileName is null");
		}
    	try {
    		Socket server = new Socket(this.adresseIp,this.port);
			out = new ObjectOutputStream(server.getOutputStream());
			in = new ObjectInputStream(server.getInputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    switch (mode) {
		case R:
			cmd.setCode(Commande.CommandeCode.CMD_READ);
		   
			break;
		case W:
			cmd.setCode(Commande.CommandeCode.CMD_WRITE);
			break;
	
		default:
			throw new RuntimeException("Exception In methode open of class RemoteFormat : Invalid Open Mode of File");

		}
	    try {
			out.writeObject(cmd);
			out.flush();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	    readActive=true;
    	
   
	
    }

	@Override
	public void close() {
		
		if(cmd.getCode()==Commande.CommandeCode.CMD_WRITE) {
			// on envoie null pour informer le server qu'on a terminé l'envoie des messages
						try {
							out.writeObject(null);
							out.flush();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						// pour la synchronisation
						try {
							in.readBoolean();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

		}else if(this.cmd.getCode()==Commande.CommandeCode.CMD_READ) {
			try {
				out.writeBoolean(true);
				out.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else {
			throw new RuntimeException("Exception In methode close of class RemoteFormat : Invalid Open Mode of File");
		}
		this.closeConnexion();
		
	}

    
    public void closeConnexion() {
    	if(in!=null) {
    		try {
    			in.close();
    		} catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    		in=null;
    	}
    	if(out!=null) {
    		try {
    			out.close();
    		} catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    		out=null;
    	}


    }

    @Override
    public long getIndex() {
        return 0;
    }

    @Override
    public String getFname() {
        return cmd.getName();
    }

    @Override
    public void setFname(String fname) {
    	cmd.setName(fname);
    }


}
