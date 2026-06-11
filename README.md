# 🚲 API REST — Sistema de Alquiler de Bicicletas

API REST construida con **Java + Spring Boot** para gestionar el sistema de alquiler
de bicicletas de una empresa de turismo urbano. Permite registrar bicicletas, iniciar
y finalizar alquileres, calcular costos automáticamente según el tipo de bicicleta,
aplicar multas por devolución tardía, y consultar disponibilidad e historial.

---

## Tecnologías

| Tecnología | Versión |
|---|---|
| Java | 17 LTS |
| Spring Boot | 3.5.14 |
| Spring Data JPA + Hibernate | — |
| H2 Database (en memoria) | — |
| Lombok | — |
| JUnit 5 + Mockito | — |
| Maven | — |

## Dependencias (pom.xml)

| Dependencia | Propósito |
|---|---|
| `spring-boot-starter-web` | Exposición de endpoints REST con Tomcat embebido |
| `spring-boot-starter-data-jpa` | Acceso a datos con Hibernate y Spring Data |
| `h2` | Base de datos en memoria para desarrollo y tests |
| `lombok` | Eliminación de código boilerplate (getters, setters, constructores) |
| `spring-boot-starter-validation` | Validación de DTOs con anotaciones (@NotBlank, @NotNull, @Min) |
| `spring-boot-starter-test` | JUnit 5 + Mockito para pruebas unitarias |

---

## Arquitectura

Se eligió una **arquitectura en capas** (Layered Architecture):

```
Controller → Service → Repository → Entity
```

```
src/main/java/com/bicicletas/bicicletas/
├── controller/       ← Endpoints HTTP, manejo de requests/responses
├── service/          ← Lógica de negocio y reglas (RN-01 a RN-05)
├── repository/       ← Acceso a datos con Spring Data JPA
├── model/
│   ├── entity/       ← Entidades JPA mapeadas a tablas
│   └── enums/        ← TipoBicicleta, EstadoBicicleta
├── dto/
│   ├── request/      ← Objetos de entrada (lo que recibe la API)
│   └── response/     ← Objetos de salida (lo que devuelve la API)
├── exception/        ← Excepciones personalizadas y handler global
└── config/           ← DataLoader y SecurityConfig
```

**¿Por qué arquitectura en capas?**

Para una API CRUD con reglas de negocio bien definidas y alcance acotado,
la arquitectura en capas es la elección más directa, mantenible y justificada:

- **Separación de responsabilidades:** cada capa tiene una única razón para
  cambiar (SRP). El Controller no contiene lógica de negocio; el Service no
  accede directamente a la BD.
- **Testabilidad:** el Service con todas las reglas de negocio se prueba de
  forma completamente aislada con Mockito, sin levantar el contexto HTTP ni
  la base de datos.
- **Mantenibilidad:** cualquier desarrollador puede ubicarse en el código
  inmediatamente sin conocer el sistema completo.

**¿Por qué no arquitectura hexagonal o CQRS?**

Hexagonal y CQRS añadirían capas de abstracción (puertos, adaptadores,
comandos, queries) que no aportan valor real para este tamaño de problema.
Aplicarlas aquí sería over-engineering, una decisión de diseño que el
enunciado evalúa explícitamente.

**Principios aplicados:**
- **SRP:** una responsabilidad por clase
- **DRY:** cálculo de tarifas y multas centralizado en un único método
- **Fail Fast:** validaciones al inicio de cada operación antes de cualquier procesamiento

---

## Ejecutar localmente

### Prerrequisitos
- Java 17+
- Maven (incluido con `./mvnw`)

### Pasos

```bash
# 1. Clonar el repositorio
git clone https://github.com/[tu-usuario]/bicicletas-api.git
cd bicicletas-api

# 2. Configurar application.properties
cp src/main/resources/application.properties.example src/main/resources/application.properties

# 3. La API Key ya está configurada en application.properties.example
#    Para pruebas usar: bicicletas-api-key-2026

# 4. Correr el proyecto
./mvnw spring-boot:run
```

La API estará disponible en `http://localhost:8080`

**Consola H2** (ver datos en el navegador): `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:bicicletasdb`
- Usuario: `sa` Contraseña: (vacía)

Los 5 datos de ejemplo se cargan automáticamente al iniciar la aplicación.

---

## Seguridad

Todas las requests requieren el header:

```
X-API-KEY: bicicletas-api-key-2026
```

> **API Key para pruebas:** `bicicletas-api-key-2026`

Sin el header `401 Unauthorized`

---

## Endpoints

### Bicicletas

| Método | Ruta | Descripción |
|---|---|---|
| POST | `/api/bicicletas` | Registrar bicicleta nueva |
| GET | `/api/bicicletas/disponibles` | Listar bicicletas disponibles |
| GET | `/api/bicicletas/disponibles?tipo=URBANA` | Filtrar disponibles por tipo |
| GET | `/api/bicicletas/{codigo}/historial` | Historial de alquileres de una bicicleta |

### Alquileres

| Método | Ruta | Descripción |
|---|---|---|
| POST | `/api/alquileres` | Iniciar un alquiler |
| PATCH | `/api/alquileres/{id}/finalizar` | Finalizar alquiler y calcular costo |

---

## Ejemplos de peticiones

