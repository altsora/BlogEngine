package main.controller;

import main.api.responses.ErrorResponse;
import main.api.responses.ResultResponse;
import main.utils.ImageUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

import static main.utils.MessageUtil.MESSAGE_IMAGE_ERROR_LOAD;
import static main.utils.MessageUtil.MESSAGE_IMAGE_INVALID_FORMAT;

@RestController
public class ImageController {

    @PostMapping(value = "/api/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity uploadImage(@RequestPart(value = "image") MultipartFile file) {
        String name = file.getOriginalFilename();
        String formatName = Objects.requireNonNull(name).split("\\.")[1];

        if (!formatName.equalsIgnoreCase("png") && !formatName.equalsIgnoreCase("jpg")) {
            ErrorResponse errors = new ErrorResponse();
            errors.setImage(MESSAGE_IMAGE_INVALID_FORMAT);
            ResultResponse response = new ResultResponse(errors);
            return ResponseEntity.badRequest().body(response);
        }

        if (!file.isEmpty()) {
            StringBuilder mainPath = new StringBuilder("src/main/resources/upload/");
            String fileName = ImageUtil.getRandomImageName(mainPath, formatName);
            try (BufferedOutputStream stream =
                         new BufferedOutputStream(new FileOutputStream(new File(mainPath.toString())));) {
                byte[] bytes = file.getBytes();
                stream.write(bytes);
                return ResponseEntity.ok(fileName);
            } catch (Exception e) {
                ErrorResponse errors = new ErrorResponse();
                errors.setImage(MESSAGE_IMAGE_ERROR_LOAD);
                ResultResponse response = new ResultResponse(errors);
                return ResponseEntity.badRequest().body(response);
            }
        }
        return null;
    }

    @GetMapping(value = "/upload/{dir1}/{dir2}/{dir3}/{fileName}")
    public ResponseEntity getImage(
            @PathVariable(value = "dir1") String dir1,
            @PathVariable(value = "dir2") String dir2,
            @PathVariable(value = "dir3") String dir3,
            @PathVariable(value = "fileName") String fileName
    ) {
        String fullPath = String.format("src/main/resources/upload/%s/%s/%s/%s", dir1, dir2, dir3, fileName);

        byte[] buffer = null;
        try {
            buffer = Files.readAllBytes(Paths.get(fullPath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        String format = fullPath.split("\\.")[1];
        final HttpHeaders headers = new HttpHeaders();
        if (format.equalsIgnoreCase("png"))
            headers.setContentType(MediaType.IMAGE_PNG);
        if (format.equalsIgnoreCase("jpg"))
            headers.setContentType(MediaType.IMAGE_JPEG);
        if (format.equalsIgnoreCase("gif"))
            headers.setContentType(MediaType.IMAGE_GIF);
        return new ResponseEntity<>(buffer, headers, HttpStatus.OK);
    }


}
