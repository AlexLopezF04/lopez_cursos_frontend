<div align="center">
  <img src="https://img.shields.io/badge/Android-34A853?style=for-the-badge&logo=android&logoColor=white" alt="Android"/>
  <img src="https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white" alt="Kotlin"/>
  <img src="https://img.shields.io/badge/Jetpack%20Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white" alt="Compose"/>
  <img src="https://img.shields.io/badge/Hilt-1A73E8?style=for-the-badge&logo=dagger&logoColor=white" alt="Hilt"/>
  <img src="https://img.shields.io/badge/Retrofit-48B983?style=for-the-badge&logo=square&logoColor=white" alt="Retrofit"/>
  <br>
  <img src="https://img.shields.io/badge/Django-092E20?style=for-the-badge&logo=django&logoColor=white" alt="Django"/>
  <img src="https://img.shields.io/badge/PostgreSQL-4169E1?style=for-the-badge&logo=postgresql&logoColor=white" alt="PostgreSQL"/>
  <img src="https://img.shields.io/badge/Django%20REST-092E20?style=for-the-badge&logo=django&logoColor=white" alt="DRF"/>
</div>

<br>

<h1 align="center">🎓 CursosOnline — App Móvil de Cursos</h1>

<p align="center">
  Aplicación Android nativa para la gestión y consumo de cursos online. 
  <br>
  Permite a estudiantes inscribirse, progresar y reseñar cursos, y a administradores gestionar todo el catálogo desde un panel completo.
</p>

<br>

---

## 🚀 Descripción General

**CursosOnline** es una aplicación móvil Android desarrollada en **Kotlin** con **Jetpack Compose** que consume una API REST construida con **Django REST Framework** y **PostgreSQL**.

### Roles de usuario

| Rol | Descripción |
|---------|--------------|
| 👑 **Admin** | Acceso total: CRUD de usuarios, cursos, categorías, lecciones, matrículas, progreso y reseñas. Panel de administración con navegación tipo drawer. |
| 👨‍🏫 **Instructor** | Puede gestionar cursos y lecciones. Acceso al panel admin con restricciones. |
| 🎓 **Estudiante** | Navega el catálogo, se inscribe a cursos, visualiza lecciones, lleva su progreso automático y deja reseñas. |

### Funcionalidades principales

- 🔐 Autenticación JWT (login/register)
- 📚 Catálogo de cursos con filtros
- 📝 Inscripción a cursos (matrícula)
- ▶️ Reproductor de lecciones con progreso automático
- ⭐ Reseñas con calificación de 1 a 5 estrellas
- 📊 Panel de administración completo
- 🔄 Sincronización en tiempo real con el backend

---

## 📦 Requisitos de Instalación

### Para desarrollo (Android Studio)

| Herramienta | Versión mínima |
|-------------|----------------|
| Android Studio | Hedgehog (2023.1.1) o superior |
| JDK | 17 |
| Kotlin | 1.9.x |
| Gradle | 8.x |
| Android SDK | API 26+ (Android 8.0) |
| Compile SDK | 35 |

### Para el backend

| Herramienta | Versión |
|-------------|---------|
| Python | 3.11+ |
| Django | 5.x |
| PostgreSQL | 14+ |
| pip/uv | Cualquiera |

---

## 🔧 Configuración de la URL del Backend

La URL base del backend se configura mediante `local.properties` en la raíz del proyecto Android.

### Paso 1: Abre `local.properties`

```properties
# local.properties
API_BASE_URL=http://tuservidor:8000/api/
```

> ⚠️ **Importante:** El archivo `local.properties` NO está en el repositorio (está en `.gitignore`). Debes crearlo manualmente.

### Paso 2 (alternativo): Modifica directamente en `build.gradle.kts`

Si no usas `local.properties`, edita el fallback en `app/build.gradle.kts:35`:

```kotlin
val apiBaseUrl = localProperties.getProperty(
    "API_BASE_URL",
    "http://lopez-cursos.uaeftt-ute.site/api/"  // ← Cambia esta URL
)
```

### Estructura de la API

```
/api/
├── auth/login/           → Obtener token JWT
├── auth/register/        → Registrar nuevo usuario
├── auth/me/              → Obtener perfil del usuario autenticado
├── token/refresh/        → Refrescar token JWT
├── cursos/               → CRUD de cursos
├── cursos/{id}/lecciones/ → Lecciones de un curso (anidado)
├── cursos/{id}/resenas/   → Reseñas de un curso (anidado)
├── categorias/           → CRUD de categorías
├── usuarios/             → CRUD de usuarios (admin)
├── matriculas/           → CRUD de matrículas
├── progresos/            → CRUD de progreso
```

