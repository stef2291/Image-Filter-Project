package com.imageProcessor.imageProcessor.file;

import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileRepository extends JpaRepository<File, Long> {
//    Optional<File> findByUserIdAndFilename(Long userId, String filename);
    Optional<File> findByFilename(String filename);

    List<File> findAllByUserId(Long userId);

    Optional<File> findByIdAndUserId(Long fileId, Long userId);
}
