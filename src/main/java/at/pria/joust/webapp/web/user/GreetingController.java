package at.pria.joust.webapp.web.user;

import at.pria.joust.webapp.model.Greeting;
import at.pria.joust.webapp.model.GreetingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;

@Controller
public class GreetingController {
    @Autowired
    private GreetingRepository greetings;

    @RequestMapping(value = "/user/greeting/", method = RequestMethod.GET)
    public String greetingForm(Model model) {
        model.addAttribute("greeting", new Greeting());
        return "user/greeting";
    }

    @RequestMapping(value = "/user/greeting/", method = RequestMethod.POST)
    public String greetingSubmit(@ModelAttribute @Valid Greeting greeting, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors())
            return "user/greeting";

        greeting = greetings.save(greeting);
        model.addAttribute("greeting", greeting);
        return "user/greeting.result";
    }
}
