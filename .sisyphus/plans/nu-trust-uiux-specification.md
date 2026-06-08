## 6. UI/UX Specifications (UI/UX Design)

### 6.1 Ticket 상세 페이지 레이아웃

Ticket 상세 페이지는 주요 상호작용 Surface (Interaction Surface)입니다. 섹션 구분은 다음과 같습니다:

1. **상단 Status Ribbon** - 페이지 너비 전체를 가로지르는 가로 바 (Horizontal Bar)
2. **Ticket Details Section** - 요청 내용, 처리 계획 데이터, 첨부파일
3. **상태별 Action Buttons** - 현재 상태 코드 (`current_status`)에 따라 동적으로 렌더링
4. **Info Section** - 프로젝트명, 고객사명, 배정된 Support Manager 정보
5. **History Timeline** - 상태 전이의 시계열 목록
6. **하단 Chat/Comment Section** - 소통의 허브 (Communication Hub)

### 6.2 Status Ribbon Specifications

| Attribute | Specifications |
|-----------|--------------|
| 스타일 | 전체 너비 그라데이션 (Horizontal Gradient Bar) |
| 현재 상태 | 바 중앙에 하이라이트된 Badge |
| 색상 매핑 | 상태 --> Hex Code (Section 5.1 참조) |
| 애니메이션 | 상태 변경 시 부드러운 Color Transition |
| 타임스탬프 | Badge에 현재 상태 진입 일시 표시 |

### 6.3 Action Buttons

`current_status` 필드에 따라 Conditionally 렌더링됩니다:

| 상태 | 사용 가능한 Actions |
|------|-------------------|
| `REGISTERED` | 상세 보기, Support Manager 배정, 검토 시작 |
| `RECEIVED` | 처리 계획(Processing Plan) 생성 |
| `PROCESSING` | 진행 상황 업데이트, 연장 요청, 완료 요청 |
| `DELAYED` | 완료 요청, 연장 사유 추가 |
| `COMPLETION_REQUESTED` | 완료 승인, 완료 반려 (Approver 전용) |
| `APPROVED` | - (자동으로 COMPLETED로 전이) |
| `COMPLETED` | - (읽기 전용, Archive) |

### 6.4 Chat/Comment Section

| Attribute | Specifications |
|-----------|--------------|
| 레이아웃 | 입력(Input) 영역 상단, 메시지 목록 하단으로 흐르는 형태 |
| 메시지 정렬 | 최신 메시지 우선, 스크롤 내려서 과거 메시지 확인 |
| 발신자 메시지 | 우측 정렬 버블, 타임스탬프 표시 |
| 상대방 메시지 | 좌측 정렬 버블, Avatar와 Name 표시 |
| 첨부파일 | 메시지 버블 내부 Inline Preview |
| Typing Indicator | WebSocket을 통해 Real-time 표시 |
| Real-time 업데이트 | 모든 새 메시지는 WebSocket (STOMP over SockJS)으로 Live 도착 |

### 6.5 Admin Panel

Admin Panel에서 System 전체 차원의 Management 기능을 제공합니다:

- **Company 관리:** 고객사 및 내부 Company 등록, 수정, 비활성화
- **User 관리:** Role별 (admin, support, customer) User 생성/관리
- **Project 관리:** Project 배정, Manager 관계 설정, 생명주기 관리
- **System 설정:** Business Calendar 공휴일, Notification 설정, Server 관리

### 6.6 Project Detail 페이지

| Section | 내용 |
|---------|------|
| **Header** | 프로젝트명, 고객사명, 계약일/착수일/종료일, Project Manager 및 Support Manager |
| **Server/SW Cards** | 프로젝트별 Server 및 Software 관리용 Card Grid |
| **Ticket List** | Status, Type, Creator, Dates, Assignee 카럼의 Filterable Ticket 테이블 |
| **Quick Actions** | 새 Ticket 생성, Project Member 추가, Project 설정 수정 버튼 |

### 6.7 디자인 철학 (Design Philosophy)

- **Clean and Minimal** - Linear에서 영감 받은 인터페이스
- **국내 비즈니스 관행** - 양식 레이아웃, 날짜 형식, 승인 패턴 등에 통합
- **Responsive Layout** - 주요 Screen Size 지원
- **Accessible Color Palette** - 색각(Color Vision) 차이에서도 Status Color 의미가 명확히 전달되도록 보장

---

### 6.8 Admin Panel UI 명세 (관리자 패널 사용자 화면 설계)

Admin Panel은 ADMIN, SUPPORT, CUSTOMER 각 역할별로 다른 대시보드와 메뉴 구조를 가진다.

#### 6.8.1 역할별 대시보드 및 메뉴 구조

**ADMIN 대시보드 (전체 시스템 관리):**

```
┌──────────────────────────────────────────────────────────┐
│  nu_Trust Admin Panel                         [Admin] ▼ │
├──────────┬───────────────────────────────────────────────┤
│          │  Dashboard                                    │
│ 🔹 대시보드│  ┌────────┐ ┌────────┐ ┌────────┐ ┌──────┐ │
│          │  │Total    │ │Active   │ │Tickets │ │Alerts│ │
│ 🏢 회사   │  │Companie │ │Projects │ │Today   │ │      │ │
│          │  │  42     │ │  18     │ │  12     │ │  3  │ │
│ 👥 사용자│  └────────┘ └────────┘ └────────┘ └──────┘ │
│          │                                              │
│ 🔧 프로젝트│  ─ Recent Activity ─                        │
│          │  [Today] Kim from ABC Corp submitted ticket  │
│ 📊 리포트│           for "Feature X" (RECEIVED)         │
│          │  [Today] Lee assigned to PROJ-003            │
│ 📋 배정   │  [Yesterday] Ticket #142 completed          │
│          │                                              │
│ 🔑 개발사│  ─ Pending SLA Review ─                     │
│          │  Ticket #139 - 4h past deadline              │
│   등록    │  Ticket #145 - approaching SLA              │
│          │                                              │
│ 🏢 고객사│  ─ Company Summary ─                        │
│   등록    │  DEVELOPER companies: 3 (includes Enubiz)   │
│          │  CLIENT companies: 39                       │
│ 📦 프로젝트│                                              │
│   관리   │  ─ Quick Actions ─                          │
│          │  [+ New Company] [+ New Project]             │
│ 📝 티켓   │  [+ New User] [+ New Project Assignment]    │
│   일괄   │                                              │
│   관리   │                                              │
│          │  ─ System Settings ─                        │
│ 🔧 시스템│  SLA Config │ Role Config │ Email Template   │
│   설정   │  Audit Log  │ Backup      │ Logout          │
└──────────┴───────────────────────────────────────────────┘
```

**SUPPORT 대시보드 (개발팀 업무 관리):**

```
┌──────────────────────────────────────────────────────────┐
│  nu_Trust Workbench                         [Support] ▼ │
├──────────┬───────────────────────────────────────────────┤
│          │  Workbench                                    │
│ 🔹 대시보드│  ┌────────┐ ┌────────┐ ┌────────┐ ┌──────┐ │
│          │  │My      │ │My      │ │My      │ │SLA   │ │
│ 🏢 소속   │  │Tickets │ │Projects│ │Comments│ │Alerts│ │
│   회사만   │  │  8     │ │  3     │ │  24    │ │  1  │ │
│          │  └────────┘ └────────┘ └────────┘ └──────┘ │
│ 📦 소속   │                                              │
│   프로젝트│  ─ My Assigned Tickets ─                    │
│          │  ┌──────┬───────┬────────┬────────┬───────┐ │
│ 📝 티켓   │  │#Ticket │Status │Type    │Priority │Due  │ │
│          │  ├──────┼───────┼────────┼────────┼───────┤ │
│ 📊 리포트│  │#142  │RECEIVED│dissatis│HIGH    │5/15   │ │
│          │  ├──────┼───────┼────────┼────────┼───────┤ │
│          │  │#138  │PROCESS│improve  │MEDIUM  │5/12   │ │
│          │  ├──────┼───────┼────────┼────────┼───────┤ │
│ 🔑 개발사│  │#145  │RECEIVED│addition │LOW     │5/20   │ │
└──────────┴───────────────────────────────────────────────┘
```

**CUSTOMER 대시보드 (클라이언트 측):**

```
┌──────────────────────────────────────────────────────────┐
│  nu_Trust Client Portal                   [Customer] ▼ │
├──────────┬───────────────────────────────────────────────┤
│          │  My Projects                                  │
│ 🔹 대시보드│  ┌─────────────────┬───────────────────────┐ │
│ 📦 프로젝트│  │ Project Name    │ Active Tickets        │ │
│          │  ├─────────────────┼───────────────────────┤ │
│ 📝 내 티켓│  │ POSM Web App    │ ● 3 Open / 2 Closed   │ │
│          │  ├─────────────────┼───────────────────────┤ │
│          │  │ Mobile App v2.1 │ ● 1 Open / 0 Closed   │ │
│          │  └─────────────────┴───────────────────────┘ │
│          │                                              │
│          │  ─ My Recent Tickets ─                       │
│          │  [Ticket #142] "Login page slow loading"     │
│          │      Status: RECEIVED | Created: 5/14       │
│          │                                              │
│          │  [Ticket #139] "Dashboard layout bug"        │
│          │      Status: PROCESSING | Updated: 2h ago   │
│          │                                              │
│          │  ─ Quick Actions ─                          │
│          │  [+ New Ticket] [View All Tickets]           │
└──────────┴───────────────────────────────────────────────┘
```

#### 6.8.2 회사 등록/관리 화면

**새 회사 등록 폼 (Company Registration Form):**

```
┌──────────────────────────────────────────────────────┐
│  ➕ 새 회사 등록                                      │
├──────────────────────────────────────────────────────┤
│                                                      │
│  회사 타입 *                                         │
│  (○) 개발사 (DEVELOPER)                              │
│  ( ) 고객사 (CLIENT)                                 │
│                                                      │
│  ┌────────────────────────────────────────────────┐  │
│  │ 회사명 *                                       │  │
│  │ [____________________________]                  │  │
│  └────────────────────────────────────────────────┘  │
│                                                      │
│  법인 대표자명 *                                     │
│  [____________________________]                      │
│                                                      │
│  사업자 등록번호 *                                   │
│  [___-__-_____]                                     │
│                                                      │
│  연락처 전화번호                                      │
│  [___-____-____]                                    │
│                                                      │
│  사업장 주소                                          │
│  [____________________________]                      │
│  [____________________________]                      │
│                                                      │
│  등록 전자우편 *                                     │
│  [____________________________@_______]              │
│                                                      │
│  ┌────────────────────────────────────────────────┐  │
│  │ [취소]                    [+ 회사 등록]         │  │
│  └────────────────────────────────────────────────┘  │
└──────────────────────────────────────────────────────┘
```

- ADMIN이 모든 회사 등록 가능 (DEVELOPER + CLIENT 모두)
- SUPPORT가 CLIENT 등록 시, 자신의 회사(company_id)와 같은 타입으로만 제한
- 개발사 선택 시 DEVELOPER 타입만 드롭다운에 표시
- 고객사 선택 시 CLIENT 타입만 드롭다운에 표시

**회사 목록 및 관리 테이블:**

| 회사명 | 타입 | 대표자 | 사업자번호 | 사용자 수 | 상태 | Action |
|-------|------|-------|----------|----------|------|--------|
| (주)엔유비즈 | DEVELOPER | 홍길동 | 123-45-67890 | 8 | ACTIVE | [Edit] [Disable] |
| ABC 물류 | CLIENT | 김갑식 | 987-65-43210 | 5 | ACTIVE | [Edit] [Disable] |
| XYZ 테크 | CLIENT | 이영희 | 111-22-33333 | 3 | INACTIVE | [Edit] [Enable] |

#### 6.8.3 프로젝트 등록/관리 화면

**새 프로젝트 등록 폼 (Project Registration Form):**

