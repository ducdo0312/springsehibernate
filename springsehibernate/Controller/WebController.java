package com.example.springsehibernate.Controller;

import com.example.springsehibernate.Entity.*;
import com.example.springsehibernate.Repository.LecturerRepository;
import com.example.springsehibernate.Repository.MessageRepository;
import com.example.springsehibernate.Service.MessageService;
import com.example.springsehibernate.Service.NewsService;
import com.example.springsehibernate.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class WebController {

    @Autowired
    UserService userService;

    @Autowired
    MessageService messageService;

    @Autowired
    private NewsService newsService;
    @Autowired
    MessageRepository messageRepository;

    @Autowired
    private LecturerRepository lecturerRepository;

    @GetMapping("/login")
    public String login(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            return "redirect:/home";
        }
        return "login";
    }


    @GetMapping("/giang-vien")
    public ModelAndView homeGiangVien() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("giang-vien.html");
        return modelAndView;
    }

    @PostMapping("/sendMessage")
    public String sendMessage(@RequestParam("receiverId") Long receiverId,
                              @RequestParam("messageContent") String messageContent,
                              Authentication authentication,
                              RedirectAttributes redirectAttributes) {

        UserDetails senderDetails = (UserDetails) authentication.getPrincipal();
        User sender = userService.findByUsername(senderDetails.getUsername());
        Optional<Lecturer> lecturer = lecturerRepository.findById(sender.getOwnerId());

        Optional<Message> lastMessage = messageRepository.findTopBySenderIdOrderBySentAtDesc(sender.getOwnerId());

        if (lastMessage.isPresent() && MessageStatus.UNSEEN == lastMessage.get().getStatusEnum()) {
            redirectAttributes.addFlashAttribute("errorSendMessages", "Bạn cần chờ cho đến khi danh sách trước đó đã được xem!");
            return "redirect:/students";
        }

        Message message = new Message();
        message.setSenderId(sender.getOwnerId());
        message.setSenderName(sender.getRealname());
        if (lecturer.isPresent()) {
            message.setReceiverId(lecturer.get().getDepartment().getDepartmentId());
        }
        message.setMessageContent(messageContent);
        message.setSentAt(LocalDateTime.now()); // Thiết lập thời gian gửi
        messageService.saveMessage(message);

        // Thêm flash message để thông báo gửi tin nhắn thành công
        redirectAttributes.addFlashAttribute("successMessage", "Danh sách đã được gửi thành công!");

        return "redirect:/students";
    }



    @GetMapping("/bo-mon")
    public String homeBoMon(Model model, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userService.findByUsername(userDetails.getUsername());
        List<Message> messages = messageService.getMessagesForUser(user.getOwnerId());
        model.addAttribute("messages", messages);
        return "bo-mon";
    }

    @GetMapping("/khoa")
    public String homeKhoa(Model model, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userService.findByUsername(userDetails.getUsername());
        List<Message> messages = messageService.getMessagesForUser(user.getOwnerId());
        model.addAttribute("messages", messages);
        return "khoa";
    }

    @GetMapping("/notify")
    public ModelAndView NotifyView(Authentication authentication) {
        ModelAndView modelAndView = new ModelAndView();

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userService.findByUsername(userDetails.getUsername());

        modelAndView.addObject("accountId", user.getOwnerId());
        modelAndView.setViewName("notify.html");

        return modelAndView;
    }
    @GetMapping(value = {"/", "/home"})
    public String home(Model model, Authentication authentication, HttpServletResponse response) {

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userService.findByUsername(userDetails.getUsername());
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        List<String> roles = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        model.addAttribute("accountId", user.getOwnerId());
        model.addAttribute("roles", roles);
        model.addAttribute("newsList", newsService.findAll());
        if (roles.contains("ROLE_GIANGVIEN")) {
            return "redirect:/giang-vien/home";
        } else {
            return "home";
        }
    }




}
