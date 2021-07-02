package com.example.demo.component;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @GetMapping("/main")
    public String about(Model model) {
        model.addAttribute("title", "Сайт находится на техническом обслуживании...");
        return "about";
    }

}