```
┌──────────────────────────────────────────────────────┐
│  ➕ 새 프로젝트 등록                                   │
├──────────────────────────────────────────────────────┤
│                                                      │
│  프로젝트명 *                                        │
│  [____________________________]                      │
│                                                      │
│  계약일 *                                            │
│  [__/__/____]                                       │
│                                                      │
│  착수일 *                                            │
│  [__/__/____]                                       │
│                                                      │
│  종료일 *                                            │
│  [__/__/____]                                       │
│                                                      │
│  고객사 선택 *                                       │
│  ▼ [ABC 물류 선택]                                  │
│  (company_type=CLIENT인 회사만 표시)                  │
│                                                      │
│  배정된 Support 담당자 *                             │
│  ▼ [김지원 선택]                                    │
│  (company_type=DEVELOPER AND role=SUPPORT)           │
│                                                      │
│  프로젝트 상태                                       │
│  (○) Active ( ) On Hold ( ) Completed                │
│                                                      │
│  ┌────────────────────────────────────────────────┐  │
│  │ [취소]                    [+ 프로젝트 등록]      │  │
│  └────────────────────────────────────────────────┘  │
└──────────────────────────────────────────────────────┘
```

**프로젝트 정보 관리 카드 추가/수정 폼:**

```
┌──────────────────────────────────────────────────────┐
│  📋 프로젝트 정보 카드 관리 - [POSM Web App]          │
├──────────────────────────────┬───────────────────────┤
│  왼쪽 메뉴                    │  오른쪽 편집 영역      │
├──────────────────────────────┤                      │
│  [➕ 새 카드 추가]            │                      │
│                          ─────────────────         │
│  (○) 🔧 서버 정보           │  카드 제목:            │
│  ( ) 💻 SW 정보             │  [AWS Production Server]│
│  ( ) 🔑 로그인 계정          │                      │
│  ( ) ⚙️ 운영 계정           │  ──────────────────  │
│  ( ) 📝 참고사항             │  서버명:              │
│                          │  [POSM-PROD-01]          │
│  카드 목록:                │                      │
│  • 서버 정보 (3개)         │  서버IP:               │
│  • SW 정보 (2개)           │  [10.0.1.50]           │
│  • 로그인 계정 (4개)       │                      │
│  • 운영 계정 (2개)         │  서버타입:              │
│  • 참고사항 (1개)          │  [Web ▾]              │
│                          │                      │
│                          │  OS:                   │
│                          │  [Ubuntu 22.04 LTS ▾]  │
│                          │                      │
│                          │  포트:                 │
│                          │  [443]                 │
│                          │                      │
│                          │  프로토콜:             │
│                          │  [HTTPS ▾]             │
│                          │                      │
│                          │  ──────────────────  │
│                          │  접속 계정:            │
│                          │  [admin]               │
│                          │                      │
│                          │  임시 비밀번호:        │
│                          │  [••••••••••] 🔒       │
│                          │                      │
│                          │  ──────────────────  │
│                          │  [취소] [+ 저장]    │
└──────────────────────────────┴───────────────────────┘
```

#### 6.8.4 프로젝트 배정 화면

```
┌──────────────────────────────────────────────────────┐
│  👥 프로젝트 배정 관리 - [POS M Web App]              │
├──────────────────────────────────────────────────────┤
│                                                      │
│  ─ SUPPORT 배정 (개발팀) ─                           │
│  ┌───────┬───────────┬──────────┬────────┬────────┐ │
│  │이름   │회사       │배정일    │상태    │Action  │ │
│  ├───────┼───────────┼──────────┼────────┼────────┤ │
│  │김지원 │(주)엔유비즈│2025/01/15│Active  │[해지]  │ │
│  │박개발│(주)엔유비즈│2025/02/01│Active  │[해지]  │ │
│  └───────┴───────────┴──────────┴────────┴────────┘ │
│  [+ SUPPORT 추가 ▼]                                  │
│                                                      │
│  ─ CLIENT 배정 (고객사) ─                            │
│  ┌───────┬───────────┬──────────┬────────┬────────┐ │
│  │이름   │회사       │배정일    │상태    │Action  │ │
│  ├───────┼───────────┼──────────┼────────┼────────┤ │
│  │김갑식 │ABC 물류   │2025/03/10│Active  │[해지]  │ │
│  │이영희 │ABC 물류   │2025/03/10│Active  │[해지]  │ │
│  └───────┴───────────┴──────────┴────────┴────────┘ │
│  [+ CLIENT 추가 ▼]                                   │
│                                                      │
│  배정 기록:                                          │
│  [•] 2025/01/15 김지원 배정 (Admin: 홍길동)          │
│  [•] 2025/02/01 박개발 배정 (Admin: 홍길동)          │
│  [•] 2025/03/10 김갑식 배정 (Admin: 홍길동)          │
└──────────────────────────────────────────────────────┘
```

- ADMIN은 전체 프로젝트의 배정/해지 가능
- SUPPORT는 소속 DEVELOPER의 프로젝트에 CLIENT 배정 가능 (SUPPORT SELF 배정/해지 불가)

---

### 6.9 모바일 반응형 및 모바일 전용 UI/UX 명세

본 섹션에서는 nu_Trust 시스템의 데스크탑(데스크탑) 환경뿐만 아니라 모바일(스마트폰/태블릿) 환경을 위한 반응형 디자인과 모바일 전용 기능 요구사항을 정의한다.

#### 6.9.1 반응형 디자인 원칙 (Responsive Design Principles)

| Breakpoint | 화면 너비 | 대상 기기 | 레이아웃 |
|------------|----------|----------|---------|
| **xs** | 320px–479px | 소형 스마트폰 (iPhone SE 등) | Single-column, Full-width, Touch-optimized |
| **sm** | 480px–639px | 대형 스마트폰 (iPhone Pro Max 등) | Single-column, Touch-optimized |
| **md** | 640px–1023px | 태블릿 (iPad 등) | Single/Two-column, Touch + Pen support |
| **lg** | 1024px–1279px | 노트북 / 데스크탑 | Three-column layout, Mouse + Keyboard |
| **xl** | 1280px–1535px | 대형 데스크탑 | Three-column layout (max-width 1440px) |

**핵심 원칙:**
- **Mobile-First:** 모든 Component, StyleSheet, Layout은 Mobile-xs → sm → md → lg → xl 순으로 Progressive enhancement 방식으로 설계한다.
- **Touch Target Size:** 모든 Interactive Element(Button, Link, Input, Checkbox, Radio)의 최소 터치 타겟 크기는 44×44px (WCAG 2.1 기준) 이상이어야 한다.
- **Gesture Support:** Swipe-left/right로 Ticket 상태 Quick 전환, Swipe-down로 Pull-to-refresh, Long-press로 Context Menu (수정/삭제/복사 메뉴) 지원.
- **Keyboard Navigation:** Focus-ring, Tab-order, Enter/Space 동작은 Desktop과 동일하게 보장 (WCAG 2.1 AA Level).
- **Typography Scale:** Mobile에서는 Base font 14px, Desktop에서 16px. Headline은 Mobile에서 `20px` Desktop `24px` 이상 적용. Line-height는 1.5 이상 유지.
- **Image Optimization:** WebP/SVG 우선. Thumbnail Lazy-loading, Carousel infinite-scroll 지원.

#### 6.9.2 모바일 전용 UI 구조 (Mobile-Only UI Layout)

**Mobile 전용 Bottom Navigation (하단 네비게이션 탭):**

Mobile(xs, sm) 화면에서 최상단 Header Bar 대신 하단에 고정된 4개 메인 탭을 사용한다.

```
┌──────────────────────────────┐
│ [Logo]  nu_Trust     (☰)    │  ← Fixed Top Header (Mini)
├──────────────────────────────┤
│                              │
│                              │
│  ─ Ticket List ─             │  ← Main Content Area
│  ┌────────────────────────┐  │
│  │📌Ticket#143...         │  │
│  │ ABC물류 · Status: RECEIVED│ │
│  └────────────────────────┘  │
│  ┌────────────────────────┐  │
│  │📌Ticket#142...          │  │
│  │ XYZ테크 · Status: IN-PROGRESS│
│  └────────────────────────┘  │
│                              │
│                              │
├──────────────────────────────┤  ← Fixed Bottom Navigation
│  🏠Home  📋Tickets  ➕Create  👤Profile│
└──────────────────────────────┘
```

**Bottom Navigation 각 탭별 상세:**

| Tab | Icon | 기능 | Badge |
|-----|------|------|-------|
| **Home (홈)** | 🏠 | Role별 대시보드 요약 (오늘 마감 티켓, 대기 중 알림, SLA Alert) | 미처리 Alert 수 |
| **Tickets (티켓)** | 📋 | 전체 Ticket list (Filter, Sort, Search). Swipe-left로 빠른 상태 변경 | 미처리 Ticket 수 |
| **Create (생성)** | ➕ | 새 Ticket 등록, 파일 첨부, 프로젝트 선택 (Customer용) | Badge 없음 |
| **Profile (프로필)** | 👤 | 프로필 조회, Notification 설정, Logout, Company 선택 | 미처리 Notification 수 |

**Tablet(md)** 화면에서는 Bottom Navigation을 Side Navigation 바(왼쪽)로 전환하고, Content를 2-column으로 배치한다.

```
┌────────────────────────────────────────────────────┐
│ [Logo]  nu_Trust  · Admin ▼  │ (🔔 Badge:3) │
├────────┬───────────────────────────────────────────┤
│ 🏠Home │                                            │
│ 📋Tickets│  ─ Ticket Detail ─                     │
│ ➕Create│  ┌──────────────────────────────────┐    │
│ 👤Profile│  │ Ticket #143 · RECEIVED          │    │
│          │  │ Title: POS M 서버 장애          │    │
│          │  │ Description: ...                │    │
│          │  │ Status Ribbon: [RECV] → [IN-P]  │    │
│          │  │ Timeline, Files, Comments       │    │
│          │  │ [Reply] [Action Button]         │    │
│          │  └──────────────────────────────────┘    │
│          │                                            │
└────────┴───────────────────────────────────────────┘
```

#### 6.9.3 모바일 전용 기능 요구사항 (Mobile-Only Feature Requirements)

**1. Push Notification (푸시알림):**
- Device 토큰 저장 `notification_preferences` 테이블의 `push_enabled` 컬럼과 연동
- FCM (Firebase Cloud Messaging) 기반 Push 전송
- 이벤트 발생 시 In-App Web Notification + Push 알림 2단계로 전송 (사용자 설정에 따라 선택적)
- Device별 `DeviceToken` 매핑 테이블 (`device_tokens`): userId, deviceType, token, createdAt

**2. 모바일용 Touch-optimized Ticket 입력 폼:**
- Date Picker는 모바일 네이티브 캘린더 UI (iOS UIPicker / Android DatePickerDialog)
- Text Input은 Mobile Keyboard Auto-Adjust (Soft Keyboard가 UI를 가리지 않도록 Scrollable Container)
- File 첨부: Mobile Gallery/Camera 직접 접근 (Mobile Web → `<input type="file" accept="image/*" capture="environment">`)
- Signature Pad: Ticket 승인 시 Mobile 터치 서명 기능 (Touch 기반 Canvas drawing, PNG 저장)

**3. 모바일용 Chat/Comment:**
- 자동 스크롤 (새 메시지 도착 시 하단으로 자동 스크롤)
- Typing indicator (상대방이 입력 중일 때 "···" 표시)
- Image inline preview (Tap full screen view with pinch-zoom)
- Quick Reply templates (Frequently used responses as chips/Pills)

**4. Offline-capable Feature (부분적 Offline 지원):**
- LocalStorage/IndexedDB에 Ticket List Cache (.network 연결 복귀 시 자동 Sync)
- Offline시 "Network Status Indicator" 표시 (상단바)

#### 6.9.4 모바일 UX 체크리스트 (Accessibility)

| Item | Guideline | Mobile Target |
|------|-----------|--------------|
| Text Contrast | WCAG AA | 4.5:1 이상 |
| Touch Target | WCAG 2.1 | 44×44px 이상 |
| Font Size | Body | 14px 이상 |
| Page Load | Lighthouse | 3초 이내 (3G 기준) |
| Navigation Depth | 3-click Rule | 어떤 Screen도 3클릭 이내 도달 |
| Error Message | Actionable | "에러 발생" → "재시도 버튼 제공" 이상 |

