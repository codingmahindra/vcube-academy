package com.vcube.academy.dto.bookmark;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookmarkDto {
    private Long id;
    private String itemType;
    private Long itemId;
    private String itemTitle;
    private String itemSubtitle;
    private String itemRoute;
    private LocalDateTime createdAt;
}
