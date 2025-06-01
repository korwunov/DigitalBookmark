package com.Client.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserEnabledRequest {
    public Long userId;
    public boolean isEnabled;
}
