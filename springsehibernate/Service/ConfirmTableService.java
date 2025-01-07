package com.example.springsehibernate.Service;

import com.example.springsehibernate.Entity.ConfirmTable;
import com.example.springsehibernate.Repository.ConfirmTableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ConfirmTableService {
    @Autowired
    ConfirmTableRepository confirmTableRepository;
    public void saveConfirmTable(ConfirmTable confirmTable) {
        confirmTableRepository.save(confirmTable);
    }
}
