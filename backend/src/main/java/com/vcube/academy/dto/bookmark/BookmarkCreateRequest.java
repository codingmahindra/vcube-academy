package com.vcube.academy.dto.bookmark;

import com.vcube.academy.enums.BookmarkItemType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookmarkCreateRequest {
    @NotNull(message = "Item type is required")
    private BookmarkItemType itemType;

    @NotNull(message = "Item ID is required")
    private Long itemId;

    @NotBlank(message = "Item title is required")
    private String itemTitle;

    private String itemSubtitle;

    @NotBlank(message = "Item route is required")
    private String itemRoute;
}
