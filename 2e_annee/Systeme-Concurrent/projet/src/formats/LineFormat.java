package formats;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;



public class LineFormat implements Format {

    /**
     * pour la méthode read on lit une ligne de text et on retourne un KV [k="numéro de ligne", v="la ligne"]
     *
     *
     * Pour la méthode write on reçoit un record de type KV et on écrit une ligne(=record.v) et on ignore le clé (=record.k)
     */
    private static final long serialVersionUID = 1L;
    String fileName=null;
    BufferedReader br =null;
    FileWriter fw=null;
    int numLine=0;
    long index=0;


    @Override
    public void open(OpenMode mode) {

        this.br =null;
        this.fw=null;
        this.numLine=0;
        this.index=0;
        switch (mode) {
            case R:
                if(fileName==null){
                    throw new RuntimeException("Exception In methode open of class LineFormat : you can not open file because the fileName is null");
                }
                try {
                    br = new BufferedReader(new FileReader(this.fileName));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                break;
            case W:
                if(fileName==null){
                    throw new RuntimeException("Exception In methode open of class LineFormat : you can not open file because the fileName is null");
                }
                try {
                    fw = new FileWriter(this.fileName);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            default:
                throw new RuntimeException("Exception In methode open of class LineFormat : Invalid Open Mode of File");
        }
    }

    @Override
    public void close() {
        if(br!=null) {
            try {
                br.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        if(fw!=null) {
            try {
                fw.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    @Override
    public long getIndex() {
        return index;

    }

    @Override
    public String getFname() {
        return this.fileName;
    }

    @Override
    public void setFname(String fname) {
        this.fileName=fname;
    }

    @Override
    public KV read() {
        if(br==null){
            throw new RuntimeException("Exception In methode read of class lineFormat : you can not read file without open it in mode read");
        }
        String line = null;
        KV kv=null;
        try {
            if ((line = br.readLine()) != null) {
                kv=new KV();
                kv.k=String.valueOf(numLine);
                kv.v=line;
                numLine++;
                index=index+line.getBytes().length+1;
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return kv;
    }

    @Override
    public void write(KV record) {
        if(fw==null){
            throw new RuntimeException("Exception in methode write of class LineFormat : you can not write in file without open it in mode write");
        }
        try {
            if(record==null){
                throw new RuntimeException("Exception in methode write of class KVFormat : the argument of write is null");
            }
            fw.write(record.v);
            fw.write(System.getProperty( "line.separator" ));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        index=index+record.v.getBytes().length+1;
    }
}