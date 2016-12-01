package org.recap.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by rajeshbabuk on 22/11/16.
 */

@Controller
public class IndexPageController {

    @RequestMapping("/")
    public String solrIndexer(Model model){
        return "index";
    }
}
