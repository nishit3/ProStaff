package com.prostaff.service.notification.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.prostaff.service.notification.entity.Notification;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long>{

}
