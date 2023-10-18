package com.cst438.services;


import org.springframework.amqp.core.Queue;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cst438.domain.Assignment;
import com.cst438.domain.Course;
import com.cst438.domain.FinalGradeDTO;
import com.cst438.domain.CourseRepository;
import com.cst438.domain.Enrollment;
import com.cst438.domain.EnrollmentDTO;
import com.cst438.domain.EnrollmentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
@ConditionalOnProperty(prefix = "registration", name = "service", havingValue = "mq")
public class RegistrationServiceMQ implements RegistrationService {

	@Autowired
	EnrollmentRepository enrollmentRepository;

	@Autowired
	CourseRepository courseRepository;

	@Autowired
	private RabbitTemplate rabbitTemplate;

	public RegistrationServiceMQ() {
		System.out.println("MQ registration service ");
	}


	Queue registrationQueue = new Queue("registration-queue", true);

	/*
	 * Receive message for student added to course
	 */
	@RabbitListener(queues = "gradebook-queue")
	@Transactional
	public void receive(String message) {
		
		System.out.println("Gradebook has received: "+message);
		
		//TODO  deserialize message to EnrollmentDTO and update database
		try {
        ObjectMapper mapper = new ObjectMapper();
        EnrollmentDTO enroll = mapper.readValue(message, EnrollmentDTO.class);
		Enrollment enr = new Enrollment();
		enr.setStudentName(enroll.studentName());
		enr.setStudentEmail(enroll.studentEmail());
		Course course = courseRepository.findById(enroll.courseId()).orElse(null);
		enr.setCourse(course);
		enr.setId(enroll.id());
		enrollmentRepository.save(enr);
	   } catch (Exception e) {
	        e.printStackTrace();
	        // Handle the exception appropriately, maybe log it or notify the user
	    }
	}

	/*
	 * Send final grades to Registration Service 
	 */
	@Override
	public void sendFinalGrades(int course_id, FinalGradeDTO[] grades) {
		 
		System.out.println("Start sendFinalGrades "+course_id);

		//TODO convert grades to JSON string and send to registration service
		for (int i = 0; i< grades.length; i++)			
		{
			String s = new String("");
			s.concat("{\"name\":" + grades[i].studentName());
			s.concat(",\"email\":"+grades [i].studentEmail());
			s.concat(",\"courseId\":"+grades[i].courseId());
			s.concat(",\"grade\":"+grades[i].grade()+"}");
	
		}
		
	}
	
	private static String asJsonString(final Object obj) {
		try {
			return new ObjectMapper().writeValueAsString(obj);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static <T> T  fromJsonString(String str, Class<T> valueType ) {
		try {
			return new ObjectMapper().readValue(str, valueType);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
