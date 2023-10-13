package com.cst438.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.cst438.domain.FinalGradeDTO;
import com.cst438.domain.Course;
import com.cst438.domain.CourseRepository;
import com.cst438.domain.EnrollmentDTO;
import com.cst438.domain.EnrollmentRepository;
import com.cst438.domain.Enrollment;

@Service
@ConditionalOnProperty(prefix = "registration", name = "service", havingValue = "rest")
@RestController
public class RegistrationServiceREST implements RegistrationService {

	
	RestTemplate restTemplate = new RestTemplate();
	
	@Value("${registration.url}") 
	String registration_url;
	
	public RegistrationServiceREST() {
		System.out.println("REST registration service ");
	}
	
	@Override
	public void sendFinalGrades(int course_id , FinalGradeDTO[] grades) { 
		Course c = courseRepository.findById(course_id).orElse(null);
		for (int i =0; i<c.getEnrollments().size(); i++)
		{
			Enrollment e = c.getEnrollments().get(i);
			String s = new String("");
			for (int l = 0; l<e.getAssignmentGrades().size(); l++)
			{
				e.getAssignmentGrades().get(l);
				if (l==e.getAssignmentGrades().size()-1)
				{
					s.concat(e.getAssignmentGrades().get(l).getScore().toString());
				}
				else
				{
					s.concat(e.getAssignmentGrades().get(l).getScore().toString() + ", ");
				}
			}
			FinalGradeDTO grade = new FinalGradeDTO(e.getStudentEmail(), e.getStudentName(), s, e.getId());
			grades[i]=grade;
		}
		
		restTemplate.put("http://localhost:8080/course", grades);
		
	}
	
	@Autowired
	CourseRepository courseRepository;

	@Autowired
	EnrollmentRepository enrollmentRepository;

	
	/*
	 * endpoint used by registration service to add an enrollment to an existing
	 * course.
	 */
	@PostMapping("/enrollment")
	@Transactional
	public EnrollmentDTO addEnrollment(@RequestBody EnrollmentDTO enrollmentDTO) {
		
		// Receive message from registration service to enroll a student into a course.
		
		System.out.println("GradeBook addEnrollment "+enrollmentDTO);
		
		Enrollment e = new Enrollment();
		Course c = courseRepository.findById(enrollmentDTO.courseId()).orElse(null);

		e.setStudentName(enrollmentDTO.studentName());
		e.setStudentEmail(enrollmentDTO.studentEmail());
		e.setCourse(c);
		e.setId(enrollmentDTO.id());
		
		enrollmentRepository.save(e);
		return enrollmentDTO;
		
	}

}
