package com.example.springsehibernate.Controller;

import com.example.springsehibernate.Entity.AcademicYearUtil;
import com.example.springsehibernate.Entity.QualifiedGraduate;
import com.example.springsehibernate.Entity.QualifiedGraduateConfirm;
import com.example.springsehibernate.Entity.TemporaryFile;
import com.example.springsehibernate.Service.QualifiedGraduateConfirmService;
import com.example.springsehibernate.Service.QualifiedGraduateService;
import com.example.springsehibernate.Service.TemporaryFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/khoa")
public class QualifiedGraduateController {

    @Autowired
    private QualifiedGraduateService qualifiedGraduateService;

    @Autowired
    private QualifiedGraduateConfirmService qualifiedGraduateConfirmService;
    @Autowired
    private TemporaryFileService temporaryFileService;

    @GetMapping("/upload")
    public String listQualifiedGraduates(Model model,
                                         @RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "7") int size,
                                         @RequestParam(required = false) String academicYear,
                                         @RequestParam(required = false) Integer semester) {

        List<String> academicYears = AcademicYearUtil.generateAcademicYearsList();
        model.addAttribute("academicYears", academicYears);

        // Lấy năm học và học kỳ hiện tại từ AcademicYearUtil
        String currentAcademicYear = AcademicYearUtil.getCurrentAcademicYear();
        int currentSemester = AcademicYearUtil.getCurrentSemester();

        // Nếu không có gì được chọn, sử dụng giá trị mặc định
        if (academicYear == null && semester == null) {
            academicYear = currentAcademicYear;
            semester = currentSemester;
        }
        model.addAttribute("selectedYear", academicYear);
        model.addAttribute("selectedSemester", semester);

//        Page<QualifiedGraduate> graduatesPage = qualifiedGraduateService.getQualifiedGraduatesPage(page, size,
//                academicYear,
//                semester);
        Page<QualifiedGraduate> graduatesPage = qualifiedGraduateService.getAllQualifiedGraduatesPage(page, size);
        model.addAttribute("graduatesPage", graduatesPage);
        return "upload";
    }

    @PostMapping("/import")
    public String importExcelFile(@RequestParam("file") MultipartFile file,
                                  @RequestParam("academicYear") String academicYear,
                                  @RequestParam("semester") Integer semester,
                                  RedirectAttributes redirectAttributes) {
        if (!qualifiedGraduateService.canUploadNewFile()) {
            redirectAttributes.addFlashAttribute("UploadMessage", "Bạn cần xác nhận hoặc xóa danh sách chờ trước đó");
            return "redirect:/khoa/upload"; // Chuyển hướng trở lại trang tải lên với thông điệp lỗi
        }
        try {
            // Gọi service để kiểm tra và import dữ liệu từ file Excel
            String message = qualifiedGraduateService.importFromExcel(file, academicYear, semester);
            // Nếu service trả về thông điệp cảnh báo rằng dữ liệu đã tồn tại
            if (message.equals("data-exists")) {
                // Xử lý file tạm thời và tạo file identifier
                Long fileIdentifier = temporaryFileService.saveFileTemporarily(file);
                // Lưu thông tin vào session để có thể hiển thị modal xác nhận ghi đè
                redirectAttributes.addFlashAttribute("confirmOverwrite", true);
                redirectAttributes.addFlashAttribute("academicYear", academicYear);
                redirectAttributes.addFlashAttribute("semester", semester);
                redirectAttributes.addFlashAttribute("fileIdentifier", fileIdentifier);
                return "redirect:/khoa/upload"; // Trang upload cần có logic để hiển thị modal dựa trên các attributes này
            } else {
                // Nếu không có vấn đề, hiển thị thông điệp thành công
                redirectAttributes.addFlashAttribute("UploadMessage", message);
                return "redirect:/khoa/upload";
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("UploadMessage", "Failed to import file: " + e.getMessage());
            return "redirect:/khoa/upload";
        }
    }

    @PostMapping("/overwrite-data")
    public String overwriteData(@RequestParam("fileIdentifier") Long fileIdentifier,
                                @RequestParam("academicYear") String academicYear,
                                @RequestParam("semester") Integer semester,
                                RedirectAttributes redirectAttributes) {

        try {
            // Lấy file từ database dựa trên fileIdentifier
            TemporaryFile tempFile = temporaryFileService.getTemporaryFile(fileIdentifier);
            if (tempFile == null) {
                // Nếu không tìm thấy file, thêm thông báo lỗi
                redirectAttributes.addFlashAttribute("message", "Could not find the file to overwrite.");
                return "redirect:/khoa/upload";
            }

            // Gọi service để xóa dữ liệu hiện tại và nhập dữ liệu mới từ file đã lấy
            String overwriteResult = qualifiedGraduateService.overwriteData(tempFile.getData(), academicYear, semester);

            // Xóa file tạm thời khỏi database
            temporaryFileService.deleteTemporaryFile(fileIdentifier);

            // Thêm thông điệp phản hồi vào RedirectAttributes để hiển thị trên giao diện người dùng
            redirectAttributes.addFlashAttribute("message", overwriteResult);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Error during overwriting: " + e.getMessage());
        }

        // Chuyển hướng người dùng trở lại trang upload
        return "redirect:/khoa/upload";
    }

    @PostMapping("/confirm")
    public String confirmUpload(RedirectAttributes redirectAttributes) {
        try {
            String message = qualifiedGraduateService.confirmDataAndTransfer();
            redirectAttributes.addFlashAttribute("SaveMessage", message);
            if (message.contains("Danh sách đã tồn tại trong bảng")) {
                return "redirect:/khoa/upload"; // Chuyển hướng nếu có trùng lặp
            }
            return "redirect:/khoa/upload-success"; // Chuyển hướng nếu thành công
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("SaveMessage", "Error saving data: " + e.getMessage());
            return "redirect:/khoa/upload"; // Chuyển hướng nếu có lỗi
        }
    }


    @GetMapping("/upload-success")
    public String showUploadSuccess(Model model,
                                    @RequestParam(defaultValue = "0") int page,
                                    @RequestParam(defaultValue = "7") int size,
                                    @RequestParam(required = false) String academicYear,
                                    @RequestParam(required = false) Integer semester) {
        List<String> academicYears = AcademicYearUtil.generateAcademicYearsList();
        model.addAttribute("academicYears", academicYears);

        // Lấy năm học và học kỳ hiện tại từ AcademicYearUtil
        String currentAcademicYear = AcademicYearUtil.getCurrentAcademicYear();
        int currentSemester = AcademicYearUtil.getCurrentSemester();

        // Nếu không có gì được chọn, sử dụng giá trị mặc định
        if (academicYear == null && semester == null) {
            academicYear = currentAcademicYear;
            semester = currentSemester;
        }
        model.addAttribute("selectedYear", academicYear);
        model.addAttribute("selectedSemester", semester);

        Page<QualifiedGraduateConfirm> confirmedGraduatesList = qualifiedGraduateConfirmService.getQualifiedGraduateConfirmPage(page,size,academicYear,semester);
        model.addAttribute("confirmedGraduates", confirmedGraduatesList);
        return "upload-success"; // View name for the upload success page
    }

    @PostMapping("/delete-upload")
    public String deleteList(RedirectAttributes redirectAttributes) {
        try {

             qualifiedGraduateService.deleteQualifiedGraduate();
            redirectAttributes.addFlashAttribute("DeleteMessage", "Danh sách chờ đã được xóa thành công.");
            return "redirect:/khoa/upload"; // Chuyển hướng đến trang phù hợp sau khi xóa
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("DeleteMessage", "Lỗi trong quá trình xóa: " + e.getMessage());
            return "redirect:/khoa/upload"; // Trang để chuyển hướng nếu có lỗi
        }
    }


}
