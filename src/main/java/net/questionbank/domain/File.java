package net.questionbank.domain;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name="file")
public class File {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int fileId;
    @Column(columnDefinition = "VARCHAR(100)")
    private String originalFileName; //원본파일이름
    @Column(columnDefinition = "VARCHAR(100)")
    private String filePath;//새로운파일이름포함 파일경로
    @OneToOne
    @JoinColumn(name = "reportId")
    private Report report;
}
