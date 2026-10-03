# API de franquicias

Solución del reto técnico para administrar franquicias, sus sucursales y los productos disponibles en cada sucursal.

La aplicación fue desarrollada con Java 17 y Spring Boot. Para persistencia se eligió MySQL porque el modelo tiene relaciones claras entre franquicia, sucursal y producto.

## Tecnologías

- Java 17
- Spring Boot 3
- Spring Web
- Spring Data JPA
- Bean Validation
- MySQL 8
- Maven
- JUnit 5
- H2 para pruebas de integración
- Docker y Docker Compose

## Modelo

La relación utilizada es:

```text
Franquicia 1 ---- N Sucursal 1 ---- N Producto
```

Una franquicia puede tener varias sucursales y cada sucursal puede tener varios productos.

## Requisitos implementados

- Crear una franquicia.
- Agregar una sucursal a una franquicia.
- Agregar un producto a una sucursal.
- Eliminar un producto de una sucursal.
- Actualizar el stock de un producto.
- Consultar el producto con mayor stock de cada sucursal para una franquicia.
- Persistencia en MySQL.
- Validación de datos de entrada y manejo de errores.

Puntos adicionales incluidos:

- Docker y Docker Compose.
- Actualización del nombre de una franquicia.
- Actualización del nombre de un producto.
- Pruebas de integración sobre la lógica principal.

## Estructura del proyecto

```text
src/main/java/com/nequi/franquicias
├── controller     -> endpoints REST
├── dto            -> datos de entrada y salida de la API
├── entity         -> entidades persistidas en MySQL
├── exception      -> manejo centralizado de errores
├── repository     -> acceso a datos con Spring Data JPA
└── service        -> reglas y lógica del negocio
```

El flujo principal es:

```text
Cliente -> Controller -> Service -> Repository -> MySQL
```

## Ejecución con Docker

Requisito: Docker Desktop o Docker Engine con Docker Compose.

Desde la raíz del proyecto:

```bash
docker compose up --build
```

La API queda disponible en:

```text
http://localhost:8080
```

MySQL se levanta automáticamente y la aplicación crea/actualiza las tablas mediante JPA.

Para detener los contenedores:

```bash
docker compose down
```

Para eliminar también el volumen de la base de datos:

```bash
docker compose down -v
```

## Ejecución local sin Docker para la API

Se necesita Java 17, Maven y una instancia MySQL disponible.

Por defecto la aplicación intenta conectarse a:

```text
jdbc:mysql://localhost:3306/franquicias
usuario: franquicias
contraseña: franquicias
```

También se pueden configurar las siguientes variables de entorno:

```text
DB_URL
DB_USER
DB_PASSWORD
PORT
```

Luego ejecutar:

```bash
mvn spring-boot:run
```

## Endpoints

### Crear franquicia

```http
POST /api/franquicias
```

```json
{
  "nombre": "Antigüedades Colombia"
}
```

Respuesta esperada: `201 Created`.

### Agregar sucursal

```http
POST /api/franquicias/{franquiciaId}/sucursales
```

```json
{
  "nombre": "Sucursal Medellín"
}
```

Respuesta esperada: `201 Created`.

### Agregar producto

```http
POST /api/sucursales/{sucursalId}/productos
```

```json
{
  "nombre": "Radio antiguo",
  "stock": 15
}
```

Respuesta esperada: `201 Created`.

### Eliminar producto

```http
DELETE /api/sucursales/{sucursalId}/productos/{productoId}
```

Respuesta esperada: `204 No Content`.

### Actualizar stock

```http
PATCH /api/productos/{productoId}/stock
```

```json
{
  "stock": 30
}
```

### Producto con mayor stock por sucursal

```http
GET /api/franquicias/{franquiciaId}/productos-mayor-stock
```

Ejemplo de respuesta:

```json
[
  {
    "sucursalId": 1,
    "sucursal": "Sucursal Medellín",
    "productoId": 2,
    "producto": "Reloj de colección",
    "stock": 35
  },
  {
    "sucursalId": 2,
    "sucursal": "Sucursal Bogotá",
    "productoId": 5,
    "producto": "Máquina de escribir",
    "stock": 20
  }
]
```

La consulta retorna un producto por cada sucursal que tenga productos. Si dos productos de una misma sucursal tienen el mismo stock máximo, se toma primero el de menor identificador para obtener un resultado determinístico.

### Actualizar nombre de franquicia

```http
PATCH /api/franquicias/{franquiciaId}/nombre
```

```json
{
  "nombre": "Nuevo nombre"
}
```

### Actualizar nombre de producto

```http
PATCH /api/productos/{productoId}/nombre
```

```json
{
  "nombre": "Nuevo nombre"
}
```

## Validaciones y respuestas de error

- Los nombres son obligatorios y no pueden superar 120 caracteres.
- El stock es obligatorio y no puede ser negativo.
- Si no existe una franquicia, sucursal o producto se retorna `404 Not Found`.
- Los nombres de franquicia no se permiten duplicados.
- Dentro de una misma franquicia no se permiten sucursales con el mismo nombre.
- Dentro de una misma sucursal no se permiten productos con el mismo nombre.
- Los conflictos de estas reglas retornan `409 Conflict`.
- Las solicitudes con datos inválidos retornan `400 Bad Request`.

## Pruebas

Las pruebas utilizan una base H2 en memoria en modo compatible con MySQL, de modo que no necesitan una base externa.

Ejecutar:

```bash
mvn test
```

Se incluyeron pruebas de integración para validar la actualización de stock y la consulta del producto con mayor stock por sucursal.

## Archivo para probar la API

En la raíz se incluye `peticiones.http` con ejemplos de todas las operaciones. IntelliJ IDEA permite ejecutarlas directamente desde el editor. También pueden copiarse las mismas solicitudes a Postman o Bruno.

## Decisiones de implementación

Se mantuvo una arquitectura por capas sencilla para separar las responsabilidades sin agregar complejidad innecesaria. Los controladores reciben las solicitudes HTTP, el servicio concentra las reglas de negocio y los repositorios manejan la persistencia.

Para el requerimiento de mayor stock se realiza una consulta que obtiene el producto con mayor cantidad por cada sucursal perteneciente a la franquicia solicitada. Esto evita confundir el requerimiento con obtener un único producto máximo de toda la franquicia.

Se utilizaron DTO para que el contrato de la API no dependa directamente de las entidades de persistencia.

## Alcance de los puntos extra

Se implementaron Docker y los endpoints de actualización de nombres. No se incluyeron programación reactiva, infraestructura como código ni despliegue cloud porque son puntos opcionales y se priorizó una solución funcional, sencilla de ejecutar y fácil de mantener.
