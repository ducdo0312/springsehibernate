package com.example.springsehibernate.Service;

import com.example.springsehibernate.Entity.News;
import com.example.springsehibernate.Repository.NewsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class NewsService {
    @Autowired
    private NewsRepository newsRepository;

    public List<News> findAll() {
        return newsRepository.findAll();
    }

    public Optional<News> findById(Long id) {
        return newsRepository.findById(id);
    }

    public News save(News news) {
        if (news.getId() == null) { // Nếu ID là null, giả định đây là tin tức mới được tạo
            news.setPublishDate(new Date()); // Đặt thời gian xuất bản cho tin tức mới
        }
        return newsRepository.save(news); // Lưu hoặc cập nhật tin tức trong cơ sở dữ liệu
    }

    public void deleteById(Long id) {
        newsRepository.deleteById(id);
    }

    public News updateNews(Long id, News newsDetails) {
        News news = newsRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid news Id:" + id));
        news.setTitle(newsDetails.getTitle());
        news.setContent(newsDetails.getContent());
        news.setPublishDate(newsDetails.getPublishDate());
        // Cập nhật thêm các trường khác nếu cần
        return newsRepository.save(news);
    }
}
