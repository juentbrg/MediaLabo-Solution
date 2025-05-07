package com.juent.patient.controller;

import com.juent.patient.DTO.PatientDTO;
import com.juent.patient.enums.GenderEnum;
import com.juent.patient.exception.PatientNotFoundException;
import com.juent.patient.model.Patient;
import com.juent.patient.service.PatientService;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.*;

public class PatientControllerTest {

    private AutoCloseable mock;

    @InjectMocks
    private PatientController patientController;

    @Mock
    private PatientService patientService;

    private PatientDTO patientDTO;
    private Patient patient;

    @BeforeEach
    public void init() {
        mock = MockitoAnnotations.openMocks(this);

        patientDTO = new PatientDTO("John", "Doe", "1990-01-01", GenderEnum.MALE, "123 Main Street", "123456789");
        patient = new Patient("12345", "John", "Doe", LocalDate.of(1990, Month.JANUARY, 1), GenderEnum.MALE, "123 Main Street", "123456789");
    }

    @AfterEach
    public void close() throws Exception {
        if (mock != null) {
            mock.close();
        }
    }

    @Test
    public void getPatients_shouldReturnListOfPatients() {
        when(patientService.findAllPatients()).thenReturn(List.of(patient));

        ResponseEntity<List<Patient>> response = patientController.getPatients();

        assertEquals(OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(patientService, times(1)).findAllPatients();
    }

    @Test
    public void getPatients_shouldReturnNoContentWhenNoPatients() {
        when(patientService.findAllPatients()).thenReturn(List.of());

        ResponseEntity<List<Patient>> response = patientController.getPatients();

        assertEquals(NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(patientService, times(1)).findAllPatients();
    }

    @Test
    public void getPatientById_shouldReturnPatient() {
        when(patientService.findPatientById("1")).thenReturn(patientDTO);

        ResponseEntity<PatientDTO> response = patientController.getPatientById("1");

        assertEquals(OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("John", response.getBody().getFirstName());
        verify(patientService, times(1)).findPatientById("1");
    }

    @Test
    public void getPatientById_shouldThrowNotFoundException() {
        when(patientService.findPatientById("99")).thenThrow(new PatientNotFoundException("99"));

        Exception exception = assertThrows(PatientNotFoundException.class, () -> {
            patientController.getPatientById("99");
        });

        assertEquals("Patient with ID 99 not found.", exception.getMessage());
        verify(patientService, times(1)).findPatientById("99");
    }

    @Test
    public void createPatient_shouldReturnCreatedPatient() {
        when(patientService.savePatient(patientDTO)).thenReturn(patientDTO);

        ResponseEntity<PatientDTO> response = patientController.createPatient(patientDTO);

        assertEquals(OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("John", response.getBody().getFirstName());
        verify(patientService, times(1)).savePatient(patientDTO);
    }

    @Test
    public void updatePatient_shouldReturnUpdatedPatient() {
        PatientDTO updateDTO = new PatientDTO();
        updateDTO.setAddress("456 New Street");
        updateDTO.setPhone("987654321");

        when(patientService.updatePatient(updateDTO, "1")).thenReturn(patientDTO);

        ResponseEntity<PatientDTO> response = patientController.updatePatient("1", updateDTO);

        assertEquals(OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(patientService, times(1)).updatePatient(updateDTO, "1");
    }

    @Test
    public void updatePatient_shouldThrowNotFoundException() {
        PatientDTO updateDTO = new PatientDTO();
        when(patientService.updatePatient(updateDTO, "99")).thenThrow(new PatientNotFoundException("99"));

        Exception exception = assertThrows(PatientNotFoundException.class, () -> {
            patientController.updatePatient("99", updateDTO);
        });

        assertEquals("Patient with ID 99 not found.", exception.getMessage());
        verify(patientService, times(1)).updatePatient(updateDTO, "99");
    }

    @Test
    public void deletePatient_shouldReturnNoContent() {
        doNothing().when(patientService).deletePatient("1");

        ResponseEntity<Void> response = patientController.deletePatient("1");

        assertEquals(NO_CONTENT, response.getStatusCode());
        verify(patientService, times(1)).deletePatient("1");
    }

    @Test
    public void deletePatient_shouldThrowNotFoundException() {
        doThrow(new PatientNotFoundException("99")).when(patientService).deletePatient("99");

        Exception exception = assertThrows(PatientNotFoundException.class, () -> {
            patientController.deletePatient("99");
        });

        assertEquals("Patient with ID 99 not found.", exception.getMessage());
        verify(patientService, times(1)).deletePatient("99");
    }
}
