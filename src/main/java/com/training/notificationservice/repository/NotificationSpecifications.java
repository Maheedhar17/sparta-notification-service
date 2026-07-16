package com.training.notificationservice.repository;

import com.training.notificationservice.entity.Notification;
import com.training.notificationservice.enums.NotificationChannel;
import com.training.notificationservice.enums.NotificationStatus;
import org.springframework.data.jpa.domain.Specification;

/**
 * Specification pattern: composes the optional {@code recipient}/{@code status}/
 * {@code channel} search filters into a single JPA query, instead of a combinatorial
 * explosion of derived repository methods (one per filter combination).
 */
public final class NotificationSpecifications {

    private NotificationSpecifications() {
    }

    public static Specification<Notification> withRecipient(String recipient) {
        return (root, query, criteriaBuilder) -> recipient == null ? null
                : criteriaBuilder.equal(root.get("recipient"), recipient);
    }

    public static Specification<Notification> withStatus(NotificationStatus status) {
        return (root, query, criteriaBuilder) -> status == null ? null
                : criteriaBuilder.equal(root.get("status"), status);
    }

    public static Specification<Notification> withChannel(NotificationChannel channel) {
        return (root, query, criteriaBuilder) -> channel == null ? null
                : criteriaBuilder.equal(root.get("channel"), channel);
    }

    public static Specification<Notification> filterBy(String recipient, NotificationStatus status,
                                                         NotificationChannel channel) {
        return Specification.where(withRecipient(recipient))
                .and(withStatus(status))
                .and(withChannel(channel));
    }
}
