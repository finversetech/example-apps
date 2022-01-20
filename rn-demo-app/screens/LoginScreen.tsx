import React, { useEffect } from "react";
import { Controller, useForm } from "react-hook-form";
import {
  Button,
  Keyboard,
  StyleSheet,
  TouchableWithoutFeedback,
} from "react-native";
import { Colors } from "react-native/Libraries/NewAppScreen";

import { Text, View, TextInput } from "../components/Themed";
import { useLinkState } from "../services/accessTokenContext";
import { login } from "../services/api";
import { RootStackScreenProps } from "../types";

interface LoginForm {
  username: string;
  password: string;
}

export default function LoginScreen({
  navigation,
}: RootStackScreenProps<"Login">) {
  const {
    control,
    handleSubmit,
    formState: { errors },
  } = useForm<LoginForm>();

  const { setToken, token } = useLinkState();

  const onSubmit = async (data: LoginForm) => {
    try {
      const accessToken = await login(data.username, data.password);
      setToken(accessToken);
    } catch (e) {
      console.log(e);
    }
  };

  useEffect(() => {
    if (token) {
      navigation.navigate("Link");
    }
  }, [token]);

  return (
    <TouchableWithoutFeedback
      style={{ flex: 1 }}
      onPress={Keyboard.dismiss}
      accessible={false}
    >
      <View style={styles.container}>
        <Text style={styles.title}>Finverse Demo App</Text>
        <View
          style={styles.separator}
          lightColor="#eee"
          darkColor="rgba(255,255,255,0.1)"
        />
        <View>
          <View style={styles.getStartedContainer}>
            <Text
              style={styles.getStartedText}
              lightColor="rgba(0,0,0,0.8)"
              darkColor="rgba(255,255,255,0.8)"
            >
              Please login to continue
            </Text>
            <Controller
              control={control}
              rules={{
                required: true,
              }}
              render={({ field: { onChange, onBlur, value } }) => (
                <TextInput
                  style={[styles.input, styles.getStartedText]}
                  placeholder="Username"
                  lightColor="rgba(0,0,0,0.8)"
                  darkColor="rgba(255,255,255,0.8)"
                  onBlur={onBlur}
                  onChangeText={onChange}
                  value={value}
                  autoCapitalize="none"
                />
              )}
              name="username"
            />
            {errors.username && (
              <Text style={styles.errorText} lightColor={Colors.light.tint}>
                This is required.
              </Text>
            )}
            <Controller
              control={control}
              rules={{
                required: true,
              }}
              render={({ field: { onChange, onBlur, value } }) => (
                <TextInput
                  style={[styles.input, styles.getStartedText]}
                  placeholder="Password"
                  lightColor="rgba(0,0,0,0.8)"
                  darkColor="rgba(255,255,255,0.8)"
                  onBlur={onBlur}
                  onChangeText={onChange}
                  value={value}
                  autoCapitalize="none"
                />
              )}
              name="password"
            />
            {errors.password && (
              <Text style={styles.errorText} lightColor={Colors.light.tint}>
                This is required.
              </Text>
            )}
          </View>

          <View style={styles.helpContainer}>
            <Button onPress={handleSubmit(onSubmit)} title="Login" />
          </View>
        </View>
      </View>
    </TouchableWithoutFeedback>
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
  getStartedContainer: {
    alignItems: "stretch",
    marginHorizontal: 50,
  },
  homeScreenFilename: {
    marginVertical: 7,
  },
  codeHighlightContainer: {
    borderRadius: 3,
    paddingHorizontal: 4,
  },
  getStartedText: {
    fontSize: 17,
    lineHeight: 24,
    textAlign: "center",
  },
  errorText: {
    fontSize: 10,
    lineHeight: 24,
    textAlign: "center",
  },
  helpContainer: {
    marginTop: 15,
    marginHorizontal: 20,
    alignItems: "center",
  },
  helpLinkText: {
    textAlign: "center",
  },
  input: {
    height: 50,
    margin: 12,
    borderWidth: 1,
    padding: 10,
  },
});
