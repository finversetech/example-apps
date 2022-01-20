# Example Expo App

This is the repository of a expo react native application integrated with finverse-link. This application is tested in IOS and Android. Expo web is not supported.

# Get started

> Make sure your machine has installed all requirement to run Expo in Managed mode

1. Install packages `npm install`
2. Run bundler `npm run start`
3. Run the demo app in Expo Go on your devices

# Integrating with Finverse Link

Currently finverse link only support WebView on mobile platform and informations are communicated by `windows.postMessage`. It will emit messages depends on the linking result;

1. Success, it will emit a `success` message.
2. Failure, it will emit a `failure` message.

To receive the message, you need to hoist the message into your app. The implementation may vary depending on your WebView driver. In this app, we use library `react-native-webview` as our WebView component and we inject a javascript function to hoist it.

```javascript
// screens/FinverseLinkModal.tsx

// Function to inject
const injectedJavaScript = `(function() {
  window.postMessage = function(data) {
    window.ReactNativeWebView.postMessage(data);
  };
})()`;

// Handler
function handleMessage(event: WebViewMessageEvent) {
  if (event.nativeEvent.data) {
    console.log(event.nativeEvent.data);
  }
}

// Pass to component
<WebView
  source={{ uri: linkUrl }}
  onMessage={handleMessage}
  injectedJavaScript={injectedJavaScript}
/>;
```
