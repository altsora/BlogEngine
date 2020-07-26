package main.util;

import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

public final class ImageUtil {
    public static String getRandomImageName(StringBuilder mainPath, String format) {
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvxyz";
        String numbers = "0123456789";
        StringBuilder fileName = new StringBuilder("/upload/");
        int lengthPath = 3;
        int fileNameLength = 5;
        for (int i = 0; i < 3; i++) {
            StringBuilder folderName = new StringBuilder();
            for (int j = 0; j < lengthPath; j++) {
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
        for (int i = 0; i < fileNameLength; i++) {
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
        int width = 35;
        int height = 35;
        String formatName = file.getOriginalFilename().split("\\.")[1];
        // MultipartFile -> Image
        BufferedImage image = null;
        try (ByteArrayInputStream bais = new ByteArrayInputStream(file.getBytes())) {
            image = ImageIO.read(bais);
            if (image.getWidth() > width && image.getHeight() > height) {
                int type = image.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : image.getType();
                BufferedImage resizeImage = new BufferedImage(width, height, type);
                Graphics2D g = resizeImage.createGraphics();
                g.drawImage(image, 0, 0, width, height, null);
                g.dispose();
                image = resizeImage;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Image -> Bytes
        byte[] imageBytes = null;
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(image, formatName, baos);
            imageBytes = baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Bytes -> Upload
        StringBuilder mainPath = new StringBuilder("src/main/resources/upload/");
        String fileName = getRandomImageName(mainPath, formatName);
        try (BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File(mainPath.toString())))) {
            stream.write(imageBytes);
            return fileName;
        } catch (IOException e) {
            return "";
        }
    }
}
