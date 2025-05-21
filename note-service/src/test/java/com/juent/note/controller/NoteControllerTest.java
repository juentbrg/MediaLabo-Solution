package com.juent.note.controller;

import com.juent.note.DTO.NoteDTO;
import com.juent.note.model.Note;
import com.juent.note.service.NoteService;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.*;

public class NoteControllerTest {

    private AutoCloseable mock;

    @InjectMocks
    private NoteController noteController;

    @Mock
    private NoteService noteService;

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
    public void getAllNotes_shouldReturnListOfNotes() {
        when(noteService.findAllNotes()).thenReturn(List.of(note));

        ResponseEntity<List<Note>> response = noteController.getAllNotes();

        assertEquals(OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(noteService, times(1)).findAllNotes();
    }

    @Test
    public void getAllNotes_shouldReturnNotFoundWhenNoNotes() {
        when(noteService.findAllNotes()).thenReturn(List.of());

        ResponseEntity<List<Note>> response = noteController.getAllNotes();

        assertEquals(NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(noteService, times(1)).findAllNotes();
    }

    @Test
    public void getAllNoteByPatId_shouldReturnNote() {
        when(noteService.findAllNoteByPatId("1")).thenReturn(List.of(note));

        ResponseEntity<List<Note>> response = noteController.getAllNoteByPatId("1");

        assertEquals(OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("12345", response.getBody().getFirst().getPatId());
        verify(noteService, times(1)).findAllNoteByPatId("1");
    }

    @Test
    public void insertNote_shouldReturnCreatedNote() {
        when(noteService.saveNote(noteDTO)).thenReturn(noteDTO);

        ResponseEntity<NoteDTO> response = noteController.insertNote(noteDTO);

        assertEquals(OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Test note", response.getBody().getNote());
        verify(noteService, times(1)).saveNote(noteDTO);
    }

    @Test
    public void updateNote_shouldReturnUpdatedNote() {
        when(noteService.updateNote(noteDTO, "1")).thenReturn(noteDTO);

        ResponseEntity<NoteDTO> response = noteController.updateNote(noteDTO, "1");

        assertEquals(OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(noteService, times(1)).updateNote(noteDTO, "1");
    }

    @Test
    public void deleteNoteById_shouldReturnNoContent() {
        doNothing().when(noteService).deleteNoteById("1");

        ResponseEntity<Void> response = noteController.deleteNoteById("1");

        assertEquals(NO_CONTENT, response.getStatusCode());
        verify(noteService, times(1)).deleteNoteById("1");
    }
}