---

### 6.10 이벤트 기반 웹 알림 (Web Notification / Push) 명세

nu_Trust 시스템에서 주요 Ticket 이벤트가 발생했을 때, 해당 Ticket에 관련된 모든 사용자에게 실시간으로 알림을 제공한다. 알림은 In-App Web Notification (Popup, SideBar, Badge 카운트)와 Push Push Notification 두 가지 경로를 따른다.

#### 6.10.1 알림 이벤트 정의 (Notification Events)

| Event ID | 이벤트명 | Trigger | Target | Description |
|----------|---------|---------|--------|-------------|
| **EVT-01** | Ticket 등록 | `POST /api/v1/tickets` | Assigned Users, Ticket Creator's Admin, Project All Admins | 새 Ticket 생성 시 관련 배정된 사용자 (Assignees, Reporter, 해당 Project의 ADMIN) 알림 |
| **EVT-02** | Ticket 접수 확정 | `Ticket → RECEIVED` 상태 전이 | Ticket Creator | Ticket이 Support에 의해 접수 확인됨 |
| **EVT-03** | 상태 변경 | `Ticket Status Transition` | Ticket Creator, All Users who watched Ticket | Ticket 상태가 변경됨 (예: RECEIVED → IN_PROGRESS, IN_PROGRESS → PLAN_READY 등) |
| **EVT-04** | 연기고/완료요청 | `POST /api/v1/tickets/:id/extension`, `POST /api/v1/tickets/:id/completion` | Assigned Support, ADMIN | Ticket Creator가 연기고/완료요청을 보냈을 때 |
| **EVT-05** | 완료통지 | `Ticket → COMPLETED` 상태 전이 (Admin/SUPPORT가 완료 처리) | Ticket Creator, CLIENT Members of the Project | Ticket이 완료 처리됨. 처리결과 Summary 포함 |
| **EVT-06** | 배정안내 | `POST /api/v1/projects/:id/assignments` | New Assignees (SUPPORT_NEW / CLIENT_NEW) | 새 Support/-client 배정 시 해당 User에게 배정 통보 |
| **EVT-07** | 댓글/답글 알림 | `POST /api/v1/tickets/:id/comments`, `POST /api/v1/tickets/:id/replies` | All Users except the Commenter (who already wrote the comment) | Ticket에 다른 사용자의 Comment/Reply 달렸을 때 |
| **EVT-08** | 만기 전일 알림 (SLA 경고) | `CRON: D-1 / D-0` | Assigned Support, Support Manager | Ticket SLA 만기 하루 전, 당일 알림 (Urgency 기준) |
| **EVT-09** | Project 만기 전일 알림 | `CRON: ProjectEndDate - 7d, - 3d, - 1d` | ADMIN, SUPPORT Manager, All Support assigned to Project | 프로젝트 종료일 D-7, D-3, D-1 알림 |
| **EVT-10** | Card 정보 변경 | `PUT/PATCH /api/v1/cards/:id` | USERS who have access to the Project | 프로젝트 카드(서버/SW/계정) 정보 변경 시 해당 Project Admin/SUPPORT |

#### 6.10.2 알림 전송 방식 및 우선순위

| Priority | Events | Delivery 방식 | Sound? | Badge? | In-App | Push |
|----------|--------|-------------|--------|--------|--------|------|
| **P0 (Urgent)** | EVT-04, EVT-05, EVT-07 | WebSocket Real-time + Immediate Push | ✅ Yes | ✅ Yes | Popup ✅ | ✅ |
| **P1 (High)** | EVT-01, EVT-02, EVT-03, EVT-06, EVT-09 | WebSocket + Push | ⚠️ Option | ⚠️ Option | In-Badge ✅ | ✅ |
| **P2 (Medium)** | EVT-08 | WebSocket + Scheduled Push | ❌ No | ❌ No | Badge ✅ | ⚠️ Option |
| **P3 (Low)** | EVT-10, EVT-09 | Background Queue | ❌ No | ❌ No | In-Badge (Summary) ✅ | ❌ |

**실시간 알림 전송 흐름 (Real-time Flow):**

```
┌──────────────┐     ┌──────────────────┐     ┌──────────────────┐
│  Business    │     │  RabbitMQ Queue  │     │   WebSocket      │
│  Logic Layer │────▶│  (notification_  │────▶│  Broadcast Hub   │
│  (Spring     │     │    event_queue)  │     │  (STOMP over     │
│   Service)   │     │                  │     │   SockJS)         │
└──────────────┘     └──────────────────┘     └────────┬─────────┘
                                                       │
                                       ┌───────────────┤
                                       │               │
                              ┌────────┴──────┐  ┌────┴─────────┐
                              │  In-App UI    │  │  FCM Push    │
                              │  (Popup/Bar)  │  │  (Mobile/    │
                              │               │  │   Browser)   │
                              └───────────────┘  └──────────────┘
```

**WebSocket Subscribe Channel (STOMP):**

```
Client → SUBSCRIBE /queue/notification.{userId}
        ↓
Server → SEND    /topic/notification.{userId}
         {
           eventId: "EVT-03",
           title: "Ticket #143 상태 변경",
           body: "Ticket #143이 IN_PROGRESS 상태로 변경되었습니다.",
           ticketId: 143,
           priority: "P1",
           timestamp: "2025-05-30T10:30:00Z",
           url: "/projects/posm/tickets/143"
         }
```

#### 6.10.3 In-App 알림 UI (Popup, SideBar, Badge 카운트)

**1. Notification Popup (우선순위 P0 이벤트 발생 시 즉시 Popup):**

```
┌──────────────────────────────────────────────────────┐
│  🔔 Ticket #143 상태 변경                           │
├──────────────────────────────────────────────────────┤
│  Ticket #143이 "IN_PROGRESS" 상태로 변경되었습니다.  │
│                                                      │
│  [프로젝트: POS M Web App]                          │
│  [상태: RECEIVED → IN_PROGRESS]                     │
│                                                      │
│  ┌──────────────────────┐  ┌──────────────────────┐  │
│  │   [-ticket 보기]     │  │    [닫기]            │  │
│  └──────────────────────┘  └──────────────────────┘  │
└──────────────────────────────────────────────────────┘
```

Popup은 Auto-dismiss 10초. 사용자가 닫지 않으면 5초 후 2회 Reminder (30초 간격).
Close할 때 User action log 기록 (`notification_preferences`의 `popup_dismissed` 카운트).

**2. Notification SideBar (사이드바 알림 패널):**

오른쪽 상단 🔔 아이콘을 클릭하거나 Hover하면 SideBar가 슬라이드 형태로 열린다.

```
┌──────────────────────────────────────────┐
│  🔔 알림      (3개 미확인)     ✕ [Close] │
├──────────────────────────────────────────┤
│                                          │
│  ─ 오늘 ─                               │
│  ┌────────────────────────────────────┐  │
│  │ 🔴 Ticket #143 상태 변경            │  │
│  │ IN_PROGRESS가 되었다.     2분 전    │  │
│  └────────────────────────────────────┘  │
│  ┌────────────────────────────────────┐  │
│  │ 🟡 Ticket #142 연기고요청           │  │
│  │ 완료요청이 들어왔다.        17분 전  │  │
│  └────────────────────────────────────┘  │
│  ┌────────────────────────────────────┐  │
│  │ 🔵 Ticket #141 댓글 달렸음          │  │
│  │ Kim: "확인했습니다."           1시간 전│ │
│  └────────────────────────────────────┘  │
│                                          │
│  ─ 어제 ─                               │
│  ┌────────────────────────────────────┐  │
│  │ ⚪ Ticket #140 완료통지              │  │
│  │ 해결되었습니다.                 2일 전│ │
│  └────────────────────────────────────┘  │
│                                          │
│  ┌────────────────────────────────────┐  │
│  │   [전부 읽음 처리]       [더 보기]   │  │
│  └────────────────────────────────────┘  │
└──────────────────────────────────────────┘
```

- 최신순 정렬 (최신 20건 먼저 표시, "더 보기"로 Pagination)
- Click하면 해당 Ticket Detail 페이지로 이동 (`url` field 연동)
- Swipe-left (Mobile)로 "Read" 마킹

**3. Badge 카운트 (전역 배지):**

모든 Bottom Navigation 탭, Header 🔔 아이콘 오른쪽에 Red Badge 원통이 표시된다.

```
  🔔 Notification Icon:    🔴 3  ← Badge (미확인 알림 3건)
  🏠 Home Tab:             🔴 2  ← Home Tab에 미처리 SLA Alert 2건
  📋 Tickets Tab:          🔴 5  ← 미처리 Ticket 5건
  👤 Profile Tab:          🔴 1  ← Profile Tab 설정 알림 1건
```

- Badge 카운트: WebSocket 실시간 업데이트 (`client-side state: unreadCount`)
- Badge 숫자 99 이상시: `99+` 표시 (Over-counting)
- Badge Color: P0 이벤트 → 🔴 Red, P1 → 🟡 Yellow, P2+ → 🔵 Blue

**Badge 카운트 API:**

```
GET /api/v1/notifications/badge-count
Response:
{
  "total": 13,
  "byType": {
    "EVT-01": 5,
    "EVT-03": 4,
    "EVT-04": 2,
    "EVT-08": 1,
    "EVT-09": 1
  },
  "byPriority": {
    "P0": 2,
    "P1": 6,
    "P2": 3,
    "P3": 2
  }
}
```

#### 6.10.4 알림 설정 (Notification Preferences)

각 User별로 알림 채널별 설정을 정의할 수 있다:

```sql
CREATE TABLE notification_preferences (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id         BIGINT NOT NULL,
    in_app_enabled  BOOLEAN DEFAULT TRUE,
    push_enabled    BOOLEAN DEFAULT FALSE,
    email_enabled   BOOLEAN DEFAULT TRUE,
    sound_enabled   BOOLEAN DEFAULT TRUE,
    badge_enabled   BOOLEAN DEFAULT TRUE,
    quiet_hours_start  TIME DEFAULT '22:00:00',
    quiet_hours_end    TIME DEFAULT '08:00:00',
    quiet_hours_mode   ENUM('SILENCE', 'QUEUE') DEFAULT 'QUIET',
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_prefs (user_id)
);
```

**이벤트별 상세 설정:**

`notification_event_subscriptions` 테이블에서 User별 EVT-01~EVT-10까지 개별 Enable/Disable와 Channel Setting을 정의.

| Channel | 이벤트 | Setting 예 |
|---------|--------|----------|
| In-App Popup | EVT-04, EVT-05 | always |
| In-App Sidebar | EVT-01~EVT-03, EVT-06~EVT-10 | always |
| Push | EVT-01~EVT-06, EVT-08 | Quiet hours 제외 항상 |
| Email | EVT-05, EVT-09 | Daily digest (18시) |

**Quiet Hours (수면 모드):**

사용자 설정 시간에 P0-P1 알림은 전송되지 않고 Queue에 쌓여 Quiet Hours 종료 후 일괄 전송. Quiet hours를 "SILENCE" 모드로 설정하면 완전히 차단 (P2+만 알림).

#### 6.10.5 알림 로그 및 추이 추적 (Notification Audit Trail)

모든 알림은 `notification_logs` 테이블에 기록된다:

