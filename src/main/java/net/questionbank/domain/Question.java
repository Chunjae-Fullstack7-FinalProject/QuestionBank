package net.questionbank.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Entity
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
