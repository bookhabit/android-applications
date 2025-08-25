package com.example.android_applicatoins.screens.basic

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.android_applicatoins.components.AppCard
import com.example.android_applicatoins.model.AppItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListViewScreen(
    onBackPressed: () -> Unit,
    onAppSelected: (String) -> Unit
) {
    val listViewApps = listOf(
        AppItem(
            id = "adapter-view-demo",
            title = "AdapterView 데모",
            description = "ListView와 ArrayAdapter를 사용한 기본 리스트 구현",
            icon = Icons.Default.ViewList,
            route = "adapter-view-demo"
        ),
        AppItem(
            id = "recycler-view-demo",
            title = "RecyclerView 데모",
            description = "RecyclerView와 ViewHolder 패턴을 사용한 고성능 리스트",
            icon = Icons.Default.ViewModule,
            route = "recycler-view-demo"
        ),
        AppItem(
            id = "spinner-demo",
            title = "Spinner 데모",
            description = "드롭다운 선택 박스 구현",
            icon = Icons.Default.ArrowDropDown,
            route = "spinner-demo"
        ),
        AppItem(
            id = "fragment-demo",
            title = "Fragment 데모",
            description = "모듈화된 UI 단위 Fragment 구현",
            icon = Icons.Default.ViewColumn,
            route = "fragment-demo"
        ),
        AppItem(
            id = "view-pager-demo",
            title = "ViewPager 데모",
            description = "스와이프 가능한 페이지 전환 컨테이너",
            icon = Icons.Default.ViewCarousel,
            route = "view-pager-demo"
        ),
        AppItem(
            id = "compose-lazy-demo",
            title = "Compose Lazy 데모",
            description = "Jetpack Compose의 LazyColumn/LazyRow 사용법",
            icon = Icons.Default.ViewStream,
            route = "compose-lazy-demo"
        )
    )
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("리스트 뷰 학습") },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(Icons.Default.ArrowBack, "뒤로가기")
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            items(listViewApps) { appItem ->
                AppCard(
                    appItem = appItem,
                    onClick = {
                        onAppSelected(appItem.id)
                    }
                )
            }
        }
    }
}
