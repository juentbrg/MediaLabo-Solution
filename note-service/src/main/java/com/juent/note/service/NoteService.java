package com.juent.note.service;

import com.juent.note.DTO.NoteDTO;
import com.juent.note.exception.NoteNotFoundException;
import com.juent.note.model.Note;
import com.juent.note.repository.NoteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NoteService {
    private static final Logger logger = LoggerFactory.getLogger(NoteService.class);

    private final NoteRepository noteRepository;

    public NoteService(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    @Transactional(readOnly = true)
    public List<Note> findAllNotes() {
        logger.info("fetching all notes");
        List<Note> notes = noteRepository.findAll();
        logger.info("found {} notes", notes.size());
        return notes;
    }

    @Transactional(readOnly = true)
    public NoteDTO findNoteById(String id) {
        logger.info("fetching note with id {}", id);
        return noteRepository.findById(id)
                .map(NoteDTO::new)
                .orElseThrow(() -> new IllegalArgumentException("Note not found for id " + id));
    }

    @Transactional
    public NoteDTO saveNote(NoteDTO noteDTO) {
        logger.info("saving note {}", noteDTO);
        if (null != noteDTO) {
            Note note = new Note();
            note.setPatId(noteDTO.getPatId());
            note.setPatient(noteDTO.getPatient());
            note.setNote(noteDTO.getNote());
            noteRepository.save(note);
            logger.info("saved note {}", note);
            return new NoteDTO(noteRepository.save(note));
        }
        throw new IllegalArgumentException("Note cannot be null");
    }

    @Transactional
    public NoteDTO updateNote(NoteDTO noteDTO, String id) {
        logger.info("updating note with id {}", id);
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new NoteNotFoundException(id));

        if (noteDTO.getNote() != null)
            note.setNote(noteDTO.getNote());

        return new NoteDTO(noteRepository.save(note));
    }

    @Transactional
    public void deleteNoteById(String id) {
        logger.info("deleting note with id {}", id);
        if (!noteRepository.existsById(id)) {
            throw new NoteNotFoundException(id);
        }
        noteRepository.deleteById(id);
    }
}
