package org.iesalixar.daw2.GarikAsatryan.dwese_ticket_logger_webapp.controllers;

import jakarta.validation.Valid;
import org.iesalixar.daw2.GarikAsatryan.dwese_ticket_logger_webapp.dao.RegionDAO;
import org.iesalixar.daw2.GarikAsatryan.dwese_ticket_logger_webapp.entities.Region;
import org.iesalixar.daw2.GarikAsatryan.dwese_ticket_logger_webapp.entities.dto.RegionDTO;
import org.iesalixar.daw2.GarikAsatryan.dwese_ticket_logger_webapp.services.FileStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Locale;

@Controller
@RequestMapping("/regions")
public class RegionController {
    private static final Logger logger = LoggerFactory.getLogger(RegionController.class);

    @Autowired
    private RegionDAO regionDAO;

    @Autowired
    private FileStorageService fileStorageService;

    @GetMapping
    public String listRegions(@RequestParam(defaultValue = "1") int page, Model model) {
        logger.info("Solicitando la lista de regiones (página {})", page);

        RegionDTO regionDTO = regionDAO.listAllRegions(page);

        model.addAttribute("listRegions", regionDTO.getRegions());
        model.addAttribute("totalPages", regionDTO.getPages());
        model.addAttribute("currentPage", regionDTO.getCurrentPage());
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
        Region region = regionDAO.getRegionById(id);

        if (region == null) {
            logger.warn("No se encontró la región con ID {}", id);
            redirectAttributes.addFlashAttribute("errorMessage", "La región no existe.");
            return "redirect:/regions";
        }

        model.addAttribute("region", region);
        return "region-form";
    }

    @PostMapping("/insert")
    public String insertRegion(
            @Valid @ModelAttribute("region") Region region,
            BindingResult result,
            @RequestParam("imageFile") MultipartFile imageFile,
            RedirectAttributes redirectAttributes,
            Locale locale,
            Model model) {

        logger.info("Insertando nueva región con código {}", region.getCode());

        if (result.hasErrors()) {
            logger.warn("Errores de validación en el formulario de nueva región.");
            return "region-form";
        }

        if (regionDAO.existsRegionByCode(region.getCode())) {
            logger.warn("El código de la región {} ya existe.", region.getCode());
            model.addAttribute("errorMessage", "El código de la región ya existe.");
            return "region-form";
        }

        if (!imageFile.isEmpty()) {
            String fileName = fileStorageService.saveFile(imageFile);
            if (fileName != null) {
                region.setImage(fileName);
            }
        }

        regionDAO.insertRegion(region);
        logger.info("Región {} insertada con éxito.", region.getCode());
        redirectAttributes.addFlashAttribute("successMessage", "Región insertada correctamente.");
        return "redirect:/regions";
    }

    @PostMapping("/update")
    public String updateRegion(
            @Valid @ModelAttribute("region") Region region,
            BindingResult result,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            Locale locale,
            Model model,
            RedirectAttributes redirectAttributes) {

        logger.info("Actualizando región con ID {}", region.getId());

        if (result.hasErrors()) {
            logger.warn("Errores de validación al actualizar región con ID {}", region.getId());
            return "region-form";
        }

        if (regionDAO.existsRegionByCodeAndNotId(region.getCode(), region.getId())) {
            logger.warn("El código de la región {} ya existe para otra región.", region.getCode());
            model.addAttribute("errorMessage", "El código de la región ya existe para otra región.");
            return "region-form";
        }

        if (!imageFile.isEmpty()) {
            String fileName = fileStorageService.saveFile(imageFile);
            if (fileName != null) {
                region.setImage(fileName);
            }
        }

        regionDAO.updateRegion(region);
        logger.info("Región con ID {} actualizada con éxito.", region.getId());
        redirectAttributes.addFlashAttribute("successMessage", "Región actualizada correctamente.");
        return "redirect:/regions";
    }

    @PostMapping("/delete")
    public String deleteRegion(@RequestParam("id") Long id, RedirectAttributes redirectAttributes) {
        logger.info("Eliminando región con ID {}", id);

        Region region = regionDAO.getRegionById(id);
        if (region == null) {
            logger.warn("No se encontró la región con ID {}", id);
            redirectAttributes.addFlashAttribute("errorMessage", "La región no existe.");
            return "redirect:/regions";
        }

        regionDAO.deleteRegion(id);
        if (region.getImage() != null && !region.getImage().isEmpty()) {
            fileStorageService.deleteFile(region.getImage());
        }
        redirectAttributes.addFlashAttribute("successMessage", "Región eliminada correctamente.");
        return "redirect:/regions";
    }

    @PostMapping("/deleteImage")
    public String deleteRegionImage(@RequestParam("id") Long id, RedirectAttributes redirectAttributes) {
        Region region = regionDAO.getRegionById(id);
        if (region != null && region.getImage() != null) {
            fileStorageService.deleteFile(region.getImage());
            region.setImage(null);
            regionDAO.updateRegion(region);
            redirectAttributes.addFlashAttribute("successMessage", "Imagen eliminada correctamente.");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "No se encontró imagen para eliminar.");
        }
        return "redirect:/regions/edit?id=" + id;
    }

}
