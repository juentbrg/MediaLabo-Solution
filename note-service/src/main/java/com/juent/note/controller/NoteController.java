package com.juent.note.controller;


import com.juent.note.DTO.NoteDTO;
import com.juent.note.model.Note;
import com.juent.note.service.NoteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/note", produces = "application/json")
public class NoteController {
    private static final Logger logger = LoggerFactory.getLogger(NoteController.class);

    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @GetMapping
    public ResponseEntity<List<Note>> getAllNotes() {
        logger.info("fetching all notes");
        List<Note> notes = noteService.findAllNotes();

        if (notes.isEmpty()) {
            logger.warn("No notes found");
            return ResponseEntity.notFound().build();
        }

        logger.info("found {} notes", notes.size());
        return ResponseEntity.ok(notes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<NoteDTO> getNoteById(@PathVariable String id) {
        logger.info("fetching note with id {}", id);
        NoteDTO noteDTO = noteService.findNoteById(id);

        if (null == noteDTO) {
            logger.warn("Note not found for id {}", id);
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(noteDTO);
    }

    @PostMapping("/insert")
    public ResponseEntity<NoteDTO> insertNote(@RequestBody NoteDTO noteDTO) {
        logger.info("Creating new note {}", noteDTO);
        NoteDTO createdNote = noteService.saveNote(noteDTO);
        logger.info("Note created successfully {}", createdNote);
        return ResponseEntity.ok(noteDTO);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<NoteDTO> updateNote(@RequestBody NoteDTO noteDTO, @PathVariable String id) {
        logger.info("updating note with id {}", id);
        NoteDTO updatedNote = noteService.updateNote(noteDTO, id);
        logger.info("Note with id {} updated successfully", id);
        return ResponseEntity.ok(updatedNote);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteNoteById(@PathVariable String id) {
        logger.info("deleting note with id {}", id);
        noteService.deleteNoteById(id);
        logger.info("Note with id {} deleted successfully", id);
        return ResponseEntity.noContent().build();
    }
}
