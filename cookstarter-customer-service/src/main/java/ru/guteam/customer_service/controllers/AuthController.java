package ru.guteam.customer_service.controllers;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ru.guteam.customer_service.controllers.utils.JwtCheckRequest;
import ru.guteam.customer_service.controllers.utils.UsernameAndPasswordRequest;
import ru.guteam.customer_service.controllers.utils.JwtTokenUtil;
import ru.guteam.customer_service.services.UsersService;

@Slf4j
@CrossOrigin("*")
@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {
    private final UsersService usersService;
    private final JwtTokenUtil jwtTokenUtil;
    private final AuthenticationManager authenticationManager;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
            public ResponseEntity<?> createCustomerAuthToken(@RequestBody UsernameAndPasswordRequest authRequest) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
        } catch (BadCredentialsException e) {
            log.info("Пользователь с логином: " + authRequest.getUsername() +
                    " и паролем: " + authRequest.getPassword() + " не обнаружен");
            return new ResponseEntity<>("Неверные логин или пароль", HttpStatus.UNAUTHORIZED);
        }
        UserDetails userDetails = usersService.loadUserByUsername(authRequest.getUsername());
        String token = jwtTokenUtil.generateToken(userDetails);
        log.info("Для пользователя с логином: " + authRequest.getUsername() +
                " и паролем: " + authRequest.getPassword() + " сгенерирован токен: " + token);
        return new ResponseEntity<>(token, HttpStatus.OK);
    }

}
