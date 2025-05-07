package com.juent.patient.controller;

import com.juent.patient.DTO.PatientDTO;
import com.juent.patient.model.Patient;
import com.juent.patient.service.PatientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/patient", produces = "application/json;charset=UTF-8")
public class PatientController {
    private static final Logger logger = LoggerFactory.getLogger(PatientController.class);

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @GetMapping()
    public ResponseEntity<List<Patient>> getPatients() {
        logger.info("Fetching all patients");

        List<Patient> patients = patientService.findAllPatients();

        if (patients.isEmpty()) {
            logger.warn("No patients found");
            return ResponseEntity.noContent().build();
        }

        logger.info("Returning {} patients", patients.size());
        return ResponseEntity.ok(patients);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PatientDTO> getPatientById(@PathVariable String id) {
        logger.info("Fetching patient with id {}", id);
        PatientDTO patientDTO = patientService.findPatientById(id);

        if (null == patientDTO) {
            logger.warn("Patient not found for id {}", id);
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(patientDTO);
    }

    @PostMapping("/insert")
    public ResponseEntity<PatientDTO> createPatient(@RequestBody PatientDTO patientDTO) {
        logger.info("Creating new patient {}", patientDTO);
        PatientDTO createdPatient = patientService.savePatient(patientDTO);
        logger.info("Patient created successfully: {}", createdPatient);
        return ResponseEntity.ok(createdPatient);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<PatientDTO> updatePatient(@PathVariable String id, @RequestBody PatientDTO patientDTO) {
        logger.info("Updating patient with id {}", id);
        PatientDTO updatedPatient = patientService.updatePatient(patientDTO, id);
        logger.info("Patient with id {} updated successfully", id);
        return ResponseEntity.ok(updatedPatient);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deletePatient(@PathVariable String id) {
        logger.info("Deleting patient with id {}", id);
        patientService.deletePatient(id);
        logger.info("Patient with id {} deleted successfully", id);
        return ResponseEntity.noContent().build();
    }
}
