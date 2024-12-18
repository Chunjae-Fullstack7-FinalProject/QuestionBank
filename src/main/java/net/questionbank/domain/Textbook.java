package net.questionbank.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Entity
public class Textbook {
    @Id
    private int textbookId;
    @Column(nullable = false, columnDefinition = "VARCHAR(20)")
    private String title;
    @ManyToOne
    @JoinColumn(name = "subjectId")
    @ToString.Exclude
    private Subject subject;
}
