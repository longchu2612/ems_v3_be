package com.ems.application.dto.file;

import java.text.SimpleDateFormat;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.RandomStringUtils;

import com.ems.application.util.HashIdsUtils;

@Getter
@Setter
public class FileRequest {

    private long id;
    private String url;
    private String fullPath;
    private String fullPathWithoutFile;
    private String realName;
    private String name;
    private long[] paths = new long[7];

    public void init(String fileName, HashIdsUtils hashIdsUtils) {
        this.name = fileName;
        int pos = fileName.lastIndexOf('.');
        pos = pos > 0 ? pos : fileName.length();
        String strPath = fileName.substring(0, pos);
        this.paths = hashIdsUtils.decodeFileName(strPath);
        if (this.paths != null && this.paths.length >= 7) {
            this.id = Long.parseLong(String.format("%014d%05d", this.paths[5], this.paths[6]));
            this.setRealName(String.format("%d%s", this.id, fileName.substring(pos)));
            this.setFullPath();
        }
    }

    public static FileRequest setFileNameByUserAndExtension(
            int userId, String ext, HashIdsUtils hashIdsUtils) {
        FileRequest req = new FileRequest();
        String fileId1 = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String fileId0 = RandomStringUtils.randomNumeric(5);
        req.setId(Long.parseLong(fileId1 + fileId0));
        req.setName(req.getId() + ext);

        req.paths[0] = Integer.parseInt(RandomStringUtils.randomNumeric(3));
        int tmp = userId;
        for (int i = 1; i < 5; ++i) {
            req.paths[i] = tmp % 1000;
            tmp = (int) Math.floor((double) tmp / 1000);
        }
        req.paths[5] = Long.parseLong(fileId1);
        req.paths[6] = Long.parseLong(fileId0);
        req.setName(hashIdsUtils.encodeFileName(req.paths) + ext);
        req.setRealName(fileId1 + fileId0 + ext);
        req.setFullPath();
        return req;
    }

    private void setFullPath() {
        this.fullPathWithoutFile = "";
        for (int i = 0; i < 5; ++i) {
            this.fullPathWithoutFile = String.format("%03d/%s", this.paths[i], this.fullPathWithoutFile);
        }
        this.fullPath = String.format("%s%s", this.fullPathWithoutFile, this.realName);
    }
}
