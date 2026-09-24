package com.sahyadri.sahyadripooltrip.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.sahyadri.sahyadripooltrip.entity.ContactMessage;
@Repository
public interface ContactMessageRepository extends JpaRepository<ContactMessage, Long> {}
