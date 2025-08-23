package io.fptu.sep490.service.impl;

import io.fptu.sep490.model.AuditLog;
import io.fptu.sep490.repository.AuditLogRepository;
import io.fptu.sep490.service.AuditLogService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository auditLogRepository;

    @Transactional
    @Override
    public void logAction(List<AuditLog> logs) {
        auditLogRepository.saveAll(logs);
    }

}
