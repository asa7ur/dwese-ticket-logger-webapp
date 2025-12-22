package org.iesalixar.daw2.GarikAsatryan.dwese_ticket_logger_webapp.controllers;

import jakarta.validation.Valid;
import org.iesalixar.daw2.GarikAsatryan.dwese_ticket_logger_webapp.entities.Region;
import org.iesalixar.daw2.GarikAsatryan.dwese_ticket_logger_webapp.repositories.RegionRepository;
import org.iesalixar.daw2.GarikAsatryan.dwese_ticket_logger_webapp.services.FileStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/regions")
public class RegionController {
    private static final Logger logger = LoggerFactory.getLogger(RegionController.class);

    @Autowired
    private RegionRepository regionRepository;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private MessageSource messageSource;

    @GetMapping
    public String listRegions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "code") String sortField,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String searchTerm,
            Model model) {

        logger.info("Listando regiones - Página: {}, Orden: {} {}, Busqueda: {}", page, sortField, sortDir, searchTerm);

        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortField).ascending() : Sort.by(sortField).descending();
        Pageable pageable = PageRequest.of(page, 5, sort);

        Page<Region> regionPage;
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            regionPage = regionRepository.findByNameContainingIgnoreCase(searchTerm, pageable);
        } else {
            regionPage = regionRepository.findAll(pageable);
        }

        model.addAttribute("listRegions", regionPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", regionPage.getTotalPages());
        model.addAttribute("searchTerm", searchTerm);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
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
            redirectAttributes.addFlashAttribute("errorMessage",
                    messageSource.getMessage("msg.region.not-found", null, LocaleContextHolder.getLocale()));
            return "redirect:/regions";
        }

        model.addAttribute("region", regionOpt.get());
        return "region-form";
    }

    @PostMapping("/insert")
    public String insertRegion(
            @Valid @ModelAttribute("region") Region region,
            BindingResult result,
            @RequestParam("imageFile") MultipartFile imageFile,
            RedirectAttributes redirectAttributes,
            Model model) {

        logger.info("Insertando nueva región con código {}", region.getCode());

        if (result.hasErrors()) {
            logger.warn("Errores de validación en el formulario de nueva región.");
            return "region-form";
        }

        if (regionRepository.existsRegionByCode(region.getCode())) {
            logger.warn("El código de la región {} ya existe.", region.getCode());
            model.addAttribute("errorMessage",
                    messageSource.getMessage("msg.region.code-exists", null, LocaleContextHolder.getLocale()));
            return "region-form";
        }

        if (!imageFile.isEmpty()) {
            String fileName = fileStorageService.saveFile(imageFile);
            if (fileName != null) {
                region.setImage(fileName);
            }
        }

        regionRepository.save(region);
        logger.info("Región {} insertada con éxito.", region.getCode());
        redirectAttributes.addFlashAttribute("successMessage",
                messageSource.getMessage("msg.region.inserted", null, LocaleContextHolder.getLocale()));
        return "redirect:/regions";
    }

    @PostMapping("/update")
    public String updateRegion(
            @Valid @ModelAttribute("region") Region region,
            BindingResult result,
            @RequestParam("imageFile") MultipartFile imageFile,
            RedirectAttributes redirectAttributes,
            Model model) {

        logger.info("Actualizando región con ID {}", region.getId());

        if (result.hasErrors()) {
            logger.warn("Errores de validación al actualizar región con ID {}", region.getId());
            return "region-form";
        }

        if (regionRepository.existsRegionByCodeAndNotId(region.getCode(), region.getId())) {
            logger.warn("El código de la región {} ya existe para otra región.", region.getCode());
            model.addAttribute("errorMessage",
                    messageSource.getMessage("msg.region.code-exists", null, LocaleContextHolder.getLocale()));
            return "region-form";
        }

        if (imageFile != null && !imageFile.isEmpty()) {
            Region existingRegion = regionRepository.findById(region.getId()).orElse(null);
            if (existingRegion != null && existingRegion.getImage() != null) {
                fileStorageService.deleteFile(existingRegion.getImage());
            }

            String fileName = fileStorageService.saveFile(imageFile);
            region.setImage(fileName);
        } else {
            regionRepository.findById(region.getId()).ifPresent(r -> region.setImage(r.getImage()));
        }

        regionRepository.save(region);
        logger.info("Región con ID {} actualizada con éxito.", region.getId());
        redirectAttributes.addFlashAttribute("successMessage",
                messageSource.getMessage("msg.region.updated", null, LocaleContextHolder.getLocale()));
        return "redirect:/regions";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/delete")
    public String deleteRegion(
            @RequestParam("id") Long id,
            RedirectAttributes redirectAttributes) {
        logger.info("Eliminando región con ID {}", id);

        Optional<Region> regionOpt = regionRepository.findById(id);
        if (regionOpt.isPresent()) {
            Region region = regionOpt.get();
            if (region.getImage() != null) {
                fileStorageService.deleteFile(region.getImage());
            }
            regionRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage",
                    messageSource.getMessage("msg.region.deleted", null, LocaleContextHolder.getLocale()));
        } else {
            redirectAttributes.addFlashAttribute("errorMessage",
                    messageSource.getMessage("msg.region.not-found", null, LocaleContextHolder.getLocale()));
        }

        return "redirect:/regions";
    }

}