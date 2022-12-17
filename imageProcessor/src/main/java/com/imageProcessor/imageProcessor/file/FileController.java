package com.imageProcessor.imageProcessor.file;

import com.imageProcessor.imageProcessor.grayscale.Grayscale;
import com.imageProcessor.imageProcessor.storage.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/files")
@CrossOrigin
public class FileController {
    private final FileService fileService;
    private final StorageService storageService;
    private final RestTemplate restTemplate;//so we can do POSTs to C++


    @Autowired
    public FileController(FileService fileService, StorageService storageService, RestTemplate restTemplate){
        this.fileService = fileService;
        this.storageService = storageService;
        this.restTemplate = restTemplate;
    }

    //Get all images DB ENTRY of user
    @GetMapping()
    public List<File> getAllUserFiles(@RequestBody Map<String, Long> request){
        return fileService.getAllUserFiles(request.get("userId"));
    }

    //get one image DB ENTRY
    @GetMapping("/file")
    public File getOneFile(@RequestBody Map<String, Long> request){
        return fileService.getOneUserFile(request.get("userId"), request.get("fileId"));
    }

    //post an image
    @PostMapping("/{userId}") //Here we need to do the extra step of creating a new file model in database
    public ResponseEntity<Resource> handleFileUpload(@RequestBody MultipartFile file,
                                   RedirectAttributes redirectAttributes,
                                   @PathVariable (value = "userId") Long userId) throws IOException {
        String filepath = fileService.saveFile(file, userId);
//        storageService.store(file); //do this in the fileService - makes more sense
        redirectAttributes.addFlashAttribute("message",
                "You successfully uploaded " + file.getOriginalFilename() + "!");

//        Grayscale.createGrayscale(filepath);

        Resource fileToSend = storageService.loadAsResource(Grayscale.createGrayscale(filepath).getName());
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + fileToSend.getFilename() + "\"").body(fileToSend);

//            return "success!!";
    }

    //delete an image

    //Get one IMAGE FILE
    @GetMapping("/file/{userId}/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable Long  userId, @PathVariable String filename) {

        //confirm file exists, confirm it is associated with user
        File fileToSend = fileService.getOneUserFileByName(userId, filename);

        Resource file = storageService.loadAsResource(fileToSend.getFilename());
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);

    }

    //Delete one file
    @DeleteMapping("/file/{userId}/{filename:.+}")
    public Boolean deleteOneFile(@PathVariable Long  userId, @PathVariable String filename) throws IOException {
        File fileToDelete = fileService.getOneUserFileByName(userId, filename);


        storageService.deleteOne(storageService.load(filename));

        return true;
    }

    //Send the image to be turned black and white, store the image and add entry in DB
    //This is a post request we are MAKING, not receiving
    public ResponseEntity<Resource> postBlackAndWhiteImage(String filename, Long userId) {
        String url = "https://localhost:5001/files/testing"; //TBC with Olly

        // create headers
        HttpHeaders headers = new HttpHeaders();
        // set `content-type` header
//        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
//        headers.setAccept(Collections.singletonList(MediaType.MULTIPART_FORM_DATA));

        Resource file = storageService.loadAsResource(filename); //load the resource to send
        //Create a key value pair in the form of a map that we can send to be processed
        MultiValueMap<String, Resource> dataToPost = new LinkedMultiValueMap<>();
        dataToPost.add("file", file);

        HttpEntity<MultiValueMap<String, Resource>> entity = new HttpEntity<>(dataToPost, headers);

        //send and wait for the response
        ResponseEntity<Resource> response = this.restTemplate.postForEntity(url, entity, Resource.class);

//        fileService.saveFile(response.getBody(), userId); //save the black and white file
        return response; //return the black and white file, so we can send it back to the user
    }

    @PostMapping("/testing") //Here we need to do the extra step of creating a new file model in database
    public ResponseEntity<Resource> testUploadCPP(@RequestBody MultipartFile file,
                                   RedirectAttributes redirectAttributes)
                                    {

        fileService.saveFile(file, 1L);
//        storageService.store(file); //do this in the fileService - makes more sense
        redirectAttributes.addFlashAttribute("message",
                "You successfully uploaded " + file.getOriginalFilename() + "!");

        File fileToSend = fileService.getOneUserFileByName(1L, file.getOriginalFilename());

        Resource fileToRespond = storageService.loadAsResource(fileToSend.getFilename());
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + fileToRespond.getFilename() + "\"").body(fileToRespond);
    }
}
