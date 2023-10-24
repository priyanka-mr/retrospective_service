package com.project.retrospective_service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.validation.FieldError;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/retrospectives")
public class RetrospectiveController {
    private final List<Retrospective> retrospectives = new ArrayList<>();
    private static final Logger logger = LogManager.getLogger(RetrospectiveController.class);


    // Create a new retrospective
    @PostMapping
    public ResponseEntity<String> createRetrospective(@RequestBody Retrospective retrospective) {
        logger.info("Creating a new retrospective: " + retrospective.getName());
        String inputDate = retrospective.getDate();
        List<String> inputParticipants = retrospective.getParticipants();
        List<Feedback> inputFeedback = retrospective.getFeedback();
        if (inputDate == null || inputParticipants.isEmpty()) {
            return new ResponseEntity<>("Date and Participants cannot be empty", HttpStatus.BAD_REQUEST);
        }
        if (!inputFeedback.isEmpty()) {
            return new ResponseEntity<>("Feedback should be empty", HttpStatus.BAD_REQUEST);
        }
        retrospectives.add(retrospective);
        return new ResponseEntity<>("Retrospective created successfully", HttpStatus.CREATED);
    }

    // Add feedback item to a retrospective
    @PostMapping("/{retrospectiveName}/feedback")
    public ResponseEntity<String> addFeedback(@PathVariable String retrospectiveName, @RequestBody Feedback feedback) {
        Optional<Retrospective> foundRetrospective = retrospectives.stream()
                .filter(r -> r.getName().equals(retrospectiveName))
                .findFirst();

        if (foundRetrospective.isPresent()) {
            foundRetrospective.get().getFeedback().add(feedback);
            return new ResponseEntity<>("Feedback added successfully", HttpStatus.CREATED);
        }
        return new ResponseEntity<>("Retrospective not found", HttpStatus.NOT_FOUND);
    }

    // Update feedback item
    @PutMapping("/{retrospectiveName}/feedback/{feedbackName}")
    public ResponseEntity<String> updateFeedback(@PathVariable String retrospectiveName, @PathVariable String feedbackName, @RequestBody Feedback feedback) {
        Retrospective retrospectiveToUpdate = retrospectives.stream()
                .filter(r -> r.getName().equals(retrospectiveName))
                .findFirst()
                .orElse(null);

        if (retrospectiveToUpdate == null) {
            return new ResponseEntity<>("Retrospective not found", HttpStatus.NOT_FOUND);
        }

        Feedback feedbackToUpdate = retrospectiveToUpdate.getFeedback().stream()
                .filter(f -> f.getName().equals(feedbackName))
                .findFirst()
                .orElse(null);

        if (feedbackToUpdate == null) {
            return new ResponseEntity<>("Feedback not found", HttpStatus.NOT_FOUND);
        }

        feedbackToUpdate.setBody(feedback.getBody());
        feedbackToUpdate.setFeedbackType(feedback.getFeedbackType());

        return new ResponseEntity<>("Feedback updated successfully", HttpStatus.OK);

    }

    // Get all retrospectives
    @GetMapping
    public List<Retrospective> getRetrospectives() {
        return retrospectives;
    }

    //Get all retrospectives with Pagination
    @GetMapping("/search")
    public ResponseEntity<List<Retrospective>> getRetrospectivesWithPagination(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "pageSize", defaultValue = "2") int pageSize) {
        return getListResponseEntity(page, pageSize, retrospectives);
    }

    private ResponseEntity<List<Retrospective>> getListResponseEntity(@RequestParam(name = "page", defaultValue = "1") int page, @RequestParam(name = "pageSize", defaultValue = "2") int pageSize, List<Retrospective> retrospectives) {
        int startIndex = (page - 1) * pageSize;
        if (startIndex >= retrospectives.size()) {
            return new ResponseEntity<>(Collections.emptyList(), HttpStatus.OK);
        }

        int endIndex = Math.min(startIndex + pageSize, retrospectives.size());
        List<Retrospective> retrospectivesPage = retrospectives.subList(startIndex, endIndex);

        return new ResponseEntity<>(retrospectivesPage, HttpStatus.OK);
    }

    //Search Retrospectives by Date
    @GetMapping("/searchByDate")
    public ResponseEntity<List<Retrospective>> searchRetrospectivesByDateWithPagination(
            @RequestParam(name = "date") String date,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "pageSize", defaultValue = "2") int pageSize) {
        List<Retrospective> foundRetrospectives = retrospectives.stream()
                .filter(retrospective -> date.equals(retrospective.getDate()))
                .collect(Collectors.toList());

        if (foundRetrospectives.isEmpty()) {
            logger.info("Retrospective list is empty");
            return new ResponseEntity<>(Collections.emptyList(), HttpStatus.NOT_FOUND);
        }

        return getListResponseEntity(page, pageSize, foundRetrospectives);
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<FieldError> errors = ex.getBindingResult().getFieldErrors();
        StringBuilder errorMessage = new StringBuilder("Validation error: ");
        for (FieldError error : errors) {
            errorMessage.append(error.getDefaultMessage()).append(", ");
        }
        return new ResponseEntity<>(errorMessage.toString(), HttpStatus.BAD_REQUEST);
    }
}

