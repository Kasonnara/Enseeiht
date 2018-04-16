package fr.n7.stl.util;

import fr.n7.stl.block.Driver;

import java.io.File;



public class FullTest {
    public static int testSuccess = 0;
    public static int testFailure = 0;
    public static int testAck = 0;

    public static void main(String[] args) throws Exception {
        // lire tous les dossier de test
        String dir = args.length==0?"./tests":args[0];
        File file = new File(dir);

        File[] files = file.listFiles();
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    System.out.println("Dossier de test trouvé: " + files[i].getAbsolutePath());

                } else if(files[i].getName().endsWith(".block")) {
                    System.out.println("  Fichier de test trouvé: " + files[i].getName());

                        try {
                            String[] test_args = {files[i].getAbsolutePath()};
                            Driver.main(test_args);

                            if (files[i].getName().indexOf("acknoledge") > -1) {
                                System.out.println("Acknolegde success test");
                                testAck += 1;
                            } else if (files[i].getName().indexOf("error")>-1) {
                                System.out.println("No error found : Faillure <================================================== /!\\");
                                testFailure += 1;
                            }else {
                                System.out.println("No error found : Success");
                                testSuccess += 1;
                            }
                        } catch (Throwable e){
                            if (files[i].getName().indexOf("acknoledge") > -1) {
                                System.out.println("Acknolegde fail test ");
                                testAck += 1;
                            } else if (files[i].getName().indexOf("error")>=0) {
                                System.out.println("Error found : Success");
                                testSuccess += 1;
                            }else{
                                System.out.println("Error found : Faillure <================================================== /!\\");
                                testFailure += 1;
                            }

                        }


                }
                if (files[i].isDirectory()) {
                    String[] newargs = {files[i].getAbsolutePath()};
                    main(newargs);
                }
            }
        }
        System.out.println("Test Succes : " + testSuccess);
        System.out.println("Test Failure : " + testFailure);
        System.out.println("Test Acknoledge : " + testAck);
    }
}
