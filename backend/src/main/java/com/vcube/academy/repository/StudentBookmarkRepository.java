package com.vcube.academy.repository;

import com.vcube.academy.entity.StudentBookmark;
import com.vcube.academy.enums.BookmarkItemType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentBookmarkRepository extends JpaRepository<StudentBookmark, Long> {
    List<StudentBookmark> findByStudentIdOrderByCreatedAtDesc(Long studentId);
    List<StudentBookmark> findByStudentIdAndItemTypeOrderByCreatedAtDesc(Long studentId, BookmarkItemType itemType);
    Optional<StudentBookmark> findByStudentIdAndItemTypeAndItemId(Long studentId, BookmarkItemType itemType, Long itemId);
    boolean existsByStudentIdAndItemTypeAndItemId(Long studentId, BookmarkItemType itemType, Long itemId);
    void deleteByStudentIdAndItemTypeAndItemId(Long studentId, BookmarkItemType itemType, Long itemId);
}
