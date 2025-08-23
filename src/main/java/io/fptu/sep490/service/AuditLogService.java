package io.fptu.sep490.service;

import io.fptu.sep490.model.AuditLog;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface AuditLogService {

    @Transactional
    void logAction(List<AuditLog> logs);

}
