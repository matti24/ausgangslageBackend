package com.ausganslage.ausgangslageBackend.controller;

import com.ausganslage.ausgangslageBackend.model.Person;
import com.ausganslage.ausgangslageBackend.repository.PersonRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final PersonRepository personRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthController(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    public static class RegisterRequest {
        public String name;
        public String email;
        public String password;
    }

    public static class LoginRequest {
        public String email;
        public String password;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        if (req.email == null || req.password == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email and password required");
        }
        Optional<Person> exists = personRepository.findByEmail(req.email);
        if (exists.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already in use");
        }
        Person p = new Person();
        p.setName(req.name == null ? "" : req.name);
        p.setEmail(req.email);
        p.setPasswordHash(passwordEncoder.encode(req.password));
        personRepository.save(p);
        return ResponseEntity.ok(p);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        if (req.email == null || req.password == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email and password required");
        }
        Optional<Person> maybe = personRepository.findByEmail(req.email);
        if (maybe.isEmpty()) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        Person p = maybe.get();
        if (!passwordEncoder.matches(req.password, p.getPasswordHash())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
        // Return basic user info (no password)
        Person out = new Person();
        out.setId(p.getId());
        out.setName(p.getName());
        out.setEmail(p.getEmail());
        return ResponseEntity.ok(out);
    }
}
