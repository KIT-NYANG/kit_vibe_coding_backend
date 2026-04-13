<p align="center">
  <img src="docs/nacom.jpg" alt="NYANG Backend" width="800"/>
</p>

# NYANG Backend

## 프로젝트 개요
NYANG Backend는 AI 기반 학습 경험 개선을 위한 LMS 백엔드입니다.  
단순히 강의를 업로드하고 시청하는 기능에 머무르지 않고, 강의 영상의 STT 자막과 학습자의 시청 로그를 함께 활용해 더 나은 학습 경험을 제공하는 것을 목표로 합니다.

학습자에게는 퀴즈, 보충 설명, 복습 포인트를 제공하고, 강사에게는 실제로 학습자가 어려움을 겪는 구간과 개선이 필요한 지점을 전달할 수 있도록 설계했습니다.

---

## 목표
- 강의 업로드, 조회, 삭제 등 LMS 핵심 기능 제공
- 영상 시청 로그를 수집하여 학습 행동 데이터 축적
- STT 서버 연동을 통한 자막 및 세그먼트 데이터 저장
- 강의 내용 요약 및 키워드 추출
- 강의 내용 기반 Pre Analysis 수행
- 실제 시청 패턴 기반 Aggregate Analysis 수행
- 학습자와 강사 모두에게 의미 있는 AI 기반 인사이트 제공

---

## 기술 스택

### Backend
- Java 21
- Spring Boot
- Spring Data JPA
- Spring Security
- Gradle

### Database
- PostgreSQL
- NeonDB

### AI / External
- FastAPI
- STT Server
- LLM 기반 분석 서버
- FFprobe

### Infra
- Docker
- Google Cloud Run
- Google Cloud Storage

---

### Architecture
<p align="center">
  <img src="docs/archi.png" alt="Architecture" width="800"/>
</p>

Frontend  
↓  
Spring Boot Backend  
├── 강의 / 강좌 관리  
├── 영상 업로드 및 메타데이터 저장  
├── 시청 로그 수집 및 세션 집계  
├── 마지막 시청 위치 관리  
├── 분석 결과 조회  
├── Database 연동  
└── AI Analysis Server 연동

Spring Boot Backend  
├─→ Database  
│   ├── 강의 정보 저장  
│   ├── 자막 / 세그먼트 저장  
│   ├── 시청 로그 및 세션 데이터 저장  
│   └── 분석 결과 저장  
│  
└─→ AI Analysis Server  
├── 전체 자막 생성  
├── 세그먼트 생성  
├── Pre Analysis 수행  
└── Aggregate Analysis 수행

---

## API 문서
Swagger를 통해 API 명세를 확인할 수 있습니다.

- Swagger UI: `http://localhost:8080/swagger-ui/index.html`

---

## 한 줄 소개
강의를 제공하는 데서 끝나지 않고, 강의 내용과 실제 학습 행동 데이터를 함께 분석해 더 나은 학습 경험을 만드는 AI 기반 LMS 백엔드입니다.