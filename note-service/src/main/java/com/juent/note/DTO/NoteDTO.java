package com.juent.note.DTO;

import com.juent.note.model.Note;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Generated
public class NoteDTO {
    private String patId;
    private String patient;
    private String note;

    public NoteDTO(Note note) {
        this.patId = note.getPatId();
        this.patient = note.getPatient();
        this.note = note.getNote();
    }
}
