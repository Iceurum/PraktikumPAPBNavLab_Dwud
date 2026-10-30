package com.example.navigationlab.ui.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.navigationlab.R
import com.example.navigationlab.ui.ui.home.HomeUiState

@Composable
fun HomeScreen(
    uiState: HomeUiState,
    onOpenDetail: (Int) -> Unit,
    onOpenProfile: () -> Unit,
    onOpenAbout: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {

        Image(
            painter = painterResource(id = R.drawable.wallpaper),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.35f))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Navigation Lab",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = "Aplikasi Mahasikma",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.85f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.students, key = { it.id }) { student ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOpenDetail(student.id) }
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = student.name,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text("ID: ${student.id}")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(bottom = 16.dp)
            ) {
                OutlinedButton(
                    onClick = onOpenProfile,
                    modifier = Modifier.fillMaxWidth(),
                    border = BorderStroke(1.dp, Color.White)
                ) {
                    Text("Buka Profile", color = Color.White)
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = onOpenAbout,
                    modifier = Modifier.fillMaxWidth(),
                    border = BorderStroke(1.dp, Color.White)
                ) {
                    Text("Buka About", color = Color.White)
                }
            }
        }
    }
}