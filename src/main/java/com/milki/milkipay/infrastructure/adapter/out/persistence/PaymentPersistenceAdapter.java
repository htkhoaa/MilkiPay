package com.milki.milkipay.infrastructure.adapter.out.persistence;

import com.milki.milkipay.application.port.out.LoadPaymentPort;
import com.milki.milkipay.application.port.out.SavePaymentPort;
import com.milki.milkipay.domain.model.Payment;
import com.milki.milkipay.domain.model.PaymentStatus;
import com.milki.milkipay.domain.model.vo.PaymentId;
import com.milki.milkipay.infrastructure.adapter.out.persistence.mapper.PaymentPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PaymentPersistenceAdapter implements LoadPaymentPort, SavePaymentPort {

    private final PaymentJpaRepository repository;
    private final PaymentPersistenceMapper mapper;

    @Override
    public Optional<Payment> findById(PaymentId id) {
        return repository.findById(id.value())
                .map(mapper::toDomain);
    }

    @Override
    public List<Payment> findByStatus(PaymentStatus status) {
        return repository.findByStatus(status)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Payment> findByDateRange(Instant from, Instant to) {
        return repository.findByRequestedAtGreaterThanEqualAndRequestedAtLessThan(from, to)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Payment> findByMerchantId(String merchantId) {
        return repository.findByMerchantId(merchantId)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Payment save(Payment payment) {
        var entity = mapper.toEntity(payment);
        var saved = repository.save(entity);
        return mapper.toDomain(saved);
    }
}
