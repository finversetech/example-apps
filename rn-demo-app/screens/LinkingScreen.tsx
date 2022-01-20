import Constants from "expo-constants";
import * as WebBrowser from "expo-web-browser";
import React, { useEffect, useState } from "react";
import { Button, Linking, StyleSheet, Switch } from "react-native";

import { Text, View } from "../components/Themed";
import { LinkStatus, useLinkState } from "../services/accessTokenContext";
import { getFinverseLink } from "../services/api";
import { RootStackScreenProps } from "../types";

export default function LinkScreen(props: RootStackScreenProps<"Link">) {
  const { token, status } = useLinkState();
  const { navigation } = props;
  const [isWebView, setIsWebView] = useState(true);
  const toggleSwitch = () => setIsWebView((previousState) => !previousState);

  function handleWebViewPress() {
    navigation.navigate("FinverseLink");
  }

  async function handleLinkPress(token: string) {
    addLinkingListener();
    const link = await getFinverseLink(token);
    const result = await WebBrowser.openAuthSessionAsync(
      link,
      Constants.linkingUri
    );
    console.log(result);
    if (Constants.platform?.ios) {
      removeLinkingListener();
    }
  }

  useEffect(() => {
    if (status === LinkStatus.success) {
      navigation.navigate("Root");
    }
  }, [status]);

  return (
    <View style={styles.container}>
      <Text style={styles.title}>Link Now</Text>
      <View
        style={styles.separator}
        lightColor="#eee"
        darkColor="rgba(255,255,255,0.1)"
      />
      <View>
        <View style={styles.textContainer}>
          <Text lightColor="rgba(0,0,0,0.8)" darkColor="rgba(255,255,255,0.8)">
            Use WebView?
          </Text>
          <Switch onValueChange={toggleSwitch} value={isWebView} />
        </View>
        <View style={styles.textContainer}>
          <Button
            onPress={() =>
              isWebView ? handleWebViewPress() : handleLinkPress(token)
            }
            title="Link"
          />
        </View>
      </View>
    </View>
  );
}

function addLinkingListener() {
  Linking.addEventListener("url", (e) => {
    if (Constants.platform?.ios) {
      WebBrowser.dismissBrowser();
    } else {
      removeLinkingListener();
    }
    console.log(e);
  });
}

function removeLinkingListener() {
  Linking.removeEventListener("url", (e) => console.log(e));
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: "center",
    justifyContent: "center",
  },
  title: {
    fontSize: 20,
    fontWeight: "bold",
  },
  separator: {
    marginVertical: 30,
    height: 1,
    width: "80%",
  },
  textContainer: {
    marginTop: 15,
    marginHorizontal: 20,
    alignItems: "center",
  },
});
