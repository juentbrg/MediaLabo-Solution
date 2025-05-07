package com.juent.note.service;

import com.juent.note.DTO.NoteDTO;
import com.juent.note.exception.NoteNotFoundException;
import com.juent.note.model.Note;
import com.juent.note.repository.NoteRepository;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class NoteServiceTest {

    private AutoCloseable mock;

    @InjectMocks
    private NoteService noteService;

    @Mock
    private NoteRepository noteRepository;

    private Note note;
    private NoteDTO noteDTO;

    @BeforeEach
    public void init() {
        mock = MockitoAnnotations.openMocks(this);

        note = new Note();
        note.setId("1");
        note.setPatId("12345");
        note.setPatient("John Doe");
        note.setNote("Test note");

        noteDTO = new NoteDTO();
        noteDTO.setPatId("12345");
        noteDTO.setPatient("John Doe");
        noteDTO.setNote("Test note");
    }

    @AfterEach
    public void close() throws Exception {
        if (mock != null) {
            mock.close();
        }
    }

    @Test
    public void findAllNotes_shouldReturnListOfNotes() {
        when(noteRepository.findAll()).thenReturn(List.of(note));

        List<Note> notes = noteService.findAllNotes();

        assertEquals(1, notes.size());
        assertEquals("12345", notes.getFirst().getPatId());
        verify(noteRepository, times(1)).findAll();
    }

    @Test
    public void findNoteById_shouldReturnNoteDTO() {
        when(noteRepository.findById("1")).thenReturn(Optional.of(note));

        NoteDTO result = noteService.findNoteById("1");

        assertNotNull(result);
        assertEquals("12345", result.getPatId());
        verify(noteRepository, times(1)).findById("1");
    }

    @Test
    public void findNoteById_shouldThrowExceptionIfNotFound() {
        when(noteRepository.findById("99")).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            noteService.findNoteById("99");
        });

        assertEquals("Note not found for id 99", exception.getMessage());
        verify(noteRepository, times(1)).findById("99");
    }

    @Test
    public void saveNote_shouldReturnSavedNoteDTO() {
        when(noteRepository.save(any(Note.class))).thenReturn(note);

        NoteDTO savedNote = noteService.saveNote(noteDTO);

        assertNotNull(savedNote);
        assertEquals("12345", savedNote.getPatId());
        verify(noteRepository, times(2)).save(any(Note.class));
    }

    @Test
    public void saveNote_shouldThrowExceptionIfNull() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            noteService.saveNote(null);
        });

        assertEquals("Note cannot be null", exception.getMessage());
    }

    @Test
    public void updateNote_shouldUpdateAndReturnNoteDTO() {
        when(noteRepository.findById("1")).thenReturn(Optional.of(note));
        when(noteRepository.save(any(Note.class))).thenReturn(note);

        NoteDTO updated = noteService.updateNote(noteDTO, "1");

        assertNotNull(updated);
        assertEquals("Test note", updated.getNote());
        verify(noteRepository, times(1)).findById("1");
        verify(noteRepository, times(1)).save(any(Note.class));
    }

    @Test
    public void updateNote_shouldThrowExceptionIfNoteNotFound() {
        when(noteRepository.findById("99")).thenReturn(Optional.empty());

        Exception exception = assertThrows(NoteNotFoundException.class, () -> {
            noteService.updateNote(noteDTO, "99");
        });

        assertEquals("Note with id 99 not found.", exception.getMessage());
        verify(noteRepository, times(1)).findById("99");
    }

    @Test
    public void deleteNoteById_shouldDeleteNote() {
        when(noteRepository.existsById("1")).thenReturn(true);
        doNothing().when(noteRepository).deleteById("1");

        noteService.deleteNoteById("1");

        verify(noteRepository, times(1)).existsById("1");
        verify(noteRepository, times(1)).deleteById("1");
    }

    @Test
    public void deleteNoteById_shouldThrowExceptionIfNotFound() {
        when(noteRepository.existsById("99")).thenReturn(false);

        Exception exception = assertThrows(NoteNotFoundException.class, () -> {
            noteService.deleteNoteById("99");
        });

        assertEquals("Note with id 99 not found.", exception.getMessage());
        verify(noteRepository, times(1)).existsById("99");
    }
}
