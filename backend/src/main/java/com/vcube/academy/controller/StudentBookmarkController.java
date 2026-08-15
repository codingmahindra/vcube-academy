package com.vcube.academy.controller;

import com.vcube.academy.dto.bookmark.BookmarkCreateRequest;
import com.vcube.academy.dto.bookmark.BookmarkDto;
import com.vcube.academy.enums.BookmarkItemType;
import com.vcube.academy.security.UserPrincipal;
import com.vcube.academy.service.BookmarkService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/student/bookmarks")
@RequiredArgsConstructor
@PreAuthorize("hasRole('STUDENT')")
public class StudentBookmarkController {

    private final BookmarkService bookmarkService;

    @GetMapping
    public ResponseEntity<List<BookmarkDto>> getBookmarks(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(required = false) BookmarkItemType itemType) {
        List<BookmarkDto> bookmarks = bookmarkService.getStudentBookmarks(principal.getId(), itemType);
        return ResponseEntity.ok(bookmarks);
    }

    @PostMapping
    public ResponseEntity<BookmarkDto> addBookmark(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody BookmarkCreateRequest req) {
        BookmarkDto dto = bookmarkService.addBookmark(principal.getId(), req);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{itemType}/{itemId}")
    public ResponseEntity<Void> removeBookmark(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable BookmarkItemType itemType,
            @PathVariable Long itemId) {
        bookmarkService.removeBookmark(principal.getId(), itemType, itemId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/check/{itemType}/{itemId}")
    public ResponseEntity<Boolean> isBookmarked(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable BookmarkItemType itemType,
            @PathVariable Long itemId) {
        boolean bookmarked = bookmarkService.isBookmarked(principal.getId(), itemType, itemId);
        return ResponseEntity.ok(bookmarked);
    }
}
