package net.questionbank.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Entity
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int reportId;
    private String type;
    @Column(columnDefinition = "VARCHAR(50)")
    private String description;
    @ManyToOne
    @JoinColumn(name = "memberId")
    @ToString.Exclude
    private Member member;
    private int itemId;
}
