package com.mgraja.springimagerestapi.controller;

import com.mgraja.springimagerestapi.model.Images;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class Controller {
    private final ImageProcessor imageProcessor;
    private final Images images;
    Controller(){
        images = new Images(this);
        imageProcessor = new ImageProcessor(this, images);

    }
    @ResponseStatus(value = HttpStatus.CREATED)
    @PostMapping("/image")
   String post(@RequestParam("file") MultipartFile file) throws Exception{
        return imageProcessor.postImage(file.getBytes());
    }
    @DeleteMapping("/image/{id}")
    public void delete(@PathVariable() String id){
        images.remove(id);
    }
    @ResponseStatus(value = HttpStatus.OK)
    @GetMapping("/image/{id}/size")
    public String getSize(@PathVariable(name = "id") String id){
        return images.getSize(id);
    }
    @ResponseStatus(value = HttpStatus.OK)
    @GetMapping(value = "/image/{id}/scale/{percent}", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> getAndScale(@PathVariable(name = "id") String id, @PathVariable(name = "percent") double percent){
        return imageProcessor.getScaled(id, percent);
    }
    @ResponseStatus(value = HttpStatus.OK)
    @GetMapping(value = "/image/{id}/histogram", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String getHistogram(@PathVariable(value = "id") String id){
        return imageProcessor.getHistogram(id);
    }
    @ResponseStatus(value = HttpStatus.OK)
    @GetMapping(value = "/image/{id}/crop/{start}/{stop}/{width}/{height}", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> crop(@PathVariable(name = "id")String id,
                       @PathVariable(name = "start")String start, @PathVariable(name = "stop")String stop,
                       @PathVariable(name = "width")String width, @PathVariable(name = "height")String height){
        return imageProcessor.getCropped(id);
    }
    @ResponseStatus(value = HttpStatus.OK)
    @GetMapping(value = "/image/{id}/greyscale", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> getInGreyscale(@PathVariable(name = "id") String id){
//        ResponseEntity<byte[]> inGreyScale;
//        inGreyScale = imageProcessor.getInGreyScale(id);
        return imageProcessor.getInGreyScale(id);
    }
    @ResponseStatus(value = HttpStatus.OK)
    @GetMapping(value ="/image/{id}/blur/{radius}", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> getBlurred(@PathVariable(name = "id") String id,
                                             @PathVariable(name = "radius") int radius,
                                             @RequestParam(name = "sigma", defaultValue = "1.0") double sigma,
                                             @RequestParam(name = "passes", defaultValue = "1") int passes){
        return imageProcessor.getBlurred(id, radius, sigma, passes);
    }
    public ImageProcessor getImageProcessor() {
        return imageProcessor;
    }
}
