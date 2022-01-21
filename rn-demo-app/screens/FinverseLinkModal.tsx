import React, { useEffect, useState } from "react";
import { WebView, WebViewMessageEvent } from "react-native-webview";

import { View } from "../components/Themed";
import { LinkStatus, useLinkState } from "../services/accessTokenContext";
import { getFinverseLink } from "../services/api";
import { RootStackScreenProps } from "../types";

const injectedJavaScript = `(function() {
  window.postMessage = function(data) {
    window.ReactNativeWebView.postMessage(data);
  };
})()`;

export default function FinverseLinkScreen({
  navigation,
}: RootStackScreenProps<"FinverseLink">) {
  const { token, setStatus } = useLinkState();
  const [linkUrl, setLinkUrl] = useState("");

  useEffect(() => {
    async function load() {
      const link = await getFinverseLink(token);
      setLinkUrl(link);
    }
    load();
  }, [token]);

  function handleMessage(event: WebViewMessageEvent) {
    if (event.nativeEvent.data) {
      if (event.nativeEvent.data === "success") {
        setStatus(LinkStatus.success);
      }
      navigation.navigate("Link");
    }
  }

  return (
    <View style={{ flex: 1 }}>
      {linkUrl ? (
        <WebView
          source={{ uri: linkUrl }}
          onMessage={handleMessage}
          injectedJavaScript={injectedJavaScript}
        />
      ) : null}
    </View>
  );
}
