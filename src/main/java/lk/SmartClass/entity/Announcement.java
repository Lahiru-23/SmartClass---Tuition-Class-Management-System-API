package lk.SmartClass.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name ="announcement")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Announcement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne @JoinColumn(name = "class_id")
    private ClassEntity classEntity;

    @ManyToOne @JoinColumn(name = "posted_by" , nullable = false)
    private User postedBy;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT" , nullable = false)
    private String body;

    @Column(name = "posted_at")
    private LocalDateTime postedAt;

    @PrePersist
    protected void onCreate() {if (postedAt == null) {postedAt = LocalDateTime.now();}}
}
