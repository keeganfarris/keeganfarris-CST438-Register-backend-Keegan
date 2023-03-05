package com.cst438.controller;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.cst438.domain.Student;
import com.cst438.domain.StudentDTO;
import com.cst438.domain.StudentRepository;

@RestController
public class StudentController {
	
	@Autowired
	StudentRepository studentRepository;
	
	
	
	/*
	 * Add student
	 */
	@PostMapping("/student")
	@Transactional
	public Student addStudent(@RequestBody StudentDTO studentDTO) {
		
		/* Finds student by email */
		Student student = studentRepository.findByEmail(studentDTO.student_email);
		
		/* If student is null, new student is added to the database */
		if (student == null) {
			Student newStudent = new Student();
			newStudent.setEmail(studentDTO.student_email);
			newStudent.setName(studentDTO.student_name);
			Student savedStudent = studentRepository.save(newStudent);
			return savedStudent;
		}else {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Student already exists. " + studentDTO.student_email);
		}
		
	}
	
	/*
	 * Add hold
	 */
	@PutMapping("/student/hold/{student_id}")
	@Transactional
	public Student addStudentHold(@PathVariable("student_id") int student_id) {
		
		/* Finds student by ID */
		Student student = studentRepository.findById(student_id).orElse(null);
		
		/* If student is not null, change status code to 1 and status to "HOLD" */
		if (student != null) {
			student.setStatusCode(1);
			student.setStatus("HOLD");
			Student updatedStudent = studentRepository.save(student);
			return updatedStudent;
		}else {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Student not found. Student ID: " + student_id);
		}
		
	}
	
	/*
	 * Release hold
	 */
	@PutMapping("/student/release/{student_id}")
	@Transactional
	public Student releaseStudentHold(@PathVariable("student_id") int student_id) {
		
		/* Finds student by ID */
		Student student = studentRepository.findById(student_id).orElse(null);
		
		/* If student is not null, change status code to 0 and status to null */
		if (student != null) {
			student.setStatusCode(0);
			student.setStatus(null);
			Student updatedStudent = studentRepository.save(student);
			return updatedStudent;
		}else {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Student not found. Student ID: " + student_id);
		}
		
	}
	
	

}
