-- ============================================================
-- V1 — Initial schema: payments table
-- Author : MilkiPay Team
-- Date   : 2026-10-10
-- ============================================================

CREATE TABLE payments
(
    id UUID NOT NULL,
    merchant_id VARCHAR(255) NOT NULL,
    amount BIGINT NOT NULL,
    currency VARCHAR(3) NOT NULL,
    method VARCHAR(50) NOT NULL,
    description VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL,
    external_reference VARCHAR(255),
    failure_reason TEXT,
    requested_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    refunded_at TIMESTAMPTZ,

    CONSTRAINT pk_payments PRIMARY KEY (id),
    CONSTRAINT chk_payments_amount CHECK (amount > 0),
    CONSTRAINT chk_payments_status CHECK (status IN ('PENDING', 'COMPLETED', 'FAILED', 'REFUNDED')),
    CONSTRAINT chk_payments_method CHECK (method IN ('CREDIT_CARD', 'DEBIT_CARD', 'BANK_QR', 'BANK_TRANSFER'))
);

CREATE INDEX idx_payments_status ON payments(status);
CREATE INDEX idx_payments_merchant_id ON payments(merchant_id);
CREATE INDEX idx_payments_requested_at ON payments(requested_at);

CREATE INDEX idx_payments_merchant_status ON payments(merchant_id, status);

COMMENT ON TABLE payments IS 'Central payments ledger — stores all payment events';
COMMENT ON COLUMN payments.status IS 'Lifecycle state: PENDING → COMPLETED → REFUNDED | PENDING → FAILED';
COMMENT ON COLUMN payments.external_reference IS 'Transaction ID returned by the payment gateway';

