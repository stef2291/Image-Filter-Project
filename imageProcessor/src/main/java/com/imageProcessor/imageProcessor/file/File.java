package com.imageProcessor.imageProcessor.file;

import com.imageProcessor.imageProcessor.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;

@Entity
@Table
public class File {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotEmpty
    private String filename;

    @NotEmpty
    private String filepath;


    @ManyToOne //this will be added once we have a user class
    User user;


    public File() {
    }

    public File(Long id, String filename, String filepath,  User user) {
        this.id = id;
        this.filename = filename;
        this.filepath = filepath;
        this.user = user;
    }

    public File(String filename, String filepath,  User user) {
        this.filename = filename;
        this.filepath = filepath;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getFilepath() {
        return filepath;
    }

    public User getUser() {
        return user;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }
}
