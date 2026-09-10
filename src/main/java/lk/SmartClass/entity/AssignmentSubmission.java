package lk.SmartClass.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "assignment_submissions",uniqueConstraints =  @UniqueConstraint(columnNames = {"assignment_id", "student_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class AssignmentSubmission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne @JoinColumn(name = "assignment_id", nullable = false)
    private Assignment assignment;

    @ManyToOne @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    @Column(name = "file_url")
    private String fileUrl;

    @Column(name = "marks_obtained")
    private Integer marksObtained;

    private String feedback;

    @PrePersist
    protected void onCreate() {
        if (submittedAt == null) submittedAt = LocalDateTime.now();
    }
}
