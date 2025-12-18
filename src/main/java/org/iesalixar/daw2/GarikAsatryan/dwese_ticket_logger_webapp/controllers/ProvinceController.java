package org.iesalixar.daw2.GarikAsatryan.dwese_ticket_logger_webapp.controllers;

import jakarta.validation.Valid;
import org.iesalixar.daw2.GarikAsatryan.dwese_ticket_logger_webapp.entities.Province;
import org.iesalixar.daw2.GarikAsatryan.dwese_ticket_logger_webapp.entities.Region;
import org.iesalixar.daw2.GarikAsatryan.dwese_ticket_logger_webapp.repositories.ProvinceRepository;
import org.iesalixar.daw2.GarikAsatryan.dwese_ticket_logger_webapp.repositories.RegionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/provinces")
public class ProvinceController {

    private static final Logger logger = LoggerFactory.getLogger(ProvinceController.class);

    @Autowired
    private ProvinceRepository provinceRepository;

    @Autowired
    private RegionRepository regionRepository;

    @GetMapping
    public String listProvinces(@RequestParam(defaultValue = "0") int page, Model model) {
        logger.info("Solicitando la lista de provincias (página {})", page);

        Pageable pageable = PageRequest.of(page, 5);
        Page<Province> provincePage = provinceRepository.findAll(pageable);

        model.addAttribute("listProvinces", provincePage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", provincePage.getTotalPages());
        model.addAttribute("activePage", "provinces");

        return "province";
    }

    @GetMapping("/new")
    public String showNewForm(Model model) {
        logger.info("Mostrando formulario para nueva provincia.");
        model.addAttribute("province", new Province());

        List<Region> regions = regionRepository.findAll();
        model.addAttribute("regions", regions);

        return "province-form";
    }

    @GetMapping("/edit")
    public String showEditForm(@RequestParam("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        logger.info("Mostrando formulario de edición para la provincia con ID {}", id);
        Optional<Province> provinceOpt = provinceRepository.findById(id);

        if (provinceOpt.isEmpty()) {
            logger.warn("No se encontró la provincia con ID {}", id);
            redirectAttributes.addFlashAttribute("errorMessage", "No se encontró la provincia con ID " + id);
            return "redirect:/provinces";
        }

        model.addAttribute("province", provinceOpt.get());
        model.addAttribute("regions", regionRepository.findAll());
        return "province-form";
    }

    @PostMapping("/insert")
    public String insertProvince(
            @Valid @ModelAttribute("province") Province province,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        logger.info("Insertando nueva provincia con código {}", province.getCode());

        if (result.hasErrors()) {
            logger.warn("Errores de validación en el formulario de nueva provincia.");
            model.addAttribute("regions", regionRepository.findAll());
            return "province-form";
        }

        if (provinceRepository.existsProvinceByCode(province.getCode())) {
            logger.warn("El código de la provincia {} ya existe.", province.getCode());
            model.addAttribute("errorMessage", "El código de la provincia ya existe.");
            model.addAttribute("regions", regionRepository.findAll());
            return "province-form";
        }

        provinceRepository.save(province);
        logger.info("Provincia {} insertada con éxito.", province.getCode());
        redirectAttributes.addFlashAttribute("successMessage", "Provincia insertada correctamente.");
        return "redirect:/provinces";
    }

    @PostMapping("/update")
    public String updateProvince(
            @Valid @ModelAttribute("province") Province province,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        logger.info("Actualizando provincia con ID {}", province.getId());

        if (result.hasErrors()) {
            logger.warn("Errores de validación al actualizar la provincia.");
            model.addAttribute("regions", regionRepository.findAll());
            return "province-form";
        }

        if (provinceRepository.existsProvinceByCodeAndNotId(province.getCode(), province.getId())) {
            logger.warn("El código de la provincia {} ya existe para otra provincia.", province.getCode());
            model.addAttribute("errorMessage", "El código de la provincia ya existe para otra provincia.");
            model.addAttribute("regions", regionRepository.findAll());
            return "province-form";
        }

        provinceRepository.save(province);
        logger.info("Provincia con ID {} actualizada con éxito.", province.getId());
        redirectAttributes.addFlashAttribute("successMessage", "Provincia actualizada correctamente.");
        return "redirect:/provinces";
    }

    @PostMapping("/delete")
    public String deleteProvince(@RequestParam("id") Long id, RedirectAttributes redirectAttributes) {
        logger.info("Eliminando provincia con ID {}", id);
        provinceRepository.deleteById(id);
        logger.info("Provincia con ID {} eliminada con éxito.", id);
        redirectAttributes.addFlashAttribute("successMessage", "Provincia eliminada correctamente.");
        return "redirect:/provinces";
    }
}
