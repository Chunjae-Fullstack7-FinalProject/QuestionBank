package net.questionbank.domain;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name="subject")
public class Subject {
    @Id
    @Column(nullable = false, columnDefinition = "CHAR(2)")
    private String subjectId;
    @Column(nullable = false, columnDefinition = "VARCHAR(5)")
    private String title;
}
