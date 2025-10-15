# Environment Variables Setup

## 환경 변수 설정 가이드

이 프로젝트는 민감한 정보(API 키, 데이터베이스 인증 정보 등)를 `.env` 파일로 관리합니다.

### 초기 설정

1. **`.env.example` 파일을 복사하여 `.env` 파일 생성**
   ```bash
   cp .env.example .env
   ```

2. **`.env` 파일에 실제 값 입력**
   ```
   # Supabase Configuration
   SUPABASE_URL=your_actual_supabase_url
   SUPABASE_ANON_KEY=your_actual_supabase_anon_key

   # OpenAI Configuration (for AI feedback)
   OPENAI_API_KEY=your_actual_openai_api_key
   ```

3. **빌드 및 실행**
   - 환경 변수는 빌드 시 `BuildConfig`에 자동으로 주입됩니다
   - `BuildConfig.SUPABASE_URL`, `BuildConfig.SUPABASE_ANON_KEY` 등으로 접근 가능

### 중요 사항

- ⚠️ **`.env` 파일은 절대 GitHub에 커밋하지 마세요!**
- ✅ `.gitignore`에 `.env`가 포함되어 있어 자동으로 무시됩니다
- ✅ `.env.example`은 커밋 가능합니다 (실제 값 없이 템플릿만)

### 팀원과 공유

- `.env.example` 파일을 공유하여 필요한 환경 변수 목록을 알려주세요
- 실제 값은 안전한 방법으로 별도로 공유하세요 (Slack DM, 1Password 등)

### CI/CD 환경

GitHub Actions 등 CI/CD에서는 Repository Secrets에 다음 변수들을 설정하세요:
- `SUPABASE_URL`
- `SUPABASE_ANON_KEY`
- `OPENAI_API_KEY`

### 문제 해결

**빌드 에러가 발생하는 경우:**
1. `.env` 파일이 프로젝트 루트에 있는지 확인
2. 환경 변수 이름이 정확한지 확인 (대소문자 구분)
3. Clean & Rebuild: `./gradlew clean assembleDebug`

**환경 변수 추가가 필요한 경우:**
1. `.env` 파일에 변수 추가
2. `.env.example`에도 템플릿 추가
3. `app/build.gradle.kts`의 `buildConfigField` 추가
4. 코드에서 `BuildConfig.YOUR_VAR_NAME`으로 사용