```sql
CREATE TABLE notification_logs (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    event_id        VARCHAR(20) NOT NULL COMMENT 'EVT-01 ~ EVT-10',
    target_user_id  BIGINT NOT NULL,
    ticket_id       BIGINT NULL COMMENT 'Related Ticket',
    project_id      BIGINT NULL COMMENT 'Related Project',
    payload         JSON NULL COMMENT 'Full notification data',
    sent_via        ENUM('WEBSOCKET', 'PUSH', 'EMAIL', 'SMS') NULL,
    status          ENUM('QUEUED', 'SENT', 'FAILED', 'DELIVERED', 'READ', 'DISMISSED') DEFAULT 'QUEUED',
    read_at         TIMESTAMP NULL,
    dismissed_at    TIMESTAMP NULL,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

#### 6.10.6 모바일 전용 알림 UX

**1. Mobile Push Notification 구조 (FCM Push Payload):**

```json
{
  "notification": {
    "title": "Ticket #143 상태 변경",
    "body": "IN_PROGRESS로 변경되었습니다.",
    "badge": "14",
    "sound": "default"
  },
  "data": {
    "eventId": "EVT-03",
    "ticketId": "143",
    "projectId": "42",
    "priority": "P1",
    "actionUrl": "/projects/posm/tickets/143",
    "action": "open_ticket_detail"
  },
  "priority": "high"
}
```

**2. Mobile (iOS/Android) 네이티브 Push UI:**

```
┌──────────────────────┐
│  (🔔) nu_Trust       │
│  Ticket #143 상태    │
│  변경               │
│  ────────────────    │
│  Ticket #143이       │
│  IN_PROGRESS로       │
│  변경되었습니다.     │
│                      │
│  [열기]      [닫기]  │  ← Swipe to Dismiss
└──────────────────────┘
```

**3. Mobile Bottom Tab Badge 연동:**

```
  🏠   📋   ➕   👤
  🔴2  🔴5       🔴1
```

**4. Mobile In-App Notification Screen (Profile Tab 내 알림 설정):**

```
┌──────────────────────────────┐
│  🔔 알림 설정        [✎Edit] │
├──────────────────────────────┤
│                              │
│  알림 채널 설정              │
│  ──────────────────────     │
│  In-App Popup       (ON) ◉  │
│  Push Notification  (OFF) ○  │
│  Email              (ON) ◉  │
│  Sound              (ON) ◉  │
│  Badge              (ON) ◉  │
│                              │
│  Quiet Hours               │
│  ──────────────────────     │
│  ON/OFF ◉ 22:00 ~ 08:00    │
│                              │
│  이벤트별 설정               │
│  ──────────────────────     │
│  Ticket 등록(EVT-01)  ◉ ON  │
│  상태 변경(EVT-03)    ◉ ON  │
│  완료(EVT-05)         ◉ ON  │
│  SLA 경고(EVT-08)     ◉ ON  │
│  (추가 이벤트 목록 ...)     │
│                              │
│  ┌────────────────────────┐  │
│  │[적용]          [취소] │  │
│  └────────────────────────┘  │
└──────────────────────────────┘
```

#### 6.10.7 알림 배치 정책 (Notification Batching Policy)

대량의 동시 이벤트 발생 시 Notification Spam을 방지하기 위해_BATCHING_Policy를 적용한다.

| 정책 항목 | 규칙 | 상세 |
|----------|------|------|
| 동일 티켓 batching | 5분 | 동일 Ticket에서 발생한 알림은 5분 간격으로 묶어 1건 발송 |
| 일일 최대 limit | 50건/일 | 동일 사용자에게 일일 최대 50건 알림 발송 (Quiet Hours 제외) |
| Quiet Hours | 22:00~08:00 | P0 이벤트만 Quiet Hours 우회 발송 (실시간 Alert 용도) |
| Priority별 발송 정책 | P0 실시간, P1 배치, P2+ 일일 요약 | P0: 즉시 전송, P1: 배치(5분), P2+: 일일 1회 요약(09:00) |

```
Batching 로직 (Spring @Scheduled):

// 5분마다 batching 실행
@Scheduled(cron = "0 */5 * * * *")
public void processPendingNotifications() {
    List<PendingNotification> pending = notificationRepo.findByQueuedAtBefore(
        Instant.now().minusSeconds(300));
    
    // ticketId 별 grouping
    Map<Long, List<Notification>> byTicket = pending.stream()
        .collect(Collectors.groupingBy(Notification::getTicketId));
    
    for (Map.Entry<Long, List<Notification>> entry : byTicket.entrySet()) {
        if (entry.getValue().size() > 1) {
            // Batching: 요약 메시지 생성
            String summary = createBatchSummary(entry.getKey(), entry.getValue());
            sendConsolidatedNotification(summary);
        } else {
            // Single: 즉시 발송
            sendImmediate(entry.getValue().get(0));
        }
    }
    
    // 일일 50건 Cap 초과 분 queue延期 (다음 배치에서 재처리)
}
```

---

### 6.11 디자인 시스템 명세 (Design System Specifications)

본 섹션에서는 nu_Trust UI의 디자인 시스템을 구성하는 핵심 토큰, 컴포넌트, 상태, 모션 스펙을 정의한다. 모든 UI Component는 이 Design System을 통해 일관된 Visual Identity와 Behavior를 가져야 한다.

#### 6.11.1 Color Palette (칼러 팔레트)

Light Mode와 Dark Mode를 양방향으로 매핑하는 Token-Base Color System을 사용한다. 모든 색상은 Design Token (예: `primary.500`)으로 참조하며, 직접 Hex Code를 Hard-code하지 않는다.

**Light Mode / Dark Mode Token 매핑 테이블:**

| Token | Light Mode | Dark Mode | Usage |
|-------|-----------|-----------|-------|
| `primary.500` | `#3B82F6` | `#60A5FA` | Main actions, links, primary CTA |
| `primary.600` | `#2563EB` | `#3B82F6` | Hover states |
| `primary.700` | `#1D4ED8` | `#2563EB` | Pressed/Active states |
| `secondary.500` | `#8B5CF6` | `#A78BFA` | Secondary actions, accent |
| `secondary.600` | `#7C3AED` | `#8B5CF6` | Hover states |
| `success.500` | `#10B981` | `#34D399` | Success, completed status |
| `success.600` | `#059669` | `#10B981` | Hover states |
| `warning.500` | `#F59E0B` | `#FBBF24` | Warning, delayed status |
| `warning.600` | `#D97706` | `#F59E0B` | Hover states |
| `error.500` | `#EF4444` | `#F87171` | Error, cancelled status |
| `error.600` | `#DC2626` | `#EF4444` | Hover states |
| `neutral.900` | `#111827` | `#F9FAFB` | Primary text |
| `neutral.700` | `#374151` | `#E5E7EB` | Body text (dark) / (light) |
| `neutral.500` | `#6B7280` | `#9CA3AF` | Secondary text, helper text |
| `neutral.400` | `#9CA3AF` | `#6B7280` | Placeholder text, icons |
| `neutral.100` | `#F3F4F6` | `#374151` | Background surfaces, borders |
| `neutral.50` | `#F9FAFB` | `#1F2937` | Page background |

**Semantic Color Map (상태별 색상):**

| 상태 | Light Mode Token | Dark Mode Token |
|------|-----------------|-----------------|
| Active / Default | `neutral.900` / `neutral.50` | `neutral.900` / `neutral.50` (inverted) |
| Focus | `primary.500` (ring +4px) | `primary.500` (ring +4px) |
| Disabled | `neutral.400` | `neutral.600` |

**접근성 보장:** 모든 Text/Background 조합은 WCAG AA Level 기준 Contrast Ratio **4.5:1** 이상을 만족해야 한다.

#### 6.11.2 Typography System (타이포그래피 시스템)

**Font Family:**
- Primary: `'Pretendard'` (Korean optimized — Pretendard Project)
- Fallback Stack (순서 중요): `'Apple SD Gothic Neo'`, `'Noto Sans KR'`, `'Malgun Gothic'`, sans-serif
- Code Font: `'SF Mono'`, `'Fira Code'`, monospace
- Loading Strategy: `<link rel="preload">` + `font-display: swap`

**Type Scale (Size / Line-Height):**

| Scale | px | Line-Height | Usage |
|-------|-----|-------------|-------|
| `xs` | 11px | 1.4 | Captions, badges, helper text |
| `sm` | 12px | 1.4 | Table cell, link text |
| `base` | 14px | 1.5 | Body text, input text |
| `md` | 16px | 1.5 | Body text (larger), labels |
| `lg` | 18px | 1.6 | Section headline, card title |
| `xl` | 20px | 1.6 | Page headline |
| `2xl` | 24px | 1.6 | Page title |
| `3xl` | 30px | 1.4 | Hero headline |

**Font Weights:**

| Weight | Numeric | Usage |
|--------|---------|-------|
| Regular | `400` | Body text, paragraphs |
| Medium | `500` | Labels, emphasis |
| Semibold | `600` | Cards, sub-headlines |
| Bold | `700` | Headlines, CTAs |

**Korean i18n Font Handling:**
- `<html lang="ko">` — 페이지 루트 언어 속성
- `<span lang="en">` — English text span (영문 데이터 표시 시)
- CSS `hyphens: none` — Korean text에서 hyphenation 비활성화

#### 6.11.3 Spacing System (스페이스 시스템)

8px Grid System 기반. 모든 마진, 패딩, gaps는 4px 배수여야 한다.

| Token | px | Usage |
|-------|-----|-------|
| `space.1` | 4px | Tight padding (icons, badges) |
| `space.2` | 8px | Button internal padding, gap between inline items |
| `space.3` | 12px | Card padding (internal) |
| `space.4` | 16px | Card padding (external), section gap |
| `space.6` | 24px | Section gap, form section spacing |
| `space.8` | 32px | Page layout gap |
| `space.12` | 48px | Layout section gap |
| `space.16` | 64px | Major section margin |
| `space.20` | 80px | Hero section gap |
| `space.24` | 96px | Page-level outer padding |

**CSS Variables:**
```css
:root {
  --space-1: 4px;
  --space-2: 8px;
  --space-3: 12px;
  --space-4: 16px;
  --space-6: 24px;
  --space-8: 32px;
  --space-12: 48px;
  --space-16: 64px;
  --space-20: 80px;
  --space-24: 96px;
}
```

#### 6.11.4 Iconography (아이코노그래피)

| Attribute | Specification |
|-----------|--------------|
| Library | Lucide Icons (일관된 선 스타일) — 대체: Heroicons |
| SVG 기반 | Inline SVG 또는 Icon Component Wrapping |
| Sizes | 16px, 20px, 24px (Default), 32px |
| Stroke Width | 2px (default), 1.5px (compact UI용) |
| Variants | Outline (default), Filled (selected 상태), Two-Tone |
| Color Inheritance | `currentColor` 사용 — Parent container color에 따라 자동 색상 변경 |
| Accessibility | Icon-only Element은 반드시 `aria-label` 필요 |

**Icon Component Structure:**
```tsx
// 예시: Lucide Icon Wrapper
<Icon
  name="ticket"
  size={24}
  strokeWidth={2}
  aria-hidden="true"
  className="text-neutral-500"
/>
```

#### 6.11.5 Component Library (컴포넌트 라이브러리)

**Button 컴포넌트:**

| Variant | Background | Text | Border | Hover | Usage |
|---------|-----------|------|--------|-------|-------|
| `primary` | `primary.500` | `neutral.50` | None | `primary.600` | Main CTA |
| `secondary` | `neutral.100` | `neutral.900` | `neutral.200` | `neutral.200` | Back, cancel |
| `outline` | Transparent | `neutral.900` | `neutral.300` | `neutral.100` | Secondary action |
| `ghost` | Transparent | `neutral.700` | None | `neutral.100` | Icon-only, link-like |
| `danger` | `error.500` | `neutral.50` | None | `error.600` | Delete, destructive |

**Button Sizes:**
```
sm: height 32px, padding 8px 12px, font-size 12px
md: height 40px, padding 10px 16px, font-size 14px
lg: height 48px, padding 12px 24px, font-size 16px
```

**Input 컴포넌트:**

| State | Border | Background | Text Color | Placeholder Color |
|-------|--------|-----------|-----------|------------------|
| Default | `neutral.300` | `neutral.50` | `neutral.900` | `neutral.400` |
| Error | `error.500` (3px) | `error.50` | `neutral.900` | `error.400` |
| Success | `success.500` (3px) | `success.50` | `neutral.900` | `neutral.400` |
| Disabled | `neutral.200` | `neutral.100` | `neutral.400` | `neutral.300` |
| With Icon | Right padding 40px | — | — | — |

**Modal 컴포넌트:**

