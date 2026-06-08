# nu_Trust

> (주)엔유비즈 CRM 플랫폼 — 클라이언트 이슈를 티켓으로 추적, 실시간 채팅, 감사 추적(이력 관리), 관리자 패널(RBAC) 제공

## 프로젝트 개요

| 항목 | 내용 |
|------|------|
| **서비스명** | nu_Trust (nutrust.kr) |
| **클라이언트** | (주)엔유비즈 |
| **아키텍처** | Spring Boot 3.x + Vue 3 + PostgreSQL 15 |
| **개발 환경** | GitLab Auto-DevOps, Docker Compose |
| **AWS** | RDS(PostgreSQL), ElastiCache(Redis), EC2/ECS, S3+CloudFront |
| **타임존** | Asia/Seoul |

## 기술 스택

| 레이어 | 기술 | 비고 |
|--------|------|------|
| **Backend** | Spring Boot 3.x (Java 21) | Web, Security, Data JPA, Validation |
| **Frontend** | Vue 3 + Vite + Vue Router | Composition API, JavaScript |
| **Database** | PostgreSQL 15 | pgcrypto, btree_gin 확장 |
| **Auth** | JWT (Access/Refresh) + Spring Security | @PreAuthorize 기반 RBAC |
| **Realtime** | RabbitMQ + STOMP over SockJS | 채팅·알림 |
| **Cache** | Redis (ElastiCache) | Refresh Token, 세션, 캐시 |
| **CI/CD** | GitLab Auto-DevOps | 12 stage 파이프라인 |
| **배포** | AWS EC2/ECS + RDS + CloudFront | dev/stage/prod 환경 분리 |

## RBAC (Role-Based Access Control)

| Role | 설명 | Scope |
|------|------|-------|
| **ADMIN** | 전체 시스템 관리자 | 전사 |
| **COMPANY_ADMIN** | 회사 관리자 | self.company_id |
| **SUPPORT** | 지원 담당자 | self.company_id |
| **CUSTOMER** | 클라이언트 (고객) | self.company_id |

**권한 계층:** ADMIN > COMPANY_ADMIN > SUPPORT > CUSTOMER

## 디렉토리 구조

```
nu-Trust/
├── .gitlab-ci.yml          # GitLab Auto-DevOps (12 stage)
├── frontend/                # Vue 3 + Vite 프론트엔드
│   ├── src/                 # 뷰, 컴포넌트, 라우터, 스타일
│   └── vite.config.js       # Vite 설정
├── .sisyphus/plans/         # 설계 문서 모음
│   ├── nu-trust-development-plan-korean.md   # 아키텍처, 기술 스택, AWS
│   ├── nu-trust-data-model-erd.md            # PostgreSQL DDL (16+ 엔티티)
│   ├── nu-trust-uiux-specification.md        # UI 레이아웃, 상태 Ribbon, 채팅
│   └── nu-trust-development-workflow.md      # Sprint WBS (16주), Git flow
├── .sisyphus/Design-UIUX/   # UI 디자인 참고 자료
├── AGENTS.md                # Sisyphus 에이전트 가이드
└── README.md
```

## 빠른 시작

```bash
# 프론트엔드 개발 서버
cd frontend && npm install && npm run dev

# Docker Compose (백엔드 인프라)
docker compose up -d   # PostgreSQL, Redis, RabbitMQ

# GitLab 연동
git push gitlab develop
```

## 환경 URL

| 환경 | URL |
|------|-----|
| 개발 | dev.nutrust.kr |
| 스테이징 | stage.nuttrust.kr |
| 프로덕션 | nutrust.kr |

## WBS / 스프린트

| Sprint | 기간 |핵심 작업 |
|--------|------|-----------|
| **Sprint 0** | W1-W2 | GitLab, Auto-DevOps, Docker, Flyway v001 |
| **Sprint 1** | W3-W4 | Auth (JWT), Login/Signup, RBAC |
| **Sprint 2** | W5-W6 | Company, User CRUD, 권한 분리 |
| **Sprint 3** | W7-W8 | Project Mgmt, 매니저 배정 |
| **Sprint 4** | W9-W12 | Ticket CRUD, 상태 전이 엔진 |
| **Sprint 5** | W13-W14 | 실시간 채팅, 알림 |
| **Sprint 6** | W15 | 관리자 패널 (RBAC 관리) |
| **Sprint 7** | W16 | QA, 모의 배포, 운영 가이드 |

