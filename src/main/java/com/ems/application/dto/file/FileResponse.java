package com.ems.application.dto.file;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FileResponse implements Serializable {

    private String fileName;
    private String fileUrl;
    private String fileType;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String token;

    private String uploadedDate;

    public static FileResponse getResponse(String fileName, String fileUrl, String fileType) {
        FileResponse uploadRes = new FileResponse();
        uploadRes.setFileName(fileName);
        uploadRes.setFileUrl(fileUrl);
        uploadRes.setFileType(fileType);

        return uploadRes;
    }
}
