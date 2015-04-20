package at.pria.joust.webapp.web.user;

import at.pria.joust.webapp.auth.UserService;
import at.pria.joust.webapp.validators.FieldMatch;
import at.pria.joust.webapp.validators.CurrentPassword;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;

@Controller
public class ChangePasswordController {
    @Autowired
    private UserService users;

    @RequestMapping(value = "/user/changePassword/", method = RequestMethod.GET)
    public String admin(Model model) {
        model.addAttribute("credentials", new Credentials());
        return "user/changePassword";
    }

    @RequestMapping(value = "/user/changePassword/", method = RequestMethod.POST)
    public String createUser(@ModelAttribute @Valid Credentials credentials, BindingResult bindingResult, Model model) {
        if(bindingResult.hasErrors())
            return "user/changePassword";

        users.changePassword(credentials.oldPassword, credentials.newPassword);
        return "user/changePassword.result";
    }

    @FieldMatch(first = "newPassword", second = "confirmPassword", message = "{accounts.confirmPassword.error}")
    public static class Credentials {
        @CurrentPassword(message = "{accounts.oldPassword.error}")
        private String oldPassword;
        private String newPassword, confirmPassword;

        public String getOldPassword() {
            return oldPassword;
        }

        public void setOldPassword(String oldPassword) {
            this.oldPassword = oldPassword;
        }

        public String getNewPassword() {
            return newPassword;
        }

        public void setNewPassword(String newPassword) {
            this.newPassword = newPassword;
        }

        public String getConfirmPassword() {
            return confirmPassword;
        }

        public void setConfirmPassword(String confirmPassword) {
            this.confirmPassword = confirmPassword;
        }
    }
}
