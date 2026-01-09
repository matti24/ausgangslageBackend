package com.ausganslage.ausgangslageBackend.config;

import com.ausganslage.ausgangslageBackend.model.Person;
import com.ausganslage.ausgangslageBackend.model.Exam;
import com.ausganslage.ausgangslageBackend.model.Result;
import com.ausganslage.ausgangslageBackend.repository.PersonRepository;
import com.ausganslage.ausgangslageBackend.repository.ExamRepository;
import com.ausganslage.ausgangslageBackend.repository.ResultRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


@Component
public class DataLoader implements CommandLineRunner {

    private final PersonRepository personRepository;
    private final ExamRepository examRepository;
    private final ResultRepository resultRepository;

    public DataLoader(PersonRepository personRepository, ExamRepository examRepository, ResultRepository resultRepository) {
        this.personRepository = personRepository;
        this.examRepository = examRepository;
        this.resultRepository = resultRepository;
    }

    @Override
    public void run(String... args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        // Create sample users with hashed passwords (Matti Koenis, Max Müller, Hans Gross)
        Person matti = new Person();
        matti.setName("Matti Koenis");
        matti.setEmail("matti.koenis@example.com");
        matti.setPasswordHash(encoder.encode("password123"));
        personRepository.save(matti);

        Person max = new Person();
        max.setName("Max Müller");
        max.setEmail("max.mueller@example.com");
        max.setPasswordHash(encoder.encode("password123"));
        personRepository.save(max);

        Person hans = new Person();
        hans.setName("Hans Gross");
        hans.setEmail("hans.gross@example.com");
        hans.setPasswordHash(encoder.encode("password123"));
        personRepository.save(hans);

        // Create sample exams
        Exam exam1 = new Exam();
        exam1.setTitle("Mathe Prüfung Gleichungen");
        exam1.setDate("2025-11-15");
        examRepository.save(exam1);

        Exam exam2 = new Exam();
        exam2.setTitle("Mathe Prüfung Variablen");
        exam2.setDate("2025-09-05");
        examRepository.save(exam2);

        // Create sample results for each exam and person
        // Exam 1
        Result r1 = new Result();
        r1.setExam(exam1);
        r1.setPerson(matti);
        r1.setResult(3); // grade 3
        resultRepository.save(r1);

        Result r2 = new Result();
        r2.setExam(exam1);
        r2.setPerson(max);
        r2.setResult(2);
        resultRepository.save(r2);

        Result r3 = new Result();
        r3.setExam(exam1);
        r3.setPerson(hans);
        r3.setResult(5);
        resultRepository.save(r3);

        // Exam 2
        Result r4 = new Result();
        r4.setExam(exam2);
        r4.setPerson(matti);
        r4.setResult(4);
        resultRepository.save(r4);

        Result r5 = new Result();
        r5.setExam(exam2);
        r5.setPerson(max);
        r5.setResult(5);
        resultRepository.save(r5);

        Result r6 = new Result();
        r6.setExam(exam2);
        r6.setPerson(hans);
        r6.setResult(3);
        resultRepository.save(r6);

        System.out.println("Sample users and exams loaded");
    }
}



