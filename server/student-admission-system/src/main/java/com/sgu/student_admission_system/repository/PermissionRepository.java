package com.sgu.student_admission_system.repository;

import com.sgu.student_admission_system.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PermissionRepository extends JpaRepository<Permission, String> {
}
