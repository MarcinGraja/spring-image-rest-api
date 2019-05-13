package com.mgraja.springimagerestapi.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mgraja.springimagerestapi.controller.error.ScaleZeroError;
import com.mgraja.springimagerestapi.model.Histogram;
import com.mgraja.springimagerestapi.model.Images;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.imageio.ImageIO;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class ImageProcessor {
    private Controller controller;
    private Images images;

    public ImageProcessor(Controller controller, Images images) {
        this.controller = controller;
        this.images = images;
    }

    String postImage(byte[] input){
        BufferedImage bufferedImage;
        try {
            bufferedImage = ImageIO.read(new ByteArrayInputStream(input));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return images.put(bufferedImage);
    }
    ResponseEntity<byte[]> getScaled(String id, double percentage){
        if (percentage == 0) throw new ScaleZeroError();
        BufferedImage originalImage = images.get(id);
        double ratio = percentage/100.0;
        int w = (int) (originalImage.getHeight()*ratio);
        int h = (int) (originalImage.getWidth()*ratio);
        BufferedImage scaledImage = new BufferedImage(w, h, originalImage.getType());
        AffineTransform at = new AffineTransform();
        at.scale(ratio, ratio);
        AffineTransformOp scale = new AffineTransformOp(at, AffineTransformOp.TYPE_BICUBIC);
        scaledImage = scale.filter(originalImage, scaledImage);
        ResponseEntity<byte[]> response = convertToResponseEntity(scaledImage);
        return response;
    }
    ResponseEntity<byte[]> getInGreyScale(String id){
        BufferedImage inputImage = images.get(id);
        BufferedImage outputImage = new BufferedImage(inputImage.getWidth(), inputImage.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        outputImage.createGraphics().drawImage(inputImage, 0, 0, null);
        ResponseEntity<byte[]> response = convertToResponseEntity(outputImage);
        return response;

    }
    String getHistogram(String id){
        Histogram histogram = new Histogram(images.get(id), true);
        try {
            return new ObjectMapper().writeValueAsString(histogram);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }
    ResponseEntity<byte[]> getCropped(String id){
        BufferedImage bufferedImage = images.get(id);
        return null;
    }
    private ResponseEntity<byte[]> convertToResponseEntity(BufferedImage bufferedImage){
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "jpg", outputStream);
            HttpHeaders headers = new HttpHeaders();
            ResponseEntity<byte[]> responseEntity = new ResponseEntity<>(outputStream.toByteArray(), headers, HttpStatus.OK);
            return responseEntity;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressWarnings("Duplicates")
    public ResponseEntity<byte[]> getBlurred(String id, int radius, Double sigma, int passes) {
        BufferedImage bufferedImage = images.get(id);
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();
        WritableRaster writableRaster = bufferedImage.getRaster();
        double[] matrix = createGaussianMatrix(radius, sigma);
        for (int w =0; w < width; w++){
            for (int h=0; h<height; h++){
                for (int RGB = 0; RGB < passes*3; RGB++) {
                    int sumHorizontal = 0;
                    int sumVertical = 0;
                    double totalApplied = 0;
                    for (int i=-radius; i<=radius; i++){

                        if (h+i>= 0 && h+i < height){//check if in bound of original image
                            sumVertical += writableRaster.getSample(w, h+i, RGB%3) * matrix[i+radius];
                            totalApplied += matrix[i+radius];
                        }
                    }
                    sumVertical /= totalApplied;
                    writableRaster.setSample(w, h, RGB%3, (sumVertical));
                    totalApplied = 0;
                    for (int i=-radius; i<=radius; i++) {
                        if (w + i >= 0 && w + i < width) { //check if in bound of original image
                            sumHorizontal += writableRaster.getSample(w + i, h, RGB%3) * matrix[i + radius];
                            totalApplied += matrix[i+radius];
                        }
                    }
                    sumHorizontal /= totalApplied;
                    writableRaster.setSample(w, h, RGB%3, (sumHorizontal));
                }
            }
        }
        BufferedImage blurred = new BufferedImage(bufferedImage.getColorModel(), writableRaster, bufferedImage.isAlphaPremultiplied(), null);
        return convertToResponseEntity(blurred);
    }
    @SuppressWarnings("Duplicates")
    private double[] createGaussianMatrix(int radius, double sigma){
        double s = 2 * sigma * sigma;
        double[] kernel = new double[2*radius+1];
        System.out.println("matrix:");
        double sum = 0;
        double divider = Math.sqrt(2*Math.PI)*sigma;
        for (int i = -radius; i<=radius; i++){
            kernel[i+radius] = Math.exp(-((i)*(i))/s)/divider;
            sum += kernel[i+radius];
        }
        for (int i = -radius; i<=radius; i++){
            kernel[i+radius]/=sum;
            System.out.print(kernel[i+radius] + "\t");
        }
        System.out.println("\n" + sum);
        return kernel;
    }
}
