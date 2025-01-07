package com.example.springsehibernate.Controller;

import com.example.springsehibernate.Entity.*;
import com.example.springsehibernate.Repository.*;
import com.example.springsehibernate.Service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/khoa")
public class FacultiesController {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private StudentVersionRepository studentVersionRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private ConfirmTableRepository confirmTableRepository;

    @Autowired
    private TimePhaseRepository timePhaseRepository;

    @Autowired
    private NotificationService notificationService;
    @Autowired
    private UserService userService;

    @Autowired
    private StudentVersionService studentVersionService;

    @Autowired
    private TimePhaseService timePhaseService;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private QualifiedGraduateService qualifiedGraduateService;

    @GetMapping("/list/{departmentID}")
    public String getList(@PathVariable("departmentID") Long departmentID, Model model) {

        String currentAcademicYear = AcademicYearUtil.getCurrentAcademicYear();
        int currentSemester = AcademicYearUtil.getCurrentSemester();
        // Lấy danh sách studentVersion dựa trên departmentID
        List<StudentVersion> confirmList = studentVersionService.getStudentVersionByStudent_Lecturer_Department_DepartmentIdAndVersionType(departmentID, "Bộ môn",
                currentAcademicYear, currentSemester);
        model.addAttribute("confirmList", confirmList);
        LocalDate currentDate = LocalDate.now();
//        LocalDate currentDate = LocalDate.of(2023, 9, 10); // Thiết lập ngày là 28/10/2023

        String showColumn = timePhaseService.getPhaseColumn(currentDate);

        model.addAttribute("showColumn", showColumn);
        return "listViewFaculty";
    }

    @PostMapping("/list/confirm/{messageId}")
    public String confirmList(@PathVariable("messageId") long messageId,
                              Authentication authentication,
                              RedirectAttributes redirectAttributes) {
        UserDetails senderDetails = (UserDetails) authentication.getPrincipal();
        User currentUser = userService.findByUsername(senderDetails.getUsername());
        Long currentKhoaId = currentUser.getOwnerId(); // Giả sử bạn lưu ID của khoa ở trường này
        String currentAcademicYear = AcademicYearUtil.getCurrentAcademicYear();
        int currentSemester = AcademicYearUtil.getCurrentSemester();


        Optional<Message> optionalMessage = messageRepository.findById(messageId);

        if (optionalMessage.isPresent()) {
            Message message = optionalMessage.get();
            message.setStatusEnum(MessageStatus.ACCEPTED);
            messageRepository.save(message);

            if (message.getStatusEnum() == MessageStatus.ACCEPTED) {
                // Lấy senderId từ message
                Long senderId = message.getSenderId();

                // Danh sách thêm vào từ Bộ Môn
                List<StudentVersion> studentVersionsConfirmByDepartment
                        = studentVersionService
                        .getStudentVersionByAcademicYearAndSemesterAndVersionTypeAndLecturer_Department_DepartmentId(currentAcademicYear,
                                currentSemester,"Bộ môn", senderId);

                // Danh sách được update vào Khoa
                List<StudentVersion> studentVersionsConfirmByFaculty
                        = studentVersionRepository
                        .findByVersionTypeAndLecturer_Department_DepartmentIdAndAcademicYearAndSemester("Khoa",
                                senderId, currentAcademicYear, currentSemester);

                //Xóa danh sách trong khoa
                studentVersionRepository.deleteAll(studentVersionsConfirmByFaculty);

                for (StudentVersion studentVersion : studentVersionsConfirmByDepartment) {
                    StudentVersion svTemp = studentVersionRepository.findByVersionTypeAndAcademicYearAndSemesterAndStudentID("Khoa", currentAcademicYear, currentSemester, studentVersion.getStudentID());
                    if (svTemp != null) {
                        studentVersionRepository.delete(svTemp);
                    }
                    StudentVersion newStudentVersion = new StudentVersion();
                    newStudentVersion.setStudent(studentVersion.getStudent());
                    newStudentVersion.setVersionType("Khoa");
                    newStudentVersion.setVersionDate(new Date());
                    studentVersionService.updateVersionWithStudentDataDepartment(newStudentVersion, studentVersion);
                    studentVersionService.save(newStudentVersion);
                }

//                for (StudentVersion studentVersion : studentVersionsConfirmByDepartment) {
//                    StudentVersion studentVersion1
//                            = studentVersionService.
//                            getStudentVersionByVersionType_Student_Lecturer_Department_DepartmentIdAndStudent_Lecturer_Department_FacultyId("Khoa", senderId, currentKhoaId);
//                    if (studentVersion1 == null || !studentVersion1.StudentVersionEqual(studentVersion)) {
//                        if (studentVersion1 == null) {
//                            studentVersion1 = new StudentVersion();
//                            studentVersion1.setStudent(studentVersion.getStudent());
//                            studentVersion1.setVersionType("Khoa");
//                            studentVersion1.setVersionDate(new Date());
//                        }
//                        studentVersionService.updateVersionWithStudentDataDepartment(studentVersion1, studentVersion);
//                        studentVersionService.save(studentVersion1);
//                    }
//                }



            }

            Long senderId = message.getSenderId();
            // Gửi thông báo cho user Bộ Môn sau khi cập nhật trạng thái
            String notificationContent = "Danh sách của bạn đã được chấp nhận.";
            notificationService.sendNotificationToLecturer(notificationContent, senderId);
        }
        return "redirect:/khoa";
    }


