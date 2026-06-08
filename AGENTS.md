# nu_Trust

**Stage:** 문서화 & 기획만 진행. 애플리케이션 코드는 없음.

## OVERVIEW

nu_Trust — (주)엔유비즈 CRM 플랫폼. 클라이언트 이슈를 티켓으로 추적, 실시간 채팅, 감사 추적(이력 관리), 관리자 패널(RBAC) 제공. 현재까지의 모든 작업은 **기획/Proof of Concept** 범위이며, 한국어_markdown_ 문서로만 존재.

## STRUCTURE

```
nu-Trust/
└── .sisyphus/plans/        # 설계 문서 전량 (4개 파일, ~3000+ 라인)
    ├── nu-trust-development-plan-korean.md   # 아키텍처, 기술 스택, AWS 배포 계획
    ├── nu-trust-data-model-erd.md            # PostgreSQL 15 DDL (16+ 엔티티, 전체 스키마)
    ├── nu-trust-uiux-specification.md        # UI 레이아웃, 상태 Ribbon, 채팅, Kanban
    └── nu-trust-development-workflow.md      # Sprint WBS (16주, Sprint 0~4), Git flow, QA 기준
└── AGENTS.md                                # 이 파일
```

`.sisyphus/` — Sisyphus 에이전트 오케스트레이션용 내부 디렉토리. 모든human-valuable 내용은 `.sisyphus/plans/` 에 있음.

## WHERE TO LOOK

| 필요 사항 | 파일 | 주요 내용 |
|-----------|------|----------|
| 기술 스택, 아키텍처, AWS | `nu-trust-development-plan-korean.md` | Spring Boot 3.x, React 18+TS, RDS/EC2/ECS/S3 |
| DB 스키마 (DDL) | `nu-trust-data-model-erd.md` | PostgreSQL 15, 16+ 엔티티, 제약조건, 트리거 |
| UI/UX 명세 | `nu-trust-uiux-specification.md` | 레이아웃, 상태 전이, 채팅 UI, Kanban, 상호작용 |
| Sprint 계획, WBS, 커밋 규칙 | `nu-trust-development-workflow.md` | 16주 WBS, RACI, 커밋 컨벤션, Git flow, QA 게이트 |

## KEY CONSTRAINTS

- **언어:** 모든 기획 문서와 프로토 타입 UI는 **한국어**로 작성
- **RBAC:** ADMIN > COMPANY_ADMIN > SUPPORT > CUSTOMER (company_id 스코프)
- **타임존:** Asia/Seoul (`SET timezone = 'Asia/Seoul'` in DDL)
- **PG 확장:** `pgcrypto`, `btree_gin`
- **실시간 통신:** RabbitMQ 기반 STOMP over SockJS
- **AWS 환경:** RDS(PostgreSQL), ElastiCache(Redis), EC2/ECS, S3+CloudFront
- **환경 URL:** dev.nutrust.kr / stage.nuttrust.kr / nutrust.kr
- **TRIMS 전략:** User 테이블은 소프트 딜리트 없음, 하드 삭제만 적용
- **문서 형식:** UTF-8, `---` 섹션 분리자 사용

## CONVENTIONS

아직 적용된 코드 컨벤션 없음 — 린터, 빌드 설정, 테스트 프레임워크, 코드 없음. 컨벤션은 markdown 기획서에만 존재하며, 첫 커밋 시 공식화.

문서화 진행 시:
- 모든 마스터 문서(AGENTS.md 등)는 **한국어**로 작성
- 계획서와 마스터 간 모순 발생 시, **마스터 문서(AGENTS.md) ← 기획서** 순으로 우선순위
- 새 문서 생성 전 `.sisyphus/plans/` 에서 기존 맥락 확인 → 중복 정보 방지
