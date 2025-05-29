# 📥 리크루팅 프로세스 자동화 통합 솔루션, 위더스(WITHUS)

🔗 Link : [https://withus-ten.vercel.app](https://withus-ten.vercel.app/)

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

PM의 별명에서 비롯된 팀명으로, 서비스명 ‘위더스(WithUs)’와 라임을 맞춰 유쾌하면서도 따뜻한 팀의 색깔을 담았습니다. ‘중전’처럼 중심을 잡고 서로를 챙기며, ‘마더스’처럼 따뜻하게 사용자 곁에서 진심을 다하는 서비스를 만들겠다는 의지를 담은 이름입니다.

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
  - **Next.js (App Router)** : App Router 기반으로 페이지, 레이아웃, 모달 등 구조적 분리를 명확히 함
  - **TypeScript** : API 통신, 컴포넌트 Props에 강한 타입을 부여해 안정성 확보
  - **vanilla-extract** : 디자인 토큰 기반 CSS-in-TypeScript로, color, spacing, typography 등을 type-safe하게 관리
  - **Turborepo + pnpm** : 모노레포 구조로 앱과 패키지(컴포넌트, 테마, 유틸 등)를 분리, 패키지 간 의존성 명확하게 관리, 병렬 빌드 성능 향상
  - **TanStack Query (React Query)** : 서버 상태 관리, 요청 중복 제거, 캐싱, SSR/CSR 통합 처리에 최적화
- #### CI/CD
  ![Storybook](https://img.shields.io/badge/Storybook-FF4785.svg?style=flat-square&logo=Storybook&logoColor=white)
  ![Github Actions](https://img.shields.io/badge/Github_Actions-2088FF.svg?style=flat-square&logo=GithubActions&logoColor=white)
  - **Storybook + GitHub Actions** : 공통 컴포넌트 변경사항을 PR마다 자동 배포로 시각화하여 디자이너·개발자 협업 간 피드백 사이클 단축
- #### 협업 툴
  ![Discord](https://img.shields.io/badge/Discord-5865F2.svg?style=flat-square&logo=discord&logoColor=white)
  ![Notion](https://img.shields.io/badge/Notion-000000.svg?style=flat-square&logo=notion&logoColor=white)
</br>

### 📜 개발 규칙
- #### branch naming convention
- #### commit convention
- #### issue template
- #### PR template
</br>

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

  | `feat` | 새로운 기능 구현 |
  | --- | --- |
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
