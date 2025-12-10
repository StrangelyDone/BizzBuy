package com.example.BizzBuy.repository;

import com.example.BizzBuy.model.Notification;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends MongoRepository<Notification, Long> {
    List<Notification> findByUserIdOrderByTimestampDesc(Long userId);

    List<Notification> findByUserIdAndReadFalseOrderByTimestampDesc(Long userId);

    long countByUserIdAndReadFalse(Long userId);
}
