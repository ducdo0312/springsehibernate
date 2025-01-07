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
import java.time.LocalDateTime;
import java.util.*;

@Controller
@RequestMapping("/bo-mon")
public class DepartmentsController {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private StudentVersionRepository studentVersionRepository;

    @Autowired
    private LecturerRepository lecturerRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private ConfirmTableRepository confirmTableRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private TimePhaseService timePhaseService;

    @Autowired
    private UserService userService;
    @Autowired
    private NotificationService notificationService;

    @Autowired
    private ConfirmTableService confirmTableService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private StudentService studentService;

    @Autowired
    private StudentVersionService studentVersionService;

    @GetMapping("/list/{lecturerID}")
    public String getList(@PathVariable("lecturerID") Long lecturerID, Model model) {
        String currentAcademicYear = AcademicYearUtil.getCurrentAcademicYear();
        int currentSemester = AcademicYearUtil.getCurrentSemester();
//        List<Student> students = studentRepository.findByLecturerID(lecturerID);
        List<Student> students = studentService.getStudentsByLecturerAndAcademicYearAndSemester(lecturerID, currentAcademicYear, currentSemester);
        model.addAttribute("students", students);

        LocalDate currentDate = LocalDate.now();
//        LocalDate currentDate = LocalDate.of(2023, 9, 10); // Thiết lập ngày là 28/10/2023

        String showColumn = timePhaseService.getPhaseColumn(currentDate);


        model.addAttribute("showColumn", showColumn);
        return "listView";
    }

    @PostMapping("/list/confirm/{messageId}")
    public String confirmList(@PathVariable("messageId") Long messageId,
                              Authentication authentication,
                              RedirectAttributes redirectAttributes) {
        UserDetails senderDetails = (UserDetails) authentication.getPrincipal();
        User currentUser = userService.findByUsername(senderDetails.getUsername());
        String currentAcademicYear = AcademicYearUtil.getCurrentAcademicYear();
        int currentSemester = AcademicYearUtil.getCurrentSemester();

        Optional<Message> optionalMessage = messageRepository.findById(messageId);
        if (optionalMessage.isPresent()) {
            Message message = optionalMessage.get();
            message.setStatusEnum(MessageStatus.ACCEPTED);
            messageRepository.save(message);
            if (message.getStatusEnum() == MessageStatus.ACCEPTED) {
                Long senderId = message.getSenderId();
                    List<Student> studentsToConfirm = studentService.getStudentsByLecturerAndAcademicYearAndSemester(senderId, currentAcademicYear, currentSemester);
                    List<StudentVersion> studentsVersionToConfirm = studentVersionRepository.findByLecturer_IdAndAcademicYearAndSemesterAndVersionType(message.getSenderId(), currentAcademicYear, currentSemester, "Bộ môn");
                    studentVersionRepository.deleteAll(studentsVersionToConfirm);

                    for (Student student : studentsToConfirm) {
                        StudentVersion svTemp = studentVersionRepository.findByVersionTypeAndAcademicYearAndSemesterAndStudentID("Bộ môn", currentAcademicYear, currentSemester, student.getStudentID());
                        if (svTemp == null) {
                            svTemp = new StudentVersion();
                            svTemp.setStudent(student);
                            svTemp.setVersionType("Bộ môn");
                            svTemp.setVersionDate(new Date());
                        }
                        // Cập nhật dữ liệu StudentVersion
                        studentVersionService.updateVersionWithStudentData(svTemp, student);
                        studentVersionService.save(svTemp);
                    }
            }

            Long senderId = message.getSenderId();
            String notificationContent = "Danh sách của bạn với tiêu đề: " + message.getMessageContent() + " đã được xác nhận";
            notificationService.sendNotificationToLecturer(notificationContent, senderId);
        }
        return "redirect:/bo-mon";
    }




    /**
     * Từ chối danh sách
     * @param messageId
     * @return
     */
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
        return "redirect:/bo-mon";
    }

    @GetMapping("/send-confirm-list")
    public String getSendConfirmList(Model model,Authentication authentication,
                                     RedirectAttributes redirectAttributes) {
        UserDetails senderDetails = (UserDetails) authentication.getPrincipal();
        User currentUser = userService.findByUsername(senderDetails.getUsername());

//        List<ConfirmTable> confirmedStudents = confirmTableRepository.findByDepartmentId(currentUser.getUserID());
        List<StudentVersion> confirmedStudents = studentVersionRepository.findByVersionTypeAndLecturer_Department_DepartmentIdAndAcademicYearAndSemester("Bộ môn", currentUser.getOwnerId(), AcademicYearUtil.getCurrentAcademicYear(), AcademicYearUtil.getCurrentSemester());
        model.addAttribute("confirmedStudents", confirmedStudents);
        LocalDate currentDate = LocalDate.now();
//        LocalDate currentDate = LocalDate.of(2023, 9, 10); // Thiết lập ngày là 28/10/2023

        String showColumn = timePhaseService.getPhaseColumn(currentDate);

        // Thêm biến showColumn vào model để truyền tới view
        model.addAttribute("showColumn", showColumn);
        return "send-confirm-list";
    }


    @PostMapping("/send-confirm-list")
    public String postSendConfirmList(@RequestParam("receiverId") Long receiverId,
                                      @RequestParam("messageContent") String messageContent,
                                      Authentication authentication,
                                      RedirectAttributes redirectAttributes) {

        UserDetails senderDetails = (UserDetails) authentication.getPrincipal();
        User senderUser = userService.findByUsername(senderDetails.getUsername());

        Optional<Message> lastMessage = messageRepository.findTopBySenderIdOrderBySentAtDesc(senderUser.getOwnerId());

        if (lastMessage.isPresent() && MessageStatus.UNSEEN == lastMessage.get().getStatusEnum()) {
            redirectAttributes.addFlashAttribute("errorSendMessages", "Bạn cần chờ cho đến khi danh sách trước đó đã được xem!");
            return "redirect:/bo-mon/send-confirm-list";
        }

        Message message = new Message();
        message.setSenderId(senderUser.getOwnerId());
        message.setSenderName(senderUser.getRealname());
        message.setReceiverId(receiverId);
        message.setMessageContent(messageContent);
        message.setSentAt(LocalDateTime.now()); // Thiết lập thời gian gửi
        messageService.saveMessage(message);

        // Thêm flash message để thông báo gửi tin nhắn thành công
        redirectAttributes.addFlashAttribute("successMessage", "Danh sách đã được gửi thành công!");

        return "redirect:/bo-mon/send-confirm-list";
    }

    @GetMapping("/register")
    public String showRegistrationForm() {
        return "register";
    }

    @GetMapping("/{departmentId}")
    public String getLecturersByDepartment(@PathVariable Long departmentId, Model model) {
        List<Lecturer> lecturers = lecturerRepository.findByDepartment_DepartmentId(departmentId);
//        List<Department> lecturers = departmentRepository.findByFacultyId(departmentId);
        model.addAttribute("lecturers", lecturers);
        return "lecturers-list"; // Tên của trang HTML trong thư mục templates
    }
}
