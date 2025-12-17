package com.example.monorepo.billing;

import com.example.monorepo.shared.Money;
import org.springframework.stereotype.Service;

/**
 * Servicio de facturación.
 * Esta es la API pública del módulo billing.
 */
@Service
public class BillingService {

    public Invoice createInvoice(String customerId, Money amount) {
        // Lógica simple de ejemplo
        return new Invoice(
            "INV-" + System.currentTimeMillis(),
            customerId,
            amount
        );
    }

    public record Invoice(String id, String customerId, Money total) {}
}
