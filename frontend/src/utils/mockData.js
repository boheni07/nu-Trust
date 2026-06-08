/* ─── nu_Trust Mock Data ───────────────────────────
   Pure data exports. NO Vue imports, NO reactivity, NO API calls.
   All data matches reference HTML (nu_trust_ui.html).
*/

// ─── User ───────────────────────────────────────────
export const getUser = () => ({
  name: '홍길동',
  company: '(주)엔유비즈',
  role: 'ADMIN',
  initial: '홍',
  email: 'hong@enubiz.co.kr',
})

// ─── Stats (Dashboard) ─────────────────────────────
export const getStats = () => [
  { label: '전체 회사', value: 42, change: '↑ 3 이번 달', color: 'teal', icon: 'companies' },
  { label: '활성 프로젝트', value: 18, change: '↑ 2 이번 달', color: 'blue', icon: 'projects' },
  { label: '오늘 접수 티켓', value: 12, change: '↓ 2 어제 대비', color: 'amber', icon: 'tickets', down: true },
  { label: 'SLA 경고', value: 3, change: '처리 필요', color: 'red', icon: 'alert', down: true },
]

// ─── Recent Tickets (Dashboard table) ───────────────
export const getRecentTickets = () => [
  { id: '#156', title: '로그인 페이지 응답 지연 현상', status: 'received', priority: 'high', company: '(주)엔유비즈', deadline: '2026/06/01' },
  { id: '#155', title: 'DB 백업 실패 알림', status: 'processing', priority: 'high', company: 'ABC물류', deadline: '2026/05/30' },
  { id: '#154', title: 'POS 시스템 크래시 재발', status: 'comp-req', priority: 'medium', company: 'XYZ식료품', deadline: '2026/05/29' },
  { id: '#153', title: '사용자 권한 오류', status: 'delayed', priority: 'high', company: '(주)엔유비즈', deadline: '2026/05/28' },
  { id: '#152', title: '결제 연동 오류', status: 'registered', priority: 'low', company: 'DEF전자', deadline: '2026/06/05' },
  { id: '#151', title: '데이터 내보내기 성능 저하', status: 'completed', priority: 'medium', company: 'ABC물류', deadline: '2026/05/25' },
]

// ─── Activity Feed ──────────────────────────────────
export const getActivityFeed = () => [
  { text: '티켓 #156이 접수되었습니다.', time: '30분 전', color: 'var(--teal)' },
  { text: '(주)엔유비즈에 새 사용자 초대', time: '2시간 전', color: 'var(--blue)' },
  { text: '티켓 #153이 완료되었습니다.', time: '4시간 전', color: 'var(--green)' },
  { text: '프로젝트 POS4기 영업 완료', time: '어제', color: 'var(--amber)' },
  { text: 'SLA 경고: 티켓 #154 (D-1)', time: '어제', color: 'var(--red)' },
]

// ─── Full Tickets Table ─────────────────────────────
export const getTickets = () => [
  { id: '#156', title: '로그인 페이지 응답 지연 현상', status: 'received', priority: 'high', company: '(주)엔유비즈', reporter: '김대표', deadline: '2026/06/01', created: '2026/05/28' },
  { id: '#155', title: 'DB 백업 실패 알림', status: 'processing', priority: 'high', company: 'ABC물류', reporter: '최개발', deadline: '2026/05/30', created: '2026/05/27' },
  { id: '#154', title: 'POS 시스템 크래시 재발', status: 'comp-req', priority: 'medium', company: 'XYZ식료품', reporter: '박점포', deadline: '2026/05/29', created: '2026/05/26' },
  { id: '#153', title: '사용자 권한 오류', status: 'delayed', priority: 'high', company: '(주)엔유비즈', reporter: '이관리', deadline: '2026/05/28', created: '2026/05/25' },
  { id: '#152', title: '결제 연동 오류', status: 'registered', priority: 'low', company: 'DEF전자', reporter: '정개발', deadline: '2026/06/05', created: '2026/05/24' },
  { id: '#151', title: '데이터 내보내기 성능 저하', status: 'completed', priority: 'medium', company: 'ABC물류', reporter: '한관리', deadline: '2026/05/25', created: '2026/05/23' },
  { id: '#150', title: '메인 화면 로딩 중斷', status: 'processing', priority: 'medium', company: 'GHI서비스', reporter: '임개발', deadline: '2026/06/02', created: '2026/05/22' },
]

