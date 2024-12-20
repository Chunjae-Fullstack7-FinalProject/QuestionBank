package net.questionbank.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.questionbank.annotation.Logging;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Logging
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/")
public class TestRestController {
}
