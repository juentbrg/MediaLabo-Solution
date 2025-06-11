package com.juent.diabetes_assessment.services;

import com.juent.diabetes_assessment.DTO.AssessmentResponseDTO;
import com.juent.diabetes_assessment.DTO.NoteDTO;
import com.juent.diabetes_assessment.DTO.PatientDTO;
import com.juent.diabetes_assessment.enums.GenderEnum;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Method;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

public class DiabetesAssessmentServiceTest {

    private AutoCloseable mock;

    private Method normalizeText;
    private Method countTrigger;

    @InjectMocks
    private DiabetesAssessmentService diabetesAssessmentService;

    @Mock
    private NoteServiceClient noteServiceClient;

    @Mock
    private PatientServiceClient patientServiceClient;

    private PatientDTO patient;
    private List<NoteDTO> notes;

    @BeforeEach
    public void init() throws NoSuchMethodException {
        mock = MockitoAnnotations.openMocks(this);

        normalizeText = DiabetesAssessmentService.class.getDeclaredMethod("normalizeText", String.class);
        countTrigger = DiabetesAssessmentService.class.getDeclaredMethod("countTrigger", String.class);
        normalizeText.setAccessible(true);
        countTrigger.setAccessible(true);

        patient = new PatientDTO();
        patient.setBirthDate("2004-06-18");
        patient.setGender(GenderEnum.MALE);

        NoteDTO note1 = new NoteDTO();
        note1.setNote("Le patient est fumeur. Hémoglobine A1C détectée.");

        NoteDTO note2 = new NoteDTO();
        note2.setNote("Le patient se plaint de vertiges. Réaction aux médicaments.");

        notes = List.of(note1, note2);
    }

    @AfterEach
    public void close() throws Exception {
        if (mock != null) mock.close();
    }

    @Test
    public void assessPatient_shouldReturnCorrectRisk() {
        when(patientServiceClient.getPatientInfo("1")).thenReturn(patient);
        when(noteServiceClient.getNotesForPatient("1")).thenReturn(notes);

        AssessmentResponseDTO result = diabetesAssessmentService.assessPatient("1");

        assertNotNull(result);
        assertEquals("1", result.getPatientId());
        assertEquals(4, result.getTriggerCount());
        assertEquals("In Danger", result.getRisk());

        verify(patientServiceClient, times(1)).getPatientInfo("1");
        verify(noteServiceClient, times(1)).getNotesForPatient("1");
    }

    @Test
    public void assessPatient_shouldReturnNone_whenNoTrigger() {
        NoteDTO emptyNote = new NoteDTO();
        emptyNote.setNote("Le patient va bien. Aucun symptôme.");
        when(patientServiceClient.getPatientInfo("2")).thenReturn(patient);
        when(noteServiceClient.getNotesForPatient("2")).thenReturn(List.of(emptyNote));

        AssessmentResponseDTO result = diabetesAssessmentService.assessPatient("2");

        assertNotNull(result);
        assertEquals("2", result.getPatientId());
        assertEquals(0, result.getTriggerCount());
        assertEquals("None", result.getRisk());
    }

    @Test
    public void normalizeText_shouldReturnCleanLowercaseString() throws Exception {
        String raw = "   RéAction  aux MÉdIcaments!!!  ";
        String normalized = (String) normalizeText.invoke(diabetesAssessmentService, raw);

        assertEquals("reaction aux medicaments", normalized);
    }

    @Test
    public void countTrigger_shouldDetectAllMatches() throws Exception {
        String note = "Hémoglobine A1C détectée. fumeuse et taille faible. Cholestérol élevé.";
        String normalized = (String) normalizeText.invoke(diabetesAssessmentService, note);

        int count = (int) countTrigger.invoke(diabetesAssessmentService, normalized);

        assertEquals(4, count);
    }
}
