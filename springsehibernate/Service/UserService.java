package com.example.springsehibernate.Service;

import com.example.springsehibernate.Security.AuthenticationFacade;
import com.example.springsehibernate.DTO.UserRegistrationDto;
import com.example.springsehibernate.Entity.*;
import com.example.springsehibernate.Repository.DepartmentRepository;
import com.example.springsehibernate.Repository.LecturerRepository;
import com.example.springsehibernate.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Role;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    @Lazy
    private PasswordEncoder passwordEncoder;

    @Autowired
    private LecturerRepository lecturerRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private AuthenticationFacade authenticationFacade; // Dịch vụ để lấy thông tin người dùng hiện tại
    @Override
    public UserDetails loadUserByUsername(String username) {
        // Kiểm tra xem user có tồn tại trong database không?
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException(username);
        }
        return new CustomUserDetails(user);
    }

    public User findByUsername(String username) {
        // Sử dụng phương thức findByUsername từ repository
        return userRepository.findByUsername(username);
        // Nếu bạn muốn xử lý trường hợp không tìm thấy tài khoản,
        // bạn có thể thay thế orElse(null) bằng cách thức khác.
    }

    // JWTAuthenticationFilter sẽ sử dụng hàm này
    @Transactional
    public UserDetails loadUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new UsernameNotFoundException("User not found with id : " + id)
        );

        return new CustomUserDetails(user);
    }

    @Transactional
    public User registerNewUser(UserRegistrationDto registrationDto) {
        if (userRepository.findByUsername(registrationDto.getUsername()) != null) {
            // Xử lý trường hợp tên người dùng đã tồn tại
            throw new IllegalStateException("Tên đăng nhập đã tồn tại");
        }

        System.out.println(registrationDto.getOwnerID());
        User newUser = new User();
        newUser.setUsername(registrationDto.getUsername());
        newUser.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        newUser.setRealname(registrationDto.getRealname());
        newUser.setOwnerId(registrationDto.getOwnerID());
        newUser.setRole(registrationDto.getRole());

        if (registrationDto.getRole() == RoleEnum.GiangVien) {
            // Lấy ownerId từ người dùng hiện tại
            Long currentUserId = authenticationFacade.getCurrentUser().getOwnerId();

            Department department = departmentRepository.findById(currentUserId)
                    .orElseThrow(() -> new EntityNotFoundException("Department not found"));

            Lecturer newLecturer = new Lecturer();
            newLecturer.setId(newUser.getOwnerId());
            newLecturer.setDepartment(department);
            newLecturer.setName(newUser.getRealname());
            lecturerRepository.save(newLecturer);
        } else if (registrationDto.getRole() == RoleEnum.BoMon) {
            // Lấy ownerId từ người dùng hiện tại
            Long currentUserId = authenticationFacade.getCurrentUser().getOwnerId();

            Department newDepartment = new Department();
            newDepartment.setDepartmentId(newUser.getOwnerId());
            newDepartment.setFacultyId(currentUserId);
            newDepartment.setName(newUser.getRealname());
            departmentRepository.save(newDepartment);
        }

        return userRepository.save(newUser);
    }

    public boolean checkOwnerIdExists(Long ownerId) {
        return userRepository.existsByOwnerId(ownerId);
    }
}