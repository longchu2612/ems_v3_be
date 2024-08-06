package com.ems.application.controller.file;

import java.util.Objects;

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ems.application.dto.file.FileResponse;
import com.ems.application.enums.ErrorMessageType;
import com.ems.application.exception.AppValidationException;
import com.ems.application.service.file.FileService;
import com.ems.config.MsgTranslator;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(tags = "File")
@RestController
@RequestMapping(value = "/api/file")
public class FileController {

    private final FileService fileService;

    FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @ApiOperation(value = "Upload file")

    @PostMapping(value = "/upload")
    public FileResponse uploadFile(@RequestParam("file") MultipartFile file) {
        if (Objects.isNull(file) || file.isEmpty()) {

            throw new AppValidationException(MsgTranslator.toLocale("file.notFound"),
                    ErrorMessageType.SHOW_IN_TITLE.getValue());

        }
        String fileType = file.getContentType().toLowerCase();
        if (!(fileType.equals("image/jpg")
                || fileType.equals("image/jpeg")
                || fileType.equals("image/png")
                || fileType.equals("application/pdf"))) {

            throw new AppValidationException(MsgTranslator.toLocale("file.invalidType"),
                    ErrorMessageType.SHOW_IN_TITLE.getValue());

        }
        // Check file size (10MB max
        long fileSize = file.getSize();
        if (fileSize > 10485760) {

            throw new AppValidationException(
                    MsgTranslator.toLocale("file.invalidSize"), ErrorMessageType.SHOW_IN_TITLE.getValue());

        }
        return fileService.saveFile(file);
    }

    @ApiOperation(value = "View file")
    @GetMapping("/download/{fileName}")
    public ResponseEntity<Resource> downloadFile(
            @PathVariable("fileName") String fileName, HttpServletRequest request) {
        return fileService.load(false, fileName, request);
    }

    @ApiOperation(value = "View temporary file")

    @GetMapping("/download-temp/{fileName}")
    public ResponseEntity<Resource> downloadFileTemp(

            @PathVariable("fileName") String fileName, HttpServletRequest request) {
        return fileService.load(true, fileName, request);
    }
}
