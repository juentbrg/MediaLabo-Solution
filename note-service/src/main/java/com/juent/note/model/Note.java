package com.juent.note.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor // Only used for tests
@EqualsAndHashCode(of = "id")
@Generated
@Document(collection = "notes")
public class Note {
    @Id
    private String id;
    private String patId;
    private String patient;
    private String note;
}