// ─── Ticket Detail ──────────────────────────────────
export const getTicketDetail = () => ({
  id: '#156',
  category: '서버 장애 유형',
  title: '로그인 페이지 응답 지연 현상',
  body: '2026년 5월 28일 오전 10시경부터 로그인 페이지의 응답 속도가 현저하게 저하되고 있습니다. 일부 사용자의 경우 30초 이상 대기 후에도 로그인이 완료되지 않아 업무 차질이 발생하고 있는 상황입니다. 특히 출퇴근 시간대(09:00–09:30, 18:00–18:30)에 집중적으로 발생하고 있으며, DB Connection Pool 고갈이 원인으로 추정됩니다.',
  status: 'processing',
  priority: 'high',
  company: '(주)엔유비즈',
  reporter: '김대표',
  assignee: '최개발',
  deadline: '2026/06/01',
  created: '2026/05/28 10:15',
  attachments: [
    { name: '에러로그_screenshot.png', type: 'image' },
    { name: 'connection_pool.log', type: 'file' },
    { name: '응답시간계측표.xlsx', type: 'file' },
  ],
  comments: [
    { author: '최개발', time: '5월 28일 11:00', text: 'Connection Pool 상태를 확인 중입니다. max connections가 100으로 설정되어 있는데, 동시에 120개 이상의 커넥션이 생성되는 것을 확인했습니다.', role: 'developer' },
    { author: '박Support', time: '5월 28일 13:30', text: 'DB 리소스를 늘려드릴까요? 현재 RDS의 instance type이 db.t3.small입니다.', role: 'support' },
    { author: '최개발', time: '5월 28일 14:00', text: '네, db.t3.medium으로 업그레이드 부탁드립니다. 그 후 모니터링해 보겠습니다.', role: 'developer' },
  ],
  stages: [
    { label: '등록됨', done: true },
    { label: '접수됨', done: true },
    { label: '처리 중', current: true },
    { label: '완료 요청', done: false },
    { label: '완료됨', done: false },
  ],
})

// ─── Companies Table ────────────────────────────────
export const getCompanies = () => [
  { name: '(주)엔유비즈', type: 'dev', owner: '홍길동', biz: '123-45-67890', members: 8, projects: 12, status: 'active', date: '2024/01/15' },
  { name: 'ABC물류(주)', type: 'client', owner: '김대표', biz: '987-65-43210', members: 5, projects: 8, status: 'active', date: '2024/02/20' },
  { name: 'XYZ식료품(주)', type: 'client', owner: '박점포', biz: '111-22-33333', members: 3, projects: 5, status: 'active', date: '2024/03/10' },
  { name: 'DEF전자(주)', type: 'client', owner: '정개발', biz: '444-55-66666', members: 15, projects: 6, status: 'active', date: '2024/04/01' },
  { name: 'GHI서비스(주)', type: 'client', owner: '임개발', biz: '777-88-99999', members: 2, projects: 1, status: 'inactive', date: '2024/06/15' },
]

// ─── Companies Tabs ─────────────────────────────────
export const getCompanyTabs = () => [
  { label: '전체', count: 42, active: true },
  { label: '개발사', count: 3 },
  { label: '고객사', count: 39 },
  { label: '비활성', count: 4 },
]

