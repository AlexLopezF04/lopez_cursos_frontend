# Project Summary

## Goal
Fix all CRUD operations across admin screens to work correctly with the backend.

## Progress
### Completed
- Fixed `LessonApi.getLessons()` return type: `PaginatedDto<LessonDto>` → `List<LessonDto>` (backend nested router returns plain array `[]`)
- Fixed `ReviewApi.getReviews()` return type: same fix
- Fixed remaining `!!` in `ReviewRepositoryImpl` and `ProgressRepositoryImpl`
- Fixed `LessonViewModel.deleteLesson` → added `isLoading = false` in `onFailure`
- Fixed `UserViewModel` create/update/delete + `ReviewViewModel` create/update/delete → added missing `isLoading = false` in `onFailure`
- Added `clearError()` to all ViewModels (Lesson, User, Enrollment, Course, Category, Review)
- Added `AlertDialog` confirmation before delete in all 6 admin screens
- Added `isLoading` + inline error display to ALL 6 form sheets:
  - `LessonFormSheet`: button disabled + spinner during load, error banner inside sheet
  - `CategoryFormSheet`: same pattern
  - `CourseFormSheet`: same pattern
  - `UserFormSheet`: same pattern
  - `EnrollmentFormSheet`: same pattern (both "update status" and "create" buttons)
  - `ReviewFormSheet`: same pattern

### Resolved
- **405 Method Not Allowed** on `cursos/{curso_pk}/lecciones/` POST/PATCH/DELETE
  - Root cause: `CursoViewSet` had a `@action(detail=True)` decorator that shadowed the nested router; removing it fixed the issue
  - Also: `LeccionViewSet` had `pagination_class = StandardPagination` which wrapped GET responses in paginated format; set to `None` to return plain array `[]`
  - Fix: `lecciones/views.py:10` — removed `@action`, added `pagination_class = None`
  - Build verified: ✅ `BUILD SUCCESSFUL`

### Matrícula (Enrollment) — Fully Implemented ✅
- **Backend** (`store/views/matricula.py`)
  - `MatriculaViewSet` with full CRUD: GET list (paginated, filtered by user), GET detail, POST create (auto-sets usuario, validates duplicates), PATCH update status, DELETE destroy (admin only)
  - Filters: `?estado=`, `?curso=`, `?fecha_desde=`, `?fecha_hasta=`
  - Permissions: authenticated users see own enrollments; admins see all
  - Unused import `EsPropietarioOAdmin` removed from `matricula.py`
  - Tests: 3/3 passing (`test_matricularse`, `test_matricula_duplicada_falla`, `test_estudiante_ve_solo_sus_matriculas`)

- **Frontend** — Full screens:
  - `EnrollmentApi.kt` — Retrofit interface with paginated GET, POST, PATCH, DELETE
  - `EnrollmentRepositoryImpl.kt` — pagination-aware with `Pair<List<Enrollment>, Int>`
  - `EnrollmentViewModel.kt` — paginated list + detail + CRUD + `markLessonComplete`
  - `EnrollmentsScreen.kt` — student "Mis Cursos" list with status badges, load more
  - `EnrollmentDetailScreen.kt` — student detail with lesson list + progress tracking
  - `EnrollmentsAdminScreen.kt` — admin CRUD list with delete confirmation
  - `EnrollmentFormSheet.kt` — create/edit bottom sheet with status dropdown
  - Build verified: ✅ `BUILD SUCCESSFUL`

### Progreso (Progress) — Fully Implemented ✅
- **Backend**
  - `store/serializers/progreso.py:5` — `ProgresoSerializer` now includes `leccion_titulo`, `curso_titulo`, `curso_id` as read-only nested fields
  - `store/views/progreso.py:9` — `ProgresoViewSet` with full CRUD (GET, POST, PATCH, DELETE), `perform_create` validates user owns the matrícula, `perform_destroy` restricts non-admin deletes
  - `store/signals.py:5` — `crear_progresos_al_matricular` post_save signal auto-creates Progreso records (completada=False) for all Lecciones when a Matrícula is created
  - `store/apps.py:10` — `ready()` imports signals
  - Django system check: ✅ `0 issues silenced`

