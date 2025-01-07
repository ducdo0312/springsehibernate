package com.example.springsehibernate.Service;

import com.example.springsehibernate.Entity.AcademicYearUtil;
import com.example.springsehibernate.Entity.QualifiedGraduate;
import com.example.springsehibernate.Entity.QualifiedGraduateConfirm;
import com.example.springsehibernate.Repository.QualifiedGraduateConfirmRepository;
import com.example.springsehibernate.Repository.QualifiedGraduateRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QualifiedGraduateService {

    @Autowired
    private QualifiedGraduateRepository qualifiedGraduateRepository;

    @Autowired
    private QualifiedGraduateConfirmRepository qualifiedGraduateConfirmRepository;

    @Autowired
    private QualifiedGraduateConfirmService qualifiedGraduateConfirmService;

    public String importFromExcel(MultipartFile file, String academicYear, Integer semester) {

        // Kiểm tra xem liệu dữ liệu đã tồn tại
        if (qualifiedGraduateRepository.existsByAcademicYearAndSemester(academicYear, semester)) {
            // Dữ liệu đã tồn tại
            return "data-exists";
        } else {
            // Không có dữ liệu tồn tại, tiến hành import
            try {
                Workbook workbook = new XSSFWorkbook(file.getInputStream());
                Sheet sheet = workbook.getSheetAt(0);
                List<QualifiedGraduate> graduates = new ArrayList<>();
                for (Row row : sheet) {
                    // Skip the header row
                    if (row.getRowNum() == 0) continue;

                    QualifiedGraduate graduate = new QualifiedGraduate();
                    graduate.setStudentId((long) row.getCell(0).getNumericCellValue());
                    graduate.setName(row.getCell(1).getStringCellValue());
                    Cell dateCell = row.getCell(2);
                    if (dateCell != null) {
                        if (dateCell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(dateCell)) {
                            graduate.setDateOfBirth(dateCell.getDateCellValue());
                        } else if (dateCell.getCellType() == CellType.STRING) {
                            String dateCellValue = dateCell.getStringCellValue();
                            // Phân tích chuỗi ngày tháng ở đây và chuyển đổi nó thành một đối tượng Date
                            try {
                                Date date = new SimpleDateFormat("dd/MM/yyyy").parse(dateCellValue);
                                graduate.setDateOfBirth(date);
                            } catch (ParseException e) {
                                // Log lỗi hoặc xử lý chuỗi ngày không hợp lệ
                            }
                        }
                    }
                    graduate.setNameClass(row.getCell(3).getStringCellValue());
                    graduate.setAccumulatedCredits((int) row.getCell(4).getNumericCellValue());
                    graduate.setTbc((float) row.getCell(5).getNumericCellValue());
                    graduate.setAcademicYear(academicYear);
                    graduate.setSemester(semester);
                    graduates.add(graduate);
                }
                qualifiedGraduateRepository.saveAll(graduates);
                return "Thêm dữ liệu thành công";
            } catch (IOException e) {
                e.printStackTrace();
                return "Failed to import file: " + e.getMessage();
            }
        }
    }
    // Thêm phương thức overwriteData nếu cần để xử lý việc ghi đè dữ liệu
    public String overwriteData(byte[] file, String academicYear, Integer semester) {
        // Xóa dữ liệu hiện tại cho năm học và học kỳ cụ thể
        List<QualifiedGraduate> existingGraduates = qualifiedGraduateRepository.findByAcademicYearAndSemester(academicYear, semester);
        qualifiedGraduateRepository.deleteAll(existingGraduates);

        try (Workbook workbook = new XSSFWorkbook(new ByteArrayInputStream(file))) {
            Sheet sheet = workbook.getSheetAt(0);
            List<QualifiedGraduate> graduates = new ArrayList<>();
            for (Row row : sheet) {
                // Skip the header row
                if (row.getRowNum() == 0) continue;

                QualifiedGraduate graduate = new QualifiedGraduate();
                graduate.setStudentId((long) row.getCell(0).getNumericCellValue());
                graduate.setName(row.getCell(1).getStringCellValue());
                Cell dateCell = row.getCell(2);
                if (dateCell != null) {
                    if (dateCell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(dateCell)) {
                        graduate.setDateOfBirth(dateCell.getDateCellValue());
                    } else if (dateCell.getCellType() == CellType.STRING) {
                        String dateCellValue = dateCell.getStringCellValue();
                        // Phân tích chuỗi ngày tháng ở đây và chuyển đổi nó thành một đối tượng Date
                        try {
                            Date date = new SimpleDateFormat("dd/MM/yyyy").parse(dateCellValue);
                            graduate.setDateOfBirth(date);
                        } catch (ParseException e) {
                            // Log lỗi hoặc xử lý chuỗi ngày không hợp lệ
                        }
                    }
                }
                graduate.setNameClass(row.getCell(3).getStringCellValue());
                graduate.setAccumulatedCredits((int) row.getCell(4).getNumericCellValue());
                graduate.setTbc((float) row.getCell(5).getNumericCellValue());
                graduate.setAcademicYear(academicYear);
                graduate.setSemester(semester);
                graduates.add(graduate);
            }
            qualifiedGraduateRepository.saveAll(graduates);
            return "Dữ liệu đã được ghi đè thành công.";
        } catch (IOException e) {
            e.printStackTrace();
            return "Failed to import file: " + e.getMessage();
        }
    }

    public Page<QualifiedGraduate> getQualifiedGraduatesPage(int pageNumber, int pageSize, String academicYear,
                                                             Integer semester) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        String currentAcademicYear = AcademicYearUtil.getCurrentAcademicYear();
        int currentSemester = AcademicYearUtil.getCurrentSemester();
        if (academicYear != null && semester != null) {
            return qualifiedGraduateRepository.findByAcademicYearAndSemester(academicYear, semester, pageable);
        } else {
            // Nếu không có thông tin về năm học và kỳ học, trả về danh sách theo năm học hiện tại
            return qualifiedGraduateRepository.findByAcademicYearAndSemester(currentAcademicYear, currentSemester, pageable);
        }
    }

    public Page<QualifiedGraduate> getAllQualifiedGraduatesPage(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        return qualifiedGraduateRepository.findAll(pageable);
    }

    public boolean canUploadNewFile() {
        // Kiểm tra xem bảng có chứa bất kỳ bản ghi nào không
        return !qualifiedGraduateRepository.existsByIdIsNotNull();
    }

    public boolean checkIfExists(String academicYear, Integer semester) {
        return qualifiedGraduateRepository.existsByAcademicYearAndSemester(academicYear, semester);
    }

    @Transactional
    public String confirmDataAndTransfer() {
        // Truy vấn tất cả bản ghi từ bảng QualifiedGraduate
        List<QualifiedGraduate> graduates = qualifiedGraduateRepository.findAll();

        List<QualifiedGraduateConfirm> confirmedGraduates = new ArrayList<>();

        for(QualifiedGraduate graduate :graduates) {
            // Kiểm tra trùng lặp cho từng bản ghi
            boolean isDuplicate = qualifiedGraduateConfirmService.checkDuplicate(graduate.getAcademicYear(), graduate.getSemester());
            if (!isDuplicate) {
                // Chuyển dữ liệu nếu không trùng lặp
                QualifiedGraduateConfirm confirm = new QualifiedGraduateConfirm(graduate);
                confirmedGraduates.add(confirm);
            } else {
               return "Danh sách đã tồn tại trong bảng với năm học" + " " + graduate.getAcademicYear()
                       + " kỳ học: " + graduate.getSemester();
            }
        }
        // Lưu vào bảng chính thức chỉ những bản ghi không trùng lặp
        if (!confirmedGraduates.isEmpty()) {
            qualifiedGraduateConfirmRepository.saveAll(confirmedGraduates);
        }

        // Cập nhật trạng thái hoặc xóa bản ghi trong bảng tạm
        qualifiedGraduateRepository.deleteAll(graduates);

        return "Xác nhận dữ liệu thành công";
    }

    public void deleteQualifiedGraduate() {
        qualifiedGraduateRepository.deleteAll();
    }

}