# Toruka Launcher

**Toruka** — мощный и оптимизированный Minecraft Java Launcher для Android.

## Возможности

- Запуск Java-версии Minecraft на Android (аналог Pojav Launcher)
- Загрузка всех версий Minecraft (Release, Snapshot, Alpha, Beta)
- Управление несколькими профилями и учётными записями
- Оптимизированная работа с памятью и графикой
- Поддержка модов (Forge, Fabric, Quilt)
- Кастомные Java Runtime (JRE 8, 11, 17, 21)
- Автоматическая настройка LWJGL и OpenGL через GL4ES/VirGL

## Технологии

- Kotlin + Jetpack Compose
- MVVM архитектура
- Coroutines + Flow
- Room для кэширования
- OkHttp + Retrofit
- JNI для нативного взаимодействия

## Сборка

```bash
./gradlew assembleRelease
```

## Лицензия

GPL-3.0
