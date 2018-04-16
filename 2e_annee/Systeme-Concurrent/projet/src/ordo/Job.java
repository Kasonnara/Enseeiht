package ordo;

import application.PiEstimation;
import formats.Format;
import formats.Format.Type;
import formats.FormatTools;
import formats.KVFormat;
import formats.ShuffleFormatImpl;
import formats.RemoteFormatImpl;
import hdfs.*;
import map.MapReduce;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import application.MyMapReduce;
import formats.ShuffleSimple;
import exceptions.*;

public class Job implements JobInterface {
	protected Type inputFormat, outputFormat;
	protected String inputFname, outputFname;
	protected SortComparator sortComparator;
	protected int nbReduce;
	protected int repFactor;
    protected String nameNodeIP ;
    protected int nameNodePort ;
    protected String pathRead ;
    protected String clientConfigPath;
    public   boolean verbose;
    
    
    public Format initFormatReduce(String nameFile,int nbMap) {
    	ArrayList<Format> liF =new ArrayList<Format>() ;
		for(int i=0;i<nbMap;i++) {
			Format f=new KVFormat();
			f.setFname(nameFile+"-red-tmp"+i);
			liF.add(f);
		}
		Format fShuffle=new ShuffleFormatImpl(liF,null);
		return fShuffle;
    	
    }
    
    
	public Format initWriter(NodeIdentifier niCh,List<ChunkMetaData> liChReduce,int m,String nameFile)  {
		ArrayList<Format> liRemoteF =new ArrayList<Format>() ;
		for(ChunkMetaData ch:liChReduce) {
			NodeIdentifier ni=ch.getNodeIds()[0];
			Format f=null;
			if(niCh.getNodeIP()==ni.getNodeIP() && ni.getHdfsPort()==niCh.getHdfsPort()) {
				f=new KVFormat();
			}else {
				f=new RemoteFormatImpl(Format.Type.KV,ni.getNodeIP(), ni.getHdfsPort());
			}
			f.setFname(nameFile+"-red-tmp"+m);
			liRemoteF.add(f);	
		}
		Format fShuffle=new ShuffleFormatImpl(liRemoteF,new ShuffleSimple());
		return fShuffle;
	}
    
    
    public int getRepFactor() {
		return repFactor;
	}

	public void setRepFactor(int repFactor) {
		this.repFactor = repFactor;
	}

	public int getNbReduce() {
		return nbReduce;
	}

	public void setClientConfigPath(String clientConfigPath) {
		this.clientConfigPath = clientConfigPath;
	}

	public String getClientConfigPath() {
		return this.clientConfigPath;
	}

    
    public void setNbReduce(int nbReduce) {
		this.nbReduce = nbReduce;
	}

	public String getNameNodeIP() {
		return nameNodeIP;
	}

	public void setNameNodeIP(String nameNodeIP) {
		this.nameNodeIP = nameNodeIP;
	}

	public int getNameNodePort() {
		return nameNodePort;
	}

	public void setNameNodePort(int nameNodePort) {
		this.nameNodePort = nameNodePort;
	}

	public String getPathRead() {
		return pathRead;
	}

	public void setPathRead(String pathRead) {
		this.pathRead = pathRead;
	}


	@Override
	public void setInputFormat(Type ft) {
		inputFormat = ft;
	}

	@Override
	public void setOutputFormat(Type ft) {
		outputFormat = ft;
	}

	@Override
	public void setInputFname(String fname) {
		// obtention du nom de fichier
		inputFname=fname;
		
	}

	@Override
	public void setOutputFname(String fname) {
		outputFname = fname;
	}

	@Override
	public void setSortComparator(SortComparator sc) {
		sortComparator = sc;
	}

	@Override
	public Type getInputFormat() {
		return inputFormat;
	}

	@Override
	public Type getOutputFormat() {
		return outputFormat;
	}

	@Override
	public String getInputFname() {
		return inputFname;
	}

	@Override
	public String getOutputFname() {
		return outputFname;
	}

	@Override
	public SortComparator getSortComparator() {
		return sortComparator;
	}

