package com.example.springsehibernate.Entity;

import com.example.springsehibernate.Validation.MaxFileSize;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@Table(name = "news")
public class News {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Lob
    @Column(nullable = false)
    private String content;

    @Temporal(TemporalType.TIMESTAMP)
    private Date publishDate;

    @Lob
    @MaxFileSize
    private byte[] attachment;

    // Thêm trường cho tên file
    private String fileName;

    // Thêm trường cho kiểu file
    private String fileType;
}
