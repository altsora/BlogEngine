package main.util;

import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

public final class ImageUtil {
    private final static int AVATAR_HEIGHT = 35;
    private final static int AVATAR_WIDTH = 35;
    private final static int IMAGE_FOLDER_NAME_LENGTH = 3;
    private final static int IMAGE_NAME_LENGTH = 5;

    public static String getRandomImageName(StringBuilder mainPath, String format) {
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvxyz";
        String numbers = "0123456789";
        StringBuilder fileName = new StringBuilder("/upload/");

        for (int i = 0; i < 3; i++) {
            StringBuilder folderName = new StringBuilder();
            for (int j = 0; j < IMAGE_FOLDER_NAME_LENGTH; j++) {
                int index = (int) (Math.random() * alphabet.length());
                folderName.append(alphabet.charAt(index));
            }
            File folder = new File(mainPath.toString() + folderName);
            if (!folder.exists()) {
                folder.mkdir();
            }
            mainPath.append(folderName.toString()).append("/");
            fileName.append(folderName.toString()).append("/");
        }
        for (int i = 0; i < IMAGE_NAME_LENGTH; i++) {
            int index = (int) (Math.random() * numbers.length());
            mainPath.append(numbers.charAt(index));
            fileName.append(numbers.charAt(index));
        }

        mainPath.append(".").append(format);
        fileName.append(".").append(format);
        File image = new File(mainPath.toString());
        if (!image.exists()) {
            try {
                image.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return fileName.toString();
    }

    public static String resizeImageAndUpload(MultipartFile file) {
        String formatName = file.getOriginalFilename().split("\\.")[1];
        // MultipartFile -> Image
        BufferedImage image = null;
        try (ByteArrayInputStream bais = new ByteArrayInputStream(file.getBytes())) {
            image = ImageIO.read(bais);
            if (image.getWidth() > AVATAR_WIDTH && image.getHeight() > AVATAR_HEIGHT) {
                int type = image.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : image.getType();
                BufferedImage resizeImage = new BufferedImage(AVATAR_WIDTH, AVATAR_HEIGHT, type);
                Graphics2D g = resizeImage.createGraphics();
                g.drawImage(image, 0, 0, AVATAR_WIDTH, AVATAR_HEIGHT, null);
                g.dispose();
                image = resizeImage;
            }
        } catch (IOException e) {
            return null;
        }
        // Image -> Bytes
        byte[] imageBytes = null;
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(image, formatName, baos);
            imageBytes = baos.toByteArray();
        } catch (IOException e) {
            return null;
        }
        // Bytes -> Upload
        StringBuilder mainPath = new StringBuilder("src/main/resources/upload/");
        String fileName = getRandomImageName(mainPath, formatName);
        try (BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File(mainPath.toString())))) {
            stream.write(imageBytes);
            return fileName;
        } catch (IOException e) {
            return null;
        }
    }
}
