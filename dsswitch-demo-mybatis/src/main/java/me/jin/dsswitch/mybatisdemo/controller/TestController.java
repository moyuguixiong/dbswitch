package me.jin.dsswitch.mybatisdemo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author jinshilei
 * @version 0.0.1
 * @date 2020/09/01
 */
@RestController
@RequestMapping("test/")
public class TestController {

    @GetMapping("test")
    public String test() {
        return "hello world";
    }
}
