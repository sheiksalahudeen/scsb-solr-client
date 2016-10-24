package org.recap.controller;

import org.recap.model.search.RequestForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by rajeshbabuk on 13/10/16.
 */

@Controller
public class RequestController {

    Logger logger = LoggerFactory.getLogger(RequestController.class);

    @RequestMapping("/request")
    public String collection(Model model) {
        RequestForm requestForm = new RequestForm();
        model.addAttribute("requestForm", requestForm);
        model.addAttribute("template", "request");
        return "searchRecords";
    }
}
