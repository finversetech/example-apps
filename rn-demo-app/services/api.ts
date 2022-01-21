import axios from "axios";

const endpoint = "https://demo-api.dev2.finverse.net";

export async function login(
  username: string,
  password: string
): Promise<string> {
  const url = `${endpoint}/login`;
  const requestBody = { username, password };
  const resp = await axios.post(url, requestBody, {
    headers: { "content-type": "application/json" },
  });
  return resp.data.accessToken;
}

export async function getFinverseLink(token: string) {
  const url = `${endpoint}/link`;
  const resp = await axios.get(url, {
    headers: {
      "content-type": "application/json",
      Authorization: `Bearer ${token}`,
    },
  });
  return resp.data;
}

export async function getLoginIdentity(token: string) {
  const url = `${endpoint}/login-identity`;
  const resp = await axios.get(url, {
    headers: {
      "content-type": "application/json",
      Authorization: `Bearer ${token}`,
    },
  });

  if (Array.isArray(resp.data) && resp.data.length > 0) {
    return resp.data.slice(-1)[0];
  }
  return resp.data;
}

export async function getAccounts(token: string) {
  const url = `${endpoint}/accounts`;
  const resp = await axios.get(url, {
    headers: {
      "content-type": "application/json",
      Authorization: `Bearer ${token}`,
    },
  });

  if (Array.isArray(resp.data) && resp.data.length > 0) {
    return resp.data.slice(-1)[0];
  }
  return resp.data;
}
