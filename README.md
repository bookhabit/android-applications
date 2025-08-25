# Android Applications

## 📱 프로젝트 개요
Jetpack Compose를 사용하여 다양한 모바일 앱 기능들을 구현한 Android 프로젝트입니다. 할일관리, 메모장, 날씨앱, 계산기 등 16개의 기본 앱과 게임, 애니메이션, 네이티브 기능들을 포함합니다.

## 🏗️ 프로젝트 구조

```
android-applications/
├── app/                          # 메인 앱 모듈
│   ├── build.gradle.kts         # 앱 레벨 빌드 설정
│   ├── proguard-rules.pro       # ProGuard 규칙
│   └── src/                      # 소스 코드
│       ├── main/                 # 메인 소스
│       │   ├── AndroidManifest.xml # 앱 매니페스트
│       │   ├── java/             # Java/Kotlin 소스
│       │   │   └── com/example/android_applicatoins/
│       │   │       ├── components/ # 재사용 컴포넌트
│       │   │       │   └── AppCard.kt # 앱 카드 컴포넌트
│       │   │       ├── MainActivity.kt # 메인 액티비티
│       │   │       ├── model/    # 데이터 모델
│       │   │       │   └── AppItem.kt # 앱 아이템 모델
│       │   │       ├── navigation/ # 네비게이션
│       │   │       │   └── AppNavigation.kt # 앱 네비게이션
│       │   │       └── screens/  # 화면들
│       │   │           ├── MainScreen.kt # 메인 화면
│       │   │           ├── basic/ # 기본 앱들
│       │   │           │   ├── BasicScreen.kt # 기본 앱 메인 화면
│       │   │           │   ├── TodoScreen.kt # 할일관리
│       │   │           │   ├── NotesScreen.kt # 메모장
│       │   │           │   ├── WeatherScreen.kt # 날씨앱
│       │   │           │   ├── CalculatorScreen.kt # 계산기
│       │   │           │   ├── EventTestScreen.kt # 이벤트테스트
│       │   │           │   ├── GraphicsDrawingScreen.kt # 그래픽그리기
│       │   │           │   ├── ImageDisplayScreen.kt # 이미지표시
│       │   │           │   ├── DialogPopupNotificationScreen.kt # 대화상자-팝업-알림
│       │   │           │   ├── ListViewScreen.kt # 리스트뷰
│       │   │           │   ├── FormExampleScreen.kt # 폼예제
│       │   │           │   ├── ActionSheetScreen.kt # 액션시트
│       │   │           │   ├── ModalScreen.kt # 모달
│       │   │           │   ├── MovieInfoScreen.kt # 영화정보
│       │   │           │   ├── CalendarScreen.kt # 캘린더
│       │   │           │   ├── StepCounterScreen.kt # 스텝카운터
│       │   │           │   ├── GalleryScreen.kt # 갤러리
│       │   │           │   ├── ShopScreen.kt # 쇼핑
│       │   │           │   ├── QuizScreen.kt # 퀴즈
│       │   │           │   └── listView/ # 리스트뷰 데모들
│       │           │               ├── AdapterViewDemoScreen.kt # AdapterView 데모
│       │           │               ├── RecyclerViewDemoScreen.kt # RecyclerView 데모
│       │           │               ├── SpinnerDemoScreen.kt # Spinner 데모
│       │           │               ├── FragmentDemoScreen.kt # Fragment 데모
│       │           │               ├── ViewPagerDemoScreen.kt # ViewPager 데모
│       │           │               └── ComposeLazyDemoScreen.kt # Compose Lazy 데모
│       │           ├── game/      # 게임 앱들
│       │           │   ├── GameScreen.kt # 게임 메인 화면
│       │           │   ├── SnakeGameScreen.kt # 스네이크 게임
│       │           │   ├── TicTacToeScreen.kt # 틱택토
│       │           │   ├── MemoryGameScreen.kt # 메모리 게임
│       │           │   └── ...    # 기타 게임들
│       │           ├── animation/ # 애니메이션 앱들
│       │           │   ├── AnimationScreen.kt # 애니메이션 메인 화면
│       │           │   ├── MindFlowScreen.kt # 마인드플로우
│       │           │   ├── TravelDreamsScreen.kt # 여행의꿈
│       │           │   ├── CookExplorerScreen.kt # 요리탐험가
│       │           │   ├── MoneyJourneyScreen.kt # 돈의여정
│       │           │   └── QuizHeroScreen.kt # 퀴즈히어로
│       │           └── native/    # 네이티브 기능 앱들
│       │               ├── NativeScreen.kt # 네이티브 메인 화면
│       │               ├── MediaNotesScreen.kt # 미디어노트
│       │               ├── MiniFileExplorerScreen.kt # 미니파일탐색기
│       │               ├── MiniHealthTrackerScreen.kt # 미니건강추적기
│       │               ├── SecureNotesScreen.kt # 보안노트
│       │               ├── SensorPlaygroundScreen.kt # 센서놀이터
│       │               ├── SimpleShopScreen.kt # 심플상점
│       │               ├── TripLoggerScreen.kt # 여행로거
│       │               └── UtilityKitScreen.kt # 유틸리티키트
│       │       ├── res/          # 리소스 파일들
│       │       │   ├── drawable/ # 드로어블 리소스
│       │       │   │   ├── ic_launcher_background.xml # 런처 배경
│       │       │   │   └── ic_launcher_foreground.xml # 런처 전경
│       │       │   ├── mipmap-*/ # 런처 아이콘들
│       │       │   ├── values/   # 값 리소스들
│       │       │   │   ├── colors.xml # 색상 정의
│       │       │   │   ├── strings.xml # 문자열 정의
│       │       │   │   └── themes.xml # 테마 정의
│       │       │   └── xml/      # XML 리소스들
│       │       │       ├── backup_rules.xml # 백업 규칙
│       │       │       └── data_extraction_rules.xml # 데이터 추출 규칙
│       │       ├── androidTest/  # Android 테스트
│       │       │   └── java/com/example/android_applicatoins/
│       │       │       └── ExampleInstrumentedTest.kt # 인스트루먼트 테스트
│       │       └── test/         # 단위 테스트
│       │           └── java/com/example/android_applicatoins/
│       │               └── ExampleUnitTest.kt # 단위 테스트
│   ├── build.gradle.kts         # 모듈 레벨 빌드 설정
│   └── proguard-rules.pro       # ProGuard 규칙
├── build.gradle.kts              # 프로젝트 레벨 빌드 설정
├── gradle/                       # Gradle 래퍼
│   ├── libs.versions.toml       # 버전 카탈로그
│   └── wrapper/                  # Gradle 래퍼 파일들
│       ├── gradle-wrapper.jar    # Gradle 래퍼 JAR
│       └── gradle-wrapper.properties # Gradle 래퍼 속성
├── gradle.properties             # Gradle 속성
├── gradlew                       # Gradle 래퍼 스크립트 (Unix)
└── gradlew.bat                   # Gradle 래퍼 스크립트 (Windows)
```

