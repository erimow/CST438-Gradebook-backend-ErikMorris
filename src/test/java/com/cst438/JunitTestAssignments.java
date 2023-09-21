package com.cst438;

import static org.junit.Assert.assertFalse;
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.cst438.domain.Assignment;
import com.cst438.domain.AssignmentDTO;
import com.cst438.domain.AssignmentGrade;
import com.cst438.domain.AssignmentGradeRepository;
import com.cst438.domain.AssignmentRepository;
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
	
	@Autowired
	private AssignmentRepository assignmentRepository;

	/* 
	 * Enter a new grade for student test4@csumb.edu for assignment id=1
	 */
	@Test
	public void testCreateAssignment() throws Exception
	{
		MockHttpServletResponse response;

		// do an http get request for assignment 3 and test4
		response = mvc.perform(MockMvcRequestBuilders.get("/assignment/4").accept(MediaType.APPLICATION_JSON))
				.andReturn().getResponse();

		// verify that assignment does not exist
		assertEquals(404, response.getStatus());
		
//		AssignmentDTO result = new AssignmentDTO(
//				3, 
//				"Restful 2", 
//				"2023-9-19", 
//				"CST 438 - Software Engineering", 
//				31046);
//		
//		//mvc.perform(MockMvcRequestBuilders.post("/assignment"))
//		response = mvc
//		.perform(MockMvcRequestBuilders.post("/assignment").accept(MediaType.APPLICATION_JSON)
//				.content(asJsonString(result)).contentType(MediaType.APPLICATION_JSON))
//		.andReturn().getResponse();
//		
//		assertEquals(200, response.getStatus());
//		
//		response = mvc.perform(MockMvcRequestBuilders.get("/assignment/3").accept(MediaType.APPLICATION_JSON)).andReturn().getResponse();
//
//		// verify that assignment exists
//		assertEquals(200, response.getStatus());
		
	       mvc.perform(MockMvcRequestBuilders.post("/assignment")
	                .contentType(MediaType.APPLICATION_JSON)
	                .content("{\"id\":4,\"assignmentName\":\"Restful 3\", \"dueDate\":\"2023-09-05\", \"courseTitle\":\"CST 438 - Software Engineering\", \"courseId\":31046}"))
	                .andExpect(MockMvcResultMatchers.status().isOk())
	                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Restful 3"));
	
	}
	
	@Test
	public void testUpdateAssignment() throws Exception
	{
		
		Assignment assign = new Assignment();
		assign.setName("Restful 2");
		assign.setId(3);
		assignmentRepository.save(assign);
		// do an http get request for assignment 3 and test4
	       mvc.perform(MockMvcRequestBuilders.get("/assignment/{id}", assign.getId()).contentType(MediaType.APPLICATION_JSON))
	       .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(3))
        .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Restful 2"));
		
	       mvc.perform(MockMvcRequestBuilders.put("/assignment/{id}", assign.getId())
	                .contentType(MediaType.APPLICATION_JSON)
	                .content("{\"id\":2,\"assignmentName\":\"Restful 2.5\", \"dueDate\":\"2023-09-06\", \"courseTitle\":\"CST 438 - Software Engineering\", \"courseId\":31046}"))
	                .andExpect(MockMvcResultMatchers.status().isOk());
	       
	       mvc.perform(MockMvcRequestBuilders.get("/assignment/{id}", assign.getId()).accept(MediaType.APPLICATION_JSON))
	       .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(3))
           .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Restful 2.5"));
	       
	}
	
    @Test
    public void testDeleteAssignment() throws Exception {
        Assignment assign = new Assignment();
        assign.setName("Test");
        assign.setId(5);
        assignmentRepository.save(assign);

        mvc.perform(MockMvcRequestBuilders.delete("/assignment/{id}", assign.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());

        // Verify that the student is no longer in the database
        boolean assignmentExists = assignmentRepository.existsById(assign.getId());
        assertFalse(assignmentExists);
    }
    
    @Test
    public void testGetAssignment() throws Exception {
        Assignment assign = new Assignment();
        assign.setName("Test");
        assign.setId(5);
        assignmentRepository.save(assign);

        mvc.perform(MockMvcRequestBuilders.get("/assignment/{id}", assign.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Test"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(5));
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
