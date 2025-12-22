package org.iesalixar.daw2.GarikAsatryan.dwese_ticket_logger_webapp.controllers;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class GlobalErrorController implements ErrorController {
    private static final Logger logger = LoggerFactory.getLogger(GlobalErrorController.class);

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());
            model.addAttribute("errorCode", String.valueOf(statusCode));

            switch (statusCode) {
                case 403:
                    model.addAttribute("errorMessage", "msg.error.403");
                    break;
                case 404:
                    model.addAttribute("errorMessage", "msg.error.404");
                    break;
                case 500:
                    model.addAttribute("errorMessage", "msg.error.500");
                    break;
                default:
                    model.addAttribute("errorMessage", "msg.error.generic");
            }
        }

        return "error/generic";
    }
}