package com.juent.diabetes_assessment.services;

import com.juent.diabetes_assessment.DTO.PatientDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class PatientServiceClient {


    private final WebClient webClient;

    public PatientServiceClient(@Value("${patient.service.url}") String patientServiceUrl, WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl(patientServiceUrl).build();
    }

    public PatientDTO getPatientInfo(String patientId) {
        return webClient.get()
                .uri("/{id}", patientId)
                .retrieve()
                .bodyToMono(PatientDTO.class)
                .block();
    }
}
