# 📥 리크루팅 프로세스 자동화 통합 솔루션, 위더스(WITHUS)

> 🔗 Link : [https://withus-ten.vercel.app](https://withus-ten.vercel.app/)

![image](https://github.com/user-attachments/assets/c6e512ce-7da8-4fca-81a8-5e54f8e415f6)


- 불필요한 반복 작업 대신 지원자를 살피는데 집중할 수 있도록!
- <b>`[공고 - 지원서 취합 - 서류/면접 평가 - 합불 발표]`</b>의 모든 과정을 한 곳에서!

</br>

## 📑 목차
- [팀소개 & RnR](#팀소개--rnr)
- [서비스 개요](#서비스-개요)
  - [1️⃣ 문제 정의 / 경쟁사 분석](#1️⃣-문제-정의--경쟁사-분석)
  - [2️⃣ 유저 리서치 / 기대 효과](#2️⃣-유저-리서치--기대-효과)
  - [3️⃣ Information Architecture / 비즈니스 모델](#3️⃣-information-architecture--비즈니스-모델)
- [서비스 기능](#서비스-기능)
- [API 명세서](#%EF%B8%8Fapi-명세서)
- [ERD](#erd)
- [시스템 아키텍처](#시스템-아키텍처)
- [프론트엔드](#프론트엔드)
- [백엔드](#백엔드)

</br>

# 👩‍🍼 팀소개 & RnR

> **중전마더스**
> 

PM의 별명에서 비롯된 팀명으로, 서비스명 `위더스(WithUs)`와 라임을 맞춰 유쾌하면서도 따뜻한 팀의 색깔을 담았습니다. `중전`처럼 중심을 잡고 서로를 챙기며, `마더스`처럼 따뜻하게 **사용자 곁에서 진심을 다하는 서비스를 만들겠다는 의지를 담은 이름**입니다.

</br>

| **분야** | **이름** | **포지션** |
| --- | --- | --- |
| 📋 기획 | 장수정 | PM, 기획 리드, 서비스 기획(서비스 정책 확립, 유저 리서치, 와이어프레임 작성, UX writing) |
| 📋 기획 | 윤수빈 | 서비스 기획(서비스 정책 확립, 유저 리서치, 와이어프레임 작성, UX writing) |
| 🎨 디자인 | 설정원 | 디자인 리드, UX/UI 디자인, GUI 디자인, 서비스 디자인, 브랜드 디자인 |
| 🎨 디자인 | 김현호 | UX/UI 디자인, GUI 디자인, 서비스 디자인, 브랜드 디자인 |
| 📲 프론트엔드 | 이채원 | 프론트엔드 리드, 화면 UI 구현, 서버 연동 |
| 📲 프론트엔드 | 서유빈 | 화면 UI 구현, 서버 연동 |
| 🖥️ 백엔드 | 김재관 | 백엔드 리드, DB 및 API 구축, 서버 배포 |
| 🖥️ 백엔드 | 우은진 | DB 및 API 구축, 서버 배포 |

</br>

# 📮 서비스 개요
### 1️⃣ 문제 정의 / 경쟁사 분석
![image](https://github.com/user-attachments/assets/0459f9e9-d718-4275-8fa9-4e0fdf471c69)

### 2️⃣ 유저 리서치 / 기대 효과
![image](https://github.com/user-attachments/assets/16bb132d-f8ea-4515-b320-c15e76dae6d1)

### 3️⃣ Information Architecture / 비즈니스 모델
![image](https://github.com/user-attachments/assets/5ab8ceaf-80bb-4292-a0c7-017e8af18a11)

</br>

# 💻 서비스 기능
![Main Features](https://github.com/user-attachments/assets/d71f5e6a-7e45-4cf7-aa14-067980fbaad5)

</br>

# 🕹️ API 명세서
Swagger : https://jk-project.site/swagger-ui/index.html

</br>

# 🗂 ERD
![image](https://github.com/user-attachments/assets/a5b80568-82c3-4924-943a-484f3ae7d0a2)

</br>

# 🏦 시스템 아키텍처
![image](https://github.com/user-attachments/assets/ad2ed410-796b-4a85-bd69-9cb9f33324ea)

</br>

# 🌆 프론트엔드
### 🛠️ 기술 스택
- #### Language, Framework, Library
  ![Next.js](https://img.shields.io/badge/Next.js-000000?style=flat-square&logo=Next.js&logoColor=white)
  ![TypeScript](https://img.shields.io/badge/TypeScript-3178C6?style=flat-square&logo=TypeScript&logoColor=white)
  ![vanilla-extract](https://img.shields.io/badge/vanilla--extract-DDD?style=flat-square&logo=css3&logoColor=black)
  ![Turborepo](https://img.shields.io/badge/Turborepo-000000?style=flat-square&logo=Vercel&logoColor=white)
  ![pnpm](https://img.shields.io/badge/pnpm-F69220?style=flat-square&logo=pnpm&logoColor=white)
  ![TanStack Query](https://img.shields.io/badge/TanStack_Query-FF4154?style=flat-square&logo=ReactQuery&logoColor=white)
  - **Next.js (App Router)** : 페이지·레이아웃·모달의 구조적 분리를 통해 유지보수성과 재사용성을 강화
  - **TypeScript** :  Props와 API 타입 명세를 통해 코드의 안정성과 예측 가능성 확보
  - **vanilla-extract** : 디자인 토큰 기반의 타입 안전한 스타일 관리를 통해 일관된 UI 구현
  - **Turborepo + pnpm** :  모노레포 구조와 병렬 빌드를 통해 패키지 간 의존성 관리와 빌드 속도 향상
  - **TanStack Query (React Query)** : 서버 상태 관리 자동화를 통해 데이터 요청 최적화 및 CSR/SSR 통합 처리
- #### CI/CD
  ![Storybook](https://img.shields.io/badge/Storybook-FF4785.svg?style=flat-square&logo=Storybook&logoColor=white)
  ![Github Actions](https://img.shields.io/badge/Github_Actions-2088FF.svg?style=flat-square&logo=GithubActions&logoColor=white)
  - **Storybook + GitHub Actions** : PR마다 공통 컴포넌트 변경사항을 시각화 배포하여 디자이너·개발자 협업 간 피드백 사이클 단축
- #### 협업 툴
  ![Discord](https://img.shields.io/badge/Discord-5865F2.svg?style=flat-square&logo=discord&logoColor=white)
  ![Notion](https://img.shields.io/badge/Notion-000000.svg?style=flat-square&logo=notion&logoColor=white)
</br>

### 📜 개발 규칙
- #### branch naming convention
  `[branch 유형]/[이슈번호-작업내용]` (예: `feat/#1-login-UI`)
- #### commit convention
  `[커밋 유형] : [커밋 메시지] ([이슈번호])` (예: `feat: 로그인 구현 (#1)`)

  | Convention Type | Description |
  | --- | --- |
  | `init` | 브랜치 첫 커밋 |
  | `feat` | 새로운 기능에 대한 커밋 |
  | `fix` | 버그 수정에 대한 커밋 |
  | `build` | 빌드 관련 파일 수정에 대한 커밋 |
  | `chore` | 그 외 자잘한 수정에 대한 커밋 |
  | `docs` | 문서 수정에 대한 커밋 |
  | `style` | 코드 스타일 혹은 포맷 등에 관한 커밋 |
  | `refactor` | 코드 리팩토링에 대한 커밋 |

- #### code convention
  - 2-space indent
  - `camelCase` : 변수명, 함수명, 폴더명, 파일 명 (컴포넌트 제외), 파라미터
  - `PascalCase` : 클래스명, 컴포넌트명
  - `BIG_SNAKE_CASE` : 상수명
- #### issue template
  **제목** : `[prefix]` `[구현할 내용]` (예: `[feat] 로그인 UI 구현`)
  ```markdown
   ## 💚 어떤 기능인가요?
    카톡 로그인 기능을 개발.
  
  
  ## ✅ To Dos
  
    - [ ]
    - [ ]
    - [ ]
  ```
- #### PR template
   **제목** : `[prefix]` `[구현할 내용]` (예: `[feat] 로그인 UI 구현`)
  ```markdown
  ## 이슈 넘버
  - close # 
  <!-- # 뒤에 이슈넘버를 써서 이슈를 닫아주세요 -->
  
  ## 구현 사항
  <!-- 실제로 변경한 사항을 설명해주세요.-->
  
  - [ ]
  - [ ]
  - [ ]
  - [ ]
  
  ## Need Review
  - ~ 부분 이렇게 구현했어요, 피드백 부탁해요!
  <!-- 어떤 부분에 리뷰어가 집중해야 하는지 or 해당 PR에서 논의가 필요한 사항을 적어주세요. -->
  
  
  
  ## 📸 스크린샷
  <!-- 팀원들이 이해하기 쉽도록 스크린샷을 첨부해주세요. -->
  
  
  
  ## Reference
  <!-- 참고한 사이트가 있다면 링크를 공유해주세요. -->
  ```
</br>

### 📂 프로젝트 구조
```
.
├── apps/
│   └── web/                  # Next.js 애플리케이션 (App Router 기반)
├── packages/
│   ├── eslint-config/        # Turborepo용 ESLint 설정 패키지 
│   ├── typescript-config/    # 공통 TypeScript 설정
│   ├── theme/                # 디자인 토큰 저장소
│   │   └── tokens/           # 색상, 타이포그래피 등 theme 구성 요소들
│   ├── ui/                   # 공통 UI 컴포넌트 및 아이콘
│   └── utils/                # 공통 유틸 함수 및 커스텀 훅 
```

### 🗂 각 폴더 설명

| 폴더 경로 | 설명 |
|------|------|
| `apps/web` | 실제 사용자 화면을 구성하기 위한 **Next.js 애플리케이션 코드**가 위치한 곳 |
| `packages/eslint-config`<br>`packages/typescript-config` | 코드 품질과 일관성을 유지하기 위한 **ESLint 및 TypeScript 공통 설정** 패키지 |
| `packages/theme` | 색상, 여백, 타이포그래피 등을 재사용 가능하게 구성하기 위한 **디자인 토큰 패키지** |
| `packages/ui` | 버튼, 인풋, 헤더 등 UI 요소의 재사용을 위한 **공통 UI 컴포넌트 및 아이콘 패키지** |
| `packages/utils` | 프로젝트 전반에 활용되는 **공통 유틸 함수 및 커스텀 훅**을 관리하기 위한 패키지 |

# 🌃 백엔드
### 🛠️ 기술 스택
- #### Language, Framework, Library
  ![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=flat-square&logo=SpringBoot&logoColor=white)
  ![Java](https://img.shields.io/badge/Java_17-007396?style=flat-square&logo=OpenJDK&logoColor=white)
  ![QueryDSL](https://img.shields.io/badge/QueryDSL-000000?style=flat-square)
  ![Gradle](https://img.shields.io/badge/Gradle-02303A?style=flat-square&logo=Gradle&logoColor=white)
  - **Spring Boot** : RESTful API 설계 및 빠른 서버 사이드 개발
  - **Java 17** : LTS 버전 기반의 안정적인 백엔드 개발
  - **QueryDSL** : 복잡한 조건의 쿼리를 타입 안정성 있게 처리
  - **Gradle** : 프로젝트 의존성 및 빌드 자동화
- #### CI/CD
  ![Github Actions](https://img.shields.io/badge/Github_Actions-2088FF.svg?style=flat-square&logo=GithubActions&logoColor=white)
  ![Docker Compose](https://img.shields.io/badge/Docker_Compose-2496ED?style=flat-square&logo=docker&logoColor=white)
  - **GitHub Actions + Docker Compose** : 코드 푸시 시 자동 빌드 및 테스트, 로컬 환경과 유사한 컨테이너 기반 배포 구성
- #### Test
  ![JUnit](https://img.shields.io/badge/JUnit-25A162?style=flat-square&logo=JUnit5&logoColor=white)
  - **JUnit** : 단위 테스트 및 통합 테스트를 통해 비즈니스 로직의 신뢰성 확보
- #### Database
  ![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=flat-square&logo=MySQL&logoColor=white)
  ![Redis](https://img.shields.io/badge/Redis-DC382D?style=flat-square&logo=Redis&logoColor=white)
  - **MySQL** : 관계형 데이터베이스로 주요 도메인 저장
  - **Redis** : 토큰 저장 및 캐싱 용도로 사용
- #### 협업 툴
  ![Discord](https://img.shields.io/badge/Discord-5865F2.svg?style=flat-square&logo=discord&logoColor=white)
  ![Notion](https://img.shields.io/badge/Notion-000000.svg?style=flat-square&logo=notion&logoColor=white)
</br>

### 📜 개발 규칙
- #### branch naming convention
  `[branch 유형]/[이슈번호-작업내용]` (예: `feat/55-login`)
- #### commit convention
  `[커밋 유형] : [커밋 메시지] [이슈번호]` (예: `feat: 로그인 구현 #4`)
  
  | Convention Type | Description |
  | --- | --- |
  | `feat` | 새로운 기능 구현 |
  | `add` | 파일 및 코드 추가 |
  | `chore` | 부수적인 코드 수정 및 기타 변경사항 |
  | `docs` | 문서 추가 및 수정, 삭제 |
  | `fix` | 버그 수정(코드 고치기) |
  | `rename` | 파일 및 폴더 이름 변경 |
  | `test` | 테스트 코드 추가 및 수정, 삭제 |
  | `refactor` | 코드 리팩토링 |
  | `setting` | ci/cd, 배포 관련, 프로젝트 세팅  |

- #### code convention
  - 2-space indent
  - camelCase 변수명
  - PascalCase 클래스명
  - Javadoc 주석 사용
- #### issue template
  **제목** : `[유형 별 이모지 (예: ✨)]` `[구현할 내용]`
  ```markdown
  ### 📌 Description
  
  ---
  ### ✅ Task
  - [ ] Task 1
  - [ ] Task 2
  ```
- #### PR template
  **제목** : `[유형 별 이모지 (예: ✨)]` `[구현한 내용]`
  ```markdown
  ### ✨ Related Issue
  
  ---
  
  ### 📌 Task Details
  - [x] Task 1
  - [x] Task 2
  
  ---
  
  ### 💬 Review Requirements (Optional)
  ```
</br>
