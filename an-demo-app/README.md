# Example Android App

This is the repository of a native application integrated with finverse-link. This application is tested in Android 10.

# Get started

> Make sure your machine has installed all requirement to run Android development

1. Open android studio and start building
2. Run the application either with an emulator or android phone

# Integrating with Finverse Link

Currently finverse link only support WebView on mobile platform and information are communicated by `windows.postMessage`. It will emit messages depends on the linking result;

1. Success, it will emit a `success` string message.
2. Failure, it will emit a `close` string message.

To receive the message, you need to hoist the message into your app. The implementation may vary depending on your WebView driver. In this app, we use android API's [WebView driver](https://developer.chrome.com/docs/multidevice/webview/) as our WebView component and we inject a javascript function to hoist web message to native side.

```kotlin
        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
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
```

```kotlin
// Javascript message bridge
class JSBridge(private val webView: WebView, val handleMessage: (msg: String) -> Unit) {

    @JavascriptInterface
    fun receiveMessage(data: String): Boolean {
        Log.d("Data from JS", data)
        // Send event from Webview thread to main thread
        webView.post {
            handleMessage(data)
        }
        return true
    }
}
```

```kotlin
// Webview initiator
WebView(ctx).apply {
    this.webViewClient = webViewClient
    this.webChromeClient = webViewChromeClient
    initSettings(this.settings)
    this.addJavascriptInterface(JSBridge(this, messageHandler), "Android")
    webView = this
    loadUrl(url)
}
```

```kotlin
// Logic of the actual handler
if (it == "close") {
    onClose()
}
if (it == "success") {
    onSuccess()
}
```
