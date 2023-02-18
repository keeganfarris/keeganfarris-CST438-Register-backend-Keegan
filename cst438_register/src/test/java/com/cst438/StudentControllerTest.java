package com.cst438;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.cst438.controller.StudentController;
import com.cst438.domain.Student;
import com.cst438.domain.StudentDTO;
import com.cst438.domain.StudentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = { StudentController.class })
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest
public class StudentControllerTest {
	
	public static final String STUDENT_EMAIL = "test2@csumb.edu";
	public static final int STUDENT_ID = 1;
	public static final String STUDENT_NAME = "test name";

	
	@MockBean
	StudentRepository studentRepository;
	
	@Autowired
	private MockMvc mvc;
	
	@Test
	public void addNewStudent() throws Exception {
		
		MockHttpServletResponse response;
		
		Student student = new Student();
		student.setEmail(STUDENT_EMAIL);
		student.setName(STUDENT_NAME);
		student.setStatusCode(0);
		student.setStudent_id(STUDENT_ID);
		
		StudentDTO studentDTO = new StudentDTO();
		studentDTO.student_email = STUDENT_EMAIL;
		studentDTO.student_name = STUDENT_NAME;
		
		given(studentRepository.findById(STUDENT_ID)).willReturn(Optional.of(student));
		
		response = mvc.perform(
				MockMvcRequestBuilders
			      .post("/student")
			      .content(asJsonString(studentDTO))
			      .contentType(MediaType.APPLICATION_JSON)
			      .accept(MediaType.APPLICATION_JSON))
				.andReturn().getResponse();
		
		/* Verify the return status is OK */
		assertEquals(response.getStatus(), 200);
		
		/* Verify the repository save method was called */
		verify(studentRepository).save(any(Student.class));
		
		/* Verify if student with ID 1 is in database */
		assertNotEquals(studentRepository.findById(STUDENT_ID), null);
		
	}
	
	@Test
	public void addHold() throws Exception {
		
		MockHttpServletResponse response;
		
		Student student = new Student();
		student.setEmail(STUDENT_EMAIL);
		student.setName(STUDENT_NAME);
		student.setStatusCode(0);
		student.setStudent_id(STUDENT_ID);
		
		given(studentRepository.findById(STUDENT_ID)).willReturn(Optional.of(student));
		
		response = mvc.perform(
				MockMvcRequestBuilders
			      .put(String.format("/student/hold/%s", STUDENT_ID)))
				.andReturn().getResponse();
		
		/* Verify the return status is OK */
		assertEquals(response.getStatus(), 200);
		
		/* Verify the repository save method was called */
		verify(studentRepository).save(any(Student.class));
		
		/* Verify status code was changed to 1 (1 = hold) */
		assertEquals(student.getStatusCode(), STUDENT_ID);
		
		/* Verify status was changed to "HOLD" */
		assertEquals(student.getStatus(), "HOLD");
		
	}
	
	@Test
	public void releaseHold() throws Exception {
		
		MockHttpServletResponse response;
		
		Student student = new Student();
		student.setEmail(STUDENT_EMAIL);
		student.setName(STUDENT_NAME);
		student.setStatusCode(1);
		student.setStudent_id(STUDENT_ID);
		student.setStatus("HOLD");
		
		given(studentRepository.findById(STUDENT_ID)).willReturn(Optional.of(student));
		
		response = mvc.perform(
				MockMvcRequestBuilders
			      .put(String.format("/student/release/%s", STUDENT_ID)))
				.andReturn().getResponse();
		
		/* Verify the return status is OK */
		assertEquals(response.getStatus(), 200);
		
		/* Verify the repository save method was called */
		verify(studentRepository).save(any(Student.class));
		
		/* Verify status code was changed to 0 (0 = no hold) */
		assertEquals(student.getStatusCode(), 0);
		
		/* Verify status was changed to null */
		assertEquals(student.getStatus(), null);
		
	}
	
	private static String asJsonString(final Object obj) {
		try {

			return new ObjectMapper().writeValueAsString(obj);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
