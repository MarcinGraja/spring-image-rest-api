package com.mgraja.springimagerestapi.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mgraja.springimagerestapi.controller.Controller;
import com.mgraja.springimagerestapi.controller.error.ImageNotFound;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;

public class Images {
    private Map<String, BufferedImage> imageMap = new HashMap<>();
    private FreeKeys freeKeys = new FreeKeys();
    private Controller controller;
    private ObjectMapper objectMapper = new ObjectMapper();
    public Images(Controller controller) {
        this.controller = controller;
    }
    public BufferedImage get(String key){
        BufferedImage bufferedImage = imageMap.get(key);
        if (bufferedImage==null) throw new ImageNotFound(key);
        BufferedImage copy = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), bufferedImage.getType());
        Graphics g = copy.getGraphics();
        g.drawImage(bufferedImage, 0, 0, null);
        g.dispose();
        return copy;
    }
    public void remove(String key){
        if (imageMap.remove(key) == null) throw new ImageNotFound(key);
        freeKeys.add(key);
    }
    public String put(BufferedImage bufferedImage){
        String key = freeKeys.nextFreeKey();
        imageMap.put(key,bufferedImage);
        ImageData imageData = new ImageData(key, bufferedImage.getWidth(), bufferedImage.getHeight());
        try {
            return objectMapper.writeValueAsString(imageData);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }
    public String getSize(String id){
        BufferedImage bufferedImage = get(id);
        try {
            return objectMapper.writeValueAsString(new ImageData(id, bufferedImage.getWidth(), bufferedImage.getHeight()));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
class FreeKeys extends LinkedList<String> {
    private String last;

    FreeKeys() {
        add("img000001");
        last = "img000001";
    }

    String nextFreeKey() {
        String nextKey = removeLast();
        if (nextKey.equals(last)) {
            last = incrementString(nextKey);
            add(last);
        }
        return nextKey;
    }

    private String incrementString(String id) {
        int value = Integer.parseInt(id.substring(3));
        ++value;
        StringBuilder idBuilder = new StringBuilder(String.valueOf(value));
        while (idBuilder.length() < 6) idBuilder.insert(0, "0");
        return idBuilder.insert(0, "img").toString();
    }
}