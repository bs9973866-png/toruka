# Toruka Launcher

**Toruka** — мощный и оптимизированный Minecraft Java Launcher для Android (аналог Pojav Launcher).

## Возможности

- Запуск Java-версии Minecraft на Android
- Загрузка всех версий Minecraft (Release, Snapshot, Alpha, Beta)
- Управление несколькими профилями и учётными записями
- Оптимизированная работа с памятью и графикой
- Поддержка модов (Forge, Fabric, Quilt — в разработке)
- Кастомные Java Runtime (JRE 8, 11, 17, 21)
- Автоматическая настройка LWJGL и OpenGL через GL4ES/VirGL

## Технологии

- **Kotlin** + Jetpack Compose для UI
- **MVVM** архитектура
- **Coroutines** + Flow для асинхронности
- **Room** для локального кэширования
- **OkHttp** + Retrofit для сети
- **Hilt** для Dependency Injection
- **JNI** + **NDK** для нативного взаимодействия с JVM и OpenGL
- **CMake** для сборки C++ кода

## Структура проекта

```
toruka/
├── app/
│   ├── build.gradle.kts          # Конфигурация модуля приложения
│   ├── proguard-rules.pro        # Правила ProGuard/R8
│   └── src/
│       ├── main/
│       │   ├── AndroidManifest.xml
│       │   ├── cpp/              # C++ нативный код (JNI)
│       │   │   ├── CMakeLists.txt
│       │   │   ├── toruka_bridge.cpp
│       │   │   ├── jvm_launcher.cpp
│       │   │   ├── gl_bridge.cpp
│       │   │   ├── input_bridge.cpp
│       │   │   └── lwjgl_bridge.cpp
│       │   ├── java/com/toruka/launcher/
│       │   │   ├── TorukaApp.kt
│       │   │   ├── MainActivity.kt
│       │   │   ├── GameActivity.kt
│       │   │   ├── TorukaAppNavigation.kt
│       │   │   ├── data/
│       │   │   │   ├── model/    # Модели данных
│       │   │   │   ├── api/      # Retrofit API
│       │   │   │   ├── local/    # Room DAO + Database
│       │   │   │   └── repository/
│       │   │   ├── di/           # Hilt модули
│       │   │   ├── ui/
│       │   │   │   ├── theme/    # Jetpack Compose тема
│       │   │   │   ├── components/
│       │   │   │   └── screens/  # Экраны приложения
│       │   │   ├── viewmodel/    # ViewModels
│       │   │   ├── service/      # Foreground сервисы
│       │   │   ├── util/         # Утилиты
│       │   │   └── jni/          # JNI Kotlin bridge
│       │   └── res/              # Ресурсы Android
│       └── test/                 # Unit тесты
├── build.gradle.kts              # Корневой build.gradle
├── settings.gradle.kts
├── gradle.properties             # Оптимизации Gradle
└── gradle/wrapper/
```

## Оптимизации

- **R8** full mode для максимальной минимизации
- **ProGuard** с агрессивными правилами оптимизации
- **CMake** с флагами `-O3`, `-fvisibility=hidden`, `-ffunction-sections`, `-fdata-sections`
- **Linker** с `--gc-sections` для удаления неиспользуемого кода
- **Gradle** parallel build, configure on demand, build cache
- **Coroutines** + Flow для неблокирующих операций
- **Room** с кэшированием для офлайн-работы
- **Streaming download** с прогрессом и докачкой

## Сборка

```bash
# Клонирование
git clone https://github.com/bs9973866-png/toruka.git
cd toruka

# Сборка debug APK
./gradlew assembleDebug

# Сборка release APK
./gradlew assembleRelease
```

## Требования

- Android 8.0+ (API 26+)
- ARM64/ARMv7/x86_64
- Минимум 2GB RAM (рекомендуется 4GB+)
- OpenGL ES 2.0+

## Лицензия

GPL-3.0