| Size | Max Width | Use Case |
|------|-----------|---------|
| Default | 480px | Form inputs, confirmations, small dialogs |
| Large | 720px | Multi-field forms, data entry |
| Full-screen | 100vw × 100vh | Complex editors, full-page workflows |

**Modal Attributes:**
- Overlay: `background: rgba(0,0,0,0.5)`, backdrop blur optional
- Shadow: `box-shadow: 0 20px 60px rgba(0,0,0,0.3)`
- Border Radius: 12px
- Close: Escape key, × button (top-right), overlay click (configurable)

**Table 컴포넌트:**

| Variant | Use Case | Characteristics |
|---------|---------|-----------------|
| Default | General data display | Full padding, hover row highlight |
| Compact | Dense data views | Reduced row height, smaller font |
| Striped | Long lists | Alternating row background |

**Card 컴포넌트:**

| Variant | Border | Shadow | Interactive |
|---------|--------|--------|------------|
| Default | `neutral.200` | None | No |
| Interactive | `neutral.200` | Hover 시 elevation | Yes (clickable) |
| Bordered | `neutral.300` | None | No |

**Badge 컴포넌트:**

| Property | small | medium | large |
|----------|-------|--------|-------|
| Height | 20px | 24px | 28px |
| Font Size | 11px | 12px | 13px |
| Padding | 4px 8px | 6px 10px | 6px 12px |
| Border Radius | 4px | 6px | 20px (pill) |

**Alert 컴포넌트:**

| Variant | Icon | Left Border | Background | Text |
|---------|------|------------|-----------|------|
| `info` | Info | `primary.500` (4px) | `primary.50` | `primary.700` |
| `success` | CheckCircle | `success.500` (4px) | `success.50` | `success.700` |
| `warning` | AlertTriangle | `warning.500` (4px) | `warning.50` | `warning.700` |
| `error` | AlertCircle | `error.500` (4px) | `error.50` | `error.700` |

#### 6.11.6 State Tokens (상태 토큰)

모든 Interactive Element는 다음 상태 전이를 따른다:

**Default → Hover → Active → Focus → Disabled**

| Component | Default | Hover | Active (Pressed) | Focus | Disabled |
|-----------|---------|-------|-----------------|-------|---------|
| Button (Primary) | BG `primary.500` | BG `primary.600` | BG `primary.700` | Ring `primary.500` (2px), outline-offset 2px | Opacity 0.5, cursor default |
| Button (Ghost) | BG none | BG `neutral.100` | BG `neutral.200` | Ring `neutral.900` (1px) | Opacity 0.5 |
| Input | Border `neutral.300` | Border `neutral.400` | Border `neutral.400` | Ring `primary.500` (2px) | BG `neutral.100`, Border `neutral.200` |
| Card | Shadow none | Shadow `elevated` | Shadow `pressed` | Ring 2px | Opacity 0.6 |
| Link | Text `primary.500` | Text `primary.600` + underline | Text `primary.700` | Ring 2px | Text `neutral.400` |
| Table Row | BG none | BG `neutral.50` | BG `neutral.100` | — | — |

**Focus Ring 표준:**
- Color: `primary.500`
- Width: 2px
- Offset: 2px (outline-offset)
- Border Radius: Element의 border-radius와 동일
- Exception: Native form elements는 브라우저 Default focus ring 유지 가능

#### 6.11.7 Dark Mode (다크 모드)

**전역 Dark Mode 전략:**
- Theme Provider (Context)로 전역 Theme 상태 관리
- `prefers-color-scheme` 미디어 쿼리로 초기 Theme 자동 감지
- User Setting Toggle (Header 상단)로 수동 변경 가능
- LocalStorage에 `theme: 'light' | 'dark'` 저장

**반전되지 않는 토큰 (반전 안되는 요소들):**
- `success.500`, `warning.500`, `error.500`는 상태 의미를 위해 Light/Dark 간 색상 유지
- Primary/secondary 계열은 Dark Mode에서 밝은 톤으로 변경 (가독성)
- `neutral` 톤은 Light/Dark 간반전 (Dark Mode: 텍스트는 흰색, 배경은 어두울색)

