<div align="center">
  <img src="app/src/main/res/mipmap-xxxhdpi/ic_launcher.webp" alt="HaHabit Logo" width="200"/>

# HaHabit

**Aplicación Android para el seguimiento y gestión de hábitos**

Una herramienta completa para ayudarte a crear, mantener y visualizar tus hábitos diarios de forma efectiva.

---

</div>

## Descripción

HaHabit es una aplicación Android nativa diseñada para facilitar el seguimiento de tus hábitos personales. Con una interfaz intuitiva y colorida, te permite organizar tus actividades en diferentes áreas de tu vida (cocina, estudio, deportes y otros), establecer recordatorios personalizados y visualizar tu progreso a través de estadísticas detalladas.

La aplicación está diseñada para motivarte a mantener tus hábitos mediante un sistema de rachas, calendarios visuales y mensajes motivacionales que te ayudarán a alcanzar tus objetivos personales.

## Características Principales

### Gestión y Creación de Hábitos

- **Tres tipos de hábitos**:
    - **Estándar**: Hábitos simples de completar/no completar
    - **Lista de tareas**: Hábitos con múltiples sub-tareas que puedes ir marcando
    - **Temporizador**: Hábitos basados en tiempo con cronómetro integrado
- **Categorización por áreas**: Organiza tus hábitos en Cocina, Estudio, Deportes u Otros
- **Configuración flexible de frecuencia**:
  - Semanal (selección de días específicos)
  - Por intervalo de días personalizado
- **Configuración de si se quiere recordatorio**:

### Recordatorios

- **Sistema de recordatorios** con notificaciones programables

### API externa

- **Mensajes motivacionales** mediante integración con ZenQuotes API

### Seguimiento y Estadísticas

- **Vista de tracker por áreas**: Visualiza tu progreso en cada área en el último mes
- **Calendario de actividad**: Mapa de calor visual que muestra tu constancia
- **Estadísticas personales**:
    - Días activos totales
    - Mejor racha consecutiva
    - Rachas actuales por área

### Interfaz de Usuario

- **Navegación por pestañas** con Bottom Navigation
- **Vista semanal interactiva** con navegación entre semanas
- **Diseño responsive** adaptado a diferentes tamaños de pantalla

### Persistencia de Datos

- **Base de datos Room** para almacenamiento local
- **Historial completo** de completados por fecha

## Tecnologías Utilizadas

- **Lenguaje**: Java
- **Base de datos**: Room (SQLite)
- **Arquitectura**: MVVM (Model-View-ViewModel)
- **API Externa**: ZenQuotes API para mensajes motivacionales

## Estructura del Proyecto

```
app/src/main/java/es/ucm/fdi/pad/hahabit/
├── data/                      # Modelos y capa de datos
│   ├── Habit.java            # Entidad principal de hábito
│   ├── HabitDao.java         # Data Access Object
│   ├── HabitDatabase.java    # Configuración de Room
│   └── HabitRepository.java  # Repositorio de datos
├── ui/                        # Interfaz de usuario
│   ├── home/                 # Pantalla principal
│   ├── add/                  # Pantalla de añadir hábitos
│   └── tracker/              # Pantalla de seguimiento
├── notifications/             # Sistema de notificaciones
└── network/                   # Cliente API ZenQuotes
```

## Autores

Proyecto desarrollado para la asignatura de Programación de Aplicaciones para Dispositivos móviles (PAD) - Universidad Complutense de Madrid (UCM), Facultad de Informática (FDI).
