package com.example.springsehibernate.Service;

import com.example.springsehibernate.Entity.TimePhase;
import com.example.springsehibernate.Repository.TimePhaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service //
public class TimePhaseService {

    @Autowired
    private TimePhaseRepository timePhaseRepository;

    public String getPhaseColumn(LocalDate currentDate) {
        TimePhase timePhase = timePhaseRepository.findCurrentTimePhase(currentDate).orElse(null);

        if (timePhase == null) {
            return null; // hoặc bất kỳ giá trị mặc định nào bạn muốn trả về khi không tìm thấy timePhase
        }

        if ((currentDate.isAfter(timePhase.getPhase1Start()) || currentDate.isEqual(timePhase.getPhase1Start()))
                && currentDate.isBefore(timePhase.getPhase1End())) {
            return "Phase1";
        } else if ((currentDate.isAfter(timePhase.getPhase2Start()) || currentDate.isEqual(timePhase.getPhase2Start()))
                && currentDate.isBefore(timePhase.getPhase2End())) {
            return "Phase2";
        } else if ((currentDate.isAfter(timePhase.getPhase3Start()) || currentDate.isEqual(timePhase.getPhase3Start()))
                && currentDate.isBefore(timePhase.getPhase3End())) {
            return "Phase3";
        }

        return null; // hoặc giá trị mặc định khác khi không khớp với bất kỳ giai đoạn nào
    }
}