## ⚙️ 프로젝트 설정

### 📦 주요 의존성
- **Android Gradle Plugin**: 8.2.0
- **Kotlin**: 1.9.20
- **Compile SDK**: 34
- **Target SDK**: 34
- **Min SDK**: 24
- **Jetpack Compose**: 1.5.4
- **Compose BOM**: 2023.10.01

### 🚀 개발 환경 설정
```bash
# 프로젝트 빌드
./gradlew assembleDebug

# 릴리즈 빌드
./gradlew assembleRelease

# 테스트 실행
./gradlew test

# 인스트루먼트 테스트 실행
./gradlew connectedAndroidTest
```

## 🎨 UI 개발 환경

### 🎯 컴포넌트 구조

#### 1. **AppCard 컴포넌트** (`components/AppCard.kt`)
- 재사용 가능한 앱 카드 컴포넌트
- 제목, 설명, 아이콘, 색상을 표시
- 클릭 이벤트 처리

#### 2. **화면 컴포넌트들** (`screens/`)
- **Basic**: 16개의 기본 앱 화면
- **Game**: 게임 관련 화면들
- **Animation**: 애니메이션 효과 화면들
- **Native**: 네이티브 기능 화면들

#### 3. **데이터 모델** (`model/AppItem.kt`)
```kotlin
data class AppItem(
    val id: String,
    val title: String,
    val description: String,
    val icon: ImageVector,
    val route: String
)
```

### 🌈 색상 시스템

#### Material Design 3 색상
- **Primary**: Material Design 3 기본 색상
- **Secondary**: 보조 색상
- **Surface**: 표면 색상
- **Background**: 배경 색상
- **Error**: 오류 색상

