package com.juent.diabetes_assessment.controller;

import com.juent.diabetes_assessment.DTO.AssessmentResponseDTO;
import com.juent.diabetes_assessment.services.DiabetesAssessmentService;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.*;

public class DiabetesAssessmentControllerTest {

    private AutoCloseable mock;

    @InjectMocks
    private DiabetesAssessmentController diabetesAssessmentController;

    @Mock
    private DiabetesAssessmentService diabetesAssessmentService;

    private AssessmentResponseDTO assessmentResponse;

    @BeforeEach
    public void init() {
        mock = MockitoAnnotations.openMocks(this);

        assessmentResponse = AssessmentResponseDTO.builder()
                .patientId("1")
                .age(58)
                .triggerCount(2)
                .risk("Borderline")
                .build();
    }

    @AfterEach
    public void close() throws Exception {
        if (mock != null) {
            mock.close();
        }
    }

    @Test
    public void assessPatient_shouldReturnRiskAssessment() {
        when(diabetesAssessmentService.assessPatient("1")).thenReturn(assessmentResponse);

        ResponseEntity<AssessmentResponseDTO> response = diabetesAssessmentController.assessPatient("1");

        assertEquals(OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Borderline", response.getBody().getRisk());
        assertEquals(58, response.getBody().getAge());
        verify(diabetesAssessmentService, times(1)).assessPatient("1");
    }

    @Test
    public void assessPatient_shouldThrowException_whenServiceFails() {
        when(diabetesAssessmentService.assessPatient("1"))
                .thenThrow(new RuntimeException("Service indisponible"));

        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> diabetesAssessmentController.assessPatient("1")
        );

        assertEquals("Service indisponible", thrown.getMessage());
        verify(diabetesAssessmentService, times(1)).assessPatient("1");
    }

}
