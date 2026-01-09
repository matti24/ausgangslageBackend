package com.ausganslage.ausgangslageBackend.service;

import com.ausganslage.ausgangslageBackend.model.Person;
import com.ausganslage.ausgangslageBackend.repository.PersonRepository;
import com.ausganslage.ausgangslageBackend.exception.AuthenticationException;
import com.ausganslage.ausgangslageBackend.exception.DuplicateDataException;
import com.ausganslage.ausgangslageBackend.exception.InvalidOperationException;
import com.ausganslage.ausgangslageBackend.exception.ResourceNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service-Klasse für Authentifizierung.
 * Demonstriert:
 * - Mehrschichtige Exception-Behandlung
 * - Throws mit verschiedenen Exception-Typen
 * - Try-Catch mit Exception-Chaining
 */
@Service
public class AuthService {
    
    private final PersonRepository personRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthService(PersonRepository personRepository) {
        this.personRepository = personRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    /**
     * Validiere Login-Daten und gebe Person zurück.
     * 
     * @param email Benutzer-Email
     * @param password Benutzer-Passwort (unkodiert)
     * @return Authentifizierte Person
     * @throws InvalidOperationException wenn Email/Passwort fehlt
     * @throws AuthenticationException wenn Kredentiale ungültig
     */
    public Person authenticateUser(String email, String password) 
            throws InvalidOperationException, AuthenticationException {
        
        try {
            // Input-Validierung
            validateAuthInput(email, password);
            
            // Suche Benutzer
            Optional<Person> maybePerson = personRepository.findByEmail(email);
            
            if (maybePerson.isEmpty()) {
                throw new AuthenticationException("Ungültige Email oder Passwort");
            }
            
            Person person = maybePerson.get();
            
            // Validiere Passwort
            if (!passwordEncoder.matches(password, person.getPasswordHash())) {
                // Log fehlgeschlagenen Versuch (in realer Anwendung)
                System.out.println("[Auth] Fehlgeschlagener Login-Versuch für: " + email);
                throw new AuthenticationException("Ungültige Email oder Passwort");
            }
            
            System.out.println("[Auth] Erfolgreicher Login für: " + person.getName());
            return person;
            
        } catch (InvalidOperationException | AuthenticationException e) {
            // Bekannte Exceptions weitergeben
            throw e;
        } catch (Exception e) {
            // Unerwartete Exceptions wrappen
            throw new AuthenticationException(
                "Fehler bei der Authentifizierung", 
                e
            );
        } finally {
            // Cleanup: Sensitive Daten aus Speicher (simuliert)
            System.out.println("[Auth] Authentifizierungsprozess beendet");
        }
    }

    /**
     * Registriere neuen Benutzer.
     * 
     * @param name Benutzername
     * @param email Email-Adresse (muss eindeutig sein)
     * @param password Passwort
     * @return Registrierte Person (ohne Passwort)
     * @throws InvalidOperationException wenn Eingang ungültig
     * @throws DuplicateDataException wenn Email bereits existiert
     */
    public Person registerUser(String name, String email, String password) 
            throws InvalidOperationException, DuplicateDataException {
        
        try {
            // Input-Validierung
            validateAuthInput(email, password);
            
            if (name == null || name.trim().isEmpty()) {
                throw new InvalidOperationException("Name darf nicht leer sein");
            }
            
            // Prüfe Eindeutigkeit der Email
            Optional<Person> existing = personRepository.findByEmail(email);
            if (existing.isPresent()) {
                throw new DuplicateDataException("Email", email);
            }
            
            // Erstelle neuen Benutzer
            Person newPerson = new Person();
            newPerson.setName(name);
            newPerson.setEmail(email);
            newPerson.setPasswordHash(passwordEncoder.encode(password));
            
            Person saved = personRepository.save(newPerson);
            System.out.println("[Auth] Neuer Benutzer registriert: " + saved.getName() + " (" + saved.getId() + ")");
            
            return saved;
            
        } catch (InvalidOperationException | DuplicateDataException e) {
            // Bekannte Exceptions weitergeben
            throw e;
        } catch (Exception e) {
            // Unerwartete Exceptions wrappen
            throw new InvalidOperationException(
                "Fehler bei der Registrierung", 
                e
            );
        } finally {
            // Cleanup: Temporary data clearing
            System.out.println("[Auth] Registrierungsprozess beendet");
        }
    }

    /**
     * Validiere grundlegende Auth-Eingaben.
     * 
     * @param email Email-Adresse
     * @param password Passwort
     * @throws InvalidOperationException wenn Eingaben ungültig
     */
    private void validateAuthInput(String email, String password) throws InvalidOperationException {
        if (email == null || email.trim().isEmpty()) {
            throw new InvalidOperationException("Email ist erforderlich");
        }
        
        if (password == null || password.trim().isEmpty()) {
            throw new InvalidOperationException("Passwort ist erforderlich");
        }
        
        if (!email.contains("@")) {
            throw new InvalidOperationException("Ungültiges Email-Format");
        }
    }

    /**
     * Ändere das Passwort eines Benutzers.
     * 
     * @param personId Person-ID
     * @param oldPassword Altes Passwort
     * @param newPassword Neues Passwort
     * @return Aktualisierte Person
     * @throws ResourceNotFoundException wenn Person nicht existiert
     * @throws AuthenticationException wenn altes Passwort falsch
     * @throws InvalidOperationException wenn neues Passwort ungültig
     */
    public Person changePassword(Long personId, String oldPassword, String newPassword) 
            throws ResourceNotFoundException, AuthenticationException, InvalidOperationException {
        
        Person person = null;
        
        try {
            // Hole Person
            person = personRepository.findById(personId)
                    .orElseThrow(() -> new ResourceNotFoundException("Person", personId));
            
            // Validiere altes Passwort
            if (!passwordEncoder.matches(oldPassword, person.getPasswordHash())) {
                throw new AuthenticationException("Altes Passwort ist falsch");
            }
            
            // Validiere neues Passwort
            if (newPassword == null || newPassword.trim().isEmpty()) {
                throw new InvalidOperationException("Neues Passwort darf nicht leer sein");
            }
            
            // Aktualisiere Passwort
            person.setPasswordHash(passwordEncoder.encode(newPassword));
            Person updated = personRepository.save(person);
            
            System.out.println("[Auth] Passwort geändert für: " + person.getEmail());
            return updated;
            
        } catch (ResourceNotFoundException | AuthenticationException | InvalidOperationException e) {
            throw e;
        } catch (Exception e) {
            throw new InvalidOperationException("Fehler beim Passwort-Wechsel", e);
        } finally {
            if (person != null) {
                System.out.println("[Auth] Passwort-Wechsel für " + person.getEmail() + " beendet");
            }
        }
    }
}