- **Frontend — Data Layer**
  - `data/remote/api/ProgressApi.kt` — Added `DELETE` endpoint, `page` param on list
  - `data/remote/dto/ProgressDto.kt` — Added `leccionTitulo`, `cursoId`, `cursoTitulo` fields
  - `domain/model/Progress.kt` — Added `leccionTitulo`, `cursoId`, `cursoTitulo` to domain model
  - `domain/repository/ProgressRepository.kt` — `getProgressList` accepts `Int?` (null = all); added `deleteProgress`
  - `data/repository/ProgressRepositoryImpl.kt` — Implements `deleteProgress` via `DELETE` call

- **Frontend — ViewModel**
  - `presentation/viewmodel/ProgressViewModel.kt` — Added:
    - `loadAllProgress()` — fetch all records (admin)
    - `deleteProgress(id)` — delete with admin refresh
    - `autoMarkComplete(matriculaId, leccionId)` — marks lesson complete without reloading list
    - `loadedOnce` flag for safe auto-progress timing
    - `deleteConfirmId` state for delete confirmation dialog

- **Frontend — Admin Screen** (full CRUD)
  - `presentation/ui/admin/progress/ProgressAdminScreen.kt` — Complete rewrite:
    - Loads all progress on init, filterable by matrícula ID
    - `ProgressCard` shows lesson title, course title, matrícula ID, completion date, completion toggle switch, delete button
    - `ProgressFormSheet` (AlertDialog) — create new progress record with matrícula + lesson ID fields
    - Delete confirmation `AlertDialog`

- **Frontend — User Auto-Progress**
  - `presentation/navigation/Screen.kt:17` — `LessonViewer` route now includes optional `?matriculaId={matriculaId}`
  - `presentation/navigation/NavGraph.kt:180` — Lesson route accepts optional `matriculaId` nav arg; passes it to `LessonViewerScreen`; `EnrollmentDetailScreen` navigates with `?matriculaId=$id`
  - `presentation/ui/lesson/LessonViewerScreen.kt` — When `matriculaId` is provided: loads progress, auto-marks lesson complete via `autoMarkComplete`, shows checkmark icon in app bar

### Reseñas (Reviews) — Fully Implemented ✅
- **Backend** — Full implementation verified:
  - Model with `unique_together` constraint, 1-5 star validation, auto-ordering by date
  - Serializer with `usuario_nombre` read-only, duplicate validation, **enrollment check** (solo estudiantes con matrícula activa pueden reseñar), auto-set `usuario` on create
  - ViewSet nested under `/api/cursos/{curso_pk}/resenas/` with AllowAny for list/retrieve, `EsPropietarioOAdmin` for update/delete
  - Filter by `calificacion_min`, `calificacion_max`, `curso`
  - **Fix**: `pagination_class` set to `None` (was `StandardPagination` — nested router returns plain array, frontend expects `List<ReviewDto>`)
  - Tests: 5/5 passing (`test_crear_resena`, `test_crear_resena_sin_matricula_falla` **new**, `test_resena_duplicada_falla`, `test_calificacion_fuera_de_rango_falla`, `test_listar_resenas_publico`)

- **Frontend — Data Layer**
  - `ReviewApi.kt` — Retrofit interface with full CRUD nested under `cursos/{curso_pk}/resenas/`
  - `ReviewDto.kt` — DTO with `toDomain()` mapping; `ReviewRequestDto` with `toRequest()` mapping from `ReviewPayload`
  - `Domain model` — `Review` and `ReviewPayload` data classes
  - `ReviewRepository.kt` — Interface with all CRUD operations
  - `ReviewRepositoryImpl.kt` — Implementation with error handling via `errorBody()?.string()`

- **Frontend — ViewModel & Admin Screen**
  - `ReviewViewModel.kt` — Full CRUD + form state (`showForm`, `editingReview`) + `clearError()`
  - `ReviewsAdminScreen.kt` — Course dropdown, star rating display, edit/delete with confirmation `AlertDialog`
  - `ReviewFormSheet.kt` — Modal bottom sheet with 5-star selector, comment field, loading state, error banner
  - Navigation: route `admin/reviews` in `Screen.kt`, NavGraph.kt, and `AdminScaffold.kt` drawer
  - Build verified: ✅ `BUILD SUCCESSFUL`

## Build Command
```
cd C:\Semestre_IV\lopez_cursos\lopez_cursos_frontend && ./gradlew assembleDebug
```

## Test Command
```
cd C:\Semestre_IV\lopez_cursos\lopez_cursos_backend && python manage.py test store.tests --verbosity=2
```
