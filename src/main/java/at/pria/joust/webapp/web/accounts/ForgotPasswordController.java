package at.pria.joust.webapp.web.accounts;

import at.pria.joust.webapp.auth.UserService;
import at.pria.joust.webapp.validators.Username;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;

@Controller
public class ForgotPasswordController {
    @RequestMapping(value = "/forgotPassword/", method = RequestMethod.GET)
    public String admin(Model model) {
        model.addAttribute("user", new User());
        return "accounts/forgotPassword";
    }

    @Autowired
    private UserService users;

    @RequestMapping(value = "/forgotPassword/", method = RequestMethod.POST)
    public String createUser(@ModelAttribute @Valid User user, BindingResult bindingResult, Model model) {
        if(bindingResult.hasErrors())
            return "accounts/forgotPassword";

        //TODO reset password
        return "accounts/forgotPassword.result";
    }

    public static class User {
        @Username(exists = true, message = "{accounts.username.existsNotError}")
        private String username;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }
    }
}
