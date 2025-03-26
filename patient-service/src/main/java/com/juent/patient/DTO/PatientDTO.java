package com.juent.patient.DTO;

import com.juent.patient.enums.GenderEnum;
import com.juent.patient.model.Patient;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Generated
public class PatientDTO {
    private String firstName;
    private String lastName;
    private String birthDate;
    private GenderEnum gender;
    private String address;
    private String phone;

    public PatientDTO(Patient patient) {
        this.firstName = patient.getFirstName();
        this.lastName = patient.getLastName();
        this.birthDate = String.valueOf(patient.getBirthDate());
        this.gender = patient.getGender();
        this.address = patient.getAddress();
        this.phone = patient.getPhone();
    }
}
