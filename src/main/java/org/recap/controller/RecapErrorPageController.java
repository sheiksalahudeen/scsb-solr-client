package org.recap.controller;

import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by rajeshbabuk on 1/11/16.
 */
@Controller
public class RecapErrorPageController implements ErrorController {

    private static final String PATH = "/error";

    @RequestMapping(value = PATH)
    public String recapErrorPage() {
        return "error";
    }

    @Override
    public String getErrorPath() {
        return PATH;
    }
}
