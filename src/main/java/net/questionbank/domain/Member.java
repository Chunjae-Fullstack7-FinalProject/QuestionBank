package net.questionbank.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Member {
    @Id
    @Column(columnDefinition = "VARCHAR(20)")
    private String memberId;
    @Column(columnDefinition = "VARCHAR(200)")
    private String pwd;
    @Column(columnDefinition = "VARCHAR(10)")
    private String name;
    @Column(columnDefinition = "VARCHAR(30)")
    private String email;
}
