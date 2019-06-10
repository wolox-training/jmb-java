package wolox.training.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * The Book's controller.
 */
@Controller
public class BookController {

    /**
     * Adds a {@code name} attribute to the given {@code model}, according to the {@code name} query
     * param, defaulting to "World".
     *
     * @param name The name of the person being greeted.
     * @param model The {@link Model} where the attribute will be added.
     * @return {@code "greeting"}.
     */
    @GetMapping("/greeting")
    public String greeting(
        @RequestParam(name = "name", required = false, defaultValue = "World") final String name,
        final Model model) {
        model.addAttribute("name", name);
        return "greeting";
    }
}
