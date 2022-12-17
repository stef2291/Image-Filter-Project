package com.imageProcessor.imageProcessor.file;

import com.imageProcessor.imageProcessor.storage.StorageService;
import com.imageProcessor.imageProcessor.user.User;
import com.imageProcessor.imageProcessor.user.UserService;
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
    private final UserService userService;
    private final FileRepository fileRepository;

    @Autowired
    public FileService(StorageService storageService, FileRepository fileRepository, UserService userService){//we also need the userService eventually
        this.storageService = storageService;
        this.fileRepository = fileRepository;
        this.userService = userService;
    }
    public String saveFile(MultipartFile file, Long userId){
        //first check that user exists (to be implemented)
        Optional<User> userExists = userService.findById(userId);

        if(userExists.isPresent()){
            String filePath = storageService.store(file); //returns file url as a string (I think?)
            String filename = file.getOriginalFilename(); //this is the alternative, as the start of the url should be the same

            File newFile = new File(filename, filePath, userExists.get()); //we will also associate with the user here once implemented

            fileRepository.save(newFile);

            return filePath;
        }else{
            //user not found/validation failed, return an error
            throw new ResponseStatusException( HttpStatus.NOT_FOUND, "User not found");
        }


    }

    public List<File> getAllUserFiles(Long userId){
        //when user is implemented, we want to do some checks here
        Optional<User> userExists = userService.findById(userId);

        if(userExists.isPresent()){
            return fileRepository.findAllByUserId(userId); //when user implemented it will be findAllByUserId(userId)
        }else{
            //user not found/validation failed, return an error
            throw new ResponseStatusException( HttpStatus.NOT_FOUND, "User not found");
        }
    }

    public File getOneUserFile(Long userId, Long fileId){
        Optional<User> userExists = userService.findById(userId);

        if(userExists.isPresent()){
            Optional<File> foundFile =  fileRepository.findByIdAndUserId(fileId, userId); //when user implemented it will be findAllByUserId(userId)
            if(foundFile.isPresent()) return foundFile.get(); //We should do a check if the file exists as well, this can throw an exception
            else throw new ResponseStatusException( HttpStatus.NOT_FOUND, "File not found");
        }else{
            //user not found/validation failed, return an error
            throw new ResponseStatusException( HttpStatus.NOT_FOUND, "User not found");
        }
    }

    public File getOneUserFileByName(Long userId, String filename){
        Optional<User> userExists = userService.findById(userId);

        if(userExists.isPresent()){
            Optional<File> foundFile =  fileRepository.findByFilename(filename);//fileRepository.findByUserIdAndFilename(userId, filename) when implemented
            if(foundFile.isPresent()) return foundFile.get(); //We should do a check if the file exists as well, this can throw an exception
            else throw new ResponseStatusException( HttpStatus.NOT_FOUND, "File not found");
        }else{
            //user not found/validation failed, return an error
            throw new ResponseStatusException( HttpStatus.NOT_FOUND, "User not found");
        }
    }

    public Boolean deleteFileFromDatabase(File file){
        fileRepository.delete(file);
        return true;
    }
}
