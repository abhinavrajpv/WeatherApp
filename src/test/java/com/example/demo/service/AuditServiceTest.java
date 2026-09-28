package com.example.demo.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.demo.entity.Audit;
import com.example.demo.repository.AuditRepository;

@ExtendWith(MockitoExtension.class)
public class AuditServiceTest {
	@Mock
	private AuditRepository auditRepository;
	@InjectMocks
	private AuditService auditService;

	@Test
	void record_shouldSaveAudit() {
		auditService.record("testuser", "Viewed weather");
		verify(auditRepository, times(1)).save(any(Audit.class));
	}

	@Test
	void record_shouldSaveEachAuditSeparately() {
		auditService.record("user1", "Action 1");
		auditService.record("user2", "Action 2");
		verify(auditRepository, times(2)).save(any(Audit.class));
	}
}