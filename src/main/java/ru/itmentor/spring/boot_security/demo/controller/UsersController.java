package ru.itmentor.spring.boot_security.demo.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import ru.itmentor.spring.boot_security.demo.model.Role;
import ru.itmentor.spring.boot_security.demo.model.User;
import ru.itmentor.spring.boot_security.demo.service.RoleService;
import ru.itmentor.spring.boot_security.demo.service.UserService;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
public class UsersController {
    private final UserService userService;
    private final RoleService roleService;

    @Autowired
    public UsersController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping("/")
    public String welcomePage(){
        return "welcome";
    }

    @GetMapping("/admin/createUser")
    public String addNewUser(ModelMap model) {
        model.addAttribute("user", new User());
        return "create-new-user";
    }

    @PostMapping("/admin/createUser")
    public String createUser(@ModelAttribute("user") User user,
                             @ModelAttribute("roles") String roles) {
        Set<Role> userRoles = new HashSet<>();
        String[] arrRoles = roles.split(",");
        for (String roleId : arrRoles) {
            roleService.findById(Long.parseLong(roleId)).ifPresent(userRoles::add);
        }
        user.setRoles(userRoles);
        userService.save(user);
        return "redirect:/admin/getAllUsers";
    }

    @GetMapping("/admin/updateUser/{id}")
    public String editUser(ModelMap model, @PathVariable("id") int id) {
        model.addAttribute("user", userService.getUserByID(id));
        model.addAttribute("initialRoles", userService.getRolesById(id));
        return "update-delete-user";
    }

    @PatchMapping("/admin/updateUser/{id}")
    public String updateUser(@ModelAttribute("user") User user,
                             @ModelAttribute("roles") String roles,
                             @PathVariable("id") int id) {
        Set<Role> userRoles = new HashSet<>();
        String[] arrRoles = roles.split(",");
        for (String roleId : arrRoles) {
            roleService.findById(Long.parseLong(roleId)).ifPresent(userRoles::add);
        }
        user.setRoles(userRoles);
        userService.update(id, user);
        return "redirect:/admin/getAllUsers";
    }

    @DeleteMapping("/admin/deleteUser/{id}")
    public String deleteUser(@PathVariable("id") int id) {
        userService.delete(id);
        return "redirect:/admin/getAllUsers";
    }

    @GetMapping("/admin/getAllUsers")
    public String showAllUsers(ModelMap model) {
        List<User> allUsers = userService.listUsers();
        model.addAttribute("allUsers", allUsers);
        return "all-users";
    }

    @GetMapping("/user/getProfile/{id}")
    public String showUserProfile(@PathVariable("id") int id, ModelMap model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = (User) userDetails;
        Long userIdFromPrincipal = user.getId();
        if (userIdFromPrincipal.equals((long) id)) {
            User userFromService = userService.getUserByID(id);
            model.addAttribute("user", userFromService);
            return "user-profile";
        }
        return "redirect:/user/getProfile/" + userIdFromPrincipal;
    }
}
