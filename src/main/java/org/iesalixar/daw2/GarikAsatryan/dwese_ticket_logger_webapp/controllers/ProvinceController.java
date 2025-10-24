package org.iesalixar.daw2.GarikAsatryan.dwese_ticket_logger_webapp.controllers;

import org.iesalixar.daw2.GarikAsatryan.dwese_ticket_logger_webapp.dao.ProvinceDAO;
import org.iesalixar.daw2.GarikAsatryan.dwese_ticket_logger_webapp.dao.RegionDAO;
import org.iesalixar.daw2.GarikAsatryan.dwese_ticket_logger_webapp.entities.Province;
import org.iesalixar.daw2.GarikAsatryan.dwese_ticket_logger_webapp.entities.Region;
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
    @Autowired
    private RegionDAO regionDAO;

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

        Province province = new Province();
        province.setRegionId(null);
        model.addAttribute("province", province);

        try {
            List<Region> regions = regionDAO.listAllRegions();
            model.addAttribute("regions", regions);
        } catch (SQLException e) {
            logger.error("Error al listar las regiones: {}", e.getMessage());
            model.addAttribute("errorMessage", "Error al listar las regiones.");
        }

        return "province-form";
    }

    @GetMapping("edit")
    public String showEditForm(@RequestParam("id") Long id, Model model) {
        logger.info("Mostrando formulario de edición para la región con ID {}", id);
        Province province = null;

        try {
            province = provinceDAO.getProvinceById(id);
            if (province == null) {
                logger.warn("No se encontró la provincia con ID {}", id);
                model.addAttribute("errorMessage", "No se encontró la provincia con ID " + id);
            }

            List<Region> regions = regionDAO.listAllRegions();
            model.addAttribute("regions", regions);

        } catch (SQLException e) {
            logger.error("Error al obtener la provincia con ID {}: {}", id, e.getMessage());
            model.addAttribute("errorMessage", "Error al obtener la provincia.");
        }
        model.addAttribute("province", province);

        return "province-form";
    }

    @PostMapping("/insert")
    public String insertProvince(@ModelAttribute("province") Province province, RedirectAttributes redirectAttributes) {
        logger.info("Insertando nueva provincia con código {}", province.getCode());
        try {
            if (provinceDAO.existsProvinceByCode(province.getCode())) {
                logger.warn("El código de la provincia {} ya existe.", province.getCode());
                redirectAttributes.addFlashAttribute("errorMessage", "El código de la provincia ya existe.");
                return "redirect:/provinces/new";
            }
            provinceDAO.insertProvince(province);
            logger.info("provincia {} insertada con éxito.", province.getCode());
        } catch (SQLException e) {
            logger.error("Error al insertar la provincia {}: {}", province.getCode(), e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Error al insertar la provincia.");
        }
        return "redirect:/provinces";
    }

    @PostMapping("/update")
    public String updateProvince(@ModelAttribute("province") Province province, RedirectAttributes redirectAttributes) {
        logger.info("Actualizando provincia con ID {}", province.getId());
        try {
            if (provinceDAO.existsProvinceByCodeAndNotId(province.getCode(), province.getId())) {
                logger.warn("El código de la provincia {} ya existe para otra provincia.", province.getCode());
                redirectAttributes.addFlashAttribute("errorMessage", "El codigo de la provincia ya existe para otra provincia");
                return "redirect:/provinces/edit?id=" + province.getId();
            }
            provinceDAO.updateProvince(province);
            logger.info("Provincia con ID {} actualizada con éxito.", province.getId());
        } catch (SQLException e) {
            logger.error("Error al actualizar la provincia con ID {}", province.getId());
            redirectAttributes.addFlashAttribute("errorMessage", "Error al actualizar la provincia.");
        }
        return "redirect:/provinces";
    }

    @PostMapping("/delete")
    public String deleteProvince(@RequestParam("id") Long id, RedirectAttributes redirectAttributes) {
        logger.info("Eliminando provincia con ID {}", id);
        try {
            provinceDAO.deleteProvince(id);
            logger.info("Provincia co ID {} eliminada con éxito.", id);
        } catch (SQLException e) {
            logger.error("Error al eliminar la provincia con ID {}: {}", id, e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Error al eliminar la provincia.");
        }
        return "redirect:/provinces";
    }
}
