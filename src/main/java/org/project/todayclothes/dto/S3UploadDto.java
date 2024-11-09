package org.project.todayclothes.dto;

import lombok.Getter;

import java.io.InputStream;

@Getter
public class S3UploadDto {
    private final InputStream inputStream;
    private final long contentLength;
    private final String contentType;

    public S3UploadDto(InputStream inputStream, long contentLength, String contentType) {
        this.inputStream = inputStream;
        this.contentLength = contentLength;
        this.contentType = contentType;
    }
}
