package com.coachpad.infrastructure.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Slf4j
@Service
public class FileStorageService {

    public static final String DIR_IMAGE = "image";
    public static final String DIR_FILE = "file";

    private final Path rootLocation = Paths.get("uploads");

    public FileStorageService() {
        try {
            Files.createDirectories(rootLocation);
            Files.createDirectories(rootLocation.resolve(DIR_IMAGE));
            Files.createDirectories(rootLocation.resolve(DIR_FILE));
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize storage", e);
        }
    }

    /**
     * Stores a file in the specified directory under the root 'uploads' folder.
     * 
     * @param file The multipart file to store
     * @param directory The subdirectory (e.g., "image", "file")
     * @return The relative path to the stored file
     */
    public String storeFile(MultipartFile file, String directory) {
        String filename = StringUtils.cleanPath(file.getOriginalFilename());
        try {
            if (file.isEmpty()) {
                throw new RuntimeException("Failed to store empty file " + filename);
            }
            if (filename.contains("..")) {
                // This is a security check
                throw new RuntimeException("Cannot store file with relative path outside current directory " + filename);
            }

            String extension = "";
            int i = filename.lastIndexOf('.');
            if (i > 0) {
                extension = filename.substring(i);
            }

            String newFilename = UUID.randomUUID().toString() + extension;
            Path targetLocation = this.rootLocation.resolve(directory).resolve(newFilename);
            
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            
            // Return the relative path for the frontend (e.g., "/uploads/image/abc.png")
            return "/uploads/" + directory + "/" + newFilename;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file " + filename, e);
        }
    }

    /**
     * Deletes a file from storage.
     * 
     * @param fileUrl The relative URL of the file to delete (e.g., "/uploads/image/abc.png")
     */
    public void deleteFile(String fileUrl) {
        if (fileUrl == null || !fileUrl.startsWith("/uploads/")) {
            return;
        }
        
        try {
            String relativePath = fileUrl.substring("/uploads/".length());
            Path filePath = this.rootLocation.resolve(relativePath);
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            // Log but don't throw, deletion failure is non-critical for the main flow
            log.warn("Could not delete file: {}", fileUrl);
        }
    }
}
