# Example Expo App

This is the repository of an IOS native application integrated with finverse-link. This application is tested in IOS 16.1.

# Get started

> Make sure your machine has installed Xcode and the IOS simulator.

# Integrating with Finverse Link

Currently finverse link only support WebView on mobile platform and informations are communicated by `windows.postMessage`. It will emit messages depends on the linking result;

1. Success, it will emit a `success` message.
2. Failure, it will emit a `close` message.

To receive the message, you need to hoist the message into your app. The implementation may vary depending on your Swift version. In this app, we use `WKWebView` as our WebView component and we inject a javascript function to hoist it.

```swift
//ios-demo-app/ios-demo-app/ContentView.swift

// Function to inject
func JsInjection() {
    let javascript: String = """
                            window.addEventListener('message', function(e) {
                                    window.webkit.messageHandlers.bridge.postMessage(JSON.stringify(e.data));
                                });
                            """
    webView.evaluateJavaScript(javascript) { (key, err) in
        if let err = err{
            print(err.localizedDescription)
        }else{
            print("JS injected!")
        }
    }
}
```

In your `WebView` struct `makeUIView` function, you need to add a `WKUserContentController` with the name matching the one you defined in the javascript. It can be any name. In this example, it's `bridge`.

```swift
//ios-demo-app/ios-demo-app/ContentView.swift
    func makeUIView(context: Context) -> WKWebView {
        let userContentController = WKUserContentController()
        userContentController.add(context.coordinator, name: "bridge")

        let webConfiguration = WKWebViewConfiguration()
        webConfiguration.userContentController = userContentController
        webConfiguration.defaultWebpagePreferences.allowsContentJavaScript = true

        webView = WKWebView(frame: .zero, configuration: webConfiguration)

        webView.navigationDelegate = context.coordinator

        return webView
    }
```

Once you have the javascript and `WKUserContentController` set up, you can add a `userContentController` function in your `Coordinator` class to receive the message as shown below.
The message for the `cross` button on top right on Finverse-link, `Continue` and `Exit` buttons at the result page in Finverse-link will post `"close"` or `"success"`(if succeeded) message.
Then you can perform action to close the WebView on receiving these message.

```swift
//ios-demo-app/ios-demo-app/ContentView.swift
    func userContentController(_ userContentController: WKUserContentController, didReceive message: WKScriptMessage) {
        let data = message.body as? String
        if data == "\"close\"" || data == "\"success\"" {
            showWebViewLocal.wrappedValue.toggle()
        }
    }
```
