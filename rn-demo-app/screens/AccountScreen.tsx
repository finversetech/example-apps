import React, { useCallback, useEffect, useState } from "react";
import { StyleSheet } from "react-native";

import { MonoText } from "../components/StyledText";
import { Text, View } from "../components/Themed";
import { useLinkState } from "../services/accessTokenContext";
import { getLoginIdentity } from "../services/api";
import { RootTabScreenProps } from "../types";

export default function AccountScreen(props: RootTabScreenProps<"Account">) {
  const { token } = useLinkState();
  const [liid, setLIID] = useState<Record<string, string>>();

  const fetchData = useCallback(
    async (token: string) => {
      const loginIdentity = await getLoginIdentity(token);
      const status = loginIdentity?.login_identity.status;
      const liid = loginIdentity?.login_identity.login_identity_id;
      return {
        status,
        liid,
      };
    },
    [token]
  );

  useEffect(() => {
    async function loadData() {
      const resp = await fetchData(token);
      setLIID(resp);
    }
    if (!liid && token) {
      loadData();
    }
  }, [liid, token]);

  return (
    <View style={styles.container}>
      <Text style={styles.title}>Account</Text>
      <View
        style={styles.separator}
        lightColor="#eee"
        darkColor="rgba(255,255,255,0.1)"
      />
      <View>
        <View style={styles.textContainer}>
          <Text lightColor="rgba(0,0,0,0.8)" darkColor="rgba(255,255,255,0.8)">
            LIID
          </Text>

          <View
            darkColor="rgba(255,255,255,0.05)"
            lightColor="rgba(0,0,0,0.05)"
          >
            <MonoText>{JSON.stringify(liid || "")}</MonoText>
          </View>
        </View>
      </View>
    </View>
  );
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
