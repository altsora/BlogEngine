package main.controller;

import main.api.responses.ResultResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import static main.utils.MessageUtil.MESSAGE_MAX_UPLOAD_SIZE;

@ControllerAdvice
public class FileUploadExceptionAdvice {
    @Value("${spring.servlet.multipart.max-file-size}")
    private String maxImageSize;

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ResultResponse> handleMaxSizeException () {
        return ResponseEntity.badRequest().body(new ResultResponse(MESSAGE_MAX_UPLOAD_SIZE + maxImageSize));
    }
}
