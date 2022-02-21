package com.example.demo_app_android.screen

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import android.webkit.*
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.example.demo_app_android.UserState
import com.example.demo_app_android.getLinkUrl
import kotlinx.coroutines.launch

@Composable
fun LinkScreen(navController: NavController) {
    var linkUrl: String? by remember { mutableStateOf(null) }
    val vm = UserState.current
    val coroutineScope = rememberCoroutineScope()
    // Display start linking screen if the link url is empty
    if (linkUrl == null) {
        Scaffold(topBar = { TopAppBar(title = { Text("Link") }) }) { StartLink { linkUrl = it } }
    } else {
        LinkingWebView(
                linkUrl!!,
                // Handler when linking is success, start fetching user data and
                // proceed to data showing page
                onSuccess = {
                    Log.d("link", "Success handler")
                    coroutineScope.launch { vm.fetchUser() }
                    navController.navigate("demo")
                },
                // Set link url to null, essentially go back to start link screen
                // TODO using a seperate screen (startlinkScreen, WebViewScreen) maybe a better way
                // to manage screen state
                onClose = {
                    Log.d("link", "Close handler")
                    linkUrl = null
                }
        )
    }
}

/**
 * Render a screen with button for user to start linking, once the button is clicked, it will make
 * request to demoapi and get a finverse link url
 */
@Composable
fun StartLink(setUrl: (url: String) -> Unit) {
    var isLoading by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val vm = UserState.current
    Column(
            Modifier.fillMaxSize().padding(32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isLoading) {
            CircularProgressIndicator()
        } else {
            Button(
                    onClick = {
                        isLoading = true
                        coroutineScope.launch {
                            getLinkUrl(vm.accessToken) {
                                if (it != null) {
                                    setUrl(it)
                                }
                                isLoading = false
                            }
                        }
                    }
            ) { Text("Link now") }
        }
    }
}

/** Component of the WebViewScreen, this host the WebView and also do the configuration job */
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun LinkingWebView(url: String, onSuccess: () -> Unit, onClose: () -> Unit) {
    var rememberWebViewProgress: Int by remember { mutableStateOf(-1) }
    Box {
        CustomWebView(
                modifier = Modifier.fillMaxSize(),
                url = url,
                onProgressChange = { progress -> rememberWebViewProgress = progress },
                initSettings = { settings ->
                    settings?.apply {
                        javaScriptEnabled = true
                        javaScriptCanOpenWindowsAutomatically = true
                    }
                },
                onBack = { webView ->
                    if (webView?.canGoBack() == true) {
                        webView.goBack()
                    }
                },
                // Handler passed to the custom webview,
                // it define which callback to call when a message is recived
                messageHandler = {
                    if (it == "close") {
                        onClose()
                    }
                    if (it == "success") {
                        onSuccess()
                    }
                }
        )
        LinearProgressIndicator(
                progress = rememberWebViewProgress * 1.0F / 100F,
                modifier =
                        Modifier.fillMaxWidth()
                                .height(if (rememberWebViewProgress == 100) 0.dp else 5.dp),
        )
    }
}

/**
 * JSBridge, class to handle anything come from the WebView JS, in this component, we defined a
 * recieveMessage, which send the string message to the handler in Main Thread and marking the
 * message as handled
 */
class JSBridge(private val webView: WebView, val handleMessage: (msg: String) -> Unit) {

    @JavascriptInterface
    fun receiveMessage(data: String): Boolean {
        Log.d("Data from JS", data)
        webView.post { handleMessage(data) }
        return true
    }
}

/** Custom Wrapper of android webview */
@Composable
fun CustomWebView(
        modifier: Modifier = Modifier,
        url: String,
        onBack: (webView: WebView?) -> Unit,
        onProgressChange: (progress: Int) -> Unit = {},
        initSettings: (webSettings: WebSettings?) -> Unit = {},
        onReceivedError: (error: WebResourceError?) -> Unit = {},
        messageHandler: (msg: String) -> Unit = {},
) {
    val webViewChromeClient =
            object : WebChromeClient() {
                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                    onProgressChange(newProgress)
                    super.onProgressChanged(view, newProgress)
                }
            }
    val webViewClient =
            object : WebViewClient() {
                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    super.onPageStarted(view, url, favicon)
                    onProgressChange(-1)
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    onProgressChange(100)
                    // Inject JS code to intercept postMessage's message
                    view?.loadUrl(
                            """
                    javascript:(function() {
                       window.parent.addEventListener ('message', function(event) {
                       Android.receiveMessage(event.data);});
                   })()
                   """
                    )
                }

                override fun shouldOverrideUrlLoading(
                        view: WebView?,
                        request: WebResourceRequest?
                ): Boolean {
                    if (null == request?.url) return false
                    val showOverrideUrl = request.url.toString()
                    try {
                        if (!showOverrideUrl.startsWith("http://") &&
                                        !showOverrideUrl.startsWith("https://")
                        ) {
                            Intent(Intent.ACTION_VIEW, Uri.parse(showOverrideUrl)).apply {
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                view?.context?.applicationContext?.startActivity(this)
                            }
                            return true
                        }
                    } catch (e: Exception) {
                        return true
                    }
                    return super.shouldOverrideUrlLoading(view, request)
                }

                override fun onReceivedError(
                        view: WebView?,
                        request: WebResourceRequest?,
                        error: WebResourceError?
                ) {
                    super.onReceivedError(view, request, error)
                    onReceivedError(error)
                }
            }
    var webView: WebView? = null
    val coroutineScope = rememberCoroutineScope()
    AndroidView(
            modifier = modifier,
            factory = { ctx ->
                WebView(ctx).apply {
                    this.webViewClient = webViewClient
                    this.webChromeClient = webViewChromeClient
                    initSettings(this.settings)
                    // Load the JS Bridge and the handler
                    this.addJavascriptInterface(JSBridge(this, messageHandler), "Android")
                    webView = this
                    loadUrl(url)
                }
            }
    )
    BackHandler { coroutineScope.launch { onBack(webView) } }
}
