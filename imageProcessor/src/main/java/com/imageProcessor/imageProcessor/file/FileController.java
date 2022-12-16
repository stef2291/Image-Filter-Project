package com.imageProcessor.imageProcessor.file;

import com.imageProcessor.imageProcessor.storage.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/files")
@CrossOrigin
public class FileController {
    private final FileService fileService;
    private final StorageService storageService;

    @Autowired
    public FileController(FileService fileService, StorageService storageService){
        this.fileService = fileService;
        this.storageService = storageService;
    }

    //Get all images DB ENTRY of user
    @GetMapping()
    public List<File> getAllUserFiles(@RequestBody Map<String, Long> request){
        return fileService.getAllUserFiles(request.get("userId"));
    }

    //get one image DB ENTRY
    @GetMapping("/file")
    public File getOneI(@RequestBody Map<String, Long> request){
        return fileService.getOneUserFile(request.get("userId"), request.get("fileId"));
    }

    //post an image
    @PostMapping("/{userId}") //Here we need to do the extra step of creating a new file model in database
    public String handleFileUpload(@RequestBody MultipartFile file,
                                   RedirectAttributes redirectAttributes,
                                   @PathVariable (value = "userId") Long userId) {
        fileService.saveFile(file, userId);
//        storageService.store(file); //do this in the fileService - makes more sense
        redirectAttributes.addFlashAttribute("message",
                "You successfully uploaded " + file.getOriginalFilename() + "!");

        return "redirect:/";
    }

    //delete an image

    //Get one IMAGE FILE
    @GetMapping("/files/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {

        Resource file = storageService.loadAsResource(filename);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);

    }
}
