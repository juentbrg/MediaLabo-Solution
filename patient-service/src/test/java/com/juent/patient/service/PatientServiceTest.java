package com.juent.patient.service;

import com.juent.patient.DTO.PatientDTO;
import com.juent.patient.enums.GenderEnum;
import com.juent.patient.exception.PatientNotFoundException;
import com.juent.patient.model.Patient;
import com.juent.patient.repository.PatientRepository;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PatientServiceTest {

    private AutoCloseable mock;

    @InjectMocks
    private PatientService patientService;

    @Mock
    private PatientRepository patientRepository;

    private Patient patient;
    private PatientDTO patientDTO;

    @BeforeEach
    void setUp() {
        mock = MockitoAnnotations.openMocks(this);

        patient = new Patient("1", "John", "Doe", LocalDate.of(1990, 1, 1), GenderEnum.MALE, "123 Main Street", "123456789");
        patientDTO = new PatientDTO(patient);
    }

    @AfterEach
    void tearDown() throws Exception {
        if (mock != null) {
            mock.close();
        }
    }

    @Test
    void findAllPatients_shouldReturnListOfPatients() {
        when(patientRepository.findAll()).thenReturn(List.of(patient));

        List<Patient> patients = patientService.findAllPatients();

        assertNotNull(patients);
        assertEquals(1, patients.size());
        assertEquals("John", patients.get(0).getFirstName());
        verify(patientRepository, times(1)).findAll();
    }

    @Test
    void findAllPatients_shouldReturnEmptyList() {
        when(patientRepository.findAll()).thenReturn(List.of());

        List<Patient> patients = patientService.findAllPatients();

        assertNotNull(patients);
        assertEquals(0, patients.size());
        verify(patientRepository, times(1)).findAll();
    }

    @Test
    void findPatientById_shouldReturnPatient() {
        when(patientRepository.findById("1")).thenReturn(Optional.of(patient));

        PatientDTO foundPatient = patientService.findPatientById("1");

        assertNotNull(foundPatient);
        assertEquals("John", foundPatient.getFirstName());
        verify(patientRepository, times(1)).findById("1");
    }

    @Test
    void findPatientById_shouldThrowNotFoundException() {
        when(patientRepository.findById("99")).thenReturn(Optional.empty());

        Exception exception = assertThrows(PatientNotFoundException.class, () -> {
            patientService.findPatientById("99");
        });

        assertEquals("Patient with ID 99 not found.", exception.getMessage());
        verify(patientRepository, times(1)).findById("99");
    }

    @Test
    void savePatient_shouldReturnSavedPatient() {
        when(patientRepository.save(any(Patient.class))).thenReturn(patient);

        PatientDTO savedPatient = patientService.savePatient(patientDTO);

        assertNotNull(savedPatient);
        assertEquals("John", savedPatient.getFirstName());
        verify(patientRepository, times(1)).save(any(Patient.class));
    }

    @Test
    void savePatient_shouldThrowExceptionIfPatientDTOIsNull() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            patientService.savePatient(null);
        });

        assertEquals("Missing required fields", exception.getMessage());
        verify(patientRepository, never()).save(any(Patient.class));
    }


    @Test
    void updatePatient_shouldReturnUpdatedPatient() {
        PatientDTO updateDTO = new PatientDTO();
        updateDTO.setAddress("456 New Street");
        updateDTO.setPhone("987654321");

        when(patientRepository.findById("1")).thenReturn(Optional.of(patient));
        when(patientRepository.save(any(Patient.class))).thenReturn(patient);

        PatientDTO updatedPatient = patientService.updatePatient(updateDTO, "1");

        assertNotNull(updatedPatient);
        verify(patientRepository, times(1)).findById("1");
        verify(patientRepository, times(1)).save(any(Patient.class));
    }

    @Test
    void updatePatient_shouldUpdateOnlyAddressIfPhoneIsNull() {
        PatientDTO updateDTO = new PatientDTO();
        updateDTO.setAddress("456 New Street");
        updateDTO.setPhone(null);

        when(patientRepository.findById("1")).thenReturn(Optional.of(patient));
        when(patientRepository.save(any(Patient.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PatientDTO updatedPatient = patientService.updatePatient(updateDTO, "1");

        assertEquals("456 New Street", updatedPatient.getAddress());
        assertEquals("123456789", updatedPatient.getPhone());
    }

    @Test
    void updatePatient_shouldUpdateOnlyPhoneIfAddressIsNull() {
        PatientDTO updateDTO = new PatientDTO();
        updateDTO.setAddress(null);
        updateDTO.setPhone("987654321");

        when(patientRepository.findById("1")).thenReturn(Optional.of(patient));
        when(patientRepository.save(any(Patient.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PatientDTO updatedPatient = patientService.updatePatient(updateDTO, "1");

        assertEquals("123 Main Street", updatedPatient.getAddress());
        assertEquals("987654321", updatedPatient.getPhone());
    }

    @Test
    void updatePatient_shouldThrowNotFoundException() {
        PatientDTO updateDTO = new PatientDTO();
        when(patientRepository.findById("99")).thenReturn(Optional.empty());

        Exception exception = assertThrows(PatientNotFoundException.class, () -> {
            patientService.updatePatient(updateDTO, "99");
        });

        assertEquals("Patient with ID 99 not found.", exception.getMessage());
        verify(patientRepository, times(1)).findById("99");
        verify(patientRepository, times(0)).save(any(Patient.class));
    }

    @Test
    void deletePatient_shouldDeletePatient() {
        when(patientRepository.existsById("1")).thenReturn(true);
        doNothing().when(patientRepository).deleteById("1");

        assertDoesNotThrow(() -> patientService.deletePatient("1"));

        verify(patientRepository, times(1)).existsById("1");
        verify(patientRepository, times(1)).deleteById("1");
    }

    @Test
    void deletePatient_shouldThrowNotFoundException() {
        when(patientRepository.existsById("99")).thenReturn(false);

        Exception exception = assertThrows(PatientNotFoundException.class, () -> {
            patientService.deletePatient("99");
        });

        assertEquals("Patient with ID 99 not found.", exception.getMessage());
        verify(patientRepository, times(1)).existsById("99");
        verify(patientRepository, times(0)).deleteById(anyString());
    }
}