---

## 🧩 Entidades Implementadas (7)

### 1. 👤 Usuario (`Usuario`)

Gestión de usuarios con 3 roles: `admin`, `instructor`, `estudiante`. Autenticación mediante JWT (access + refresh tokens).

**Atributos:** `id`, `username`, `email`, `password`, `rol`, `is_active`, `date_joined`

**API:**
```
GET/POST   /api/usuarios/
GET/PATCH/DELETE /api/usuarios/{id}/
```

### 2. 📂 Categoría (`Categoria`)

Clasificación de cursos por categoría (Python, Diseño, Marketing, etc.).

**Atributos:** `id`, `nombre`, `slug`

**API:**
```
GET/POST   /api/categorias/
GET/PATCH/DELETE /api/categorias/{id}/
```

### 3. 📖 Curso (`Curso`)

Entidad principal del sistema. Cada curso pertenece a una categoría y es creado por un instructor.

**Atributos:** `id`, `titulo`, `descripcion`, `precio`, `nivel` (básico/intermedio/avanzado), `publicado`, `instructor`, `categoria`, `created_at`

**API:**
```
GET/POST   /api/cursos/?search=&nivel=&categoria=&precio_min=&precio_max=
GET/PATCH/DELETE /api/cursos/{id}/
```

### 4. 📄 Lección (`Leccion`)

Contenido educativo asociado a un curso. Las lecciones se ordenan por el campo `orden` y se acceden de forma anidada.

**Atributos:** `id`, `curso`, `titulo`, `contenido`, `orden`, `created_at`

**API:**
```
GET/POST   /api/cursos/{curso_pk}/lecciones/
GET/PATCH/DELETE /api/cursos/{curso_pk}/lecciones/{id}/
```

### 5. 📋 Matrícula (`Matricula`)

Registro de inscripción de un estudiante a un curso. Estados: `activa`, `completada`, `cancelada`.

**Atributos:** `id`, `usuario`, `curso`, `estado`, `monto_pagado`, `fecha_pago`

**API:**
```
GET/POST      /api/matriculas/?estado=&curso=
GET/PATCH/DELETE /api/matriculas/{id}/
```

### 6. 📊 Progreso (`Progreso`)

Seguimiento del avance del estudiante en cada lección de un curso. Se crea automáticamente al matricularse (mediante señal `post_save`).

**Atributos:** `id`, `matricula`, `leccion`, `completada`, `completada_en`

**API:**
```
GET/POST   /api/progresos/?matricula=&completada=
GET/PATCH/DELETE /api/progresos/{id}/
```

> 💡 **Dato clave:** Al crear una matrícula, una señal (`crear_progresos_al_matricular`) genera automáticamente un registro de `Progreso` por cada lección del curso con `completada=False`.

### 7. ⭐ Reseña (`Resena`)

Opinión y calificación (1-5 estrellas) que un estudiante deja sobre un curso. Solo estudiantes con matrícula activa pueden reseñar. No se permiten reseñas duplicadas.

**Atributos:** `id`, `usuario`, `curso`, `calificacion` (1-5), `comentario`, `created_at`

**API:**
```
GET/POST   /api/cursos/{curso_pk}/resenas/
GET/PATCH/DELETE /api/cursos/{curso_pk}/resenas/{id}/
```

> 💡 **Dato clave:** Las reseñas son anidadas bajo cursos (`/api/cursos/{id}/resenas/`). La respuesta no está paginada (retorna `List<Resena>` directamente).

---

## 🖥️ Listado de Pantallas

### Pantallas públicas

| Pantalla | Ruta | Descripción |
|----------|------|-------------|
| **Login** | `login` | Inicio de sesión con JWT |
| **Registro** | `register` | Creación de cuenta nueva |
| **Catálogo** | `catalog` | Listado de cursos públicos con filtros |
| **Detalle del Curso** | `course/{id}` | Información del curso + reseñas |

### Pantallas de estudiante

