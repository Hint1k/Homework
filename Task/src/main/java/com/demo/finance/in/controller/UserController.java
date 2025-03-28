package com.demo.finance.in.controller;

import com.demo.finance.domain.dto.UserDto;
import com.demo.finance.domain.mapper.UserMapper;
import com.demo.finance.domain.model.User;
import com.demo.finance.domain.utils.Mode;
import com.demo.finance.domain.utils.ValidationUtils;
import com.demo.finance.exception.DuplicateEmailException;
import com.demo.finance.exception.ValidationException;
import com.demo.finance.out.service.RegistrationService;
import com.demo.finance.out.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@SessionAttributes("currentUser")
public class UserController extends BaseController {

    private final RegistrationService registrationService;
    private final UserService userService;
    private final ValidationUtils validationUtils;
    private final UserMapper userMapper;

    @Autowired
    public UserController(RegistrationService registrationService, UserService userService,
                          ValidationUtils validationUtils, UserMapper userMapper) {
        this.registrationService = registrationService;
        this.userService = userService;
        this.validationUtils = validationUtils;
        this.userMapper = userMapper;
    }

    @PostMapping("/registration")
    public ResponseEntity<Map<String, Object>> handleRegistration(@RequestBody UserDto userDtoNew) {
        try {
            UserDto userDto = validationUtils.validateRequest(userDtoNew, Mode.REGISTER_USER);
            boolean success = registrationService.registerUser(userDto);
            if (success) {
                User user = userService.getUserByEmail(userDto.getEmail());
                if (user != null) {
                    UserDto registeredUserDto = UserDto.removePassword(userMapper.toDto(user));
                    return buildSuccessResponse(
                            HttpStatus.CREATED, "User registered successfully", registeredUserDto);
                }
                return buildErrorResponse(
                        HttpStatus.INTERNAL_SERVER_ERROR, "Failed to retrieve user details.");
            }
            return buildErrorResponse(HttpStatus.BAD_REQUEST, "Failed to register user.");
        } catch (DuplicateEmailException | ValidationException e) {
            return buildErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping("/authenticate")
    public ResponseEntity<Map<String, Object>> handleAuthentication(
            @RequestBody UserDto userDtoNew, HttpServletRequest request, HttpServletResponse response) {
        try {
            UserDto userDto = validationUtils.validateRequest(userDtoNew, Mode.AUTHENTICATE);
            boolean success = registrationService.authenticate(userDto);
            if (success) {
                User user = userService.getUserByEmail(userDto.getEmail());
                if (user != null) {
                    HttpSession session = request.getSession();
                    session.setMaxInactiveInterval(1800);
                    UserDto authUserDto = UserDto.removePassword(userMapper.toDto(user));
                    session.setAttribute("currentUser", authUserDto);
                    String cookie = String.format("JSESSIONID=%s; Path=/; HttpOnly; SameSite=Strict%s",
                            session.getId(), request.isSecure() ? "; Secure" : "");
                    response.setHeader("Set-Cookie", cookie);
                    return buildSuccessResponse(HttpStatus.OK, "Authentication successful", authUserDto);
                }
                return buildErrorResponse(
                        HttpStatus.INTERNAL_SERVER_ERROR, "Failed to retrieve user details.");
            }
            return buildErrorResponse(HttpStatus.UNAUTHORIZED, "Invalid credentials.");
        } catch (ValidationException e) {
            return buildErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logoutUser(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        Cookie cookie = new Cookie("JSESSIONID", "");
        cookie.setPath("/");
        cookie.setMaxAge(0);
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
        return buildSuccessResponse(HttpStatus.OK, "Logged out successfully", null);
    }

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getCurrentUser(@SessionAttribute("currentUser") UserDto userDto) {
        return buildSuccessResponse(HttpStatus.OK, "Authenticated user details", userDto);
    }

    @PutMapping
    public ResponseEntity<Map<String, Object>> updateUser(
            @RequestBody UserDto userDtoNew, @SessionAttribute("currentUser") UserDto currentUserDto, Model model) {
        try {
            UserDto userDto = validationUtils.validateRequest(userDtoNew, Mode.UPDATE_USER);
            boolean success = userService.updateOwnAccount(userDto, currentUserDto.getUserId());
            if (success) {
                User updatedUser = userService.getUserByEmail(userDto.getEmail());
                if (updatedUser != null) {
                    UserDto updatedUserDto = UserDto.removePassword(userMapper.toDto(updatedUser));
                    model.addAttribute("currentUser", updatedUserDto);
                    return buildSuccessResponse(HttpStatus.OK, "User updated successfully", updatedUserDto);
                }
                return buildErrorResponse(
                        HttpStatus.INTERNAL_SERVER_ERROR, "Failed to retrieve updated user details.");
            }
            return buildErrorResponse(HttpStatus.BAD_REQUEST, "Failed to update account.");
        } catch (ValidationException e) {
            return buildErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @DeleteMapping
    public ResponseEntity<Map<String, Object>> deleteUser(
            @SessionAttribute("currentUser") UserDto userDto, SessionStatus sessionStatus) {
        try {
            boolean success = userService.deleteOwnAccount(userDto.getUserId());
            if (success) {
                sessionStatus.setComplete();
                return buildSuccessResponse(HttpStatus.OK, "Account deleted successfully",
                        Map.of("email", userDto.getEmail(), "timestamp", Instant.now()));
            }
            return buildErrorResponse(HttpStatus.BAD_REQUEST, "Failed to delete account.");
        } catch (Exception e) {
            return buildErrorResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred while deleting the account.");
        }
    }
}