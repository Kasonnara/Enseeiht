package application;

import formats.Format;
import formats.FormatReader;
import formats.FormatWriter;
import formats.KVFormat;
import ordo.Job;

import java.io.*;
import java.text.Normalizer;
import java.util.Properties;

public class MainPiEstimation {

    public static void main(String[] args) throws IOException {
        if (args.length != 2){
            usage();
            return;
        }
        long t_start = System.currentTimeMillis();

        // Généréer le fichier source
        int nb_section = Integer.parseInt(args[0]);
        int nb_point_par_section = Integer.parseInt(args[1]);
        String directory = "data/client/";
        String source_path = "generated_source_for_pi_estiation";

        Writer writer = new BufferedWriter(new OutputStreamWriter(
                                new FileOutputStream(directory+source_path), "utf-8"));
        double pasSection = 1.0/nb_section;
        for (int xi = 0; xi < nb_section; xi ++){
            for (int yi = 0; yi < nb_section; yi ++){
                writeSourceSection(writer, xi * pasSection, yi * pasSection,
                        (xi+1)*pasSection, (yi+1)*pasSection,
                        nb_point_par_section);
            }
        }
        writer.close();
        // Lancer le calcul map reduce
        Job j = new Job();
        j.setInputFname(directory+source_path);
        j.setOutputFname(source_path+"-res");
        j.setInputFormat(Format.Type.KV);
        j.setOutputFormat(Format.Type.KV);
        j.setNbReduce(1);
        j.setRepFactor(1);
        // charger la config du clientHDFS
        String clientConfigPath = "./config/clients/localhost.properties";
        j.setClientConfigPath(clientConfigPath);

        Properties propClient = new Properties();
        try {
            propClient.load(new FileInputStream(clientConfigPath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        j.setNameNodeIP (propClient.getProperty("adresseNameNode"));
        j.setNameNodePort ( 4141);
        j.setPathRead(propClient.getProperty("pathRead"));
        if(propClient.getProperty("verbose").equals("true")) {
            j.verbose=true;
        }

        j.startJob(new PiEstimation());


        long t_end = System.currentTimeMillis();
        System.out.println("Done");
        KVFormat r = new KVFormat();
        r.setFname(directory+source_path+"-res");
        r.open(Format.OpenMode.R);
        long nb_in = Long.parseLong(r.read().v);
        long nb_out = Long.parseLong(r.read().v);
        r.close();
        System.out.println("Resultat pi = "+(4.0*nb_in/(nb_in+nb_out))+"; Nombre de points total : "+(nb_in+nb_out));
        System.out.println("Temps de calcul : "+(t_end-t_start)+"ms");
        System.exit(0);
    }

    private static void usage(){
        System.out.println("Usage : MainPiEstimation nombre_section nombre_point_par_section");
    }

    private static void writeSourceSection(Writer writer,
                                    double low_x, double low_y,
                                    double high_x, double high_y,
                                    int nb_pts){
        try {
            writer.write("section<->lx"+low_x+"ly"+low_y+"hx"+high_x+"hy"+high_y+"np"+nb_pts+"\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
