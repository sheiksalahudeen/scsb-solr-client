package org.recap.controller;

import org.recap.model.search.ReportsForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by rajeshbabuk on 13/10/16.
 */

@Controller
public class ReportsController {

    Logger logger = LoggerFactory.getLogger(ReportsController.class);

    @RequestMapping("/reports")
    public String collection(Model model) {
        ReportsForm reportsForm = new ReportsForm();
        model.addAttribute("reportsForm", reportsForm);
        model.addAttribute("template", "reports");
        return "searchRecords";
    }
}
