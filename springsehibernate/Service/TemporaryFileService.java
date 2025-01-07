package com.example.springsehibernate.Service;

import com.example.springsehibernate.Entity.TemporaryFile;
import com.example.springsehibernate.Repository.TemporaryFileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class TemporaryFileService {

    @Autowired
    private TemporaryFileRepository temporaryFileRepository;

    public Long saveFileTemporarily(MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();
        String contentType = file.getContentType();
        byte[] data = file.getBytes();

        TemporaryFile tempFile = new TemporaryFile();
        tempFile.setFileName(fileName);
        tempFile.setContentType(contentType);
        tempFile.setData(data);

        TemporaryFile savedFile = temporaryFileRepository.save(tempFile);
        return savedFile.getId(); // Trả về ID của file đã lưu
    }

    // Thêm phương thức để xóa file tạm thời sau khi xử lý
    public void deleteTemporaryFile(Long id) {
        temporaryFileRepository.deleteById(id);
    }

    public TemporaryFile getTemporaryFile(Long id) {
        return temporaryFileRepository.findById(id).orElse(null);
    }
}

