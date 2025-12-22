package org.iesalixar.daw2.GarikAsatryan.dwese_ticket_logger_webapp.controllers;

import jakarta.validation.Valid;
import org.iesalixar.daw2.GarikAsatryan.dwese_ticket_logger_webapp.entities.Product;
import org.iesalixar.daw2.GarikAsatryan.dwese_ticket_logger_webapp.entities.Ticket;
import org.iesalixar.daw2.GarikAsatryan.dwese_ticket_logger_webapp.repositories.ProductRepository;
import org.iesalixar.daw2.GarikAsatryan.dwese_ticket_logger_webapp.repositories.ProvinceRepository;
import org.iesalixar.daw2.GarikAsatryan.dwese_ticket_logger_webapp.repositories.TicketRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Controller
@RequestMapping("/tickets")
public class TicketProductController {

    private static final Logger logger = LoggerFactory.getLogger(TicketProductController.class);

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MessageSource messageSource;

    @GetMapping
    public String listTickets(Model model) {
        logger.info("Solicitando la lista de todos los tickets...");
        List<Ticket> listTickets = null;
        try {
            listTickets = ticketRepository.findAll();
            logger.info("Se han cargado {} tickets.", listTickets.size());
        } catch (Exception e) {
            logger.error("Error al listar los tickets: {}", e.getMessage());
            model.addAttribute("errorMessage", "Error al listar los tickets.");
        }
        model.addAttribute("listTickets", listTickets);
        return "ticket";
    }

    /**
     * Muestra el formulario para crear un nuevo ticket.
     *
     * @param model Modelo para pasar datos a la vista.
     * @return El nombre de la plantilla Thymeleaf para el formulario de ticket.
     */
    @GetMapping("/new")
    public String showNewForm(Model model) {
        logger.info("Mostrando formulario para nuevo ticket.");
        List<Product> listProducts = productRepository.findAll();
        model.addAttribute("ticket", new Ticket());
        model.addAttribute("listProducts", listProducts);
        return "ticket-form.html";
    }

