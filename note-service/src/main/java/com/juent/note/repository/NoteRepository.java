package com.juent.note.repository;

import com.juent.note.model.Note;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface NoteRepository extends MongoRepository<Note, String> {
    List<Note> findAllByPatId(String id);
}

