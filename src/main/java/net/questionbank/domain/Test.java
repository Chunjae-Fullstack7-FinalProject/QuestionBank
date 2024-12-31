package net.questionbank.domain;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name="test")
public class Test {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int testId;
    @Column(nullable = true, columnDefinition = "VARCHAR(100)")
    private String title;
    @Column(columnDefinition = "datetime default now()")
    private LocalDateTime createdAt;
    @ManyToOne
    @JoinColumn(name = "textbookId")
    @ToString.Exclude
    private Textbook textbook;
    @ManyToOne
    @JoinColumn(name = "memberId")
    @ToString.Exclude
    private Member member;
    @Column(nullable = true, columnDefinition = "VARCHAR(100)")
    private String filePath;
}