    @PostMapping("/list/reject/{messageId}")
    public String rejectList(@PathVariable("messageId") long messageId) {
        Optional<Message> optionalMessage = messageRepository.findById(messageId);
        if (optionalMessage.isPresent()) {
            Message message = optionalMessage.get();
            message.setStatusEnum(MessageStatus.REJECTED);
            messageRepository.save(message);
            // Lấy sender_id của người gửi thông báo
            Long senderId = message.getSenderId();
            // Gửi thông báo cho user Giảng viên sau khi cập nhật trạng thái
            String notificationContent = "Danh sách của bạn đã bị từ chối.";
            notificationService.sendNotificationToLecturer(notificationContent, senderId);
        }
        return "redirect:/khoa";
    }

    @GetMapping("/send-confirm-list")
    public String getSendConfirmList(Model model,Authentication authentication,
                                     RedirectAttributes redirectAttributes) {
        UserDetails senderDetails = (UserDetails) authentication.getPrincipal();
        User currentUser = userService.findByUsername(senderDetails.getUsername());

//        List<StudentVersion> confirmedStudents = studentVersionService.getStudentVersionByVersionTypeAndStudent_Lecturer_Department_FacultyId("Khoa", currentUser.getUserID());
       List<StudentVersion> confirmedStudents = studentVersionRepository.findByVersionTypeAndAcademicYearAndSemester("Khoa", AcademicYearUtil.getCurrentAcademicYear(), AcademicYearUtil.getCurrentSemester());
        model.addAttribute("confirmedStudents", confirmedStudents);
        LocalDate currentDate = LocalDate.now();
//        LocalDate currentDate = LocalDate.of(2023, 9, 10); // Thiết lập ngày là 28/10/2023

        String showColumn = timePhaseService.getPhaseColumn(currentDate);

        // Thêm biến showColumn vào model để truyền tới view
        model.addAttribute("showColumn", showColumn);
        return "send-confirm-list";
    }


    @GetMapping("/TimePhase")
    public String getTimePhase(Model model) {
        boolean exists = timePhaseRepository.existsByIdIsNotNull();
        model.addAttribute("exists", exists);
        if (exists) {
            List<TimePhase> timePhases = timePhaseRepository.findAll();
            model.addAttribute("timePhases", timePhases);
        }
        model.addAttribute("timePhase", new TimePhase());
        return "time-phase";
    }
    @PostMapping("/setTimePhase")
    public String saveTimePhase(@ModelAttribute TimePhase timePhase, RedirectAttributes redirectAttributes) {
        try {

            timePhaseRepository.save(timePhase);
            redirectAttributes.addFlashAttribute("toastMessage", "Thời gian giai đoạn đã được cập nhật!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("toastMessage", "Có lỗi xảy ra: " + e.getMessage());
        }
        return "redirect:/khoa/TimePhase";
    }

    @PostMapping("/deleteTimePhases")
    public String deleteAllTimePhases(RedirectAttributes redirectAttributes) {
        timePhaseRepository.deleteAll();
        redirectAttributes.addFlashAttribute("toastMessage", "Thời gian giai đoạn đã được xóa");
        return "redirect:/khoa/TimePhase";
    }

    @GetMapping("/register")
    public String showRegistrationForm() {
        return "register-department";
    }

    @GetMapping("/{facultyId}")
    public String getDepartmentByFaculty(@PathVariable Long facultyId, Model model) {
        List<Department> departmentList = departmentRepository.findByFacultyId(facultyId);
        model.addAttribute("departmentList", departmentList);
        return "departments-list";
    }

}
