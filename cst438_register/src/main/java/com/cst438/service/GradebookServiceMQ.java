package com.cst438.service;


import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;

import com.cst438.domain.CourseDTOG;
import com.cst438.domain.Enrollment;
import com.cst438.domain.EnrollmentDTO;
import com.cst438.domain.EnrollmentRepository;


public class GradebookServiceMQ extends GradebookService {
	
	@Autowired
	RabbitTemplate rabbitTemplate;
	
	@Autowired
	EnrollmentRepository enrollmentRepository;
	
	@Autowired
	Queue gradebookQueue;
	
	
	public GradebookServiceMQ() {
		System.out.println("MQ grade book service");
	}
	
	// send message to grade book service about new student enrollment in course
	@Override
	public void enrollStudent(String student_email, String student_name, int course_id) {
		 
		// Creates new enrollment entity and
		// adds course_id, student_email, student_name
		EnrollmentDTO enrollment = new EnrollmentDTO();
		enrollment.course_id = course_id;
		enrollment.studentEmail = student_email;
		enrollment.studentName = student_name;
		
		// Message sent to gradebook with enrollment information
		System.out.println("Send rabbitmq message " + enrollment);
		rabbitTemplate.convertAndSend(gradebookQueue.getName(), enrollment);
		
		System.out.println("Message sent to gradbook service for student " + student_email + " " + course_id);  
		
	}
	
	@RabbitListener(queues = "registration-queue")
	public void receive(CourseDTOG courseDTOG) {
		System.out.println("Receive enrollment :" + courseDTOG);
		
		// Goes through all grade objects
		
		for (CourseDTOG.GradeDTO g : courseDTOG.grades) {
			
			// Gets enrollment entity by email and course id
			Enrollment enrollment = enrollmentRepository.findByEmailAndCourseId(g.student_email, courseDTOG.course_id);
			
			// Sets and saves the grade to the database
			enrollment.setCourseGrade(g.grade);
			enrollmentRepository.save(enrollment);
		}
		
		System.out.println("Final grades set.");
		
	}

}
