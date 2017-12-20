package com.gumi.paytest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@RequestMapping("/")
@Controller
@ResponseBody
public class HomeController {

    @RequestMapping("/")
    public String home() {
        return "支付测试";
    }
}
