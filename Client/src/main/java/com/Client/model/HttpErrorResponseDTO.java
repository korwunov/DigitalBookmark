package com.Client.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HttpErrorResponseDTO {
    public String timestamp;
    public int status;
    public String error;
    public String message;
    public String path;
}
