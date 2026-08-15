package com.vcube.academy.entity;

import com.vcube.academy.enums.BookmarkItemType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "student_bookmarks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentBookmark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_id", nullable = false)
    private Long studentId;

    @Enumerated(EnumType.STRING)
    @Column(name = "item_type", nullable = false, length = 50)
    private BookmarkItemType itemType;

    @Column(name = "item_id", nullable = false)
    private Long itemId;

    @Column(name = "item_title", nullable = false)
    private String itemTitle;

    @Column(name = "item_subtitle")
    private String itemSubtitle;

    @Column(name = "item_route", nullable = false)
    private String itemRoute;

    @Column(name = "created_at", nullable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
