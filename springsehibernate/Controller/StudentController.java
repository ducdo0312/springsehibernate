package com.example.springsehibernate.Controller;

import com.example.springsehibernate.Entity.*;
import com.example.springsehibernate.Repository.*;
import com.example.springsehibernate.Service.LecturerService;
import com.example.springsehibernate.Service.StudentService;
import com.example.springsehibernate.Service.TimePhaseService;
import com.example.springsehibernate.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Controller
public class StudentController {

    @Autowired
    private StudentService studentService;

    @Autowired
    private LecturerService lecturerService;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private LecturerRepository lecturerRepository;

    @Autowired
    private QualifiedGraduateConfirmRepository qualifiedGraduateConfirmRepository;

    @Autowired
    private TimePhaseService timePhaseService;

    @Autowired
    private UserService userService;

    // Tải người dùng hiện tại dựa trên thông tin xác thực
    private User getCurrentUser(Authentication authentication) {
        if (authentication != null) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            return userService.findByUsername(userDetails.getUsername());
        }
        return null;
    }

    @GetMapping("/students")
    public String listStudents(Model model,
                               Authentication authentication,
                               @RequestParam(name="page", defaultValue="0") int page,
                               @RequestParam(name="academicYear", required=false) String academicYear,
                               @RequestParam(name="semester", required=false) Integer semester) {
        if(authentication != null) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User user = userService.findByUsername(userDetails.getUsername());
            List<Student> students;
            if (user != null) {
                Long lecturerId = user.getOwnerId();
                Optional<Lecturer> optionalLecturer = lecturerRepository.findById(lecturerId);
                Lecturer lecturer = optionalLecturer.get();

                // trả về chuỗi năm học hiện tại và thêm vào model
                String currentAcademicYear = AcademicYearUtil.getCurrentAcademicYear();
                model.addAttribute("currentAcademicYear", currentAcademicYear);

                int currentSemester = AcademicYearUtil.getCurrentSemester();
                model.addAttribute("currentSemester", currentSemester);

                if (academicYear != null && !academicYear.trim().isEmpty()) {
                   Page<Student> studentsPage = studentService
                           .getStudentsByLecturerAndAcademicYearAndSemester(lecturerId, academicYear,
                           semester, PageRequest.of(page, 5) );
                    model.addAttribute("studentsPage", studentsPage);
                } else {
                    academicYear = AcademicYearUtil.getCurrentAcademicYear();
                    semester = AcademicYearUtil.getCurrentSemester();
                    Page<Student> studentsPage = studentService
                            .getStudentsByLecturerAndAcademicYearAndSemester(lecturerId, academicYear,
                                    semester, PageRequest.of(page, 5));
                    model.addAttribute("studentsPage", studentsPage);
                }

                // Kiểm tra năm học hiện tại và năm học được chọn có giống nhau không

                boolean isCurrentAcademicYear = (academicYear.equals(currentAcademicYear));
                boolean isCurrentSemester = (semester.equals(currentSemester));

                if (isCurrentAcademicYear && isCurrentSemester) {
                    // Nếu là năm học và học kỳ hiện tại, kiểm tra TimePhase
                    LocalDate currentDate = LocalDate.now();
//                    LocalDate currentDate = LocalDate.of(2023,9,10);
                    String showColumn = timePhaseService.getPhaseColumn(currentDate);
                    model.addAttribute("showColumn", showColumn);
                } else {
                    // Nếu không phải năm học hiện tại, set showColumn để hiển thị tất cả
                    model.addAttribute("showColumn", "all");
                }

//                model.addAttribute("studentsPage", studentsPage);
                model.addAttribute("selectedSemester", semester);
                model.addAttribute("selectedAcademicYear", academicYear);
                model.addAttribute("LecturerId", lecturerId);
//                model.addAttribute("showColumns", showColumns);
            } else {
                students = Collections.emptyList();
            }

            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            model.addAttribute("userRoles", authorities);
            model.addAttribute("newStudent", new Student());
            return "students";
        }

        return "redirect:/login";
    }

    @PostMapping("/students/add")
    public String addStudent(@ModelAttribute Student student,
                             RedirectAttributes redirectAttributes,
                             Authentication authentication) {

        String currentAcademicYear = AcademicYearUtil.getCurrentAcademicYear();
        int currentSemester = AcademicYearUtil.getCurrentSemester();
        // Bước 1: Kiểm tra xem có sinh viên nào có studentID giống và status là "Đã bảo vệ"
        Optional<Student> existingProtectedStudentOpt = studentRepository.findByStudentIDAndStatus(student.getStudentID(), "Đã bảo vệ");
        if (existingProtectedStudentOpt.isPresent()) {
            redirectAttributes.addFlashAttribute("errorAddMessage", "Mã sinh viên này đã tồn tại với trạng thái 'Đã bảo vệ'!");
            return "redirect:/students";
        }

        // Bước 2: Kiểm tra sinh viên có cùng studentID trong năm học hiện tại
        Optional<Student> existingStudentThisYearOpt = studentRepository.findByStudentIDAndAcademicYearAndSemester(student.getStudentID(), currentAcademicYear, currentSemester);
        if (existingStudentThisYearOpt.isPresent()) {
            redirectAttributes.addFlashAttribute("errorAddMessage", "Mã sinh viên này đã tồn tại trong năm học "
                    + currentAcademicYear + " Học Kỳ " +currentSemester +"!" );
            return "redirect:/students";
        }
        if(authentication != null) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User user = userService.findByUsername(userDetails.getUsername());
            if (user != null) {
                Lecturer lecturerEntity = lecturerRepository.findById(user.getOwnerId()).orElse(null);
                student.setLecturer(lecturerEntity);
                student.setAcademicYear(AcademicYearUtil.getCurrentAcademicYear());
                student.setSemester(AcademicYearUtil.getCurrentSemester());
            }
        }

        Optional<QualifiedGraduateConfirm> existingGradConfirmOpt = qualifiedGraduateConfirmRepository.findByStudentIdAndAcademicYearAndSemester(student.getStudentID(), student.getAcademicYear(), student.getSemester());
        if (!existingGradConfirmOpt.isPresent()) {
            redirectAttributes.addFlashAttribute("errorAddMessage", "Sinh viên với "
                    + currentAcademicYear + " Học Kỳ " +currentSemester +" chưa đủ điều kiện bảo vệ" );
            return "redirect:/students";
        }
        if (student.getNamesecondlecturer() != null) {
            Lecturer SecondLecturer = lecturerRepository.findByName(student.getNamesecondlecturer());
            if (SecondLecturer != null) {
                student.setSecondLecturerId(SecondLecturer.getId());
            } else {
                student.setSecondLecturerId(null); // Xử lý trường hợp không tìm thấy giảng viên
            }
        } else {
            student.setSecondLecturerId(null); // Xử lý khi student.getNamesecondlecturer() là null
        }


        studentRepository.save(student);
        redirectAttributes.addFlashAttribute("successAddMessage", "Thêm dữ liệu thành công!");
        return "redirect:/students";
    }

    @DeleteMapping("/students/{id}")
    public String deleteStudent(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            studentRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("message", "Đã xóa học sinh thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi xóa học sinh!");
        }
        return "redirect:/students";
    }

