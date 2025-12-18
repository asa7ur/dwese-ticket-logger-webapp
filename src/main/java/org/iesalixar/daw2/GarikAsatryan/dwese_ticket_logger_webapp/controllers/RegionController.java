package org.iesalixar.daw2.GarikAsatryan.dwese_ticket_logger_webapp.controllers;

import jakarta.validation.Valid;
import org.iesalixar.daw2.GarikAsatryan.dwese_ticket_logger_webapp.entities.Region;
import org.iesalixar.daw2.GarikAsatryan.dwese_ticket_logger_webapp.repositories.RegionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Locale;
import java.util.Optional;

@Controller
@RequestMapping("/regions")
public class RegionController {
    private static final Logger logger = LoggerFactory.getLogger(RegionController.class);

    @Autowired
    private RegionRepository regionRepository;

    @GetMapping
    public String listRegions(@RequestParam(defaultValue = "1") int page, Model model) {
        logger.info("Solicitando la lista de regiones (página {})", page);

        model.addAttribute("listRegions", regionRepository.findAll());
        model.addAttribute("activePage", "regions");

        return "region";
    }

    @GetMapping("/new")
    public String showNewForm(Model model) {
        logger.info("Mostrando formulario para nueva región.");
        model.addAttribute("region", new Region());
        return "region-form";
    }

    @GetMapping("/edit")
    public String showEditForm(@RequestParam("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        logger.info("Mostrando formulario de edición para la región con ID {}", id);
        Optional<Region> regionOpt = regionRepository.findById(id);

        if (regionOpt.isEmpty()) {
            logger.warn("No se pudo encontrar la región con ID {}", id);
            redirectAttributes.addFlashAttribute("errorMessage", "La región no existe.");
            return "redirect:/regions";
        }

        model.addAttribute("region", regionOpt.get());
        return "region-form";
    }

    @PostMapping("/insert")
    public String insertRegion(
            @Valid @ModelAttribute("region") Region region,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            Locale locale,
            Model model) {

        logger.info("Insertando nueva región con código {}", region.getCode());

        if (result.hasErrors()) {
            logger.warn("Errores de validación en el formulario de nueva región.");
            return "region-form";
        }

        if (regionRepository.existsRegionByCode(region.getCode())) {
            logger.warn("El código de la región {} ya existe.", region.getCode());
            model.addAttribute("errorMessage", "El código de la región ya existe.");
            return "region-form";
        }

        regionRepository.save(region);
        logger.info("Región {} insertada con éxito.", region.getCode());
        redirectAttributes.addFlashAttribute("successMessage", "Región insertada correctamente.");
        return "redirect:/regions";
    }

    @PostMapping("/update")
    public String updateRegion(
            @Valid @ModelAttribute("region") Region region,
            BindingResult result,
            Locale locale,
            Model model,
            RedirectAttributes redirectAttributes) {

        logger.info("Actualizando región con ID {}", region.getId());

        if (result.hasErrors()) {
            logger.warn("Errores de validación al actualizar región con ID {}", region.getId());
            return "region-form";
        }

        if (regionRepository.existsRegionByCodeAndNotId(region.getCode(), region.getId())) {
            logger.warn("El código de la región {} ya existe para otra región.", region.getCode());
            model.addAttribute("errorMessage", "El código de la región ya existe para otra región.");
            return "region-form";
        }

        regionRepository.save(region);
        logger.info("Región con ID {} actualizada con éxito.", region.getId());
        redirectAttributes.addFlashAttribute("successMessage", "Región actualizada correctamente.");
        return "redirect:/regions";
    }

    @PostMapping("/delete")
    public String deleteRegion(@RequestParam("id") Long id, RedirectAttributes redirectAttributes) {
        logger.info("Eliminando región con ID {}", id);

        Optional<Region> regionOpt = regionRepository.findById(id);
        if (regionOpt.isEmpty()) {
            logger.warn("No se encontró la región con ID {}", id);
            redirectAttributes.addFlashAttribute("errorMessage", "La región no existe.");
            return "redirect:/regions";
        }

        regionRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("successMessage", "Región eliminada correctamente.");
        return "redirect:/regions";
    }

}
