package com.imageProcessor.imageProcessor.file;

import com.imageProcessor.imageProcessor.grayscale.Grayscale;
import com.imageProcessor.imageProcessor.storage.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

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
        //returns a list of DB entries belonging to the user ID, these can then be used to request served files
        return fileService.getAllUserFiles(request.get("userId"));
    }

    //get one image DB ENTRY
    @GetMapping("/file")
    public File getOneFile(@RequestBody Map<String, Long> request){
        //This returns the file id, filename, filepath and who the file belongs to
        return fileService.getOneUserFile(request.get("userId"), request.get("fileId"));
    }

    //post an image
    @PostMapping("/{userId}") //Here we need to do the extra step of creating a new file model in database
    public ResponseEntity<Resource> handleFileUpload(@RequestBody MultipartFile file,
                                   @PathVariable (value = "userId") Long userId) throws IOException {

        //Save the incoming file, and associate it with user
        //This will return an error if user doesn't exist
        String filepath = fileService.saveFile(file, userId);

        //This creates a grayscale image, saves it, then loads as a resource to send as response
        //Will change once C++ grayscale version is implemented
        Resource fileToSend = storageService.loadAsResource(Grayscale.createGrayscale(filepath).getName());
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + fileToSend.getFilename() + "\"").body(fileToSend);

    }

    //Get one IMAGE FILE
    @GetMapping("/file/{userId}/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable Long  userId, @PathVariable String filename) {

        //confirm file exists, confirm it is associated with user
        File fileToSend = fileService.getOneUserFileByName(userId, filename);

        //if file exists, then load it as a resource to send as response
        Resource file = storageService.loadAsResource(fileToSend.getFilename());
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);

    }

    //make image black and white using c++ executable
    @GetMapping("/file/grayscale/{userId}/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveBlackAndWhiteFile(@PathVariable Long  userId, @PathVariable String filename) throws IOException {

        //confirm file exists, confirm it is associated with user
        File fileToSend = fileService.getOneUserFileByName(userId, filename);

        Resource file = storageService.loadAsResource(fileToSend.getFilename());

        //Process runs the executable, giving the filename and image processing type
        //process will save the file, which we can then return to the frontend
        Process process = new ProcessBuilder("C:\\PathToExe\\MyExe.exe",fileToSend.getFilepath(),"greyscale").start();

        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);

    }


    //Delete one file
    @DeleteMapping("/file/{userId}/{filename:.+}")
    public Boolean deleteOneFile(@PathVariable Long  userId, @PathVariable String filename) throws IOException {
        //Check file exists
        File fileToDelete = fileService.getOneUserFileByName(userId, filename);
        //delete if it exists
        storageService.deleteOne(storageService.load(filename));

        return true;
    }

}
