package net.questionbank.controller;

import net.questionbank.annotation.Logging;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
@Logging
@Controller
@RequestMapping("/example")
public class ExampleController {
    @GetMapping("/api")
    public String api() {
        return "api-example";
    }
}
