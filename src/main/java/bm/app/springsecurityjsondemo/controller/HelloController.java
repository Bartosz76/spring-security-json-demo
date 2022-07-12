package bm.app.springsecurityjsondemo.controller;

import bm.app.springsecurityjsondemo.dto.MessageDto;

import org.springframework.web.bind.annotation.*;

@RestController
public class HelloController {

    @GetMapping("/")
    public MessageDto hello() {
        return new MessageDto("Hello!");
    }

    @GetMapping("/secured")
    public MessageDto helloSecured() {
        return new MessageDto("Hello but secured!");
    }

}