    @PostMapping("/insert")
    public String insertTicket(@Valid @ModelAttribute("ticket") Ticket ticket, BindingResult result,
                               RedirectAttributes redirectAttributes, Locale locale, Model model) {
        logger.info("Insertando nuevo ticket con fecha {}", ticket.getDate());
        try {
            if (result.hasErrors()) {
                List<Product> listProducts = productRepository.findAll();
                model.addAttribute("listProducts", listProducts);
                return "ticket-form.html";
            }
            ticketRepository.save(ticket);
            logger.info("Ticket insertado con éxito.");
        } catch (Exception e) {
            logger.error("Error al insertar el ticket: {}", e.getMessage());
            String errorMessage = messageSource.getMessage("msg.ticket-controller.insert.error", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
        }
        return "redirect:/tickets";
    }

    @GetMapping("/edit")
    public String showEditForm(@RequestParam("id") Long id, Model model) {
        logger.info("Mostrando formulario de edición para el ticket con ID {}", id);
        Optional<Ticket> ticket = ticketRepository.findById(id);
        if (ticket.isEmpty()) {
            logger.warn("No se encontró el ticket con ID {}", id);
            return "redirect:/tickets";
        }
        List<Product> listProducts = productRepository.findAll();
        model.addAttribute("ticket", ticket.get());
        model.addAttribute("listProducts", listProducts);
        return "ticket-form.html";
    }

    @PostMapping("/update")
    public String updateTicket(@Valid @ModelAttribute("ticket") Ticket ticket, BindingResult result,
                               RedirectAttributes redirectAttributes, Locale locale, Model model) {
        logger.info("Actualizando ticket con ID {}", ticket.getId());
        try {
            if (result.hasErrors()) {
                List<Product> listProducts = productRepository.findAll();
                model.addAttribute("listProducts", listProducts);
                return "ticket-form.html";
            }
            ticketRepository.save(ticket);
            logger.info("Ticket con ID {} actualizado con éxito.", ticket.getId());
        } catch (Exception e) {
            logger.error("Error al actualizar el ticket con ID {}: {}", ticket.getId(), e.getMessage());
            String errorMessage = messageSource.getMessage("msg.ticket-controller.update.error", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
        }
        return "redirect:/tickets";
    }

    @PostMapping("/delete")
    public String deleteTicket(@RequestParam("id") Long id, RedirectAttributes redirectAttributes) {
        logger.info("Eliminando ticket con ID {}", id);
        try {
            ticketRepository.deleteById(id);
            logger.info("Ticket con ID {} eliminado con éxito.", id);
        } catch (Exception e) {
            logger.error("Error al eliminar el ticket con ID {}: {}", id, e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Error al eliminar el ticket.");
        }
        return "redirect:/tickets";
    }

    @GetMapping("/detail")
    public String showTicketDetail(@RequestParam("id") Long id, Model model) {
        logger.info("Mostrando detalles para el ticket con ID {}", id);
        Optional<Ticket> ticketOptional = ticketRepository.findById(id);

        if (ticketOptional.isEmpty()) {
            logger.warn("No se encontró el ticket con ID {}", id);
            return "redirect:/tickets";
        }

        Ticket ticket = ticketOptional.get();
        model.addAttribute("ticket", ticket);
        model.addAttribute("products", ticket.getProducts());

        return "ticket-detail";
    }

    @PostMapping("/addExistingProduct")
    public String searchProduct(@RequestParam("productSearch") String productSearch, @RequestParam("ticketId") Long ticketId, Model model) {
        logger.info("Buscando productos que coincidan con '{}'", productSearch);
        List<Product> searchResults = productRepository.findByNameContainingIgnoreCase(productSearch);
        Optional<Ticket> ticketOpt = ticketRepository.findById(ticketId);

        if (ticketOpt.isPresent()) {
            model.addAttribute("ticket", ticketOpt.get());
            model.addAttribute("products", ticketOpt.get().getProducts());
        } else {
            model.addAttribute("errorMessage", "No se encontró el ticket.");
            return "redirect:/tickets";
        }

        model.addAttribute("searchResults", searchResults);
        return "ticket-detail";
    }

    @PostMapping("/addProduct")
    public String addProductToTicket(@RequestParam("ticketId") Long ticketId, @RequestParam("productId") Long productId, RedirectAttributes redirectAttributes, Locale locale) {
        logger.info("Añadiendo producto con ID {} al ticket con ID {}", productId, ticketId);
        try {
            Optional<Ticket> ticketOpt = ticketRepository.findById(ticketId);
            Optional<Product> productOpt = productRepository.findById(productId);

            if (ticketOpt.isPresent() && productOpt.isPresent()) {
                Ticket ticket = ticketOpt.get();
                Product product = productOpt.get();
                ticket.getProducts().add(product);
                ticketRepository.save(ticket);
                logger.info("Producto añadido con éxito.");
            } else {
                logger.warn("No se encontró el ticket o el producto.");
                redirectAttributes.addFlashAttribute("errorMessage", "No se pudo añadir el producto al ticket.");
            }
        } catch (DataIntegrityViolationException e) {
            logger.error("Violación de integridad de datos al insertar el ticket: {}", e.getMessage());
            String errorMessage = messageSource.getMessage("msg.ticket-controller.insert.integrity-violation", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
            return "redirect:/tickets/detail?id=" + ticketId;
        } catch (Exception e) {
            logger.error("Error al añadir el producto al ticket: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Error al añadir el producto.");
        }
        return "redirect:/tickets/detail?id=" + ticketId;
    }

    @PostMapping("/addNewProduct")
    public String addNewProductToTicket(@RequestParam("ticketId") Long ticketId, @RequestParam("productName") String productName,
                                        @RequestParam("productPrice") BigDecimal productPrice, RedirectAttributes redirectAttributes) {
        logger.info("Añadiendo nuevo producto '{}' con precio {} al ticket con ID {}", productName, productPrice, ticketId);
        try {
            Optional<Ticket> ticketOpt = ticketRepository.findById(ticketId);

            if (ticketOpt.isPresent()) {
                Ticket ticket = ticketOpt.get();

                boolean productExists = ticket.getProducts().stream()
                        .anyMatch(product -> product.getName().equalsIgnoreCase(productName));

                if (productExists) {
                    logger.warn("El producto con nombre '{}' ya existe en el ticket con ID {}", productName, ticketId);
                    redirectAttributes.addFlashAttribute("errorMessage", "El producto con el nombre especificado ya está asociado al ticket.");
                } else {
                    Product newProduct = new Product();
                    newProduct.setName(productName);
                    newProduct.setPrice(productPrice);
                    productRepository.save(newProduct);

                    ticket.getProducts().add(newProduct);
                    ticketRepository.save(ticket);

                    logger.info("Nuevo producto añadido con éxito.");
                }
            } else {
                logger.warn("No se encontró el ticket.");
                redirectAttributes.addFlashAttribute("errorMessage", "No se pudo añadir el producto al ticket.");
            }
        } catch (Exception e) {
            logger.error("Error al añadir el nuevo producto al ticket: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Error al añadir el nuevo producto.");
        }
        return "redirect:/tickets/detail?id=" + ticketId;
    }

    @PostMapping("/removeProduct")
    public String removeProductFromTicket(@RequestParam("ticketId") Long ticketId, @RequestParam("productId") Long productId, RedirectAttributes redirectAttributes) {
        logger.info("Eliminando producto con ID {} del ticket con ID {}", productId, ticketId);
        try {
            Optional<Ticket> ticketOpt = ticketRepository.findById(ticketId);
            Optional<Product> productOpt = productRepository.findById(productId);

            if (ticketOpt.isPresent() && productOpt.isPresent()) {
                Ticket ticket = ticketOpt.get();
                Product product = productOpt.get();
                ticket.getProducts().remove(product);
                ticketRepository.save(ticket);
                logger.info("Producto eliminado con éxito.");
            } else {
                logger.warn("No se encontró el ticket o el producto.");
                redirectAttributes.addFlashAttribute("errorMessage", "No se pudo eliminar el producto del ticket.");
            }
        } catch (Exception e) {
            logger.error("Error al eliminar el producto del ticket: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Error al eliminar el producto.");
        }
        return "redirect:/tickets/detail?id=" + ticketId;
    }

}
