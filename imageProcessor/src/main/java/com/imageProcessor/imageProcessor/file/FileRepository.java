package com.imageProcessor.imageProcessor.file;

import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface FileRepository extends JpaRepository<File, Long> {

}
