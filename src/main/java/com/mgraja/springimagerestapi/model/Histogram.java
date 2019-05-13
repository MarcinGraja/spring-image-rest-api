package com.mgraja.springimagerestapi.model;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.util.Arrays;
import java.util.Collections;
public class Histogram {
    private Double[] R;
    private Double[] G;
    private Double[] B;
    public Histogram(BufferedImage image, boolean normalized){
        R = new Double[256];
        G = new Double[256];
        B = new Double[256];
        for (int i=0; i<256; i++){
            R[i] = (double) 0;
            G[i] = (double) 0;
            B[i] = (double) 0;
        }
        createHistogram(image, normalized);
    }
    private void createHistogram(BufferedImage bufferedImage, boolean normalized){
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();
        Raster raster = bufferedImage.getRaster();
        for (int i =0; i < width; i++){
            for (int j=0; j<height; j++){
                R[raster.getSample(i,j,0)]++;
                G[raster.getSample(i,j,1)]++;
                B[raster.getSample(i,j,2)]++;
            }
        }
        if (normalized) {
            normalize(R);
            normalize(G);
            normalize(B);
        }
    }
    private void normalize(Double[] arr){
        Double min = Collections.min(Arrays.asList(arr));
        Double max = Collections.max(Arrays.asList(arr));
        double scale = 1.0/(max-min);
        for (int i = 0; i < 255; i++){
            if(!max.equals(min))
                arr[i] = (arr[i]-min) * scale;
            else arr[i] = 1.0;
        }
    }

    public Double[] getR() {
        return R;
    }
    public Double[] getG() {
        return G;
    }
    public Double[] getB() {
        return B;
    }
}
