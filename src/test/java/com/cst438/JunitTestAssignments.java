package com.cst438;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.cst438.domain.Assignment;
import com.cst438.domain.AssignmentDTO;
import com.cst438.domain.AssignmentGrade;
import com.cst438.domain.AssignmentGradeRepository;
import com.cst438.domain.GradeDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

/* 
 * Example of using Junit 
 * Mockmvc is used to test a simulated REST call to the RestController
 * This test assumes that students test4@csumb.edu, test@csumb.edu are enrolled in course 
 * with assignment with id=1
 */
@SpringBootTest
@AutoConfigureMockMvc
public class JunitTestAssignments {

	@Autowired
	private MockMvc mvc;
	
	@Autowired
	private AssignmentGradeRepository assignmentGradeRepository;

	/* 
	 * Enter a new grade for student test4@csumb.edu for assignment id=1
	 */
	@Test
	public void testCreateAssignment() throws Exception
	{
		MockHttpServletResponse response;

		// do an http get request for assignment 3 and test4
		response = mvc.perform(MockMvcRequestBuilders.get("/assignment/3").accept(MediaType.APPLICATION_JSON))
				.andReturn().getResponse();

		// verify that assignment does not exist
		assertEquals(404, response.getStatus());
		
		AssignmentDTO result = new AssignmentDTO(
				3, 
				"Restful 2", 
				"2023-9-19", 
				"CST 438 - Software Engineering", 
				31046);
		
		//mvc.perform(MockMvcRequestBuilders.post("/assignment"))
		response = mvc
		.perform(MockMvcRequestBuilders.post("/assignment").accept(MediaType.APPLICATION_JSON)
				.content(asJsonString(result)).contentType(MediaType.APPLICATION_JSON))
		.andReturn().getResponse();
		
		assertEquals(200, response.getStatus());
	
	}
	
	
	private static String asJsonString(final Object obj) {
		try {

			return new ObjectMapper().writeValueAsString(obj);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static <T> T fromJsonString(String str, Class<T> valueType) {
		try {
			return new ObjectMapper().readValue(str, valueType);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	

}
