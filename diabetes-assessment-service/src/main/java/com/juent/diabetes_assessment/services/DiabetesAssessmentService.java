package com.juent.diabetes_assessment.services;

import com.juent.diabetes_assessment.DTO.AssessmentResponseDTO;
import com.juent.diabetes_assessment.DTO.NoteDTO;
import com.juent.diabetes_assessment.DTO.PatientDTO;
import com.juent.diabetes_assessment.enums.GenderEnum;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DiabetesAssessmentService {
    private static final List<String> TRIGGERS_LIST = List.of(
            "hemoglobine a1c",
            "microalbumine",
            "taille",
            "poids",
            "fume",
            "anormal",
            "cholestérol",
            "vertige",
            "rechute",
            "reaction",
            "anticorps"
    );

    private final NoteServiceClient noteServiceClient;
    private final PatientServiceClient patientServiceClient;

    public DiabetesAssessmentService(NoteServiceClient noteServiceClient, PatientServiceClient patientServiceClient) {
        this.noteServiceClient = noteServiceClient;
        this.patientServiceClient = patientServiceClient;
    }

    public AssessmentResponseDTO assessPatient(String patientId) {
        PatientDTO patientInfo = patientServiceClient.getPatientInfo(patientId);
        List<NoteDTO> patientNotesList = noteServiceClient.getNotesForPatient(patientId);

        String cleanedNotes = patientNotesList.stream()
                .map(NoteDTO::getNote)
                .map(this::normalizeText)
                .collect(Collectors.joining(" "));

        int triggerCount = countTrigger(cleanedNotes);

        int patientAge = (int) ChronoUnit.YEARS.between(LocalDate.parse(patientInfo.getBirthDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd")), LocalDate.now());

        String patientRisk = definePatientRisk(triggerCount, patientInfo.getGender(), patientAge);

        return AssessmentResponseDTO.builder()
                .patientId(patientId)
                .age(patientAge)
                .triggerCount(triggerCount)
                .risk(patientRisk)
                .build();
    }

    private String normalizeText(String text) {
        if (text == null) return "";

        String normalized = Normalizer.normalize(text, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");

        return normalized
                .toLowerCase()
                .replaceAll("[^\\p{L}\\p{N}\\s]", "")
                .replaceAll("\\s+", " ")
                .strip();
    }

    private int countTrigger(String cleanedNotes) {
        int triggerCount = 0;
        for (String trigger : TRIGGERS_LIST) {
            String normalizedTrigger = normalizeText(trigger);
            if (cleanedNotes.contains(normalizedTrigger)) {
                triggerCount++;
            }
        }
        return triggerCount;
    }


    private String definePatientRisk(int triggerCount, GenderEnum gender, int patientAge) {
        boolean isMale = gender == GenderEnum.MALE;
        boolean isFemale = gender == GenderEnum.FEMALE;

        if (triggerCount == 0)
            return "None";


        if (patientAge > 30 && triggerCount >= 2 && triggerCount <= 5)
            return "Borderline";

        if (patientAge < 30) {
            if (isMale && triggerCount >= 3 && triggerCount < 5)
                return "In Danger";
            if (isFemale && triggerCount >= 4 && triggerCount < 7)
                return "In Danger";
        } else {
            if (triggerCount >= 6 && triggerCount <= 7)
                return "In Danger";
        }

        if (patientAge < 30) {
            if (isMale && triggerCount >= 5)
                return "Early onset";
            if (isFemale && triggerCount >= 7)
                return "Early onset";
        } else {
            if (triggerCount >= 8)
                return "Early onset";
        }

        return "None";
    }
}
