package formats;

import java.io.*;

public class KVFormat implements Format {

    protected String fname;
    protected OpenMode mode;
    protected int index;

    protected InputStream ins;
    protected InputStreamReader insReader;
    protected BufferedReader buffReader;

    protected FileWriter outs;
    protected BufferedWriter buffWriter;
    protected PrintWriter pWriter;


    @Override
    public KV read() {
        if (mode == OpenMode.R){
            try {
                String line = buffReader.readLine();
                if (line !=null){
                    int i = line.indexOf(KV.SEPARATOR);
                    index=index+line.getBytes().length+1;
                    if (i >= 0) {
                        return new KV(line.substring(0, i), line.substring(i + KV.SEPARATOR.length()));
                    } else {
                        // error
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // error
        }
        return null;
    }

    @Override
    public void write(KV record) {
        if (mode == OpenMode.W) {
            this.pWriter.println(record.k + KV.SEPARATOR + record.v);
            // TODO robustesse
            index=index+record.v.getBytes().length+1;
        } else {
            // error
        }
    }

    @Override
    public void open(OpenMode mode) {
        if (fname != null) {
            this.mode = mode;
            this.index = 0;
            if (mode == OpenMode.R) {
                try {
                    this.ins = new FileInputStream(this.fname);
                    this.insReader = new InputStreamReader(this.ins);
                    this.buffReader = new BufferedReader(this.insReader);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    // TODO
                }
            } else {
                try {
                    this.outs = new FileWriter(this.fname);
                    this.buffWriter = new BufferedWriter(this.outs);
                    this.pWriter = new PrintWriter(this.buffWriter);
                } catch (IOException e) {
                    e.printStackTrace();
                    // TODO
                }
            }
        } else {
            //error
        }
    }

    @Override
    public void close() {
        if (mode == OpenMode.R) {
            try {
                buffReader.close();
                insReader.close();
                ins.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (mode == OpenMode.W) {
            pWriter.close();
            try {
                buffWriter.close();
                outs.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        mode = null;
    }

    @Override
    public long getIndex() {
        return this.index; // TODO incorrect pour le mode W
    }

    @Override
    public String getFname() {
        return fname;
    }

    @Override
    public void setFname(String fname) {
        this.fname = fname;
    }
}
