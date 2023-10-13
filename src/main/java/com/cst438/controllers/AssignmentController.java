package com.cst438.controllers;

import java.util.List;
import java.text.SimpleDateFormat;
import java.sql.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.cst438.domain.Assignment;
import com.cst438.domain.AssignmentDTO;
import com.cst438.domain.AssignmentRepository;
import com.cst438.domain.Course;
import com.cst438.domain.CourseRepository;
import com.google.common.base.Optional;

@RestController
@CrossOrigin 
public class AssignmentController {
	
	@Autowired
	AssignmentRepository assignmentRepository;
	
	@Autowired
	CourseRepository courseRepository;
	
	@GetMapping("/assignment")
	public AssignmentDTO[] getAllAssignmentsForInstructor() {
		// get all assignments for this instructor
		String instructorEmail = "dwisneski@csumb.edu";  // user name (should be instructor's email) 
		List<Assignment> assignments = assignmentRepository.findByEmail(instructorEmail);
		AssignmentDTO[] result = new AssignmentDTO[assignments.size()];
		for (int i=0; i<assignments.size(); i++) {
			Assignment as = assignments.get(i);
			AssignmentDTO dto = new AssignmentDTO(
					as.getId(), 
					as.getName(), 
					as.getDueDate().toString(), 
					as.getCourse().getTitle(), 
					as.getCourse().getCourse_id());
			result[i]=dto;
		}
		return result;
	}
	
	// TODO create CRUD methods for Assignment
	@PostMapping("/assignment")
	public Assignment createAssignment(@RequestBody AssignmentDTO assignment) {
		Course course = courseRepository.findById(assignment.courseId()).orElse(null);
		Assignment assign = new Assignment();
		assign.setId(assignment.id());
		assign.setName(assignment.assignmentName());
		Date date = Date.valueOf(assignment.dueDate());
		assign.setDueDate(date);
		if(course != null) {
			assign.setCourse(course);
			return assignmentRepository.save(assign);
		} else {
			Course newCourse = new Course();
			newCourse.setCourse_id(assignment.courseId());
			newCourse.setTitle(assignment.courseTitle());
			newCourse.setInstructor("dwisneski@csumb.edu");
//			newCourse.getAssignments().add(assign);
			courseRepository.save(newCourse);
			assign.setCourse(newCourse);

			return assignmentRepository.save(assign);
	}
}
    @GetMapping("/assignment/{id}")
    public AssignmentDTO read(@PathVariable int id) {
        java.util.Optional<Assignment> assignment = assignmentRepository.findById(id);
        if (assignment.isPresent()) {
    		AssignmentDTO result = new AssignmentDTO(id, null, null, null, 0);
    			Assignment as = assignment.get();
    			AssignmentDTO dto = new AssignmentDTO(
    					as.getId(), 
    					as.getName(), 
    					as.getDueDate().toString(), 
    					as.getCourse().getTitle(), 
    					as.getCourse().getCourse_id());
    			result=dto;
    		return result;
        } else {
        	System.out.println("Id not found");
            return null;
        }
    }
    
    @PutMapping("/assignment/{id}")
    public void update(@RequestBody AssignmentDTO assignment, @PathVariable int id) {
    	Course course = courseRepository.findById(assignment.courseId()).orElse(null);
		Assignment assign = assignmentRepository.findById(id).orElse(null);
		//assign = assignmentRepository.findById(id);
		if (assign != null)
		{
		assign.setName(assignment.assignmentName());
		Date date = Date.valueOf(assignment.dueDate());
		assign.setDueDate(date);
		if(course != null) {
			assign.setCourse(course);
			assignmentRepository.save(assign);
		} else {
			Course newCourse = new Course();
			newCourse.setCourse_id(assignment.courseId());
			newCourse.setTitle(assignment.courseTitle());
			newCourse.setInstructor("dwisneski@csumb.edu");
//			newCourse.getAssignments().add(assign);
			courseRepository.save(newCourse);
			assign.setCourse(newCourse);
			}
		}
		else
		{
			System.out.println("Id not found");
		}
}
    
    @DeleteMapping("/assignment/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        assignmentRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
    
	
}
