package lk.SmartClass.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "students")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne @JoinColumn(name = "guardian_id")
    private Guardian guardian;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    private LocalDate dob;

    @Column(name = "enrollment_date")
    private LocalDate enrollmentDate;

    @PrePersist
    protected void onCreate() {
        if (enrollmentDate == null) enrollmentDate = LocalDate.now();
    }
}

