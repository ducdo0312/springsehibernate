package com.example.springsehibernate.Entity;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

public class AcademicYearUtil {
    public static String getCurrentAcademicYear() {
        LocalDate today = LocalDate.now();
        Month month = LocalDate.now().getMonth();
        int startYear;
        int endYear;
        if (today.getMonthValue() >= 6) {
            startYear = today.getYear();
            endYear = today.getYear() + 1;
        } else {
            startYear = today.getYear() - 1;
            endYear = today.getYear();
        }
        return startYear + "-" + endYear;
    }

    public static int getCurrentSemester() {
        Month month = LocalDate.now().getMonth();
        if (month.getValue() >= 6 && month.getValue() <= 12) {
            return 1;
        } else if ((month.getValue() >= 1 && month.getValue() <= 5)) {
            return 2;
        }
        else {
            return 0;
        }
    }

    public static List<String> generateAcademicYearsList() {
        List<String> academicYears = new ArrayList<>();
        LocalDate today = LocalDate.now();
        // Điểm bắt đầu cố định cho hệ thống
        int systemStartYear = 2023;
        // Xác định năm học hiện tại dựa trên tháng hiện tại
        int currentYear = (today.getMonthValue() >= 5) ? today.getYear() : today.getYear() - 1;
        // Tính số lượng năm từ năm bắt đầu của hệ thống đến năm hiện tại
        int yearsSinceStart = currentYear - systemStartYear;

        // Tạo danh sách năm học, bắt đầu từ năm bắt đầu của hệ thống và thêm một năm cho mỗi năm kể từ đó
        for (int i = 0; i <= yearsSinceStart + 1; i++) { // Thêm 1 để bao gồm cả năm học tiếp theo
            academicYears.add((systemStartYear + i) + "-" + (systemStartYear + i + 1));
        }

        return academicYears;
    }

}
