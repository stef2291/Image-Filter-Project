package com.imageProcessor.imageProcessor;

import com.imageProcessor.imageProcessor.file.FileController;
import com.imageProcessor.imageProcessor.file.FileRepository;
import com.imageProcessor.imageProcessor.file.FileService;
import com.imageProcessor.imageProcessor.grayscale.Grayscale;
import com.imageProcessor.imageProcessor.storage.StorageService;
import org.json.JSONObject;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
public class FileControllerTests {

    @Autowired
    FileController fileController;

    @MockBean
    FileService fileService;
    @MockBean
    Grayscale grayscale;

    @MockBean
    FileRepository fileRepository;

    @MockBean
    StorageService storageService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    //Test save file route - we're basically testing what we get back, assuming all the levels below accept our input as valid
    //There is A LOT of set up in terms of mocks, to ensure things like file storage don't crash
    @Test
    public void saveFileWithValidInput() throws Exception {
        //set up mock service layer to return good results
        String filepath = "/Users/eps09/Downloads/cat.jpeg";
        FileInputStream fis = new FileInputStream(filepath);
        MultipartFile multipartFile = new MockMultipartFile("file", "file", "form-data", fis);

        java.io.File mockReturnFile = new java.io.File(filepath);
        Resource resource = new UrlResource("http://google.com");

        Mockito.when(fileService.saveFile(multipartFile,1L)).thenReturn(filepath);
        Mockito.when(storageService.loadAsResource("cat_black_and_white.jpg")).thenReturn(resource);

        try (MockedStatic<Grayscale> utilities = Mockito.mockStatic(Grayscale.class)) {
            utilities.when(() -> Grayscale.createGrayscale(filepath))
                    .thenReturn(mockReturnFile);

        }

        mockMvc.perform(multipart("/files/1").file((MockMultipartFile) multipartFile))
                .andExpect(status().isOk());

    }

    //Test to check we get a 404 if user not recognised
    @Test
    public void saveFileUserNotFound() throws Exception {
        //set up mock service layer to return good results
        String filepath = "/Users/eps09/Downloads/cat.jpeg";
        FileInputStream fis = new FileInputStream(filepath);
        MultipartFile multipartFile = new MockMultipartFile("file", "file", "form-data", fis);

        java.io.File mockReturnFile = new java.io.File(filepath);
        Resource resource = new UrlResource("http://google.com");

        Mockito.when(fileService.saveFile(multipartFile,1L)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));
        Mockito.when(storageService.loadAsResource("cat_black_and_white.jpg")).thenReturn(resource);

        try (MockedStatic<Grayscale> utilities = Mockito.mockStatic(Grayscale.class)) {
            utilities.when(() -> Grayscale.createGrayscale(filepath))
                    .thenReturn(mockReturnFile);

        }

        mockMvc.perform(multipart("/files/1").file((MockMultipartFile) multipartFile))
                .andExpect(status().isNotFound());

    }
}