| Pantalla | Ruta | Descripción |
|----------|------|-------------|
| **Inicio** | `home` | Bienvenida con cursos destacados |
| **Mis Cursos** | `enrollments` | Cursos en los que está inscrito |
| **Detalle de Matrícula** | `enrollments/{id}` | Lecciones del curso y progreso |
| **Reproductor de Lección** | `lesson/{cursoId}/{leccionId}` | Visualiza contenido y marca progreso automático |
| **Perfil** | `profile` | Datos del usuario y cierre de sesión |

### Pantallas de administración

| Pantalla | Ruta | Descripción |
|----------|------|-------------|
| **Dashboard** | `admin` | Panel de control con KPIs |
| **Categorías** | `admin/categories` | CRUD de categorías |
| **Cursos** | `admin/courses` | CRUD de cursos |
| **Lecciones** | `admin/lessons` | CRUD de lecciones por curso |
| **Matrículas** | `admin/enrollments` | CRUD de matrículas |
| **Progreso** | `admin/progress` | CRUD de progreso |
| **Reseñas** | `admin/reviews` | CRUD de reseñas por curso |
| **Usuarios** | `admin/users` | CRUD de usuarios |

---

## 🔌 Ejemplos de Consumo de la API

### 1. Obtener token JWT

```http
POST /api/auth/login/
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123"
}
```

**Respuesta:**
```json
{
  "access": "eyJhbGciOiJIUzI1NiIs...",
  "refresh": "eyJhbGciOiJIUzI1NiIs..."
}
```

### 2. Listar cursos con filtros

```http
GET /api/cursos/?nivel=basico&publicado=true
Authorization: Bearer eyJhbGciOiJIUzI1NiIs...
```

### 3. Crear una matrícula (inscripción)

```http
POST /api/matriculas/
Authorization: Bearer eyJhbGciOiJIUzI1NiIs...
Content-Type: application/json

{
  "curso": 1,
  "monto_pagado": 29.99
}
```

### 4. Crear una reseña (estudiante matriculado)

```http
POST /api/cursos/1/resenas/
Authorization: Bearer eyJhbGciOiJIUzI1NiIs...
Content-Type: application/json

{
  "curso": 1,
  "calificacion": 5,
  "comentario": "Excelente curso, muy completo."
}
```

### 5. Marcar progreso de lección

```http
PATCH /api/progresos/1/
Authorization: Bearer eyJhbGciOiJIUzI1NiIs...
Content-Type: application/json

{
  "completada": true
}
```

### 6. Refrescar token

```http
POST /api/token/refresh/
Content-Type: application/json

{
  "refresh": "eyJhbGciOiJIUzI1NiIs..."
}
```

---

## 🛠️ Instrucciones para Ejecutar la App

### Opción 1: APK precompilado

1. Descarga el archivo `.apk` desde la sección **Releases** del repositorio
2. Transfiérelo a tu dispositivo Android
3. Habilita **"Instalar apps de orígenes desconocidos"** en Ajustes
4. Abre el archivo APK e instala

### Opción 2: Compilar desde Android Studio

```bash
# 1. Clonar el repositorio
git clone https://github.com/tu-usuario/lopez-cursos.git
cd lopez-cursos

# 2. Abrir con Android Studio (File → Open → seleccionar carpeta)

# 3. Crear/editar local.properties en la raíz del proyecto:
#    API_BASE_URL=http://localhost:8000/api/

# 4. Compilar APK de depuración
./gradlew assembleDebug

# 5. El APK se genera en:
#    app/build/outputs/apk/debug/app-debug.apk
```

### Opción 3: Ejecutar en emulador

```bash
# Desde Android Studio, seleccionar un dispositivo virtual y presionar Run ▶️

# O desde terminal:
./gradlew installDebug
```

### Requisitos del backend (para ejecución local)

```bash
# 1. Clonar y preparar backend
cd lopez_cursos_backend
python -m venv .venv
source .venv/bin/activate   # Windows: .venv\Scripts\activate
pip install -r requirements.txt

# 2. Configurar .env (copiar desde .env.example)
# 3. Ejecutar migraciones
python manage.py migrate

# 4. Crear datos de prueba
python manage.py seed_data

# 5. Iniciar servidor
python manage.py runserver
```

---

<div align="center">
  <br>
  <p>
    🚀 <strong>CursosOnline</strong> — Proyecto desarrollado con ❤️ usando <strong>Kotlin + Jetpack Compose</strong> (Android) y <strong>Django REST Framework</strong> (Backend).
  </p>
  <p>
    <sub>© 2026 — Todos los derechos reservados</sub>
  </p>
</div>
