package com.project.retrospective_service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.validation.FieldError;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/retrospectives")
public class RetrospectiveController {
    private final List<Retrospective> retrospectives = new ArrayList<>();
    private static final Logger logger = LogManager.getLogger(RetrospectiveController.class);


    // Create a new retrospective
    @PostMapping
    public ResponseEntity<String> createRetrospective( @RequestBody Retrospective retrospective) {
        logger.info("Creating a new retrospective: " + retrospective.getName());
        String inputDate = retrospective.getDate();
        List<String> inputParticipants = retrospective.getParticipants();
        List<Feedback> inputFeedback = retrospective.getFeedback();
        if ((inputDate == null || inputParticipants.isEmpty()) && inputFeedback.isEmpty()) {
            return new ResponseEntity<>("Date and Participants cannot be empty", HttpStatus.BAD_REQUEST);
        }
        retrospectives.add(retrospective);
        return new ResponseEntity<>("Retrospective created successfully", HttpStatus.CREATED);
    }

    // Add feedback item to a retrospective
    @PostMapping("/{retrospectiveId}/feedback")
    public ResponseEntity<String> addFeedback(@PathVariable int retrospectiveId, @RequestBody Feedback feedback) {
        if (retrospectiveId >= 0 && retrospectiveId < retrospectives.size()) {
            Retrospective retrospective = retrospectives.get(retrospectiveId);
            retrospective.getFeedback().add(feedback);
            logger.info("Add new feedback: " + retrospective.getFeedback());
            return new ResponseEntity<>("Feedback added successfully", HttpStatus.CREATED);
        }
        return new ResponseEntity<>("Retrospective not found", HttpStatus.NOT_FOUND);
    }

    // Update feedback item
    @PutMapping("/{retrospectiveId}/feedback/{feedbackId}")
    public ResponseEntity<String> updateFeedback(@PathVariable int retrospectiveId, @PathVariable int feedbackId, @RequestBody Feedback feedback) {
        if (retrospectiveId >= 0 && retrospectiveId < retrospectives.size()) {
            Retrospective retrospective = retrospectives.get(retrospectiveId);
            if (feedbackId >= 0 && feedbackId < retrospective.getFeedback().size()) {
                Feedback existingFeedback = retrospective.getFeedback().get(feedbackId);
                existingFeedback.setBody(feedback.getBody());
                existingFeedback.setFeedbackType(feedback.getFeedbackType());
                return new ResponseEntity<>("Feedback updated successfully", HttpStatus.OK);
            }
            return new ResponseEntity<>("Feedback not found", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>("Retrospective not found", HttpStatus.NOT_FOUND);
    }

    // Get all retrospectives
    @GetMapping
    public List<Retrospective> getRetrospectives() {
        return retrospectives;
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