	@Override
	public void startJob(MapReduce mr) {
		
		System.out.println(this.nbReduce);

        if (verbose) System.out.println("Verification des paramètres");
	    try {
            // Programmation défensive, vérifier que tout a été set
            if (this.getInputFname() == null){
                throw new JobNotSetup("inputFname");
            }
            if (this.getInputFormat() == null) {
                throw new JobNotSetup("inputFormat");
            }
            if (this.getOutputFname() == null) {
                // correction automatique
                int i = this.getInputFname().lastIndexOf(".");
                if (i == -1){
                    // pas d'extension dans le nom de fichier d'entrée
                    this.setOutputFname(this.getInputFname() + "-res");
                } else {
                    this.setOutputFname(this.getInputFname().substring(0, i) + "-res" +
                            this.getInputFname().substring(i));
                }
            }
            if (this.getOutputFormat() == null) {
                throw new JobNotSetup("outputFormat");
            }

            if (verbose) System.out.println("Demander au HDFS de répartir le fichier "+inputFname+"("+inputFormat.name()+")");
            HdfsClient.HdfsWrite(inputFormat, inputFname,this.repFactor,this.nbReduce, this.getClientConfigPath());
            //HdfsClient.HdfsAssociateResult(outputFormat, inputFname,outputFname+"-tmp");
            String[] words = inputFname.split("/");
   		    String fNameHdfs= words[words.length - 1];

            if (verbose) System.out.println("Demander au NameNode de localiser les fragments.");
   		    //int chunkNumber;
            NameNodeDaemon nnd = null;

            FileMetaData fileMD = null;
			try {
                nnd = (NameNodeDaemon)Naming.lookup("//"+this.getNameNodeIP()+":"+this.getNameNodePort()+"/NameNodeDaemon");
				fileMD = nnd.locateFile(fNameHdfs);
			} catch (RemoteException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

            TaskFinish[] taskFinish = new TaskFinish[fileMD.getLiChunks().size()];




            if (verbose) System.out.println("Demander aux daemon associés d'executer le map");


            long t1 = System.currentTimeMillis();
            for (int m = 0; m < fileMD.getLiChunks().size(); m++) {
            	Format writer=null;
				
				writer = initWriter(fileMD.getLiChunks().get(m).getNodeIds()[0],fileMD.getReduceChunks(),m,fileMD.getfName());
			
            	
                ChunkMetaData chMD = fileMD.getLiChunks().get(m);
               
                
                
                // on essaie d'applique le map sur le premier chunk de la liste liCiRep
                //si on n'arrive pas on essaie de l'appliquer sur le 2eme et ainsi de suite
                //si on n'arrive pas à applique le sur un element de liste lèvera une exception
                
                boolean nonTrouve=true;
                int i=-1;
           

                while(nonTrouve && i+1<chMD.getNodeIds().length ) {

                	
                	
                	
                	i++;
                    Format reader = FormatTools.SelectFormat(inputFormat);
                    
    				reader.setFname(chMD.getNodeIds()[i].getPathData() + chMD.getChunkLocalFname());


                    try {
                        CallbackTaskImpl callbackTask = new CallbackTaskImpl();
                        taskFinish[m] = callbackTask;

                        Daemon d = (Daemon) Naming.lookup("//"+chMD.getNodeIds()[i].getRMIUrl(true) +"/hidoop_daemon/");
                        d.runMap(mr, reader, writer, callbackTask);
                       
           
                        nonTrouve=false;

                        
                    } catch (RemoteException e) {
                       // e.printStackTrace();
                    	//throw new DaemonUnreachable(ci.getNodeIdentifer());
                    }
                }
                if(nonTrouve && i > -1) {
                	throw new DaemonUnreachable(chMD.getNodeIds()[i].getRMIUrl(true)); // TODO bizarre
                }
            }

          

            // Attendre la fin de toutes les tâches
            if (verbose) System.out.println("Attente de la fin des tâches map.");
            for (int k = 0; k < taskFinish.length; k++) {
                try {
                    taskFinish[k].waitFinish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    System.exit(2);
                }
            } 
            

            

            TaskFinish[] taskFinishRed = new TaskFinish[fileMD.getNumberOfReduceChunk()];
            
            if (verbose) System.out.println("Demander aux daemon  d'executer les reduces");
            
        	
            for (int r = 0; r < fileMD.getNumberOfReduceChunk(); r++) {
     
                ChunkMetaData chRed=fileMD.getReduceChunks().get(r);
                 Format writer = FormatTools.SelectFormat(outputFormat);
                 writer.setFname(chRed.getNodeIds()[0].getPathData() + chRed.getChunkLocalFname());

                 try {
                     CallbackTaskImpl callbackTask = new CallbackTaskImpl();
                     taskFinishRed[r] = callbackTask;

                     Daemon d = (Daemon) Naming.lookup("//"+chRed.getNodeIds()[0].getRMIUrl(true) +"/hidoop_daemon/");
                     Format reader=initFormatReduce(chRed.getNodeIds()[0].getPathData()+fileMD.getfName(), fileMD.getNumberOfChunk());
         
                     d.runReduce(mr,reader, writer, callbackTask);
                 } catch (RemoteException e) {
                     // e.printStackTrace();

                  }
                     
            }
            
            
            // Attendre la fin de toutes des tâches reduces
            if (verbose) System.out.println("Attente de la fin des tâches reduce");
            for (int k = 0; k < taskFinishRed.length; k++) {
                try {
                    taskFinishRed[k].waitFinish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    System.exit(2);
                }
            }


            if (verbose) System.out.println("Demander au HDFS de rappatrier les resultats");
            
            HdfsClient.HdfsRead(fileMD.getReduceFName(), pathRead + outputFname);
            long t2 = System.currentTimeMillis();
            System.out.println("Temps d'execution du job (en calcul brut) " + (t2 - t1) + "ms.");

        } catch (NotBoundException e) {
            e.printStackTrace();
            // TODO (cette erreur peut etre plus limité, pour rattrapper plsu finement l'erreur)
        } catch (MalformedURLException e) {
            e.printStackTrace();
            // TODO (cette erreur peut etre plus limité, pour rattrapper plsu finement l'erreur)
        }
    }

	
	public static void usage() {
		System.out.println("Usage: java Job <path to Job configuration> <path to client configuration>");
		System.out.println("exemple : java Job config/job/Job.properties ./config/clients/localhost.properties");
	}
	
	
    public static void main(String[] argv){
        if (argv.length != 2) {
            usage();
            return;
         }
        //long t1 = System.currentTimeMillis();
    	 try {
    	 String jobConfigPath = argv[0]; 
    	 String clientConfigPath = argv[1];
    	 Properties propJob = new Properties();
 	     Properties propClient = new Properties();
         try {
             propJob.load(new FileInputStream(jobConfigPath));
             propClient.load(new FileInputStream(clientConfigPath));
         } catch (FileNotFoundException e) {
             throw new ConfigNotFound(clientConfigPath);
         } catch (IOException e) {
             throw new CanNotReadConfig();
         }

  
         Job j = new Job();
         j.setClientConfigPath(clientConfigPath);
         j.setNameNodeIP (propClient.getProperty("adresseNameNode"));
         j.setNameNodePort ( 4141);
         j.setPathRead(propClient.getProperty("pathRead"));
        if(propClient.getProperty("verbose").equals("true")) {
        	 j.verbose=true;
        }
         j.setInputFname(propJob.getProperty("inputFname"));
         j.setOutputFname(propJob.getProperty("outputFname"));
         j.setInputFormat(Format.Type.valueOf(propJob.getProperty("inputFormat")));
         j.setOutputFormat(Format.Type.valueOf(propJob.getProperty("outputFormat")));
         j.setNbReduce(Integer.parseInt(propJob.getProperty("nbReduce","1")));
         j.setRepFactor(Integer.parseInt(propJob.getProperty("repFactor","1")));
            
            
            

       

            MapReduce mr;

            switch (propJob.getProperty("mapReduce")) {
                // ===================================================================================================
                // INSÉRER ICI TOUTES LES CLASSES DE MapReduce POSSIBLE À LA MANIÈRE DE MonExempleMapReduce CI-DESSOUS
                // case "MonExempleMapReduce":
                //     mr = new MonExempleMapReduce();
                //     break;
                // ET PENSER À IMPORTER LA CLASSE EN DÉBUT DE FICHIER.
                // import application.MonExempleMapReduce;
                // ===================================================================================================
                case "MyMapReduce":
                    mr = new MyMapReduce();
                    break;
                case "pi":
                    mr = new PiEstimation();
                    break;
                default:
                    // Aucun map-reduce correspondant : erreur
                    throw new MapReduceNotReferenced();
            }
            // Execution de hidoop
            j.startJob(mr);

            System.out.println("Done");
          //  long t2 = System.currentTimeMillis();
           // System.out.println("Temps d'execution du job " + (t2 - t1) + "ms.");
            System.exit(0);
        } catch (HidoopMissUsed e) {
	        // Erreur d'utilisation de hidoop par l'utilisateur
	        System.out.println("Critical error : " + e.getMessage());
	        System.exit(-1);
	    } catch (HidoopFail e){
	        // Erreur imprévisible
            System.out.println("Critical RuntimeException : " + e.getMessage());
            System.exit(1);
        }
    }
}
