# Подборочка

Приложение для подбора подарков

## Запуск приложения

Требуются Docker с запущенным движком и Docker Compose v2.

1. Скопировать `.env.example` в `.env`, если файл ещё не создан.
2. Заполнить `DB_USER`, `DB_PASSWORD`, `GEMINI_API_KEY`, `YC_ACCESS_KEY`, `YC_SECRET_KEY`. Для подбора подарков нужен действующий ключ Gemini, для загрузки аватаров — доступ к настроенному Yandex Object Storage.
3. Из корня проекта выполнить:

```bash
docker compose up -d --build --wait
```

## Архитектура

```text
Браузер → localhost:8082 → frontend (Nginx)
                            ├── страницы, стили, JavaScript
                            └── /api/* и /logout → backend:8080 → postgres:5432
```


## Структура проекта

- `src/main/kotlin` — бэкенд и REST API.
- `src/main/resources/db` — миграции Liquibase.
- `frontend/src` —  интерфейс, общие компоненты и ресурсы.
- `frontend/build.mjs` — сборка статических страниц без сторонних npm-зависимостей.
- `frontend/Dockerfile` — сборка в Node.js и раздача результата через Nginx.
- `frontend/nginx.conf` — маршруты страниц и проксирование API.
- `Dockerfile` — сборка бэкенда через Gradle Wrapper и запуск на JRE 21.
- `docker-compose.yml` — общий запуск трёх сервисов.
- `docker-compose.deploy.yml` — запуск готовых образов из registry без исходников и Dockerfile.
- `scripts/publish-images.sh` — публикация образов приложения и PostgreSQL в registry.
## Стабильная версия и запуск одной командой

Стабильная версия: **1.0.0**, Git-тег: **v1.0.0**. Номер версии хранится в `VERSION`; сборка бэкенда и скрипты запуска и публикации читают его из этого файла. В Compose и `.env.example` задана та же версия по умолчанию.

После настройки `.env`:

```bash
./scripts/start.sh
```

На macOS можно также открыть `start.command` двойным щелчком. Docker Desktop должен быть запущен. Скрипт собирает образы, поднимает сервисы и ждет их готовности; ошибка сборки или готовности приводит к ненулевому коду завершения.

Повторный запуск выполняется той же командой. Данные PostgreSQL сохраняются при `docker compose down`; параметр `--volumes` для обычной остановки не используется.

```bash
docker compose down
./scripts/start.sh
```

Для следующего релиза обновляются `VERSION`, версия фронтенда, значения по умолчанию в Compose и `.env.example`, затем `CHANGELOG.md`. После проверки создаются коммит и аннотированный тег `vMAJOR.MINOR.PATCH`. Выпущенный тег не переносится на другой коммит; исправления выпускаются под новым номером.
