package com.example.ui.components

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.unit.dp

@Composable
fun SpotifyEmbedPlayer(trackId: String, modifier: Modifier = Modifier) {
    // trackId should be a Spotify ID like '4uLU61CZdwh6909ub99s7u'
    val embedUrl = "https://open.spotify.com/embed/track/$trackId?utm_source=generator"
    
    AndroidView(
        factory = { context ->
            WebView(context).apply {
                webViewClient = WebViewClient()
                settings.javaScriptEnabled = true
                loadUrl(embedUrl)
            }
        },
        modifier = modifier.height(352.dp).fillMaxWidth()
    )
}

@Composable
fun AppleMusicEmbedPlayer(songId: String, storefront: String = "us", modifier: Modifier = Modifier) {
    // songId should be an Apple Music ID like '1440742903'
    val embedUrl = "https://embed.music.apple.com/$storefront/album/song/$songId"
    
    AndroidView(
        factory = { context ->
            WebView(context).apply {
                webViewClient = WebViewClient()
                settings.javaScriptEnabled = true
                loadUrl(embedUrl)
            }
        },
        modifier = modifier.height(175.dp).fillMaxWidth()
    )
}

@Composable
fun MusicAccountConnectDialog(
    serviceName: String,
    onDismiss: () -> Unit,
    onConnectSuccess: (String) -> Unit
) {
    var isLoading by remember { mutableStateOf(true) }
    var connectionComplete by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Connect $serviceName") },
        text = {
            Box(modifier = Modifier.fillMaxWidth().height(400.dp)) {
                AndroidView(
                    factory = { context ->
                        WebView(context).apply {
                            webViewClient = object : WebViewClient() {
                                override fun onPageFinished(view: WebView?, url: String?) {
                                    super.onPageFinished(view, url)
                                    isLoading = false
                                    // In a real OAuth flow, we'd check for redirect URI and extract code/token
                                    if (url?.contains("callback") == true || url?.contains("success") == true) {
                                        connectionComplete = true
                                    }
                                }
                            }
                            settings.javaScriptEnabled = true
                            // Mocking the auth URL for now
                            val authUrl = when (serviceName) {
                                "Spotify" -> "https://accounts.spotify.com/authorize?client_id=YOUR_CLIENT_ID&response_type=code&redirect_uri=wereequal://callback&scope=user-read-private%20user-read-email"
                                "Apple Music" -> "https://authorize.music.apple.com"
                                else -> "about:blank"
                            }
                            loadUrl(authUrl)
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(androidx.compose.ui.Alignment.Center))
                }
                
                if (connectionComplete) {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
                    ) {
                        Text("Connection Successful!", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { onConnectSuccess("mock_user_id") }) {
                            Text("Finish")
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
