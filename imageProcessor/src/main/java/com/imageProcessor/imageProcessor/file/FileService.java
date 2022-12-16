package com.imageProcessor.imageProcessor.file;

import com.imageProcessor.imageProcessor.storage.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
@Service
public class FileService {
    private final StorageService storageService;
    private final FileRepository fileRepository;

    @Autowired
    public FileService(StorageService storageService, FileRepository fileRepository){//we also need the userService eventually
        this.storageService = storageService;
        this.fileRepository = fileRepository;
    }
    public String saveFile(MultipartFile file, Long userId){
        //first check that user exists (to be implemented)
        Boolean userExists = true;

        if(userExists){
            String filePath = storageService.store(file); //returns file url as a string (I think?)
            String filename = file.getOriginalFilename(); //this is the alternative, as the start of the url should be the same

            File newFile = new File(filename, filePath); //we will also associate with the user here once implemented

            fileRepository.save(newFile);
        }else{
            //user not found/validation failed, return an error
            throw new ResponseStatusException( HttpStatus.NOT_FOUND, "User not found");
        }

        return "success";
    }

    public List<File> getAllUserFiles(Long userId){
        //when user is implemented, we want to do some checks here
        Boolean userExists = true;

        if(userExists){
            return fileRepository.findAll(); //when user implemented it will be findAllByUserId(userId)
        }else{
            //user not found/validation failed, return an error
            throw new ResponseStatusException( HttpStatus.NOT_FOUND, "User not found");
        }
    }

    public File getOneUserFile(Long userId, Long fileId){
        Boolean userExists = true;

        if(userExists){
            Optional<File> foundFile =  fileRepository.findById(fileId); //when user implemented it will be findAllByUserId(userId)
            return foundFile.get(); //We should do a check if the file exists as well, this can throw an exception
        }else{
            //user not found/validation failed, return an error
            throw new ResponseStatusException( HttpStatus.NOT_FOUND, "User not found");
        }
    }
}
