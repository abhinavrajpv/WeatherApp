package com.example.demo.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.example.demo.entity.Audit;
import com.example.demo.repository.AuditRepository;

@Service
public class AuditService {

	private final AuditRepository auditRepository;

	public AuditService(AuditRepository auditRepository) {
		this.auditRepository = auditRepository;
	}

	public void record(String username, String action) {

		Audit audit = new Audit();
		audit.setUsername(username);
		audit.setAction(action);
		audit.setTimestamp(LocalDateTime.now());

		auditRepository.save(audit);

	}

}
