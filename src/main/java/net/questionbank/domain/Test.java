package net.questionbank.domain;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class Test {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int testId;
    @Column(nullable = false, columnDefinition = "VARCHAR(100)")
    private String title;
    private LocalDateTime createdAt;
    @ManyToOne
    @JoinColumn(name = "textbookId")
    @ToString.Exclude
    private Textbook textbook;
    @ManyToOne
    @JoinColumn(name = "memberId")
    @ToString.Exclude
    private Member member;
    @Column(nullable = false, columnDefinition = "VARCHAR(100)")
    private String filePath;
}
