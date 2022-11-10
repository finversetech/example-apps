//
//  ContentView.swift
//  ios-demo-app
//
//  Created by Anson Tang on 9/11/2022.
//


import SwiftUI
import WebKit

var webView: WKWebView = WKWebView()


struct ContentView: View {
    @State var showWebViewLocal = false
    // paste the generated finverse-link url here, or you can make it a variable to be imported from calling ContentView
    private let urlString: String = "finverse-link url gose here"
    
    var body: some View {
        VStack{
            // demo app with just a button to open Finverse-link
            Button("Open Webview", action: {
                showWebViewLocal = true
            })
            
            .fullScreenCover(isPresented: $showWebViewLocal) {
                // open Finverse-link
                WebView(showWebView: $showWebViewLocal, url: URL(string: urlString)!)
            }
        }
    }
}

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

// WebView Struct
struct WebView: UIViewRepresentable {
    var showWebView: Binding<Bool>
    var url: URL
    
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
    
    func updateUIView(_ webView: WKWebView, context: Context) {
        let request = URLRequest(url: url)
        webView.load(request)
    }

    func makeCoordinator() -> Coordinator {
        return Coordinator(owner: self, show: showWebView)
    }
}

class Coordinator: NSObject, WKNavigationDelegate, WKScriptMessageHandler {
    var showWebViewLocal: Binding<Bool>
    func userContentController(_ userContentController: WKUserContentController, didReceive message: WKScriptMessage) {
        let data = message.body as? String
        if data == "\"close\"" || data == "\"success\"" {
            showWebViewLocal.wrappedValue.toggle()
        }
    }
    
    func webView(_ webView: WKWebView, didFinish navigation: WKNavigation!) {
        let _ = JsInjection()
    }
    
    private var owner: WebView
    init(owner: WebView, show: Binding<Bool>) {
        self.showWebViewLocal = show
        self.owner = owner
    }
}


struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
