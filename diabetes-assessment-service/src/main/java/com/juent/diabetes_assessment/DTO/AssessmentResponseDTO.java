package com.juent.diabetes_assessment.DTO;

import lombok.*;

@Data
@Builder
@Generated
public class AssessmentResponseDTO {
    private String patientId;
    private int age;
    private int triggerCount;
    private String risk;
}
