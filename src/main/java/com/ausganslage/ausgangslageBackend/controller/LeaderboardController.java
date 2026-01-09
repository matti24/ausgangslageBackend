package com.ausganslage.ausgangslageBackend.controller;

import com.ausganslage.ausgangslageBackend.model.Person;
import com.ausganslage.ausgangslageBackend.repository.PersonRepository;
import com.ausganslage.ausgangslageBackend.repository.ResultRepository;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/leaderboard")
public class LeaderboardController {

    private final PersonRepository personRepository;
    private final ResultRepository resultRepository;

    public LeaderboardController(PersonRepository personRepository, ResultRepository resultRepository) {
        this.personRepository = personRepository;
        this.resultRepository = resultRepository;
    }

    public static class LeaderboardEntry {
        public Long id;
        public String name;
        public Integer points;
        public Integer rank;

        public LeaderboardEntry(Long id, String name, Integer points, Integer rank) {
            this.id = id;
            this.name = name;
            this.points = points;
            this.rank = rank;
        }
    }

    @GetMapping
    public List<LeaderboardEntry> getLeaderboard() {
        List<Person> allPersons = personRepository.findAll();
        Map<Long, Integer> pointsByPerson = new HashMap<>();

        // Calculate points for each person: (6 - grade) * 10
        for (Person person : allPersons) {
            int totalPoints = 0;
            List<com.ausganslage.ausgangslageBackend.model.Result> results = resultRepository.findByPersonId(person.getId());
            for (com.ausganslage.ausgangslageBackend.model.Result result : results) {
                int points = Math.max(0, (6 - result.getResult()) * 10);
                totalPoints += points;
            }
            pointsByPerson.put(person.getId(), totalPoints);
        }

        // Create leaderboard entries
        List<LeaderboardEntry> entries = new ArrayList<>();
        for (Person person : allPersons) {
            entries.add(new LeaderboardEntry(
                    person.getId(),
                    person.getName(),
                    pointsByPerson.getOrDefault(person.getId(), 0),
                    0 // rank will be set after sorting
            ));
        }

        // Sort by points descending
        entries.sort((a, b) -> b.points - a.points);

        // Set ranks
        for (int i = 0; i < entries.size(); i++) {
            entries.get(i).rank = i + 1;
        }

        return entries;
    }
}
