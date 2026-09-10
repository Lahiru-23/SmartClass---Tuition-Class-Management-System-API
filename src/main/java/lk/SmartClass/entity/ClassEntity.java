package lk.SmartClass.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "classes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClassEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @ManyToOne @JoinColumn(name = "teacher_id", nullable = false)
    private Teacher teacher;

    @ManyToOne @JoinColumn(name = "term_id", nullable = false)
    private Term term;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    @Builder.Default
    private Integer capacity = 30;

    @Column(nullable = false)
    @Builder.Default
    private String mode = "PHYSICAL";

    private String room;

    @OneToMany(mappedBy = "classEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ClassSchedule> schedules = new ArrayList<>();
}