//    @GetMapping("students/edit/{id}")
//    public String editStudent(@PathVariable("id") Long id, Model model) {
//        Student student = studentService.getStudentById(id);
//        model.addAttribute("student", student);
//        return "edit-student"; // name of the thymeleaf template for editing student
//    }



//    @GetMapping("students/edit/{studentId}")
//    public String editStudent(@PathVariable("studentId") Long studentId, Model model) {
//        try {
//            Student student = studentRepository.getStudentByID(studentId);
//
//            model.addAttribute("student", student);
//            model.addAttribute("pageTitle", "Edit Tutorial (ID: " + student.getID() + ")");
//
//            return "student_form";
//        } catch (Exception e) {
//            throw new RuntimeException("dm minh chien");
//        }
//    }

//    @RequestMapping(value = "/students/{id}", method = RequestMethod.PUT)
//    public String updateStudent(@PathVariable Long id, Student student, Authentication authentication, RedirectAttributes redirectAttrs) {
//        try {
//            Optional<Student> optionalStudent = studentRepository.findById(id);
//            if (!optionalStudent.isPresent()) {
//                // Nếu sinh viên không tồn tại trong cơ sở dữ liệu, thêm thông báo lỗi và chuyển hướng
//                redirectAttrs.addFlashAttribute("updateFailed", true);
//                return "redirect:/students";
//            }
//
//            // Lấy sinh viên từ cơ sở dữ liệu và cập nhật thông tin
//            Student existingStudent = optionalStudent.get();
//
//            // Cập nhật thông tin từ đối tượng student được gửi từ form
//            existingStudent.setName(student.getName());
//            existingStudent.setDateOfBirth(student.getDateOfBirth());
//            // ... (Cập nhật các trường khác tương tự)
//
//            User currentUser = getCurrentUser(authentication);
//            existingStudent.setLecturerID(currentUser.getUserID());
//            existingStudent.setAcademicYear(AcademicYearUtil.getCurrentAcademicYear());
//            existingStudent.setSemester(AcademicYearUtil.getCurrentSemester());
//
//            studentRepository.save(existingStudent);
//
//            // Thêm thông báo thành công vào redirect attributes
//            redirectAttrs.addFlashAttribute("updateSuccess", true);
//        } catch (Exception e) {
//            // Bắt và xử lý ngoại lệ
//            // Thêm thông báo thất bại vào redirect attributes
//            redirectAttrs.addFlashAttribute("updateFailed", true);
//        }
//
//        return "redirect:/students";
//    }

    @GetMapping("/students/suggestions")
    public String getLecturerSuggestions(@RequestParam String query, Model model) {
        model.addAttribute("suggestions", lecturerService.findByNameContaining(query));
        return "fragments/lecturerSuggestions"; // Fragment cập nhật
    }



}

