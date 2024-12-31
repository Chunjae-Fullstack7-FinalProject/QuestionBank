package net.questionbank.domain;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name="question")
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int questionId;
    @ManyToOne
    @JoinColumn(name="testId")
    @ToString.Exclude
    private Test test;
    private int itemNo;
    private int itemId;
}
