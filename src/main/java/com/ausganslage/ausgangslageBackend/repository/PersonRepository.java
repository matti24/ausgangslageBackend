package com.ausganslage.ausgangslageBackend.repository;

import com.ausganslage.ausgangslageBackend.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PersonRepository extends JpaRepository<Person, Long> {
	Optional<Person> findByEmail(String email);
}