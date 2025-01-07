package com.example.springsehibernate.Controller;

import com.dropbox.core.DbxDownloader;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.sharing.SharedLinkMetadata;
import com.dropbox.core.v2.users.FullAccount;
import com.example.springsehibernate.Config.DropboxConfig;
import com.example.springsehibernate.Entity.AcademicYearUtil;
import com.example.springsehibernate.Entity.Lecturer;
import com.example.springsehibernate.Entity.Student;
import com.example.springsehibernate.Entity.User;
import com.example.springsehibernate.Repository.LecturerRepository;
import com.example.springsehibernate.Repository.StudentRepository;
import com.example.springsehibernate.Service.DropboxService;
import com.example.springsehibernate.Service.StudentService;
import com.example.springsehibernate.Service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
public class StudentApiController {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private LecturerRepository lecturerRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private DropboxService dropboxService;

    @Autowired
    private DropboxConfig dropboxConfig;

    private User getCurrentUser(Authentication authentication) {
        if (authentication != null) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            return userService.findByUsername(userDetails.getUsername());
        }
        return null;
    }
    // Các phương thức xử lý API

    @GetMapping("/students/{id}")
    public ResponseEntity<Student> getStudentById(@PathVariable Long id)  {
        Optional<Student> optionalStudent = studentRepository.findByID(id);
        if (optionalStudent.isPresent()) {
            return new ResponseEntity<>(optionalStudent.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/students/{id}")
    public ResponseEntity<Map<String, Object>> updateStudent(
            @PathVariable Long id,
            @RequestParam(value = "fileUpload", required = false) MultipartFile fileUpload,
            @RequestParam Map<String, String> formData,
            Authentication authentication,
            @RequestParam(name="academicYear", required=false) String chosenAcademicYear,
            @RequestParam(name="semester", required=false) Integer chosenSemester) {
        Map<String, Object> response = new HashMap<>();
        try {
            Optional<Student> optionalStudent = studentRepository.findById(id);
            if (!optionalStudent.isPresent()) {
                response.put("status", "failed");
                response.put("message", "Student not found.");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

            // Lấy sinh viên từ cơ sở dữ liệu và cập nhật thông tin
            Student existingStudent = optionalStudent.get();

            Long studentId = Long.parseLong(formData.get("IdEdit"));

            existingStudent.setStudentID(studentId);

            String nameStudent = formData.get("NameEdit");
            existingStudent.setName(nameStudent);

            String dobString = formData.get("BirthEdit");
            LocalDate dateOfBirth = null;
            // Kiểm tra và phân tích cú pháp dobString
            if (dobString != null && !dobString.isEmpty()) {
                try {
                    dateOfBirth = LocalDate.parse(dobString, DateTimeFormatter.ISO_LOCAL_DATE);
                } catch (DateTimeParseException e) {
                    dateOfBirth = null;
                    e.printStackTrace();
                }
            }
            if (dobString == null) {
                dateOfBirth = null;
            }
            existingStudent.setDateOfBirth(dateOfBirth);

            String topicString = formData.get("TopicEdit");
            existingStudent.setThesistopics(topicString);

            String newTopicString = formData.get("NewTopicEdit");
            existingStudent.setNewTopics(newTopicString);

            String dtbcString = formData.get("DTBCEdit");
            Float dtbc = null;
            if (dtbcString != null && !dtbcString.isEmpty()) {
                try {
                    dtbc = Float.parseFloat(dtbcString);
                } catch (NumberFormatException e) {
                    // Xử lý lỗi, có thể ghi log hoặc trả về thông báo lỗi phù hợp
                    e.printStackTrace();
                }
            }
            if (dtbc != null) {
                existingStudent.setDtbc(dtbc);
            }


            String nameLecturerString = formData.get("lecturerEdit");
            System.out.println(nameLecturerString);
            existingStudent.setNamelecturer(nameLecturerString);

            String nameSecondLecturerString = formData.get("secondLecturerEdit");
            existingStudent.setNamesecondlecturer(nameSecondLecturerString);

            if (nameSecondLecturerString != null) {
                Lecturer SecondLecturer = lecturerRepository.findByName(nameSecondLecturerString);
                if (SecondLecturer != null) {
                    existingStudent.setSecondLecturerId(SecondLecturer.getId());
                } else {
                    existingStudent.setSecondLecturerId(null); // Xử lý trường hợp không tìm thấy giảng viên
                }
            } else {
                existingStudent.setSecondLecturerId(null);
            }

            String uniString = formData.get("UniversityEdit");
            existingStudent.setUniversity(uniString);

            String SecondUniString = formData.get("UniversitySeEdit");
            existingStudent.setSecondLecturerWorkSpace(SecondUniString);

            String lecturerRwSt = formData.get("lecturerReviewEdit");
            existingStudent.setLecturerReviewer(lecturerRwSt);

            String lecturerRwWorkSpaceSt = formData.get("lecturerReviewWorkSpaceEdit");
            existingStudent.setLecturerReviewerWorkplace(lecturerRwWorkSpaceSt);

            String lecturerSecondRw = formData.get("secondLecturerReviewEdit");
            existingStudent.setSecondLecturerReviewer(lecturerSecondRw);

            String lecturerSecondRwWorkspace = formData.get("secondLecturerReviewWorkSpaceEdit");
            existingStudent.setSecondLecturerReviewerWorkplace(lecturerSecondRwWorkspace);

            String status = formData.get("StatusEdit");
            existingStudent.setStatus(status);

            // Xử lý tập tin tải lên
            if (fileUpload != null && !fileUpload.isEmpty()) {
                String filePath = uploadFileToDropbox(fileUpload);
                System.out.println(filePath);
                existingStudent.setFilePath(filePath);
            } else {
                System.out.println("file is null");
            }

            User currentUser = getCurrentUser(authentication);
            Lecturer lecturerEntity = lecturerRepository.findById(currentUser.getOwnerId()).orElse(null);
            existingStudent.setLecturer(lecturerEntity);
            // Đặt năm học và học kỳ dựa trên sự lựa chọn của người dùng hoặc thời gian hiện tại
            if(chosenAcademicYear != null) {
                existingStudent.setAcademicYear(chosenAcademicYear);
            } else {
                existingStudent.setAcademicYear(AcademicYearUtil.getCurrentAcademicYear());
            }

            if(chosenSemester != null) {
                existingStudent.setSemester(chosenSemester);
            } else {
                existingStudent.setSemester(AcademicYearUtil.getCurrentSemester());
            }
            studentRepository.save(existingStudent);

            response.put("status", "success");
            response.put("message", "Student updated successfully.");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            // Bắt và xử lý ngoại lệ
            response.put("status", "failed");
            response.put("message", "Error updating student.");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private String uploadFileToDropbox(MultipartFile file) throws Exception {

        String accessToken = dropboxService.getUpdatedAccessToken(dropboxConfig.getRefreshToken());
        System.out.println(dropboxConfig.getRefreshToken());
        dropboxConfig.setAccessToken(accessToken);
        DbxRequestConfig config = DbxRequestConfig.newBuilder("uet-project-storage").build();

        DbxClientV2 client = new DbxClientV2(config, dropboxConfig.getAccessToken());
        try {
            FullAccount account = client.users().getCurrentAccount();
            System.out.println("Token is valid. User's account: " + account.getName().getDisplayName());
        } catch (Exception e) {
            // Handle the exception if the token is invalid or has expired
            System.out.println("Token is invalid or has expired.");
            e.printStackTrace();
        }
        try (InputStream in = file.getInputStream()) {
            String dropboxPath = "/home/uetStorage/" + file.getOriginalFilename(); // Specify the Dropbox path
            System.out.println(dropboxPath);
            FileMetadata metadata = client.files().uploadBuilder(dropboxPath)
                    .uploadAndFinish(in);
            System.out.println("File uploaded successfully. Dropbox path: " + metadata.getPathDisplay());
            return metadata.getPathLower();
        } catch (Exception e) {
            // Handle the exception if the upload fails
            System.out.println("File upload failed.");
            e.printStackTrace();
            return "Error";
        }
    }
    @GetMapping("/download/{studentId}")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long studentId) {
        try {
            Student student = studentRepository.findById(studentId)
                    .orElseThrow(() -> new RuntimeException("Student not found"));
            Resource fileResource = dropboxService.downloadFileFromURL(student.getFilePath());
            MediaType mediaType = dropboxService.determineMediaType(student.getFilePath());

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + student.getFilePath() + "\"")
                    .body(fileResource);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
//
//    public Resource directDownloadFromDropbox(String dropboxPath, DbxClientV2 client) throws DbxException {
//        try {
//            // Tải xuống file từ Dropbox
//            DbxDownloader<FileMetadata> downloader;
//            InputStream inputStream;
//            try {
//                downloader = client.files().download(dropboxPath);
//                inputStream = downloader.getInputStream();
//            } catch (DbxException e) {
//                // Xử lý lỗi khi tải file từ Dropbox
//                e.printStackTrace();
//                throw e;
//            }
//
//            // Tạo Resource từ InputStream
//            return new InputStreamResource(inputStream);
//        } catch (Exception e) {
//            // Xử lý các lỗi khác
//            e.printStackTrace();
//            throw new RuntimeException("Error downloading file from Dropbox", e);
//        }
//    }

}

