package utils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.TreeMap;

public class ResourceHandler {

    private final TreeMap<String, BufferedImage> atlas;

    public ResourceHandler() {
        atlas = new TreeMap<>();
        // TODO Initialize Resources
        loadImages();
    }

    /**
     * Puts images from resource folder into treeMap atlas.
     */
    private void loadImages() {

        try {
            URL spriteURL = getClass().getResource("/SpriteSheet.png");
            int spriteWidth = 256;
            int spriteHeight = 256;
            assert spriteURL != null;
            BufferedImage spriteSheet = ImageIO.read(spriteURL);
            atlas.put("happy", spriteSheet.getSubimage(0, 0, spriteWidth, spriteHeight));
            atlas.put("sad", spriteSheet.getSubimage(spriteWidth, spriteHeight, spriteWidth, spriteWidth));
            atlas.put("annoyed", spriteSheet.getSubimage(spriteWidth, 0, spriteWidth, spriteHeight));
            atlas.put("grumpy", spriteSheet.getSubimage(0, spriteHeight, spriteWidth, spriteHeight));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Finds image corresponding to the type given.
     * @param type - the type of mood to display
     * @return the image corresponding to the mood.
     */
    public BufferedImage getImage(String type) {
        return atlas.get(type);
    }

}
