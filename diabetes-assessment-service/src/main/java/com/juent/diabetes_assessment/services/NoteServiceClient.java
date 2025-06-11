package com.juent.diabetes_assessment.services;

import com.juent.diabetes_assessment.DTO.NoteDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
public class NoteServiceClient {

    private final WebClient webClient;

    public NoteServiceClient(@Value("${note.service.url}") String noteServiceUrl, WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl(noteServiceUrl).build();
    }

    public List<NoteDTO> getNotesForPatient(String patientId) {
        NoteDTO[] notesArray = webClient.get()
                .uri("/{id}", patientId)
                .retrieve()
                .bodyToMono(NoteDTO[].class)
                .block();

        return notesArray != null ? List.of(notesArray) : List.of();
    }
}
