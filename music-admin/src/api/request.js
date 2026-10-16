import axios from "axios";
import store from "../store/index.js";

const request = axios.create({
  baseURL: import.meta.env.VITE_SERVER,
  timeout: 30000,
});

request.interceptors.request.use(
  (config) => {
    const user = store.state.user;
    const token = user?.token || sessionStorage.getItem("token") || localStorage.getItem("token");
    if (token) {
      config.headers.token = token;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

request.interceptors.response.use(
  (response) => {
    const res = response.data;
    if (res.success) {
      return res;
    } else {
      return Promise.reject(new Error(res.message || "Error"));
    }
  },
  (error) => {
    console.error("请求错误:", error);
    return Promise.reject(error);
  }
);

export default request;