// ─── Users Table ────────────────────────────────────
export const getUsers = () => [
  { name: '홍길동', initial: '홍', email: 'hong@enubiz.co.kr', role: 'admin', company: '(주)엔유비즈', status: 'active', joinDate: '2024/01/15', lastLogin: '방금 전' },
  { name: '최개발', initial: '최', email: 'choi@enubiz.co.kr', role: 'support', company: '(주)엔유비즈', status: 'active', joinDate: '2024/02/20', lastLogin: '1시간 전' },
  { name: '박Support', initial: '박', email: 'park@enubiz.co.kr', role: 'support', company: '(주)엔유비즈', status: 'active', joinDate: '2024/03/10', lastLogin: '30분 전' },
  { name: '김대표', initial: '김', email: 'kim@abc-logistics.co.kr', role: 'customer', company: 'ABC물류(주)', status: 'active', joinDate: '2024/04/01', lastLogin: '어제' },
  { name: '임개발', initial: '임', email: 'lim@ghi-service.co.kr', role: 'customer', company: 'GHI서비스(주)', status: 'inactive', joinDate: '2024/06/15', lastLogin: '1주 전' },
]

// ─── Users Tabs ─────────────────────────────────────
export const getUserTabs = () => [
  { label: '전체', count: 68, active: true },
  { label: 'Admin', count: 3 },
  { label: 'Support', count: 12 },
  { label: 'Customer', count: 53 },
]

// ─── Projects Cards ─────────────────────────────────
export const getProjects = () => [
  {
    name: 'POSM Web App',
    company: 'ABC 물류(주)',
    status: 'active',
    color: 'teal',
    activeTickets: 8,
    teamMembers: 3,
    slaRisk: 1,
    period: '2024/01 ~ 2026/12',
  },
  {
    name: 'IoT 모니터링',
    company: 'DEF 전자(주)',
    status: 'active',
    color: 'blue',
    activeTickets: 12,
    teamMembers: 5,
    slaRisk: 0,
    period: '2024/03 ~ 2026/06',
  },
  {
    name: 'ERP 연동',
    company: 'XYZ 식료품(주)',
    status: 'onhold',
    color: 'amber',
    activeTickets: 4,
    teamMembers: 2,
    slaRisk: 2,
    period: '2024/06 ~ 2025/12',
  },
  {
    name: 'Mobile POS',
    company: 'ABC 물류(주)',
    status: 'active',
    color: 'seafoam',
    activeTickets: 6,
    teamMembers: 4,
    slaRisk: 0,
    period: '2025/01 ~ 2026/12',
  },
  {
    name: '재고 관리 시스템',
    company: 'DEF 전자(주)',
    status: 'completed',
    color: 'green',
    activeTickets: 0,
    teamMembers: 3,
    slaRisk: 0,
    period: '2024/01 ~ 2025/06',
  },
]

// ─── Project Tabs ───────────────────────────────────
export const getProjectTabs = () => [
  { label: '총 프로젝트', count: 33, active: true },
  { label: '활성', count: 18 },
  { label: '보류', count: 3 },
  { label: '완료', count: 12 },
]

// ─── SLA Settings ───────────────────────────────────
export const getSLASettings = () => ({
  high: 2,
  medium: 5,
  low: 14,
  warnD: 1,
})

// ─── Setting Nav Items ──────────────────────────────
export const getSettingNavItems = () => [
  { label: 'SLA 설정', active: true },
  { label: '알림 설정' },
  { label: 'Rate Limit' },
  { label: '캐시 정책' },
  { label: 'Business Calendar' },
  { label: '감사 로그' },
]

// ─── Notification Data ──────────────────────────────
export const getNotifications = () => [
  { text: '티켓 #156이 접수되었습니다.', time: '30분 전', read: false },
  { text: '티켓 #154 SLA:D-1 경고', time: '1시간 전', read: false },
  { text: '(주)엔유비즈에 새 사용자 초대', time: '2시간 전', read: true },
  { text: '프로젝트 POS4기 영업 완료', time: '어제', read: true },
  { text: '티켓 #153이 완료되었습니다.', time: '어제', read: true },
]
