package com.example.springsehibernate.Controller;

import com.example.springsehibernate.DTO.UserRegistrationDto;
import com.example.springsehibernate.Entity.StudentVersion;
import com.example.springsehibernate.Entity.User;
import com.example.springsehibernate.Repository.StudentVersionRepository;
import com.example.springsehibernate.Service.DropboxService;
import com.example.springsehibernate.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bo-mon")
public class DepartmentsApiController {
    @Autowired
    private UserService userService;

    @Autowired
    private StudentVersionRepository studentVersionRepository;

    @Autowired
    private DropboxService dropboxService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserRegistrationDto registrationDto) {
        try {
            User registeredUser = userService.registerNewUser(registrationDto);
            
            return ResponseEntity.ok("Người dùng đã được đăng ký thành công với tên đăng nhập: " + registeredUser.getUsername());
        } catch (IllegalStateException e) {
            
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            
            return ResponseEntity.internalServerError().body("Có lỗi xảy ra trong quá trình đăng ký.");
        }
    }

//    Kiểm tra mã giảng viên đã tồn tại hay chưa
    @GetMapping("/checkOwnerId")
    public ResponseEntity<?> checkOwnerId(@RequestParam("ownerId") Long ownerId) {
        boolean exists = userService.checkOwnerIdExists(ownerId);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/send-confirm-list/download/{studentVersionId}")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long studentVersionId) {
        try {
            StudentVersion studentVersion = studentVersionRepository.findById(studentVersionId)
                    .orElseThrow(() -> new RuntimeException("Student not found"));
            Resource fileResource = dropboxService.downloadFileFromURL(studentVersion.getFilePath());
            MediaType mediaType = dropboxService.determineMediaType(studentVersion.getFilePath());

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + studentVersion.getFilePath() + "\"")
                    .body(fileResource);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
