package main.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiGeneralController {

    @GetMapping(value = "/api/init")
    public ResponseEntity init() {
        return new ResponseEntity("Hello", HttpStatus.OK);
    }

    @GetMapping(value = "/api/settings")
    public ResponseEntity getSettings() {
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }


}
