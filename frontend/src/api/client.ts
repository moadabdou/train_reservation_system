export const API_BASE_URL = process.env.REACT_APP_API_BASE_URL ?? "http://localhost:8080";

type HTTPOptions = RequestInit & {
  headers?: Record<string, string>;
};

export async function getJSON<T>(path: string, options: HTTPOptions = {}): Promise<T> {
  const url = path.startsWith("http") ? path : `${API_BASE_URL}${path}`;
  const resp = await fetch(url, {
    ...options,
    headers: {
      Accept: "application/json",
      ...(options.headers ?? {}),
    },
  });
  if (!resp.ok) {
    const text = await resp.text().catch(() => "");
    throw new Error(text || `Request failed with ${resp.status}`);
  }
  return resp.json() as Promise<T>;
}
