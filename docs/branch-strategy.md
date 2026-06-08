# Branch Strategy — nu_Trust

## Overview

GitFlow 기반分支 전략을を採用합니다. hotfix-only exceptions are allowed.

## Branch Types

### Main (production)
- **목적:** 프로덕션 배포용 stable branch
- **병합 가능:** `develop` (release 완료 시만), `hotfix/*`
- **보호 설정:** 
  - ✅ Force push 금지
  - ✅ 직전 리뷰어 승인 필수 (1+)
  - ✅ CI pipeline 통과 필수
  - ✅ Merge 요청은 `develop → main`

### Develop (integration)
- **목적:** Sprint 통합 & GitLab 저장소 대상
- **병합 가능:** `feature/*`, `release/*`, `hotfix/*`
- **보호 설정:**
  - ✅ Force push 금지
  - ✅ 직전 리뷰어 승인 필수 (1+)
  - ✅ CI pipeline 통과 필수

### Feature branches
- **형식:** `feature/ISSUE-NUMBER-brief-description`
- **예시:** `feature/189-spring-security-jwt-setup`
- **기원:** `develop`
- **병합 대상:** `develop`
- **수명:** 1 Sprint (보통 1-2주)

### Release branches
- **형식:** `release/vX.Y.Z`
- **예시:** `release/v1.0.0`
- **기원:** `develop`
- **병합 대상:** `main` + `develop` (merge back)
- **생성 시기:** Sprint 7 QA 완료 시

### Hotfix branches
- **형식:** `hotfix/ISSUE-NUMBER-brief-description`
- **예시:** `hotfix/192-jwt-expired-token-bug`
- **기원:** `main`
- **병합 대상:** `main` + `develop` (merge back)
- **사용 시기:** 프로덕션 critical bug만

## Workflow Summary

```
main ──────────────────────────────────────●───●───────────→
              ↗ release/v1.0.0 ↗             │   │
develop ●──●─────────────────────────────────●───●───→
         ↑      ↖ feature/* ↙                  │   │
hotfix●──┘                                       │   │
                                                 │   │
                                          prod  stage dev
```

1. **Sprint 개발:** `develop`에서 `feature/*` 브랜치 생성 → PR → `develop` 병합
2. **Sprint 종료:** `release/vX.Y.Z` 생성 → QA → `main` 병합 → `develop` merge back
3. **Hotfix:** `main`에서 `hotfix/*` 생성 → 긴급 수정 → `main` + `develop` 병합

## GitLab 설정 체크리스트

- [x] `main` branch protection enabled
- [x] `develop` branch protection enabled  
- [x] Require approvals: минимум 1
- [x] Require CI to pass
- [x] Disallow force pushes
- [ ] (나중에) Require review from CODEOWNERS
