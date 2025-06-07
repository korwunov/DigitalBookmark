package com.BookmarkService.web.dto.response;

import com.BookmarkService.domain.EROLE;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShortenUserData {
    public Long id;
    public String userName;
    public String name;
    public EROLE role;

}
