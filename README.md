
# 🎮 Easy Game

> A backend API server for storing scores and retrieving rankings in single-player web games.  
> 싱글플레이어 웹 게임의 점수를 저장하고 랭킹을 조회할 수 있는 백엔드 API 서버입니다.

Built with **Spring Boot**, this RESTful API server adopts a **multi-module architecture** and includes features such as **JWT authentication**, **Redis-based locking**, and a **Dockerized development environment**.  
**Spring Boot** 기반으로, **멀티 모듈 아키텍처**, **JWT 인증**, **Redis 락**, **Docker 개발 환경** 등을 갖추고 있습니다.

---

## 📌 Tech Stack / 주요 기술 스택

- **Language & Framework**: Java 17, Spring Boot 3
- **Database**: MySQL 8, Redis
- **Build Tool**: Gradle (Multi-module structure) / 멀티 모듈 구조
- **Testing**: JUnit 5, Mockito
- **DevOps**: Docker

---

## 🧱 Project Structure / 프로젝트 구조

```
📦 easygame
├── api         # REST API (Controllers, DTOs)
├── service     # Business Logic (Services)
└── repository  # Data Access Layer (Entities, JPA, Queries)
```

---

## 🧩 Key Features / 주요 기능

- ✅ User authentication via JWT / JWT 기반 사용자 인증
- ✅ Score submission API (with duplicate prevention) / 점수 저장 API (중복 저장 방지 포함)
- ✅ Ranking retrieval per game / 게임별 상위 랭킹 조회
- ✅ Redis TTL locking / Redis TTL 락을 이용한 중복 방지
- ✅ Unified exception handling / 공통 예외 처리

---

## 🔐 Authentication / 인증 방식

- JWT access token is issued when a nickname is submitted. / 닉네임 입력 시 JWT 토큰 발급
- Token is required for score submission requests. / 점수 저장 요청 시 인증 필요
- See **Todos** for upcoming changes. / 향후 개선 예정은 Todos 참고

---

## 🧪 API Documentation / API 명세서

> Accessible via Swagger UI  
> 📎 [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

**Example / 예시:**

```
POST /api/v1/scores
Headers: Authorization: Bearer <ACCESS_TOKEN>

Body:
{
  "gameType": "TETRIS",
  "score": 12345
}
```

---

## ⚙️ Getting Started / 실행 방법

```bash
# 1. Clone the repository / 레포지토리 클론
git clone https://github.com/rocket-developer-sketch/new-version-enjoyGames-backend.git
cd easygame

# 2. Build the API server / API 서버 빌드
./gradlew clean :api:bootJar

# 3. Start development environment / Docker 실행
docker compose up --build -d

# 4. Stop Docker environment / Docker 종료
docker compose down
```

> Update `application.yml` with your DB credentials.  
> `application.yml` 파일에 본인의 DB 접속 정보를 입력해주세요.  
>  
> Sample `Dockerfile` and `docker-compose.yml` are provided.  
> 예시 `Dockerfile`, `docker-compose.yml` 파일이 포함되어 있습니다.

---

## ✅ Testing / 테스트

- Unit tests for repository and service layers  
  `repository`, `service` 단위 테스트 포함
- Over 85% test coverage on core business logic  
  핵심 비즈니스 로직 테스트 커버리지 85% 이상 유지

```bash
./gradlew :service:test
./gradlew :repository:test
```

---

## 📊 Database Schema (ERD) / 데이터베이스 구조

```
[User] --- (1:N) --- [GameScore]
          ↳ userId (BIGINT)
```
- See **Todos** for upcoming changes. / 향후 개선 예정은 Todos 참고

---

## 🛠️ Troubleshooting & Highlights / 기술 포인트

| Topic / 주제           | Description / 내용 |
|------------------------|---------------------|
| Spring Security        | Custom JWT authentication / JWT 기반 인증 처리 |
| Redis TTL Lock         | Prevents duplicate scores / 점수 중복 저장 방지 |
| Multi-module Setup     | Dependency resolution / 의존성 문제 해결 |
| Testing Environment    | Separated test config / 테스트 환경 분리 구성 |

---

## 🚧 Todos

### Deployment / 배포

- Deploy to AWS EC2 and RDS / AWS EC2 및 RDS 배포 예정
- Separate Spring Boot profiles for test and production environments / 배포 환경별 프로파일 분리 예정

### Auth & Validation / 인증 및 검증

- Support duplicate nicknames securely / 중복 닉네임 허용 + 보안 검증
- Remove user table / 사용자 테이블 제거 예정
- Refactor Redis lock logic / Redis 락 로직 개선 예정

### Testing / 테스트

- Add integration tests / 통합 테스트 추가 예정
