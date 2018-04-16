package application;

import formats.Format;
import formats.FormatReader;
import formats.FormatWriter;
import formats.KV;
import map.MapReduce;

public class PiEstimation implements MapReduce {
    @Override
    public void map(FormatReader reader, FormatWriter writer) {
        KV kv;
        while ((kv = reader.read()) != null) {
            int low_x_i = kv.v.indexOf("lx");
            int low_y_i = kv.v.indexOf("ly");
            int high_x_i = kv.v.indexOf("hx");
            int high_y_i = kv.v.indexOf("hy");
            int nombre_point_i = kv.v.indexOf("np");

            double low_x = Double.parseDouble(kv.v.substring(low_x_i + 2, low_y_i));
            double low_y = Double.parseDouble(kv.v.substring(low_y_i + 2, high_x_i));
            double high_x = Double.parseDouble(kv.v.substring(high_x_i + 2, high_y_i));
            double high_y = Double.parseDouble(kv.v.substring(high_y_i + 2, nombre_point_i));
            int nb_point = Integer.parseInt(kv.v.substring(nombre_point_i + 2));

            if (dist2(low_x, low_y) > 1) {
                saveResults(0, nb_point*nb_point, writer);
            } else if(dist2(high_x, high_y)<1) {
                saveResults(nb_point*nb_point, 0, writer);
            } else {
                int nb_in = 0;
                int nb_out = 0;

                double pasx = (high_x - low_x) / nb_point;
                double pasy = (high_y - low_y) / nb_point;
                double x = low_x;
                while (x < high_x){
                    double y = low_y;
                    while (y < high_y){
                        if (dist2(x,y) > 1){
                            nb_out ++;
                        }else{
                            nb_in ++;
                        }
                        y += pasy;
                    }
                    x += pasx;
                }
                saveResults(nb_in, nb_out, writer);
            }
        }
    }

    private void saveResults(int nb_in, int nb_out, FormatWriter writer){
        writer.write(new KV("in", ""+nb_in));
        writer.write(new KV("out", ""+nb_out));
    }

    private double dist2(double x, double y){
        return x*x + y*y;
    }

    @Override
    public void reduce(FormatReader reader, FormatWriter writer) {
        KV kv;
        long nb_in = 0;
        long nb_out = 0;
        while ((kv = reader.read()) != null) {
            if (kv.k.equals("in")){
                nb_in += Long.parseLong(kv.v);
            }else{
                nb_out += Long.parseLong(kv.v);
            }
        }
        writer.write(new KV("in", ""+nb_in));
        writer.write(new KV("out", ""+nb_out));
    }
}
