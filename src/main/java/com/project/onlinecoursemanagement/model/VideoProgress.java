package com.project.onlinecoursemanagement.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class VideoProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String studentEmail;

    @ManyToOne(optional = false)
    @JoinColumn(name = "video_lecture_id")
    private VideoLecture videoLecture;

}
