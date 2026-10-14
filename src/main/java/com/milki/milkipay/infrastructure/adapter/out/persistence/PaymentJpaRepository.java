package com.milki.milkipay.infrastructure.adapter.out.persistence;

import com.milki.milkipay.domain.model.PaymentStatus;
import com.milki.milkipay.infrastructure.adapter.out.persistence.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.milki.milkipay.application.port.out.LoadPaymentPort;
import com.milki.milkipay.application.port.out.SavePaymentPort;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Provides database access to {@link PaymentEntity} records.
 */

@Repository
public interface PaymentJpaRepository extends JpaRepository<PaymentEntity, UUID> {

    List<PaymentEntity> findByStatus(PaymentStatus status);

    List<PaymentEntity> findByRequestedAtGreaterThanEqualAndRequestedAtLessThan(Instant from, Instant to);

    List<PaymentEntity> findByMerchantId(String merchantId);
}
