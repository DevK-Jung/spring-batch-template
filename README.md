# Batch Template Project

> Spring Batch + Quartz 기반의 스케줄러 템플릿 프로젝트

이 프로젝트는 Spring Batch와 Quartz를 통합하여 유연한 배치 처리 및 스케줄링 환경을 제공하는 템플릿입니다.  
`application.yml`과 `schedule.yml`을 기반으로 배치 Job을 등록하고, API 또는 Quartz 트리거로 실행할 수 있습니다.

---

## 🛠 기술 스택

| 기술           | 버전     | 설명                            |
|----------------|----------|---------------------------------|
| Java           | 21       | 프로젝트 개발 언어              |
| Spring Boot    | 3.5.0    | 전반적인 애플리케이션 프레임워크 |
| Spring Batch   | latest   | 배치 처리 프레임워크           |
| Spring Quartz  | latest   | 스케줄링 프레임워크            |
| Spring Data JPA| latest   | DB 접근 레이어 (선택적 사용)   |

---

## 🗂 프로젝트 구조

```
+---java
|   \---com.kjung.batchtemplate
|       |   BatchTemplateApplication.java         # Spring Boot 메인 클래스
|       |
|       +---api
|       |   +---dto
|       |   |       SampleDto.java                # API 요청용 DTO
|       |   \---sample
|       |           SampleController.java         # 샘플 컨트롤러
|       |
|       +---core
|       |   +---base
|       |   |       AbstractJobConfig.java        # Job 구성 공통 추상 클래스
|       |   +---batch
|       |   |       BatchJobRunner.java           # API/Quartz 실행 공통 유틸
|       |   +---config
|       |   |       QuartzPropertiesConfig.java   # Quartz 속성 바인딩 설정
|       |   +---factory
|       |   |       YamlPropertySourceFactory.java# schedule.yml 로딩 유틸
|       |   \---property
|       |           QuartzJobProperties.java      # schedule.yml 프로퍼티 모델
|       |
|       +---jobs
|       |   \---sample
|       |           SampleJob.java                # 샘플 배치 Job
|       |
|       \---quartz
|           +---executor
|           |       QuartzBatchJobExecutor.java   # Quartz → Batch 실행 클래스
|           +---listener
|           |       QuartzJobMonitoringListener.java # Job 모니터링 리스너
|           \---registrar
|                   QuartzBatchJobRegistrar.java  # schedule.yml 기반 Job 등록기
|
\---resources
    application.yml                               # 일반 설정
    schedule.yml                                  # 배치/Quartz Job 등록 설정
```

---

## 🚀 실행 방법

### 1. 의존성 설치
```bash
./gradlew clean build
```

### 2. 애플리케이션 실행
```bash
java -jar build/libs/batch-template-0.0.1-SNAPSHOT.jar
```

### 3. REST API 실행
예시:
```
POST /api/v1/sample
Content-Type: application/json

{
  "name": "test",
  "age": 5
}
```

---

## 📌 기타 참고

- `QuartzBatchJobExecutor`는 Quartz 트리거가 발생했을 때 Batch Job을 실행합니다.
- `BatchJobRunner`를 통해 Controller에서도 Batch Job을 직접 실행할 수 있습니다.
- Job은 `schedule.yml`에 등록되며, 실행 주기 및 Job 이름을 설정할 수 있습니다.

---

> 작성자: 김정현  
> GitHub: [DevK-Jung](https://github.com/DevK-Jung)
