package com.juent.patient.service;

import com.juent.patient.DTO.PatientDTO;
import com.juent.patient.exception.PatientNotFoundException;
import com.juent.patient.model.Patient;
import com.juent.patient.repository.PatientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class PatientService {
    private static final Logger logger = LoggerFactory.getLogger(PatientService.class);

    private final PatientRepository patientRepository;

    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    @Transactional(readOnly = true)
    public List<Patient> findAllPatients() {
        logger.info("fetching all patients");
        List<Patient> patients = patientRepository.findAll();
        logger.info("found {} patients", patients.size());
        return patients;
    }

    @Transactional(readOnly = true)
    public PatientDTO findPatientById(String id) {
        logger.info("Fetching patient with id {}", id);
        return patientRepository.findById(id)
                .map(PatientDTO::new)
                .orElseThrow(() -> new PatientNotFoundException(id));
    }

    @Transactional
    public PatientDTO savePatient(PatientDTO patientDTO) {
        logger.info("saving patient {}", patientDTO);
        if (null != patientDTO) {
            Patient patient = new Patient();
            patient.setFirstName(patientDTO.getFirstName());
            patient.setLastName(patientDTO.getLastName());
            patient.setBirthDate(LocalDate.parse(patientDTO.getBirthDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            patient.setGender(patientDTO.getGender());
            patient.setAddress(patientDTO.getAddress());
            patient.setPhone(patientDTO.getPhone());
            patientRepository.save(patient);
            logger.info("saved patient {}", patient);
            return new PatientDTO(patient);
        }
        throw new IllegalArgumentException("Patient cannot be null");
    }

    @Transactional
    public PatientDTO updatePatient(PatientDTO patientDTO, String id) {
        logger.info("Updating patient with id {}", id);
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new PatientNotFoundException(id));

        if (patientDTO.getFirstName() != null)
            patient.setFirstName(patientDTO.getFirstName());
        if (patientDTO.getLastName() != null)
            patient.setLastName(patientDTO.getLastName());
        if (patientDTO.getBirthDate() != null)
            patient.setBirthDate(LocalDate.parse(patientDTO.getBirthDate()));
        if (patientDTO.getGender() != null)
            patient.setGender(patientDTO.getGender());
        if (patientDTO.getAddress() != null)
            patient.setAddress(patientDTO.getAddress());
        if (patientDTO.getPhone() != null)
            patient.setPhone(patientDTO.getPhone());

        return new PatientDTO(patientRepository.save(patient));
    }

    @Transactional
    public void deletePatient(String id) {
        logger.info("Deleting patient with id {}", id);
        if (!patientRepository.existsById(id)) {
            throw new PatientNotFoundException(id);
        }
        patientRepository.deleteById(id);
    }
}
