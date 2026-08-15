package com.vcube.academy.service;

import com.vcube.academy.dto.bookmark.BookmarkCreateRequest;
import com.vcube.academy.dto.bookmark.BookmarkDto;
import com.vcube.academy.entity.StudentBookmark;
import com.vcube.academy.enums.BookmarkItemType;
import com.vcube.academy.repository.StudentBookmarkRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookmarkService {

    private final StudentBookmarkRepository bookmarkRepository;

    @Transactional(readOnly = true)
    public List<BookmarkDto> getStudentBookmarks(Long studentId, BookmarkItemType itemType) {
        List<StudentBookmark> bookmarks;
        if (itemType != null) {
            bookmarks = bookmarkRepository.findByStudentIdAndItemTypeOrderByCreatedAtDesc(studentId, itemType);
        } else {
            bookmarks = bookmarkRepository.findByStudentIdOrderByCreatedAtDesc(studentId);
        }

        return bookmarks.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public BookmarkDto addBookmark(Long studentId, BookmarkCreateRequest req) {
        return bookmarkRepository.findByStudentIdAndItemTypeAndItemId(studentId, req.getItemType(), req.getItemId())
                .map(this::toDto)
                .orElseGet(() -> {
                    StudentBookmark bookmark = StudentBookmark.builder()
                            .studentId(studentId)
                            .itemType(req.getItemType())
                            .itemId(req.getItemId())
                            .itemTitle(req.getItemTitle())
                            .itemSubtitle(req.getItemSubtitle())
                            .itemRoute(req.getItemRoute())
                            .createdAt(LocalDateTime.now())
                            .build();
                    StudentBookmark saved = bookmarkRepository.save(bookmark);
                    log.info("Student {} bookmarked {} #{}", studentId, req.getItemType(), req.getItemId());
                    return toDto(saved);
                });
    }

    @Transactional
    public void removeBookmark(Long studentId, BookmarkItemType itemType, Long itemId) {
        bookmarkRepository.deleteByStudentIdAndItemTypeAndItemId(studentId, itemType, itemId);
        log.info("Student {} removed bookmark {} #{}", studentId, itemType, itemId);
    }

    @Transactional(readOnly = true)
    public boolean isBookmarked(Long studentId, BookmarkItemType itemType, Long itemId) {
        return bookmarkRepository.existsByStudentIdAndItemTypeAndItemId(studentId, itemType, itemId);
    }

    private BookmarkDto toDto(StudentBookmark entity) {
        return BookmarkDto.builder()
                .id(entity.getId())
                .itemType(entity.getItemType().name())
                .itemId(entity.getItemId())
                .itemTitle(entity.getItemTitle())
                .itemSubtitle(entity.getItemSubtitle())
                .itemRoute(entity.getItemRoute())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
