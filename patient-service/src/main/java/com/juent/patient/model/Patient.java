package com.juent.patient.model;

import com.juent.patient.enums.GenderEnum;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor // Only used for tests
@EqualsAndHashCode(of = "patientId")
@Generated
@Document(collection = "patients")
public class Patient {
    @Id
    private String patientId;
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private GenderEnum gender;
    private String address;
    private String phone;
}
