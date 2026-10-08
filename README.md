# FitMate Backend

친구들과 **체성분 기록과 운동 기록을 공유하며 함께 운동하는** 피트니스 앱 FitMate의 API 서버입니다.

## 기술 스택

| 구분 | 사용 기술 |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.5, Spring Data JPA, Spring Security |
| Database | MariaDB |
| 인증 | JWT (jjwt 0.11.5) |
| 이미지 업로드 | Cloudinary |
| Build | Maven |

## 주요 기능

- **회원 / 인증**: 회원가입, JWT 로그인·로그아웃(토큰 블랙리스트), 탈퇴 유예 후 배치로 데이터 파기
- **체성분 기록**: 체중·근육량·체지방량 기록, 최근 기록 조회, 대시보드
- **목표**: 개인 체성분 목표 설정 및 조회
- **운동 기록**: 운동 종류·시간·강도 기록, 수정·삭제 (본인만 가능)
- **Mate(서클)**: 생성, 초대 링크 발급, 링크 미리보기 후 참여, 멤버 조회, 방장의 멤버 강퇴
- **Mate 피드** *(개발 중)*: 멤버들의 체성분·운동 기록을 모아 보여주는 피드, 이모지 반응, 댓글

## API 목록

| Method | URL | 설명 |
|---|---|---|
| POST | `/api/members/join` | 회원가입 |
| POST | `/api/auth/login` | 로그인 |
| POST | `/api/auth/logout` | 로그아웃 |
| GET / PUT / DELETE | `/api/members/me` | 내 정보 조회 / 수정 / 탈퇴 |
| PUT | `/api/members/me/privacy` | 체성분 공개범위 설정 |
| POST | `/api/members/me/profile-image` | 프로필 이미지 업로드 |
| PUT | `/api/members/me/password` | 비밀번호 변경 |
| GET | `/api/home` | 홈 화면 데이터 |
| POST | `/api/body-info` | 체성분 기록 등록 |
| GET | `/api/body-info/recent` | 최근 체성분 기록 |
| GET | `/api/body-info/dashboard` | 체성분 대시보드 |
| POST / GET | `/api/goal` | 목표 저장 / 조회 |
| POST | `/api/workouts` | 운동 기록 등록 |
| PATCH / DELETE | `/api/workouts/{id}` | 운동 기록 수정 / 삭제 |
| POST | `/api/mates` | Mate 생성 |
| GET / PATCH | `/api/mates/{id}` | Mate 조회 / 수정 |
| POST | `/api/mates/{id}/invite-link` | 초대 링크 발급 |
| GET | `/api/mates/invite/{token}` | 초대 링크 미리보기 |
| POST | `/api/mates/join` | 초대 링크로 참여 |
| GET | `/api/mates/{id}/members` | 멤버 목록 |
| DELETE | `/api/mates/{id}/members/{targetMemberId}` | 멤버 강퇴 (방장) |

> 로그인·회원가입을 제외한 API는 `Authorization: Bearer {accessToken}` 헤더가 필요합니다.

## 실행 방법

1. 설정 파일을 복사합니다.
   ```bash
   cp src/main/resources/application.properties.sample src/main/resources/application.properties
   ```
2. `application.properties`에 아래 값을 채웁니다. (이 파일은 `.gitignore`에 포함되어 있어 커밋되지 않습니다.)

   | 키 | 설명 |
   |---|---|
   | `spring.datasource.url` / `username` / `password` | MariaDB 접속 정보 |
   | `jwt.secret` | JWT 서명 키 |
   | `jwt.expiration-ms` | Access Token 만료 시간(ms) |
   | `cloudinary.cloud-name` / `api-key` / `api-secret` | Cloudinary 계정 정보 |

3. 서버를 실행합니다.
   ```bash
   ./mvnw spring-boot:run
   ```

## 설계 포인트

### 체성분 공개범위를 서버에서 재검증
회원은 체중·근육량·체지방량마다 공개범위를 따로 정할 수 있습니다.

| 공개범위 | 피드에 보이는 정보 |
|---|---|
| `PUBLIC` | 수치 + 변화량 |
| `DELTA_ONLY` | 변화량만 (예: `▼ 0.8`) |
| `PRIVATE` | 아무것도 보이지 않음 |

클라이언트가 숨겨 주는 방식에 의존하지 않습니다. 서버가 응답을 만들 때 숨겨야 하는 값을 **`null`로 비워서** 내려 보냅니다.
운동 기록은 공개범위 대상이 아니라서, 서클 멤버 전체에게 그대로 보입니다.

### 서로 다른 두 종류의 글을 하나의 피드로 통합
체성분 기록(`BodyInfo`)과 운동 기록(`Workout`)은 서로 다른 테이블에 저장됩니다. 피드에서는 두 기록을 공통 응답 형태(`MateFeedItemResponse`)로 바꾼 뒤, 하나로 합쳐 최신순으로 정렬합니다.
반응과 댓글은 `(post_type, post_id)` 쌍으로 어떤 글에 달렸는지 구분합니다. 그래서 id가 같은 체성분 글과 운동 글이 섞이지 않습니다.

### 반응·댓글 일괄 조회로 N+1 방지
피드 글마다 반응과 댓글을 따로 조회하지 않습니다. 글 타입별로 id를 모아 `IN` 쿼리로 한 번에 가져온 뒤, 메모리에서 글별로 묶어 반응 수, 내 반응, 댓글 수를 채웁니다. 글이 몇 개든 쿼리는 타입 수만큼만 나갑니다.
