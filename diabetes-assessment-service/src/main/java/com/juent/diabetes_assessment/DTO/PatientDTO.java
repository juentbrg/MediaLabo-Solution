package com.juent.diabetes_assessment.DTO;

import com.juent.diabetes_assessment.enums.GenderEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Generated;
import lombok.NoArgsConstructor;

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
}
