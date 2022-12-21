package com.imageProcessor.imageProcessor;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.imageProcessor.imageProcessor.file.File;
import com.imageProcessor.imageProcessor.file.FileService;
import com.imageProcessor.imageProcessor.user.User;
import com.imageProcessor.imageProcessor.user.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;

@SpringBootTest
@AutoConfigureMockMvc
public class FileServiceTest {

    @Autowired
    private FileService fileService;

    @Autowired
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testSaveFile() throws Exception {
        // Create a mock MultipartFile to simulate the file being uploaded
        String filepath = "/Users/eps09/Downloads/cat.jpeg";
        FileInputStream fis = new FileInputStream(filepath);
        MockMultipartFile file = new MockMultipartFile("test.txt", "test.txt", "form-data", fis);

        //we need to create a user first in order to test, as user must exist to upload a file
        User createdUser = new User("teddyp", "rubbishpass", "teddy@teddy.com");
        userService.createUser(createdUser);

        // Send a POST request to the /files endpoint with the mock file
        mockMvc.perform(multipart("/files/1")
                        .file(file)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(content().string("/files/test.txt"));

        // Verify that the file was saved and the user was associated with it
        File savedFile = fileService.getOneUserFileByName(1L, "test.txt");
        assertEquals(savedFile.getFilename(),"test.txt");
        assertEquals(savedFile.getUser().getId(),1L);
    }

    @Test
    public void testGetAllUserFiles() throws Exception {
        // Send a GET request to the /files endpoint with a user ID
        mockMvc.perform(get("/files")
                        .param("userId", "1"))
// Expect a list of files to be returned
                .andExpect(status().isOk())
                .andExpect(content().json("[{filename: 'test.txt', filepath: '/files/test.txt'}]"));
    }

    @Test
    public void testGetOneUserFile() throws Exception {
        // Save a test file
        String file = fileService.saveFile(mock(MultipartFile.class), 1L);

        // Send a GET request to the /files/{fileId} endpoint with a user ID and file ID
        mockMvc.perform(get("/files/{fileId}", 1L)
                        .param("userId", "1"))
                // Expect the file to be returned
                .andExpect(status().isOk())
                .andExpect(content().json("{filename: 'test.txt', filepath: '/files/test.txt'}"));
    }

    @Test
    public void testGetOneUserFileByName() throws Exception {
        // Save a test file
        String file = fileService.saveFile(mock(MultipartFile.class), 1L);

        // Send a GET request to the /files/{filename} endpoint with a user ID and file name
        mockMvc.perform(get("/files/{filename}", file)
                        .param("userId", "1"))
                // Expect the file to be returned
                .andExpect(status().isOk())
                .andExpect(content().json("{filename: 'test.txt', filepath: '/files/test.txt'}"));
    }

    @Test
    public void testDeleteFile() throws Exception {
        // Save a test file
        String file = fileService.saveFile(mock(MultipartFile.class), 1L);

        // Send a DELETE request to the /files/{fileId} endpoint with a user ID and file ID
        mockMvc.perform(delete("/files/{fileId}", 1L)
                        .param("userId", "1"))
                // Expect a success response
                .andExpect(status().isOk());

        // Verify that the file was deleted
        assertEquals(fileService.getOneUserFile(1L, 1L), null);
    }
}
