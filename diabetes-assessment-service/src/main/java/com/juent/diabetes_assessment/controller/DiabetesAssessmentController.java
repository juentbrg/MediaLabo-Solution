package com.juent.diabetes_assessment.controller;

import com.juent.diabetes_assessment.DTO.AssessmentResponseDTO;
import com.juent.diabetes_assessment.services.DiabetesAssessmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/assess")
public class DiabetesAssessmentController {

    private final DiabetesAssessmentService diabetesAssessmentService;

    public DiabetesAssessmentController(DiabetesAssessmentService diabetesAssessmentService) {
        this.diabetesAssessmentService = diabetesAssessmentService;
    }

    @GetMapping("/{patientId}")
    public ResponseEntity<AssessmentResponseDTO> assessPatient(@PathVariable String patientId) {
        return ResponseEntity.ok(diabetesAssessmentService.assessPatient(patientId));
    }
}
