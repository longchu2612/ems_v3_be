package com.ems.application.service.file;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.ems.application.dto.file.FileRequest;
import com.ems.application.dto.file.FileResponse;
import com.ems.application.exception.FileStorageException;
import com.ems.application.exception.NotFoundException;
import com.ems.application.service.BaseService;
import com.ems.application.util.HashIdsUtils;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FileService extends BaseService {

    private final HashIdsUtils hashIdsUtils;

    public FileService(HashIdsUtils hashIdsUtils) {
        this.hashIdsUtils = hashIdsUtils;
    }

    @Value("${file.upload-dir-temp}")
    private String uploadDirTemp;

    @Value("${file.upload-dir}")
    private String uploadDir;

    private Path fileStorageLocationTemp;
    private Path fileStorageLocation;

    // Initializes file storage locations
    public void init() {
        this.fileStorageLocationTemp = Paths.get(uploadDirTemp).toAbsolutePath().normalize();
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileStorageLocationTemp);
            Files.createDirectories(this.fileStorageLocation);
        } catch (IOException e) {
            log.error("Could not initialize folder for upload!");
        }
    }

    // Checks if a file exists based on its URL
    public boolean isFileExisted(String url) {
        try {
            FileRequest fileRequest = new FileRequest();
            fileRequest.init(url, hashIdsUtils);
            File srcFile = this.fileStorageLocationTemp.resolve(fileRequest.getFullPath()).toFile();
            return srcFile.exists();
        } catch (Exception ex) {
            return false;
        }
    }

    // Saves a file to the file storage location
    public FileResponse saveFile(MultipartFile file) {
        Optional<String> extensionOptional = getExtension(file.getOriginalFilename());
        if (extensionOptional.isPresent()) {
            FileRequest fileNameReq = FileRequest.setFileNameByUserAndExtension(
                    getUser().getId(), extensionOptional.get(), hashIdsUtils);
            try {
                // Check if the file's name contains invalid characters
                if (fileNameReq.getFullPath().contains("..")) {
                    throw new FileStorageException(
                            "Sorry! Filename contains invalid path sequence " + fileNameReq.getFullPath());
                }

                // Copy file to the target location (Replacing existing file with the same name)
                Path dirPath = this.fileStorageLocation.resolve(fileNameReq.getFullPathWithoutFile());
                Files.createDirectories(dirPath);
                Path targetLocation = this.fileStorageLocation.resolve(fileNameReq.getFullPath());
                Files.copy(file.getInputStream(), targetLocation);
                String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                        .path("/api/file/download/")
                        .path(fileNameReq.getName())
                        .build()
                        .toUri()
                        .toString();
                return FileResponse.getResponse(
                        fileNameReq.getName(), fileDownloadUri, file.getContentType());
            } catch (Exception ex) {
                throw new FileStorageException(
                        "Could not store file " + fileNameReq.getName() + ". Please try again!", ex);
            }
        }

        return null;
    }

    // Copies files from the temporary folder to the main folder

    public List<String> copyFileTempToMainFolder(List<String> documents) {
        List<String> errors = new ArrayList<>();
        documents.forEach(
                document -> {
                    FileRequest fileRequest = new FileRequest();
                    fileRequest.init(document, hashIdsUtils);
                    try {
                        File srcFile = this.fileStorageLocationTemp.resolve(fileRequest.getFullPath()).toFile();
                        File destDir = this.fileStorageLocation.resolve(fileRequest.getFullPathWithoutFile()).toFile();
                        if (!srcFile.exists()) {
                            errors.add("copyFileTempToMainFolder - File not found");
                        }
                        FileUtils.copyFileToDirectory(srcFile, destDir);
                    } catch (Exception ex) {
                        errors.add("copyFileTempToMainFolder - " + document + ": " + ex.getMessage());
                    }
                });
        return errors;
    }

    // Loads a file for dowload
    public ResponseEntity<Resource> load(
            boolean isTempFile, String fileName, HttpServletRequest request) {
        FileRequest fileRequest = new FileRequest();
        fileRequest.init(fileName, hashIdsUtils);
        try {
            Path filePath;
            if (isTempFile) {
                filePath = this.fileStorageLocationTemp.resolve(fileRequest.getFullPath()).normalize();
            } else {
                filePath = this.fileStorageLocation.resolve(fileRequest.getFullPath()).normalize();
            }
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                String contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
                if (contentType == null) {
                    contentType = "application/octet-stream";
                }

                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .contentLength((int) resource.contentLength())
                        .header("Content-Disposition", "inline; filename=\"" + fileRequest.getName() + "\"")
                        .body(resource);
            } else {
                throw new NotFoundException("File not found " + fileName);
            }
        } catch (IOException ex) {
            throw new NotFoundException("File not found " + fileName, ex);
        }
    }

    // Retrieves the file extension from a filename
    private Optional<String> getExtension(String filename) {
        return Optional.ofNullable(filename)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(filename.lastIndexOf('.')));
    }
}
