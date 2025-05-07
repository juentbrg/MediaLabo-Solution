package com.juent.patient.exception;

public class PatientNotFoundException extends RuntimeException {
    public PatientNotFoundException(String id) {
        super("Patient with id " + id + " not found.");
    }
}
