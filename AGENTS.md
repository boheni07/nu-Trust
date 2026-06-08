# nu_Trust

**Stage:** 기획 + 프론트엔드 프로토타입 완료. 백엔드 구현은 아직 없음.

## OVERVIEW

nu_Trust — (주)엔유비즈 CRM 플랫폼. 클라이언트 이슈를 티켓으로 추적, 실시간 채팅, 감사 추적(이력 관리), 관리자 패널(RBAC) 제공.
**기획 문서** (.sisyphus/plans/) + **Vue 3 프론트엔드 프로토타입** (frontend/) + **백엔드 스키마** (.sisyphus/plans/data-model) 존재. 백엔드(Spring Boot)는 미구현.

## STRUCTURE

```
nu-Trust/
├── .sisyphus/plans/        # 설계 문서 (4개 파일, ~3000+ 라인)
│   ├── nu-trust-development-plan-korean.md   # 아키텍처, 기술 스택, AWS 배포
│   ├── nu-trust-data-model-erd.md            # PostgreSQL 15 DDL (16+ 엔티티)
│   ├── nu-trust-uiux-specification.md        # UI 레이아웃, 상태 Ribbon, 채팅
│   └── nu-trust-development-workflow.md      # Sprint WBS (16주), Git flow, QA
├── .sisyphus/Design-UIUX/       # UI 디자인 참고 자료
├── frontend/                    # Vue 3 + Vite 프로토타입 (working)
│   ├── src/                     # 뷰, 컴포넌트, 라우터, 스타일
│   ├── public/                  # favicon, 아이콘 SVG sprite
│   └── vite.config.js
├── .gitlab-ci.yml               # GitLab Auto-DevOps (12 stage)
├── AGENTS.md                    # 이 파일
└── README.md
```

`.sisyphus/` — Sisyphus 에이전트 오케스트레이션용 내부 디렉토리. 모든 human-valuable 내용은 `.sisyphus/plans/` 에 있음.

## WHERE TO LOOK

| 필요 사항 | 파일/위치 | 주요 내용 |
|-----------|-----------|----------|
| 전체 아키텍처, 기술 스택, AWS | `.sisyphus/plans/nu-trust-development-plan-korean.md` | Spring Boot 3.x, React 18+TS, RDS/EC2/ECS/S3 |
| DB 스키마 (DDL) | `.sisyphus/plans/nu-trust-data-model-erd.md` | PostgreSQL 15, 16+ 엔티티, 제약조건, 트리거 |
| UI/UX 명세 | `.sisyphus/plans/nu-trust-uiux-specification.md` | 레이아웃, 상태 전이, 채팅 UI, Kanban |
| Sprint/WBS/커밋 규칙 | `.sisyphus/plans/nu-trust-development-workflow.md` | 16주 WBS, RACI, 컨벤션, Git flow, QA |
| 프론트엔드 소스 | `frontend/src/` | Vue 3 SFC, Composition API, Vue Router |
| 디자인 토큰 | `frontend/src/styles/tokens.css` | 색상, 스페이싱, 서체, 애니메이션 |
| 라우팅 정의 | `frontend/src/router/index.js` | SPA 라우트, chunk split, nested layout |
| CI/CD | `.gitlab-ci.yml` | GitLab Auto-DevOps, 12 stage 파이프라인 |

## KEY CONSTRAINTS

- **언어:** 기획 문서, UI 라벨, 프로토타입 모두 **한국어**
- **RBAC:** ADMIN > COMPANY_ADMIN > SUPPORT > CUSTOMER (company_id 스코프)
- **타임존:** Asia/Seoul (`SET timezone = 'Asia/Seoul'` in DDL)
- **PG 확장:** `pgcrypto`, `btree_gin`
- **실시간 통신:** RabbitMQ 기반 STOMP over SockJS (백엔드 구현 예정)
- **AWS 환경:** RDS(PostgreSQL), ElastiCache(Redis), EC2/ECS, S3+CloudFront
- **환경 URL:** dev.nutrust.kr / stage.nuttrust.kr / nutrust.kr
- **TRIMS 전략:** User 테이블 하드 삭제만 (소프트 딜리트 없음)
- **문서 형식:** UTF-8, `---` 섹션 분리자 사용

## COMMANDS

```bash
# 프론트엔드
cd frontend && npm run dev     # Vite dev server (localhost:5173)
cd frontend && npm run build   # 프로덕션 빌드 → dist/
cd frontend && npm run preview # 빌드 결과 로컬 미리보기

# CI/CD
git push origin main           # GitLab Auto-DevOps 파이프라인 실행 (.gitlab-ci.yml)
```

## NOTES

- 프론트엔드는 **JavaScript** (TypeScript 아님). 백엔드 구현 시 TS로 전환 예정.
- 상태 관리: 현재 컴포넌트 로컬 `ref/computed` + `provide/inject`. 백엔드 연동 시 Pinia 도입.
- 가짜 데이터: `frontend/src/utils/mockData.js` — 실제 API 연동 전 프로토타입용.
- 기획서와 마스터(AGENTS.md) 간 모순 → **AGENTS.md 우선**. 새 문서 생성 전 `.sisyphus/plans/` 확인 → 중복 방지.
