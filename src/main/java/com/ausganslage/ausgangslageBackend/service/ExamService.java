package com.ausganslage.ausgangslageBackend.service;

import com.ausganslage.ausgangslageBackend.model.Person;
import com.ausganslage.ausgangslageBackend.model.Exam;
import com.ausganslage.ausgangslageBackend.repository.PersonRepository;
import com.ausganslage.ausgangslageBackend.repository.ExamRepository;
import com.ausganslage.ausgangslageBackend.exception.ResourceNotFoundException;
import com.ausganslage.ausgangslageBackend.exception.InvalidOperationException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Service-Klasse demonstriert:
 * - Geschäftslogik-Behandlung mit try-catch-finally
 * - Throws-Deklarationen für Exception-Propagation
 * - Ressourcen-Management im finally-Block
 */
@Service
public class ExamService {
    
    private final ExamRepository examRepository;
    private final PersonRepository personRepository;

    public ExamService(ExamRepository examRepository, PersonRepository personRepository) {
        this.examRepository = examRepository;
        this.personRepository = personRepository;
    }

    /**
     * Validiert ein Exam-Datum und gibt einen beschreibenden Status zurück.
     * Demonstriert try-catch mit DateTimeParseException.
     * 
     * @param dateString Datum im Format YYYY-MM-DD
     * @return Validierungsstatus
     * @throws InvalidOperationException bei ungültigem Datumsformat
     */
    public String validateExamDate(String dateString) throws InvalidOperationException {
        LocalDate examDate;
        boolean isValidDate = false;
        
        try {
            // Versuche Datum zu parsen
            examDate = LocalDate.parse(dateString);
            LocalDate today = LocalDate.now();
            
            // Bestimme Datum-Status
            if (today.isBefore(examDate)) {
                return "Exam liegt in der Zukunft (" + examDate + ")";
            } else if (today.isEqual(examDate)) {
                return "Exam findet heute statt (" + examDate + ")";
            } else {
                return "Exam liegt in der Vergangenheit (" + examDate + ")";
            }
            
        } catch (DateTimeParseException e) {
            // Spezifische Exception-Behandlung mit Cause-Chaining
            throw new InvalidOperationException(
                "Datumsformat ungültig. Erwartet: YYYY-MM-DD, erhalten: " + dateString, 
                e
            );
        } finally {
            // Cleanup: hier könnte z.B. temporäre Ressourcen freigegeben werden
            // oder Logging stattfinden
            System.out.println("[Service] Exam-Datum-Validierung abgeschlossen für: " + dateString);
        }
    }

    /**
     * Ruft alle Exams für eine bestimmte Person ab.
     * Demonstriert Ressourcen-Management im finally-Block.
     * 
     * @param personId Person-ID
     * @return Liste von Exams für diese Person
     * @throws ResourceNotFoundException wenn Person nicht existiert
     * @throws InvalidOperationException wenn Fehler beim Laden auftritt
     */
    public List<Exam> getExamsForPerson(Long personId) throws ResourceNotFoundException, InvalidOperationException {
        Person person = null;
        List<Exam> exams;
        
        try {
            // Validiere dass Person existiert
            person = personRepository.findById(personId)
                    .orElseThrow(() -> new ResourceNotFoundException("Person", personId));
            
            // Hole alle Exams
            exams = examRepository.findAll();
            
            System.out.println("[Service] Geladen " + exams.size() + " Exams für Person: " + person.getName());
            
            return exams;
            
        } catch (ResourceNotFoundException e) {
            // Bekannte Exception: weitergeben für GlobalExceptionHandler
            throw e;
        } catch (Exception e) {
            // Unerwartete Exception wrappen und weitergeben
            throw new InvalidOperationException(
                "Fehler beim Laden von Exams für Person " + personId, 
                e
            );
        } finally {
            // Cleanup: Ressourcen-Freigabe oder Logging
            if (person != null) {
                System.out.println("[Service] Cleanup: Person-Kontext für " + person.getEmail() + " beendet");
            }
        }
    }

    /**
     * Erstelle ein neues Exam mit Validierung.
     * Demonstriert mehrere try-catch Blöcke für unterschiedliche Fehlerquellen.
     * 
     * @param title Exam-Titel
     * @param dateString Datum im Format YYYY-MM-DD
     * @return Gespeichertes Exam
     * @throws InvalidOperationException bei ungültigem Eingang
     */
    public Exam createExamWithValidation(String title, String dateString) throws InvalidOperationException {
        Exam exam = null;
        
        try {
            // Validiere Input
            if (title == null || title.trim().isEmpty()) {
                throw new InvalidOperationException("Exam-Titel darf nicht leer sein");
            }
            
            if (dateString == null || dateString.trim().isEmpty()) {
                throw new InvalidOperationException("Exam-Datum darf nicht leer sein");
            }
            
            // Validiere Datum-Format
            LocalDate examDate;
            try {
                examDate = LocalDate.parse(dateString);
            } catch (DateTimeParseException e) {
                throw new InvalidOperationException(
                    "Datumsformat ungültig: " + dateString + " (erwartet: YYYY-MM-DD)", 
                    e
                );
            }
            
            // Erstelle und speichere Exam
            exam = new Exam();
            exam.setTitle(title);
            exam.setDate(dateString);
            
            Exam saved = examRepository.save(exam);
            System.out.println("[Service] Exam erstellt: " + saved.getTitle() + " (" + saved.getId() + ")");
            
            return saved;
            
        } finally {
            // Cleanup: z.B. Transaktions-Logging oder temporäre Daten löschen
            System.out.println("[Service] Exam-Erstellung-Prozess beendet");
        }
    }
}
