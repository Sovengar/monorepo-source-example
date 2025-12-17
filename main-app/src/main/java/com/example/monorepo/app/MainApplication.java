package com.example.monorepo.app;

import com.example.monorepo.billing.BillingService;
import com.example.monorepo.inventory.InventoryService;
import com.example.monorepo.shared.Money;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * Aplicación principal que demuestra la comunicación entre módulos.
 * 
 * La comunicación es por FUNCTION CALLS (llamadas directas a métodos)
 * porque todos los módulos corren en el mismo proceso/JVM.
 */
@SpringBootApplication(scanBasePackages = "com.example.monorepo")
public class MainApplication {

    public static void main(String[] args) {
        SpringApplication.run(MainApplication.class, args);
    }

    @Bean
    CommandLineRunner demo(BillingService billingService, InventoryService inventoryService) {
        return args -> {
            System.out.println("\n========================================");
            System.out.println("  MONOREPO - Source Code Boundary Demo");
            System.out.println("========================================\n");

            // Llamada directa al módulo inventory (function call, no HTTP)
            var product = inventoryService.findProduct("PROD-001");
            System.out.println("📦 Producto encontrado: " + product);

            // Reservar stock (function call)
            boolean reserved = inventoryService.reserveStock("PROD-001", 2);
            System.out.println("📋 Stock reservado: " + reserved);

            // Crear factura usando el módulo billing (function call)
            var invoice = billingService.createInvoice("CUST-123", product.price());
            System.out.println("🧾 Factura creada: " + invoice);

            System.out.println("\n✅ Comunicación entre módulos por llamadas directas a funciones.");
            System.out.println("   Todos los módulos corren en la misma JVM.\n");
        };
    }
}
