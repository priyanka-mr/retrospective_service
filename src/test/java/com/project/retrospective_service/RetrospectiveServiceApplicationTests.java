package com.project.retrospective_service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

// import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import java.util.Arrays;
//import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest
public class RetrospectiveServiceApplicationTests {

	private RetrospectiveController retrospectiveController;

	@BeforeEach
	public void setup() {
		retrospectiveController = new RetrospectiveController();
	}

	@Test
	public void testCreateRetrospective_Success() {
	}

	@Test
	public void testAddFeedback_Success() {
	}

	@Test
	public void testAddFeedback_RetrospectiveNotFound() {
	}

	@Test
	public void testUpdateFeedback_Success() {
	}

	@Test
	public void testUpdateFeedback_RetrospectiveNotFound() {
	}

	@Test
	public void testUpdateFeedback_FeedbackNotFound() {
	}

}