**Dark Mode Contrast Ratio 검증:**
- 모든 Text/Background 조합 4.5:1+ (AA 준수)
- Focus Ring Color: `primary.500` (#3B82F6 on #1F2937 = 5.8:1, OK)
- Icon Color: `neutral.400` (#9CA3AF on #1F2937 = 5.4:1, OK)

#### 6.11.8 Animation / Motion (애니메이션/모션)

**Easing Curve:**
```
standard: cubic-bezier(0.4, 0, 0.2, 1)  /* Material Design standard */
decelerate: cubic-bezier(0.0, 0, 0.2, 1)  /* End slowly */
accelerate: cubic-bezier(0.4, 0, 1, 1)   /* Start slowly */
spring: cubic-bezier(0.175, 0.885, 0.32, 1.275)  /* Subtle bounce */
```

**Animation Timing Matrix:**

| Motion Type | Duration | Easing | Use Case |
|-------------|----------|--------|---------|
| Page Transition | 200ms | `ease-out` | 라우트 변경, 화면 전환 |
| Modal Open/Close | 200ms | `decelerate` | 모달 표시/숨김 |
| Micro-interaction | 150ms | `ease-in-out` | Toggle, checkbox, button press |
| Status Ribbon Gradient | 300ms | `linear` | 상태 변경 시 Gradient 애니메이션 |
| Toast Appear/Disappear | 250ms | `decelerate` | 토스트 알림 등장/소멸 |
| Skeleton Shimmer | 1.5s cycle | `linear` (auto loop) | 로딩 스크레톤 효과 |
| Fade In (Content) | 150ms | `decelerate` | 데이터 로딩 완료 시 등장 |
| Slide Up (List items) | 200ms | `decelerate` | 새 항목 추가 시 애니메이션 |
| Error Shake | 300ms | `spring` | 유효성 검증 실행 시 진동 피드백 |

**Reduced Motion 지원 (접근성):**
- `@media (prefers-reduced-motion: reduce)` 쿼리 기반
- 애니메이션 80% 감소 또는 완전 비활성화
- Fade/slide → 즉시 전환
- Shimmer → 정적 상태 표시

**CSS 변수 기반 Animation 정의:**
```css
:root {
  --animation-fast: 150ms;
  --animation-normal: 200ms;
  --animation-slow: 300ms;
  --easing-standard: cubic-bezier(0.4, 0, 0.2, 1);
  --easing-decelerate: cubic-bezier(0.0, 0, 0.2, 1);
}
```

---

### 6.12 회사 및 사용자 관리 UI (Company & User Management UI)

Admin Panel의 Company 및 User Management 기능을 위한 상세 UI 명세. Role(ADMIN) 전용 페이지이며, Company와 User의 전체 생명주기를 관리한다.

#### 6.12.1 회사 목록 페이지 (Company List Page)

**레이아웃 구조:**
```
┌──────────────────────────────────────────────────────────────────┐
│  🔹 회사 관리                                    [+] 회사 등록   │
├──────────────────────────────────────────────────────────────────┤
│  🔍 회사名称 또는 사업자번호 검색                           [▼필터]│
│                                                                  │
│  [전체 42] [ACTIVE 38] [INACTIVE 4]   [선택: 0개] [↓ 선택해제]  │
│                                                                  │
│  ┌────┬─────────────┬──────────┬───────┬──────┬───────┬───────┐ │
│  │☐   │회사名称     │ 타입     │ 등록일 │ 상태 │ Admin │ 멤버수│ │
│  ├────┼─────────────┼──────────┼───────┼──────┼───────┼───────┤ │
│  │☐  │(주)엔유비즈 │DEVELOPER │24/01/15│● 활성│김지원 │  8    │ │
│  │☐  │ABC 물류    │CLIENT    │24/02/20│● 활성│박개발 │  5    │ │
│  │☐  │XYZ 테크    │CLIENT    │24/03/10│○ 비활성│이영희│  3    │ │
│  │☐  │DEF 커머스  │CLIENT    │24/04/05│● 활성│최관리│ 12    │ │
│  └────┴─────────────┴──────────┴───────┴──────┴───────┴───────┘ │
│                                                                  │
│  ──────── 1-25 of 42 ────────  [‹] 1 2 3 4 5 [›]              │
│                                                                  │
│  ┌─────────────────────┬──────────────────────────────────┐     │
│  │ 10개/페이지 ▼         │ [비활성화] [활성화] [내보내기 ▾] │     │
│  │                     ├──────────────────────────────────┤     │
│  └─────────────────────┴──────────────────────────────────┘     │
└──────────────────────────────────────────────────────────────────┘
```

**기능 상세:**
- **검색 (Search):** Fuzzy Search — 회사名称, 사업자번호, 대표자명 전역 검색. Enter 또는 자동 검색 (300ms debounce)
- **필터 (Filters):**
  - Company Type: 전체/DEVELOPER/CLIENT
  - Status: 전체/Active/Inactive
  - Registration Date Range: DatePicker range picker
- **정렬 (Sort):** 등록일(Oldest/Newest)/Company Name(A-Z/Z-A)/Member Count
- **Bulk Actions:**
  - Selection 모드에서 `활성화`/`비활성화` 버튼 활성화
  - Bulk Export: CSV/Excel로 선택된 Company 데이터 내보내기
- **Pagination:** 10/25/50/100개/페이지 선택 가능
- **UX:** Status Inline Toggle 활성/비활성 (토스트 확인), 각 행 Hover 시 Action 버튼 표시

**API 연동:**
```
GET /api/v1/companies?page=1&limit=25&type=DEVELOPER&status=ACTIVE&search=엔유비즈
Response: { data: [...], meta: { total: 42, page: 1, limit: 25 } }
```

#### 6.12.2 회사 상세 페이지 (Company Detail Page)

**Layout:**
```
┌──────────────────────────────────────────────────────────────────┐
│  ← 회사 관리 / (주)엔유비즈                               [⚙설정] │
├──────────────────────────────────────────────────────────────────┤
│  [🏠 개요] [👥 멤버] [📦 프로젝트] [💳 결제]                     │
├──────────────────────────────────────────────────────────────────┤
│                                                                  │
│  ── [활성] 탭 ─────────────────────────────────────────       │
│  ┌─────┬────────────────────────────────────────────────────┐   │
│  │     │  (주)엔유비즈                                     │   │
│  │LOGO │  DEVELOPER · Since 2024/01/15                     │   │
│  │     │  사업자번호: 123-45-67890                         │   │
│  │     │  대표: 홍길동 · 이메일: hong@enubiz.co.kr          │   │
│  │     │  연락처: 02-1234-5678                              │   │
│  └─────┴────────────────────────────────────────────────────┘   │
│                                                                  │
│  ── 사용량 요약 ─────────────────────────────────────────      │
│  ┌──────────────┐ ┌──────────────┐ ┌──────────────┐            │
│  │ 프로젝트      │ │ 멤버         │ │ 활성 티켓     │            │
│  │    12         │ │    8         │ │    3           │            │
│  └──────────────┘ └──────────────┘ └──────────────┘            │
│                                                                  │
│  ── Recent Activity ────────────────────────────────────        │
│  [Today] New ticket registered (#156)                           │
│  [Yesterday] Member added: 박개발                              │
│  [2 days ago] Project assignment updated: PROJ-003             │
│                                                                  │
└──────────────────────────────────────────────────────────────────┘
```

**Tab 상세:**

| Tab | Content |
|-----|---------|
| **개요 (Overview)** | Company 정보 요약, 로고, 구독 티어, 사용량 스탯, 최근 활동 |
| **멤버 (Members)** | 멤버 목록 테이블 (이름, 역할, 이메일,가입일, 상태), 초대 버튼, 역할 변경 |
| **프로젝트 (Projects)** | 소속 프로젝트列表 (프로젝트名称, 상태, 진행률, 시작/종료일) |
| **결제 (Billing)** | 구독 Plans, 청구서 히스토리, 결제 방법, 과금 상세 |

**UX:**
- Breadcrumb 네비게이션 (`회사 관리 > (주)엔유비즈`)
- 뒤로 가기 버튼 (←)
- 모든 편집 필드는 Hover 시 Edit 아이콘 표시
- Confirm Dialog로 상태 변경(활성/비활성)

#### 6.12.3 사용자 등록/수정 (User Registration / Editing)

**사용자 등록 플로우 (Invite Flow):**
```
Step 1: ADMIN 이메일 입력 → 초대 발송
  ┌──────────────────────────────────────┐
  │  ➕ 사용자 초대                       │
  │  이메일: [user@example.com] [초대]   │
  │  역할: ▼ SUPPORT                     │
  │  회사: ▼ (주)엔유비즈                │
  │  메시지: [선택사항 - 초대 메시지]    │
  └──────────────────────────────────────┘

Step 2: 초대한 사용자 이메일 수신 → 계정 생성 페이지
  ┌──────────────────────────────┐
  │  nu_Trust 초대 받았습니다    │
  │                              │
  │  이름: [홍길동]              │
  │  비밀번호: [••••••••] 🔒     │
  │  비밀번호 확인: [••••••••] 🔒│
  │                              │
  │  [계정 생성]                 │
  └──────────────────────────────┘

Step 3: 완료 → 로그인 유도
  ┌──────────────────────────────┐
  │  ✅ 계정이 생성되었습니다     │
  │  로그인 페이지로 이동합니다... │
  └──────────────────────────────┘
```

**사용자 수정 (Modal Dialog):**
```
┌──────────────────────────────────────────┐
│  ✎ 사용자 정보 수정                  [✕] │
├──────────────────────────────────────────┤
│                                          │
│  이름 *                                  │
│  [홍길동]                               │
│                                          │
│  이메일 *                                │
│  [hong@enubiz.co.kr]                    │
│                                          │
│  역할 *                                  │
│  ▼ SUPPORT ▾                             │
│  [ADMIN, SUPPORT, CUSTOMER]              │
│                                          │
│  회사 *                                  │
│  ▼ (주)엔유비즈 ▾                        │
│                                          │
│  상태                                    │
│  (●) Active  ( ) Inactive                │
│                                          │
│  ┌──────────────────────────┐            │
│  │   [취소]            [저장] │            │
│  └──────────────────────────┘            │
└──────────────────────────────────────────┘
```

**UX:**
- **진행률 표시:** Multi-step 등록 플로우 시 Progress Indicator (Step 1-2-3)
- **실시간 검증:** Focus out 시 필드별 유효성 체크 (필수 입력, 이메일 형식, 비밀번호 강도)
- **역할 변경 확인:** ADMIN → SUPPORT/ADMIN → ROLE 변경 시 Confirm Dialog ("역할을 변경하면 기존 권한이 해제됩니다")
- **Toast 피드백:** 저장 성공/실패 시 하단-right Toast 알림

---

### 6.13 접근성 명세 (Accessibility Specifications)

본 섹션에서는 WCAG 2.1 AA 준수 및 모든 사용자에게 접근 가능한 UI/UX 요구사항을 정의한다. Accessibility는 nu_Trust의 핵심 설계 원칙이며, 모든 Component, Layout, Interaction에 적용되어야 한다.

#### 6.13.1 WCAG 2.1 AA Compliance (WCAG 2.1 AA 준수)

**핵심 준수 사항:**

| WCAG Criteria | 요구사항 | nu_Trust 적용 |
|--------------|---------|--------------|
| **1.4.3 Contrast (Minimum)** | Normal text ≥ 4.5:1, Large text ≥ 3:1 | 모든 Text/Background 조합 검증 / Color Token 표준 준수 |
| **1.4.11 Non-text Contrast** | UI Components ≥ 3:1, Semantic info ≥ 3:1 | Icon buttons, form inputs, focus indicators, status indicators |
| **1.4.3 Color not sole indicator** | Color만으로 정보 전달 금지 | Status는 Color + Icon + Text로 이중/삼중 표시 |
| **2.1.1 Keyboard** | 모든 기능 키보드 접근 가능 | Tab/Shift+Tab/Enter/Space/Escape/Arrow 키 지원 |
| **2.4.7 Focus Visible** | Focus indicator 명확하게 표시 | 모든 Interactive Element에 visible Focus Ring |
| **2.4.1 Bypass Blocks** | Skip navigation 제공 | "Main content" 스킵 링크 (Screen Reader용) |
| **2.4.6 Headings and Labels** | Descriptive headings and labels | 모든 Section에 의미 있는 Heading / 모든 Input에 Label |
| **3.3.1 Error Identification** | Error를 식별하고 설명 제공 | Inline validation + descriptive message |
| **3.3.2 Labels or Instructions** | Labels or instructions provided | 모든 Form Field에 Label + Optional helper text |

**Color + Text/Icon 조합 예시 (Status Badges):**

| Status | Color | Icon | Text |
|--------|-------|------|------|
| Active | 🟢 green + `성공` | 확인 아이콘 | "활성" |
| Delayed | 🟡 yellow + `지연` | 경고 아이콘 | "지연" |
| Error | 🔴 red + `오류` | 경고 원 아이콘 | "오류" |
| Processing | 🔵 blue + `처리중` | 시계 아이콘 | "처리 중" |

#### 6.13.2 ARIA Labels and Roles (ARIA 레이블 및 역할)

**ARIA Label 규칙:**

| Element 유형 | Required ARIA | Example |
|-------------|--------------|---------|
| Icon-only button | `aria-label` | `<button aria-label="티켓 생성하기"> + <Plus />` |
| Status badge | `aria-label` | `<span aria-label="상태: 지연">延迟</span>` |
| Close button | `aria-label` | `<button aria-label="닫기">✕</button>` |
| Modal | `role="dialog"`, `aria-modal`, `aria-labelledby` | `<div role="dialog" aria-modal="true" aria-labelledby="modal-title">` |
| Toast notification | `aria-live="polite"` | `<div aria-live="polite" aria-atomic="true">` |
| Form validation | `aria-describedby` | `<input aria-describedby="email-error">` |
| Loading state | `aria-busy="true"` | `<div aria-busy="true">로딩 중...</div>` |
| Tab interface | `role="tablist"`, `role="tab"`, `role="tabpanel"` | Tabbed UI 구성 |
| List | `role="list"`, `role="listitem"` | Ticket list, notification list |
| Navigation | `role="navigation"`, `aria-label` | `<nav role="navigation" aria-label="Main">` |

**Focus Management:**
- Modal Open → Focus 첫 번째 Input Field 또는 Close Button
- Modal Close → Focus를 Modal trigger Button으로 복귀
- Route Change → Page heading(`<h1>`)에 Focus
- Dynamic Content → `aria-live` region으로 Screen Reader 업데이트

#### 6.13.3 Keyboard Shortcuts (키보드 단축키)

| Shortcuts | Action | Scope |
|-----------|--------|-------|
| `Escape` | Modal/Dialog 닫기 | 전역 |
| `Tab` | 다음 Interactive Element로 포커스 이동 | 전역 |
| `Shift+Tab` | 이전 Element로 포커스 뒤로 이동 | 전역 |
| `Enter` | Button/Link 활성화 | 전역 |
| `Space` | Checkbox/Radio/Toggle 토글 | 전역 |
| `Arrow Up/Down` | Ticket list 내 항목 이동 / Dropdown 내 선택 | Ticket List, Dropdown |
| `Arrow Left/Right` | Ticket Detail에서 이전/다음 Ticket 이동 | Ticket Detail |
| `Ctrl+K` | Command Palette 열기 (전역 검색) | 전역 |
| `Ctrl+N` | 새 Ticket 생성 | Ticket List 뷰에서 |
| `Ctrl+Shift+K` | Command Palette 닫기 | Command Palette |
| `/` | Command Palette 열기 (Shortcut 대체) | 전역 |
| `.` | Search 필드 포커스 | 전역 (특정 페이지) |

**Keyboard Shortcut UI 표시:**
- 모든 Keyboard Shortcuts는 Help Dialog 또는 Settings 내에서 문서화
- UI Element 상단 우단에 Shortcut Hint 표시 (예: `[⌘K] Search`)
- Mobile에서는 터치 대체 UI만 제공 (Keyboard Shortcuts 비활성화)

#### 6.13.4 Screen Reader Support (스크린 리더 지원)

| Feature | Requirement |
|---------|------------|
| Semantic HTML | `<header>`, `<main>`, `<article>`, `<section>`, `<nav>` 등 적절한 Landmark 사용 |
| Image Alt Text | 모든 `<img>`에 meaningful `alt` 속성 / 장식용 이미지는 `alt=""` |
| Dynamic Content | `aria-live` region untuktoast, notification, real-time updates |
| Form Accessibility | `<label>` 명시적 연결 (`for` attribute), required 표시 (`*` + `aria-required`) |
| Skip Links | `<a href="#main-content" class="skip-link">Main content` (visually hidden, visible on focus) |
| Heading Hierarchy | H1 → H2 → H3 order 준수. Section heading에 `aria-labelledby` |
| Data Tables | `<thead>`, `<tbody>`, `<th scope="col/row">` 구조 준수 |
| Empty States | `aria-live="polite"`으로 빈 상태 메시지 읽어주기 |

#### 6.13.5 Accessibility Testing Checklist (접근성 테스트 체크리스트)

| Test Category | Tool | Criteria |
|--------------|------|---------|
| Automated Audit | axe-core / WAVE | Zero critical/violation errors |
| Keyboard Test | Manual Tab Navigation | 모든 Interactive Element 접근 가능 |
| Screen Reader | NVDA / VoiceOver | 모든 주요 Flow Screen Reader로 읽힘 |
| Color Contrast | Contrast Checker (WebAIM) | 4.5:1+ (text), 3:1+ (UI) |
| Focus Management | Manual | Focus trap in modal, focus restoration |
| Form Labels | Lighthouse / Manual | 모든 입력 필드에 label 또는 aria-label |
| Image Alt | Manual | 모든 `<img>`에 의미 있는 alt |
| Language | Manual | `<html lang="ko">` 및 동적 언어 변경 |

---

### 6.14 빈 상태 정의 (Empty State Definitions)

데이터가 없을 때 표시되는 Empty State는 사용자에게 친절한 안내와 다음 액션을 제공합니다. Empty State는 기능적 필요 이상의 UX 요소이며, 사용자 이탈을 방지하고 시스템 탐색성을 높입니다.

#### 6.14.1 티켓 목록 빈 상태 (Ticket List Empty State)

**Trigger 조건:**
- 필터 검색 결과 0건
- 전체 목록에서 데이터 0건
- 빈 카테고리/프로젝트

**Empty State UI:**
```
┌──────────────────────────────────────────────────────┐
│                                                      │
│                                                      │
│           📭                                          │
│      (Empty Inbox Illustration)                      │
│                                                      │
│      등록된 티켓이 없습니다                           │
│                                                      │
│      Tickets가 아직 없습니다. 티켓을 생성하여          │
│      작업을 시작하세요.                               │
│                                                      │
│      ┌────────────────────────┐                      │
│      │   [🎫 티켓 생성하기]    │                      │
│      └────────────────────────┘                      │
│                                                      │
│      ↗ 조건 변경하기                                 │
│                                                      │
└──────────────────────────────────────────────────────┘
```

**조건 검색 결과 빈 상태:**
```
"조건에 해당하는 티켓이 없습니다"
"다른 조건으로 검색해보세요" → [조건 변경하기] 링크
```

#### 6.14.2 프로젝트 카드 빈 상태 (Project Cards Empty State)

**Trigger 조건:**
- 대시보드 프로젝트 영역에 데이터 0건
- 프로젝트 목록 전체 0건

**Empty State UI:**
```
┌──────────────────────────────────────────────────────┐
│                                                      │
│           🏗️                                         │
│  (Project Workspace Illustration)                    │
│                                                      │
│      아직 프로젝트가 없습니다                         │
│                                                      │
│      새 프로젝트를 만들어                           │
│      팀의 작업을 조직화하세요.                        │
│                                                      │
│      ┌────────────────────────┐                      │
│      │   📦 프로젝트 만들기   │                      │
│      └────────────────────────┘                      │
│                                                      │
└──────────────────────────────────────────────────────┘
```

#### 6.14.3 대시보드 빈 상태 (Dashboard Empty State)

**대시보드 전체 빈 상태:**
```
┌──────────────────────────────────────────────────────┐
│                                                      │
│           📊                                         │
│   (Dashboard Data Illustration)                      │
│                                                      │
│   대시보드에 표시할 데이터가 없습니다                 │
│                                                      │
│   티켓이 생성되거나 프로젝트가                       │
│   배정되면 여기에 요약 정보가 표시됩니다.              │
│                                                      │
└──────────────────────────────────────────────────────┘
```

**Role별 컨텍스트 Aware empty state:**

| Role | Message | Suggested CTA |
|------|---------|--------------|
| ADMIN | "대시보드에 표시할 데이터가 없습니다. 회사를 등록하면 시스템 관리가 시작됩니다." | [회사 등록하기] |
| SUPPORT | "배정된 티켓이 없습니다. 새로운 프로젝트에 배정되면 티켓 목록이 여기에 표시됩니다." | [프로젝트 배정 요청] |
| CUSTOMER | "등록한 티켓이 없습니다. 서버/소프트웨어 문제가 있다면 티켓을 생성하세요." | [티켓 생성하기] |

#### 6.14.4 검색 결과 빈 상태 (Search Results Empty State)

**Trigger 조건:**
- Search query 결과 0건

**Empty State UI:**

```
┌──────────────────────────────────────────────────────┐
│                                                      │
│           🔍                                         │
│   (Search Illustration)                              │
│                                                      │
│   "{query}"에 대한 검색 결과가 없습니다              │
│                                                      │
│   💡 다른 키워드로 검색해보세요                       │
│   또는                                                 │
│   전체 티켓 목록을 확인해보세요                      │
│                                                      │
│   [🔍 다른 키워드로 검색]    [📋 전체 목록 보기]     │
│                                                      │
└──────────────────────────────────────────────────────┘
```

**Suggestion Keywords (자동 제안):**
- 검색어와 유사한 기존 티켓 제목 표시 (오타 교정 제안)
- "검색어와 관련된_tickets_: #143, #139, #146"
- `search.suggestions: ["{query} 관련", "전체 목록"]`

#### 6.14.5 컴포넌트별 빈 상태

| Component | Empty Message | CTA |
|-----------|--------------|-----|
| Chat/Comment 빈 상태 | " 아직 대화 내역이 없습니다" | — |
| Notification Empty | "알림이 없습니다" | [설정 확인] |
| Profile Empty | "프로필이 완료되지 않았습니다" | [프로필 수정] |
| Card Grid Empty | "이 프로젝트에 등록된 서버/SW 정보가 없습니다" | [정보 추가] |
| Extension List Empty | "연장 기록이 없습니다" | — |

---

### 6.15 에러 및 토스트 UX (Error & Toast UX)

에러 처리, 상태 표시, 검증 피드백을 통해 시스템의 신뢰성과 사용자 경험을 보장한다.

#### 6.15.1 에러 경계 (Error Boundary)

**패턴:** React Error Boundary 패턴을 각 주요 View에 적용.

```tsx
// Error Boundary Wrapper
class TicketDetailErrorBoundary extends React.Component<Props, State> {
  state = { hasError: false, error: null }

  static getDerivedStateFromError(error) {
    return { hasError: true, error }
  }

  render() {
    if (this.state.hasError) {
      return <ErrorFallback error={this.state.error} onRetry={this.handleRetry} />
    }
    return this.props.children
  }
}

// Fallback UI
function ErrorFallback({ error, onRetry }) {
  return (
    <div className="flex items-center justify-center min-h-[400px]">
      <div className="text-center">
        {/* Illustration: broken page graphic */}
        <h2 className="text-xl font-semibold text-neutral-900 mb-2">
          문제가 발생했습니다
        </h2>
        <p className="text-sm text-neutral-500 mb-4">
          페이지를 표시하는 중 오류가 발생했습니다.
        </p>
        <button onClick={onRetry} className="btn-primary">
          다시 시도
        </button>
        <button className="ml-2 text-sm text-neutral-500 hover:text-primary-500">
          관리자에게 문의
        </button>
      </div>
    </div>
  )
}
```

**에러 경계 적용 위치:**
- Ticket Detail View 전체
- Project Detail View 전체
- Admin Panel 각 Tab Content
- Dashboard Widget 영역별

**에러 리포트:**
- `window.onerror` + `window.onunhandledrejection` 리스너로 자동 에러 캡처
- 버그트래킹 시스템(예: Sentry)에 Stack Trace + Context 자동 리포트
- 리포트 시 User Session ID, Page URL, Action Path 포함

**회복 전략:**
- 에러 발생 시 Session 데이터 유지 (로컬 스토리지/세션 스토리지)
- "다시 시도" 버튼은 직전 API 요청 재시도 (Exponential Backoff: 1s, 2s, 4s)
- 에러가 지속되면 "관리자에게 문의" 옵션 표시

#### 6.15.2 토스트 알림 명세 (Toast Notification Spec)

**Toast 기본 속성:**

| 속성 | 값 |
|------|-----|
| Position | Desktop: Bottom-right, Mobile: Top-center |
| Max Stack | 5개 (오래된 토스트가 밀려 올라감) |
| Auto-dismiss | 타입별 상이 (아래 참조) |
| Manual dismiss | X 버튼 (우측 상단) |

**Toast 타입별 스펙:**

| 타입 | 색상 | 메시지 예 | 지속시간 | Actions |
|------|------|----------|---------|---------|
| success | 🟢 Green (`success.500`) | "티켓이 성공적으로 생성되었습니다" | 4s | — |
| error | 🔴 Red (`error.500`) | "서버 오류가 발생했습니다" | 6s | "다시 시도" (configurable) |
| warning | 🟡 Yellow (`warning.500`) | "SLA가 임박했습니다 — #139" | 5s | [확인] |
| info | 🔵 Blue (`primary.500`) | "알림 설정이 저장되었습니다" | 3s | — |

**Action 포함 Toast (예: Delete):**
```
┌──────────────────────────────────────┐
│ ✅ 작업이 성공적으로 완료되었습니다  │
│                                      │
│   [↩撤销]              [✕]          │
└──────────────────────────────────────┘
```
- "undo" 액션 옵션: 삭제, 상태 변경 등 파괴적(Destructive) 작업 시
- Undo 타이머: 5초 내undo → 작업 롤백, 이후에 Toast 사라짐

**접근성:**
- `aria-live="polite"` region으로 토스트 메시지 Screen Reader에게 전달
- 토스트 자동 dismissal 전에 최소 3초는 유지 (screen reader 읽기 위한 최소 시간)
- 토스트에 `role="alert"` 또는 `role="status"`적용

#### 6.15.3 유효성 검증 피드백 (Validation Feedback)

**검증 패턴:**
```
Field 입력 → (사용자가 입력)
  ↓ (Blur 이벤트)
필드 유효성 검증 (Server-side + Client-side)
  ↓
결과 표시:
  ✅ Success: 초록색 테두리, 확인 아이콘
  ❌ Error: 빨간색 테두리, 경고 아이콘 + 설명 메시지
  ℹ️ Info: 파란색 헬퍼 텍스트
```

**필드별 검증 규칙 예시:**

| Field | Validation | Error Message | Trigger |
|-------|-----------|--------------|---------|
| Email | RFC 5322 형식 | "올바른 이메일 주소를 입력하세요" | onBlur |
| Password | Min 8자, 대소문자+숫자+특수문자 | "비밀번호는 8자 이상이어야 합니다" | onBlur + onSubmit |
|_company_name_ | 2자 이상 | "회사명은 2자 이상이어야 합니다" | onBlur |
| Phone | 숫자 11자 (000-0000-0000) | "올바른 전화번호 형식이 아닙니다" | onBlur |
| Business Number | `xxx-xx-xxxxx` 형식 | "올바른 사업자번호 형식이 아닙니다" | onBlur |
| Required field | 비어있을 수 없음 | "필수 입력 항목입니다" | onBlur |

**Validation Feedback UI:**
```
┌────────────────────────────────────┐
│ 이메일 *                           │
│ [user@example.com]  ✅             │
├────────────────────────────────────┤
│ 비밀번호 *                         │
│ [••••••••••]  🔴 비밀번호는 8자 이상 │
├────────────────────────────────────┤
│ 이름 *                            │
│ [홍길동]  ℹ️ 한글 또는 영문 50자 이내  │
└────────────────────────────────────┘
```

#### 6.15.4 404/500 페이지 (Error Pages)

**404 Not Found:**
```
┌──────────────────────────────────────────────┐
│                                              │
│          🗺️                                  │
│      (Lost Map Illustration)                 │
│                                              │
│      페이지를 찾을 수 없습니다               │
│                                              │
│      요청하신 페이지가 존재하지 않거나          │
│      이동되었을 수 있습니다.                  │
│                                              │
│      ┌──────────────────┐                    │
│      │  🏠 홈으로 이동   │                    │
│      └──────────────────┘                    │
│                                              │
│      ┌──────┐ ┌──────┐ ┌──────┐             │
│      │ 대시보드│ │ 티켓   │ │ 회사 │             │
│      └──────┘ └──────┘ └──────┘             │
│                                              │
└──────────────────────────────────────────────┘
```

**500 Internal Server Error:**
```
┌──────────────────────────────────────────────┐
│                                              │
│          ⚠️                                  │
│  (Server Error Illustration)                 │
│                                              │
│      일시적인 오류가 발생했습니다              │
│                                              │
│      문제가 계속되면 관리자에게 문의하세요.     │
│                                              │
│      ┌──────────────────┐                    │
│      │   🔄 다시 시도    │                    │
│      └──────────────────┘                    │
│                                              │
│      관리자에게 문의                         │
│                                              │
└──────────────────────────────────────────────┘
```

#### 6.15.5 네트워크 오프라인 UX (Network Offline UX)

**오프라인 감지 및 표시:**
- Browser `online`/`offline` 이벤트 리스너 (또는 `navigator.onLine`)
- 상태 변경 시 전역 상태 관리로 React Component 재렌더링

**오프라인 배너:**
```
┌──────────────────────────────────────────────┐
│ ⚠️ 인터넷 연결이 끊겼습니다 — 온라인 복귀 시 자동 동기화 │
└──────────────────────────────────────────────┘
```
- Height: 36px, Background: `warning.500`, Text: `neutral.900`
- Top 고정 (Fixed position, above all content)
- Close 버튼 (일시 숨김 — 24시간 동안 안 표시)

**온라인 상태 표시:**
```
Header 우측:
  ● Online (green indicator)
  ─── Offline (yellow indicator + banner)
```

**오프라인 동작:**
- **Queue Optimistic Updates:** Delete, Create, Update 작업은 로컬 큐에 저장
- **Reconnect Sync:** 네트워크 복귀 시 자동으로 큐에 쌓인 작업 재시도
- **Exponential Backoff:** 재시도 시 1s → 2s → 4s → 8s (최대 5회)
- **Manual Retry:** 수동 재시도 버튼 (배너 내)

**Service Worker:**
- Cache API를 통해 주요 페이지 및 API 응답 캐싱
- Cache-first 전략 (네트워크 실패 시 캐시된 데이터 표시)
- API 캐시: `stale-while-revalidate` 전략 (오래된 캐시 + 백그라운드 업데이트)

---

### 6.16 로그인 및 인증 UI (Login & Authentication UI)

보안이 필요한 System의 첫 관문인 Login 및 Authentication Flow를 위한 UI 명세.

#### 6.16.1 로그인 화면 (Login Screen)

**레이아웃 구조:**
```
┌──────────────────────────────────────────────────────┐
│                                                      │
│                     [LOGO]                           │
│                   (주)엔유비즈                        │
│                                                      │
│  ┌────────────────────────────────────────────┐     │
│  │                                            │     │
│  │      nu_Trust 로그인                       │     │
│  │                                            │     │
│  │  이메일                                    │     │
│  │  [____________________________]            │     │
│  │                                            │     │
│  │  비밀번호                                   │     │
│  │  [____________________________] 👁️         │     │
│  │                                            │     │
│  │  ☐ 로그인 기억하기    [비밀번호 재설정]     │     │
│  │                                            │     │
│  │  ┌────────────────────────────────────┐   │     │
│  │  │          [로그인]                   │   │     │
│  │  └────────────────────────────────────┘   │     │
│  │                                            │     │
│  │           ─── 또는 ───                     │     │
│  │                                            │     │
│  │  [Google 로그인]  [Kakao 로그인]           │     │
│  │                                            │     │
│  └────────────────────────────────────────────┘     │
│                                                      │
│      ┌────────────────────────────────────┐         │
│      │  [회원가입]  |  [기업 문의]          │         │
│      └────────────────────────────────────┘         │
│                                                      │
└──────────────────────────────────────────────────────┘
```

**필드 상세:**
- **이메일:** Type="email", Required, Focus on page load
- **비밀번호:** Type="password" (Default), Type="text" (Toggle), Regex 강도 체크
- **Remember Me:** localStorage 또는 Session Cookie (30일)
- ** Forgot Password:** 비밀번호 재설정 페이지로 연결

**2FA (Two-Factor Authentication):**
```
┌──────────────────────────────────────┐
│  🔐 2단계 인증                       │
├──────────────────────────────────────┤
│                                      │
│  이메일로 전송된 인증번호 6자리를      │
│  입력하세요.                          │
│                                      │
│  [___] [___] [___] [___] [___] [___]│
│                                      │
│  인증번호 재전송: [59초]              │
│                                      │
│  [인증하기]                           │
│                                      │
└──────────────────────────────────────┘
```
- Support: TOTP (Google Authenticator 등), SMS
- OTP Auto-focus: 입력 칸마다 다음 칸으로 자동 이동
- ResendTimer: 60초 countdown

**소셜 로그인:**
| Provider | Button Style | OAuth Scope |
|----------|-------------|-------------|
| Google | White with Google "G" logo | `openid profile email` |
| Kakao | Yellow (`warning.500` bg) with Kakao logo | `account_profile email` |

**로딩/에러 상태:**
- 로그인 중: `disabled` 상태 + 로딩 스피너 `[로그인...] ⏳`
- 실패時: Field top에 빨간 에러 바 "이메일 또는 비밀번호가 올바르지 않습니다"
- 2FA 실패: "인증번호가 올바르지 않습니다. 다시 시도하세요."

#### 6.16.2 비밀번호 재설정 플로우 (Password Reset Flow)

**Step 1: 이메일 입력**
```
┌──────────────────────────────────────┐
│  🔑 비밀번호 재설정                   │
├──────────────────────────────────────┤
│                                      │
│  등록된 이메일 주소를 입력하세요.     │
│  재설정 링크가 전송됩니다.            │
│                                      │
│  이메일                              │
│  [____________________________]       │
│                                      │
│  [재설정 링크 보내기]                 │
│                                      │
└──────────────────────────────────────┘
```

**Step 2: 이메일 수신 → 재설정 폼**
```
┌──────────────────────────────────────┐
│  🔑 비밀번호 재설정                   │
├──────────────────────────────────────┤
│                                      │
│  새 비밀번호 (8자 이상)              │
│  [••••••••••] 🔒                     │
│                                      │
│  비밀번호 확인                        │
│  [••••••••••] 🔒                     │
│                                      │
│  비밀번호 조건:                       │
│  ✅ 8자 이상         ✅ 대문자       │
│  ✅ 소문자         ✅ 숫자  ✅ 특수문자│
│                                      │
│  ┌────────────────────────────────┐  │
│  │    [비밀번호 재설정]            │  │
│  └────────────────────────────────┘  │
│                                      │
└──────────────────────────────────────┘
```

**Step 3: 완료**
```
┌──────────────────────────────────────┐
│  ✅ 비밀번호가 성공적으로 변경되었습니다│
│                                      │
│  로그인 페이지로 3초 후 이동합니다...  │
│  [이동하기]                           │
└──────────────────────────────────────┘
```

**보안 조건:**
- Reset Token 유효 기간: 30分
- Single-use Token (사용后立即 무효화)
- 비밀번호 복잡도: Min 8자, 대소문자+숫자+특수문자
- 기존 비밀번호 재사용 불가 (최근 5회)

#### 6.16.3 회원가입 플로우 (Registration Flow)

**Invite Code 플로우 (Admin 초대):**
```
Step 1: Admin 초대 → 이메일 수신
  "안녕하세요! nu_Trust에 초대되었습니다."
  → [계정 만들기] 버튼 클릭

Step 2: 계정 생성 폼
  ├── 이름 (필수)
  ├── 이메일 (필수, Auto-filled)
  ├── 비밀번호 (필수, 강도 체크)
  ├── 비밀번호 확인 (필수)
  └── [계정 생성]

Step 3: 완료 → 로그인 화면으로 리디렉트
```

**Self-registration 플로우 (일반 회원가입):**
```
Step 1: 회사 정보 입력
  ├── 회사名称 (필수)
  ├── 이메일 (필수)
  └── [이메일 인증번호 전송] → 6자리 OTP

Step 2: 계정 생성
  ├── 이름 (필수)
  ├── 비밀번호 (필수, 강도 체크)
  ├── 약관 동의 (필수): [이용약관] ☑️  [개인정보처리방침] ☑️
  └── [계정 생성]

Step 3: 완료
  → "대기 중" 상태 (Admin Approval Required)
  → ["승인 대기 중입니다. 관리자가 승인하면 알림을 받으십니다."]
```

**UX:**
- Multi-step 진행률 표시: `Step 1/3 ●━━━ ○○○ Step 2/3`
- Progress bar: blue gradient fill
- 실시간 필드 검증 (blur + submit 시)
- Required 필드: `*` 표시 + 하이라이트 (border-color: `error.500`)

---

### 6.17 다국어 아키텍처 (i18n Architecture)

nu_Trust는 한국어(Korean)를 기본 언어로 하며, 영어(English)를 보조 언어로 지원합니다. 모든 텍스트는 Hard-code가 아닌 Translation Key 기반의 i18n 시스템으로 관리된다.

#### 6.17.1 다국어 지원 전략 (Multi-Language Strategy)

**지원 언어:**

|언어 | 코드 | 우선순위 | Status |
|-----|------|---------|--------|
| 한국어 | `ko` | Primary (Default) | ✅ |
| English | `en` | Secondary (Fallback) | ✅ |

**로케일 감지 순서:**
1. Browser Language (`navigator.language` / `Accept-Language` header)
2. URL Parameter (`?lang=en`)
3. URL Path 기반 (`/en/...` → `en`, `/ko/...` → `ko`)
4. LocalStorage 저장된 선택 (`preferredLocale`)
5. Final Default: `ko` (한국어)

**Fallback 전략:**
```
Requested locale → ko → en → Display raw key (never blank text)
```
- `ko` → `en` 번역이 없는 경우 → Key 표시: `tickets.list.empty.title`
- 절대 빈 텍스트(`""`) 또는 `undefined`를 표시하지 않는 것을 절대 금지

**Route 구조:**
```
/nutrust/...     → Default (ko)
/nutrust/ko/...  → Explicit Korean
/nutrust/en/...  → English
```

#### 6.17.2 번역 파일 구조 (Translation File Structure)

**디렉토리 구조:**
```
locales/
├── ko/
│   ├── common.json        # 공통 (buttons, labels, statuses)
│   ├── tickets.json       # 티켓 관련
│   ├── projects.json      # 프로젝트 관련
│   ├── users.json         # 사용자 관련
│   ├── companies.json     # 회사 관련
│   ├── errors.json        # 에러 메시지
│   ├── notifications.json # 알림 메시지
│   ├── auth.json          # 인증/Login 관련
│   └── forms.json         # 폼 필드 라벨/validation
├── en/
│   ├── common.json
│   ├── tickets.json
│   ├── projects.json
│   ├── users.json
│   ├── companies.json
│   ├── errors.json
│   ├── notifications.json
│   ├── auth.json
│   └── forms.json
```

**Key 포맷 (Dot Notation):**
```json
{
  "auth": {
    "login": {
      "title": "로그인",
      "email_label": "이메일",
      "password_label": "비밀번호",
      "forgot_password": "비밀번호 재설정",
      "remember_me": "로그인 기억하기",
      "submit": "로그인",
      "errors": {
        "invalid_credentials": "이메일 또는 비밀번호가 올바르지 않습니다",
        "account_disabled": "계정이 비활성화되었습니다. 관리자에게 문의하세요"
      }
    }
  },
  "tickets": {
    "list": {
      "title": "티켓 목록",
      "empty": {
        "title": "등록된 티켓이 없습니다",
        "message": "Tickets이 아직 없습니다. 티켓을 생성하세요.",
        "cta": "티켓 생성하기"
      }
    }
  }
}
```

#### 6.17.3 ICU Message Format (ICU 메시지 포맷)

**Pluralization (복수형):**
```json
{
  "tickets": {
    "list": {
      "count": "{count, plural, =0 {티켓이 없습니다} =1 {티켓 1개} other {티켓 {count}개}}"
    },
    "status": {
      "completed": "{count, plural, =0 {완료된 티켓이 없습니다} =1 {티켓 1개가 완료되었습니다} other {티켓 {count}개가 완료되었습니다}}"
    }
  }
}
```

**Number Formatting (숫자):**
```json
{
  "common": {
    "member_count": "멤버 {count, number}명",
    "ticket_count": "티켓 {count, number}개"
  }
}
```

**Variables (변수):**
```json
{
  "welcome": "안녕하세요, {name}님!",
  "pending_tickets": "처리 대기 중인 티켓이 {pending, number}개 있습니다"
}
```

**Rich Text Support (리치 텍스트):**
```json
{
  "auth": {
    "terms": "<1>사용약관</1>과 <3>개인정보처리방침</3>에 동의하세요."
  }
}
```
- `<1>`, `<3>` 태그로 Link 컴포넌트 Wrapping
- React: `<FormattedMessage>` 또는 `next-intl`의 `RichText` Component

#### 6.17.4 날짜/숫자 포맷팅 (Date/Number Formatting)

**날짜 포맷:**

| Locale | Format | Example |
|--------|--------|---------|
| `ko` | YYYY.MM.DD | 2025.05.15 |
| `en` | MM/DD/YYYY | 05/15/2025 |
| `ko` (full) | YYYY.MM.DD (D) HH:mm | 2025.05.15 (목) 14:30 |
| `en` (full) | MMM D, YYYY HH:mm | May 15, 2025 14:30 |

**시간 포맷:**

| Context | ko | en |
|---------|-----|-----|
| Relative (2 minutes ago) | 2분 전 | 2 min ago |
| Relative (1 hour ago) | 1시간 전 | 1 hour ago |
| Relative (2 days ago) | 2일 전 | 2 days ago |

**국제화 API 사용:**
```tsx
// Date formatting
const formatDate = (date: Date, locale: string) => {
  return new Intl.DateTimeFormat(locale, {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    hour12: false,
  }).format(date)
}

// Number formatting
const formatNumber = (num: number, locale: string) => {
  return new Intl.NumberFormat(locale).format(num)
}

// Currency formatting
const formatCurrency = (amount: number, locale: string, currency: string) => {
  return new Intl.NumberFormat(locale, {
    style: 'currency',
    currency,
  }).format(amount)
}
```

**Currency by Locale:**

| Locale | Currency Code | Symbol |
|--------|--------------|--------|
| ko | KRW | ₩ |
| en | USD | $ |

**i18n Testing Checklist:**
- [ ] 모든 UI 텍스트가 번역 파일에서 참조됨
- [ ] Missing Key 감지 → Console Warning + Key 표시
- [ ] Pluralization (ko/en) 테스트 — 0, 1, N 개수
- [ ] RTL (우측에서 왼쪽) 언어 미지원 (현재 ko/en만)
- [ ] Date/Number/Currency Locale별 출력 검증
- [ ] i18n Key Migration: 코드 변경 시 누락된 Key 자동 감지