### Registrar bicicleta
```bash
curl -X POST http://localhost:8080/api/bicicletas \
  -H "Content-Type: application/json" \
  -H "X-API-KEY: bicicletas-api-key-2026" \
  -d '{"codigo":"BIC-006","tipo":"URBANA","estado":"DISPONIBLE"}'
```

### Consultar disponibles
```bash
curl http://localhost:8080/api/bicicletas/disponibles \
  -H "X-API-KEY: bicicletas-api-key-2026"
```

### Consultar disponibles por tipo
```bash
curl "http://localhost:8080/api/bicicletas/disponibles?tipo=MONTANA" \
  -H "X-API-KEY: bicicletas-api-key-2026"
```

### Iniciar alquiler
```bash
curl -X POST http://localhost:8080/api/alquileres \
  -H "Content-Type: application/json" \
  -H "X-API-KEY: bicicletas-api-key-2026" \
  -d '{"codigoBicicleta":"BIC-001","nombreCliente":"Ana Garces","duracionEstimadaHoras":2}'
```

### Finalizar alquiler
```bash
curl -X PATCH http://localhost:8080/api/alquileres/1/finalizar \
  -H "X-API-KEY: bicicletas-api-key-2026"
```

### Historial de una bicicleta
```bash
curl http://localhost:8080/api/bicicletas/BIC-001/historial \
  -H "X-API-KEY: bicicletas-api-key-2026"
```

---

## Tests

```bash
./mvnw test
```

8 tests unitarios cubriendo:
- Cálculo de costo base con redondeo al alza
- Cálculo de multa por devolución tardía (ejemplo exacto del enunciado)
- Caso sin multa, devolución antes de tiempo
- Caso sin multa, devolución exactamente a tiempo
- Tarifa correcta por tipo de bicicleta (ELÉCTRICA)
- Rechazo de alquiler sobre bicicleta no disponible
- Rechazo de finalización de alquiler ya finalizado
- Flujo completo de inicio de alquiler

---

## Reglas de negocio

| Tipo | Tarifa/hora |
|---|---|
| URBANA | $3.500 |
| MONTAÑA | $5.000 |
| ELÉCTRICA | $7.500 |

**Costo base:** horas reales redondeadas al alza (`Math.ceil`) × tarifa/hora

**Multa:** horas de retraso redondeadas al alza × (tarifa × 50%)

**Ejemplo del enunciado:**
Bicicleta MONTAÑA, estimada 2h, devuelta a las 3h 20min:
- Costo base: `ceil(200/60) = 4h × $5.000 = $20.000`
- Retraso: `200 - 120 = 80 min → ceil(80/60) = 2h × $2.500 = $5.000`
- **Total: $25.000** ✅

---


## Despliegue en nube

El proyecto está configurado y listo para despliegue en cualquier plataforma cloud.

**Perfil de producción:** `application-prod.properties` configurado con PostgreSQL y variables de entorno.

**Variables de entorno requeridas:**

| Variable | Descripción |
|---|---|
| `DATABASE_URL` | URL de conexión PostgreSQL |
| `API_KEY` | Clave de seguridad de la API |
| `SPRING_PROFILES_ACTIVE` | Valor: `prod` |

**Comandos de despliegue:**

```bash
# Build
./mvnw clean package -DskipTests
 
# Start
java -Dspring.profiles.active=prod -jar target/bicicletas-0.0.1-SNAPSHOT.jar
```

Compatible con Railway, Render, Heroku, o cualquier plataforma que soporte Java 17.

> **Nota:** El despliegue en nube no fue completado durante el desarrollo de esta
> prueba debido a limitaciones de las plataformas gratuitas evaluadas: Render no
> soporta Java nativamente en su plan free, Railway tenía créditos agotados, y
> Koyeb requiere tarjeta de crédito. La aplicación usa H2 en memoria y puede
> desplegarse sin base de datos externa  simplemente clonando el repositorio y
> ejecutando `./mvnw spring-boot:run`. Para producción con persistencia real,
> el perfil `prod` con PostgreSQL está configurado y listo.
 
---




## Manejo de errores

Todos los errores devuelven JSON con esta estructura:

```json
{
  "timestamp": "2026-06-10T15:30:00",
  "status": 409,
  "error": "Conflict",
  "mensaje": "La bicicleta BIC-004 no está disponible. Estado actual: EN_MANTENIMIENTO"
}
```

| Status | Cuándo |
|---|---|
| 400 | Datos inválidos o campos requeridos vacíos |
| 401 | API Key inválida o no proporcionada |
| 404 | Bicicleta o alquiler no encontrado |
| 409 | Regla de negocio violada |

---

## Supuestos documentados

1. Los enums se definieron sin tildes (`MONTANA`, `ELECTRICA`) para evitar
   problemas de encoding en diferentes sistemas operativos y herramientas.
   El comportamiento es idéntico al especificado en el enunciado.
2. La duración real en el historial aparece como `null` si el alquiler está
   activo/ no ha sido devuelto todavía.
3. El historial se muestra ordenado por fecha de inicio descendente
   (el más reciente primero).
4. El estado `EN_MANTENIMIENTO` solo se asigna al momento de registrar
   la bicicleta. No existe un endpoint para cambiar bicicletas a ese estado
   ya que el enunciado no lo contempla.