package at.pria.joust.webapp.web.accounts;

import at.pria.joust.webapp.auth.UserService;
import at.pria.joust.webapp.validators.FieldMatch;
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
public class RegisterController {
    @Autowired
    private UserService users;

    @RequestMapping(value = "/register/", method = RequestMethod.GET)
    public String admin(Model model) {
        model.addAttribute("user", new User());
        return "accounts/register";
    }

    @RequestMapping(value = "/register/", method = RequestMethod.POST)
    public String createUser(@ModelAttribute @Valid User user, BindingResult bindingResult, Model model) {
        if(bindingResult.hasErrors())
            return "accounts/register";

        users.registerUser(user.getUsername(), user.getPassword(), users.roles("USER"));
        return "accounts/register.result";
    }

    @FieldMatch(first = "password", second = "confirmPassword", message = "{accounts.confirmPassword.error}")
    public static class User {
        @Username(exists = false, message = "{accounts.username.existsError}")
        private String username;
        private String password, confirmPassword;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getConfirmPassword() {
            return confirmPassword;
        }

        public void setConfirmPassword(String confirmPassword) {
            this.confirmPassword = confirmPassword;
        }
    }
}