#### 색상 적용 예시
```kotlin
import androidx.compose.ui.graphics.Color

// 기본 색상
Color.Red
Color.Blue
Color.Green

// 커스텀 색상
Color(0xFFFF9800) // Orange
Color(0xFF9C27B0) // Purple
Color(0xFFE91E63) // Pink
```

### 🔤 폰트 시스템

#### 시스템 폰트 사용
- **Default**: 시스템 기본 폰트
- **Material Icons**: Material Design 아이콘 폰트

#### 폰트 적용 예시
```kotlin
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

Text(
    text = "제목",
    fontSize = 24.sp,
    fontWeight = FontWeight.Bold
)
```

### 🎭 테마 시스템

#### Material Design 3 테마
```kotlin
@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
```

#### 다크/라이트 모드 지원
- 시스템 설정에 따른 자동 테마 전환
- `isSystemInDarkTheme()` 함수 사용

## 📱 네비게이션 구조

### Jetpack Compose Navigation
- **NavHost**: 네비게이션 그래프 정의
- **composable**: 개별 화면 정의
- **navController**: 네비게이션 제어

#### 네비게이션 예시
```kotlin
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    
    NavHost(navController = navController, startDestination = Screen.Main.route) {
        composable(Screen.Main.route) {
            MainScreen(
                onBasicSelected = { navController.navigate(Screen.Basic.route) }
            )
        }
        
        composable(Screen.Basic.route) {
            BasicScreen(
                onBackPressed = { navController.popBackStack() },
                onAppSelected = { appId ->
                    when (appId) {
                        "todo" -> navController.navigate(Screen.Todo.route)
                        "notes" -> navController.navigate(Screen.Notes.route)
                        // ... 기타 앱들
                    }
                }
            )
        }
        
        // ... 기타 화면들
    }
}
```

## 🧪 테스트 환경

### 단위 테스트
- **JUnit**: 기본 테스트 프레임워크
- **Mockito**: 모킹 라이브러리
- **Coroutines Test**: 코루틴 테스트

### 인스트루먼트 테스트
- **Espresso**: UI 테스트
- **UI Automator**: 자동화 테스트

### 테스트 실행
```bash
# 단위 테스트
./gradlew test

# 인스트루먼트 테스트
./gradlew connectedAndroidTest

# 모든 테스트
./gradlew check
```

## 🔧 개발 도구

### 코드 품질
- **Kotlin**: 정적 타입 검사
- **Android Lint**: 코드 품질 검사
- **ktlint**: Kotlin 코드 스타일

### 빌드 도구
- **Gradle**: 빌드 시스템
- **Android Gradle Plugin**: Android 빌드 플러그인
- **Kotlin DSL**: Kotlin 기반 빌드 스크립트

## 🚀 배포

### APK 빌드
```bash
# 디버그 APK
./gradlew assembleDebug

# 릴리즈 APK
./gradlew assembleRelease

# 번들 APK
./gradlew bundleRelease
```

### 앱 서명
- **Debug**: 개발용 자동 서명
- **Release**: 프로덕션용 서명 키 필요

### Google Play Console 배포
1. 릴리즈 APK/AAB 생성
2. Google Play Console에 업로드
3. 테스트 및 출시

## 📚 주요 기술 스택

### UI 프레임워크
- **Jetpack Compose**: 현대적인 UI 툴킷
- **Material Design 3**: 디자인 시스템

### 아키텍처
- **MVVM**: Model-View-ViewModel 패턴
- **Repository**: 데이터 접근 패턴
- **Use Case**: 비즈니스 로직 패턴

### 상태 관리
- **State**: Compose 상태 관리
- **ViewModel**: 화면 상태 관리
- **LiveData**: 데이터 관찰

### 비동기 처리
- **Coroutines**: 비동기 프로그래밍
- **Flow**: 반응형 스트림
- **StateFlow**: 상태 스트림

## 🤝 기여 방법

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 라이선스

이 프로젝트는 MIT 라이선스 하에 배포됩니다.

## 🔗 추가 리소스

- [Android 개발자 문서](https://developer.android.com/)
- [Jetpack Compose 문서](https://developer.android.com/jetpack/compose)
- [Material Design 3](https://m3.material.io/)
- [Kotlin 공식 문서](https://kotlinlang.org/)
