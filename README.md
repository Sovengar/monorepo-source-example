# Monorepo Demo - Source Code Boundary

Ejemplo de un **Monorepo** con desacoplamiento a nivel de **Source Code** usando Maven Multi-module.

## Estructura del Monorepo

```
cryo-crab/
├── pom.xml                          ◄── Parent POM (orquesta módulos)
│
├── shared-module/                   ◄── Módulo base (sin dependencias internas)
│   ├── pom.xml
│   └── src/.../Money.java
│
├── billing-module/                  ◄── Módulo de negocio
│   ├── pom.xml                          (depende de shared)
│   └── src/.../BillingService.java
│
├── inventory-module/                ◄── Módulo de negocio
│   ├── pom.xml                          (depende de shared)
│   └── src/.../InventoryService.java
│
└── main-app/                        ◄── Aplicación que ensambla todo
    ├── pom.xml                          (depende de billing e inventory)
    └── src/.../MainApplication.java
```

## Características del Source Code Boundary

| Aspecto | Valor |
|---|---|
| **Versionado** | Todos los módulos usan `${project.version}` del parent |
| **Compilación** | `mvn clean install` desde la raíz compila todo junto |
| **Comunicación** | Function calls (llamadas directas in-process) |
| **Despliegue** | Un único `main-app.jar` (fat JAR) |
| **JVM** | Todos corren en la misma JVM |
| **Dependencias** | Comparten las mismas versiones (Spring Boot, Java, etc.) |

## Grafo de Dependencias

```
                 ┌──────────────┐
                 │  main-app    │
                 └──────┬───────┘
                        │
          ┌─────────────┼─────────────┐
          ▼             ▼             │
   ┌─────────────┐ ┌─────────────┐    │
   │   billing   │ │  inventory  │    │
   └──────┬──────┘ └──────┬──────┘    │
          │               │           │
          └───────┬───────┘           │
                  ▼                   │
           ┌─────────────┐            │
           │   shared    │◄───────────┘
           └─────────────┘
```

## Uso

```bash
# Compilar todo desde la raíz
mvn clean install

# Ejecutar la aplicación
mvn spring-boot:run -pl main-app

# O ejecutar el JAR directamente
java -jar main-app/target/main-app-1.0.0-SNAPSHOT.jar
```

## ⚙️ Cómo se Vinculan los Módulos (La Magia del POM)

### 1. Parent POM: Declara los módulos

```xml
<!-- pom.xml (raíz) -->
<modules>
    <module>shared-module</module>      <!-- Orden importa: primero las dependencias -->
    <module>billing-module</module>
    <module>inventory-module</module>
    <module>main-app</module>
</modules>
```

Esto le dice a Maven: "cuando compile desde la raíz, incluye estos 4 subproyectos".

### 2. Parent POM: Centraliza versiones con `dependencyManagement`

```xml
<!-- pom.xml (raíz) -->
<dependencyManagement>
    <dependencies>
        <!-- Módulos internos - TODOS usan ${project.version} -->
        <dependency>
            <groupId>com.example.monorepo</groupId>
            <artifactId>shared-module</artifactId>
            <version>${project.version}</version>   <!-- 👈 La clave! -->
        </dependency>
        <!-- ... más módulos ... -->
    </dependencies>
</dependencyManagement>
```

**`${project.version}`** = Todos los módulos siempre usan la misma versión. Si cambias la versión del parent, todos cambian.

### 3. Submódulo: Hereda del parent y declara dependencias

```xml
<!-- billing-module/pom.xml -->
<parent>
    <groupId>com.example.monorepo</groupId>
    <artifactId>monorepo-parent</artifactId>
    <version>1.0.0-SNAPSHOT</version>      <!-- Hereda del parent -->
</parent>

<artifactId>billing-module</artifactId>

<dependencies>
    <!-- No necesita especificar versión! La toma del dependencyManagement -->
    <dependency>
        <groupId>com.example.monorepo</groupId>
        <artifactId>shared-module</artifactId>   <!-- Sin <version> -->
    </dependency>
</dependencies>
```

### Resumen de la Vinculación

| Archivo | Responsabilidad |
|---|---|
| `pom.xml` (parent) | Define `<modules>` + `<dependencyManagement>` |
| `*/pom.xml` (submódulos) | `<parent>` hereda + `<dependencies>` sin versión |
| `${project.version}` | Garantiza que todos usen la misma versión |

---

## Niveles de Desacoplamiento

Este proyecto demuestra **Source Code Boundary**. Existen 4 niveles:

| Nivel | Descripción | ¿Aplica aquí? |
|---|---|---|
| **Package/Folder** | Separación por carpetas/paquetes | ✅ |
| **Source Code** | Módulos Maven separados, misma versión | ✅ Este ejemplo |
| **Binary** | Artefactos versionados independientemente | ❌ |
| **Executable** | Procesos/JVMs separadas | ❌ |

## Comunicación entre Módulos

La comunicación es por **llamadas directas a funciones** (no HTTP):

```java
// MainApplication.java
@Bean
CommandLineRunner demo(BillingService billingService, InventoryService inventoryService) {
    return args -> {
        // Llamada directa al módulo inventory (function call, no HTTP)
        var product = inventoryService.findProduct("PROD-001");
        
        // Crear factura usando el módulo billing (function call)
        var invoice = billingService.createInvoice("CUST-123", product.price());
    };
}
```
