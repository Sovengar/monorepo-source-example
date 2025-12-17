package com.example.monorepo.inventory;

import com.example.monorepo.shared.Money;
import org.springframework.stereotype.Service;

/**
 * Servicio de inventario.
 * Esta es la API pública del módulo inventory.
 */
@Service
public class InventoryService {

    public Product findProduct(String productId) {
        // Lógica simple de ejemplo
        return new Product(
                productId,
                "Product " + productId,
                Money.euros(99.99),
                10);
    }

    public boolean reserveStock(String productId, int quantity) {
        // Simula reserva de stock
        return quantity <= 10;
    }

    public record Product(String id, String name, Money price, int stock) {
    }
}
