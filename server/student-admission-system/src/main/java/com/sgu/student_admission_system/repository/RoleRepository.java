package com.sgu.student_admission_system.repository;

import com.sgu.student_admission_system.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, String> {
}
