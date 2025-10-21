package org.iesalixar.daw2.GarikAsatryan.dwese_ticket_logger_webapp.controllers;

import org.iesalixar.daw2.GarikAsatryan.dwese_ticket_logger_webapp.dao.ProvinceDAO;
import org.iesalixar.daw2.GarikAsatryan.dwese_ticket_logger_webapp.entities.Province;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.sql.SQLException;
import java.util.List;

@Controller
@RequestMapping("provinces")
public class ProvinceController {
    private static final Logger logger = LoggerFactory.getLogger(ProvinceController.class);

    @Autowired
    private ProvinceDAO provinceDAO;

    @GetMapping
    public String listProvinces(Model model) {
        logger.info("Solicitando la lista de todas las provincias...");
        List<Province> listProvinces = null;
        try {
            listProvinces = provinceDAO.listAllProvinces();
            logger.info("Se han cargado {} provincias.", listProvinces.size());
        } catch (SQLException e) {
            logger.error("Error al listar las provincias: {}", e.getMessage());
            model.addAttribute("errorMessage", "Error al listar las provincias.");
        }
        model.addAttribute("listProvinces", listProvinces);
        return "province";
    }

    @GetMapping("/new")
    public String showNewForm(Model model) {
        logger.info("Mostrando formulario para nueva provincia.");
        model.addAttribute("province", new Province());
        return "province-form";
    }

    @GetMapping("edit")
    public String showEditForm(@RequestParam("id") long id, Model model) {
        logger.info("mopstrando formulario de edición para la provincia con ID {}", id);
        Province province = null;
        try {
            province = provinceDAO.getProvinceById(id);
            if (province == null) {
                logger.warn("No se encontró la provincia con ID {}", id);
            }
        } catch (SQLException e) {
            logger.error("Error al obtener la provincia con ID {}: {}", id, e.getMessage());
            model.addAttribute("errorMessage", "Error al obtener la provincia.");
        }
        model.addAttribute("province", province);
        return "province-form";
    }

    @PostMapping("/insert")
    public String insertRegion(@ModelAttribute("province") Province province, RedirectAttributes redirectAttributes) {
        logger.info("Insertando nueva provincia con código {}", province.getCode());
        try {
            if (provinceDAO.existsProvinceByCode(province.getCode())) {
                logger.warn("El código de la provincia {} ya existe.", province.getCode());
                redirectAttributes.addFlashAttribute("errorMessage", "El código de la provincia ya existe.");
                return "redirect:/provinces/new";
            }
            provinceDAO.insertProvince(province);
            logger.info("provincia {ç insertada con ´çexito.", province.getCode());
        } catch (SQLException e) {
            logger.error("Error al insertar la provincia {}: {}", province.getCode(), e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Error al insertar la provincia.");
        }
        return "redirect:/provinces";
    }
}
