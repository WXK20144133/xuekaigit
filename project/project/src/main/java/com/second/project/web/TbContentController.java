package com.second.project.web;

import com.second.project.model.TbContent;
import com.second.project.service.TbService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("test")
public class TbContentController {
    @Autowired
    private TbService tbService;
    @RequestMapping("one")
    @ResponseBody
    public String test(){
        return "asfa";
    }
    @RequestMapping("two")
    @ResponseBody
    public List<TbContent> find(){
        return tbService.get();
    }
}
