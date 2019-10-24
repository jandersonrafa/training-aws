import axios from "axios";

const api = axios.create({
    baseURL: "http://localhost:2010/api"
});

export default api;