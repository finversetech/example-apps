import React, { createContext, ReactNode, useContext, useState } from "react";

export enum LinkStatus {
  unlinked = 0,
  success = 1,
  failed = 2,
}

export const LinkStateContext = createContext({
  token: "",
  status: LinkStatus.unlinked,
  setToken: (s: string) => {},
  setStatus: (s: LinkStatus) => {},
});

export const LinkStateProvider = ({ children }: { children: ReactNode }) => {
  const [token, setToken] = useState<string>("");
  const [status, setStatus] = useState<number>(LinkStatus.unlinked);

  return (
    <LinkStateContext.Provider value={{ token, status, setToken, setStatus }}>
      {children}
    </LinkStateContext.Provider>
  );
};

export const useLinkState = () => {
  return useContext(LinkStateContext);
};