## 개발 규칙

- **커밋 메시지:** Conventional Commits (`feat:`, `fix:`, `refactor:`, `test:`, `chore:`)
- **브랜치 전략:** GitFlow 기반 (→ docs/branch-strategy.md 참조)
- **코드 스타일:** Prettier + ESLint + EditorConfig
- **PR:** CODEOWNERS 기반 1+ 리뷰 필수

## 라이선스

© 2025 (주)엔유비즈. All rights reserved.
* [Automatically close issues from merge requests](https://docs.gitlab.com/user/project/issues/managing_issues/#closing-issues-automatically)
* [Enable merge request approvals](https://docs.gitlab.com/user/project/merge_requests/approvals/)
* [Set auto-merge](https://docs.gitlab.com/user/project/merge_requests/auto_merge/)

## Test and Deploy

Use the built-in continuous integration in GitLab.

* [Get started with GitLab CI/CD](https://docs.gitlab.com/ci/quick_start/)
* [Analyze your code for known vulnerabilities with Static Application Security Testing (SAST)](https://docs.gitlab.com/user/application_security/sast/)
* [Deploy to Kubernetes, Amazon EC2, or Amazon ECS using Auto Deploy](https://docs.gitlab.com/topics/autodevops/requirements/)
* [Use pull-based deployments for improved Kubernetes management](https://docs.gitlab.com/user/clusters/agent/)
* [Set up protected environments](https://docs.gitlab.com/ci/environments/protected_environments/)

***

# Editing this README

When you're ready to make this README your own, just edit this file and use the handy template below (or feel free to structure it however you want - this is just a starting point!). Thanks to [makeareadme.com](https://www.makeareadme.com/) for this template.

## Suggestions for a good README

Every project is different, so consider which of these sections apply to yours. The sections used in the template are suggestions for most open source projects. Also keep in mind that while a README can be too long and detailed, too long is better than too short. If you think your README is too long, consider utilizing another form of documentation rather than cutting out information.

## Name
Choose a self-explaining name for your project.

## Description
Let people know what your project can do specifically. Provide context and add a link to any reference visitors might be unfamiliar with. A list of Features or a Background subsection can also be added here. If there are alternatives to your project, this is a good place to list differentiating factors.

## Badges
On some READMEs, you may see small images that convey metadata, such as whether or not all the tests are passing for the project. You can use Shields to add some to your README. Many services also have instructions for adding a badge.

## Visuals
Depending on what you are making, it can be a good idea to include screenshots or even a video (you'll frequently see GIFs rather than actual videos). Tools like ttygif can help, but check out Asciinema for a more sophisticated method.

## Installation
Within a particular ecosystem, there may be a common way of installing things, such as using Yarn, NuGet, or Homebrew. However, consider the possibility that whoever is reading your README is a novice and would like more guidance. Listing specific steps helps remove ambiguity and gets people to using your project as quickly as possible. If it only runs in a specific context like a particular programming language version or operating system or has dependencies that have to be installed manually, also add a Requirements subsection.

## Usage
Use examples liberally, and show the expected output if you can. It's helpful to have inline the smallest example of usage that you can demonstrate, while providing links to more sophisticated examples if they are too long to reasonably include in the README.

## Support
Tell people where they can go to for help. It can be any combination of an issue tracker, a chat room, an email address, etc.

## Roadmap
If you have ideas for releases in the future, it is a good idea to list them in the README.

## Contributing
State if you are open to contributions and what your requirements are for accepting them.

For people who want to make changes to your project, it's helpful to have some documentation on how to get started. Perhaps there is a script that they should run or some environment variables that they need to set. Make these steps explicit. These instructions could also be useful to your future self.

You can also document commands to lint the code or run tests. These steps help to ensure high code quality and reduce the likelihood that the changes inadvertently break something. Having instructions for running tests is especially helpful if it requires external setup, such as starting a Selenium server for testing in a browser.

## Authors and acknowledgment
Show your appreciation to those who have contributed to the project.

## License
For open source projects, say how it is licensed.

## Project status
If you have run out of energy or time for your project, put a note at the top of the README saying that development has slowed down or stopped completely. Someone may choose to fork your project or volunteer to step in as a maintainer or owner, allowing your project to keep going. You can also make an explicit request for maintainers.
