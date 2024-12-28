package net.questionbank.domain;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Table(name="textbook")
public class Textbook {
    @Id
    private int textbookId;
    @Column(nullable = false, columnDefinition = "VARCHAR(20)")
    private String title;
    @Column(nullable = false, columnDefinition = "VARCHAR(5)")
    private String author;
    @ManyToOne
    @JoinColumn(name = "subjectId")
    @ToString.Exclude
    private Subject subject;
}
