import axios from "axios";

const api = axios.create({
    baseURL: "http://localhost:8085",
    withCredentials: true, // Crucial for sending and receiving secure cookies
});

api.interceptors.response.use(
    async (response) => {
        // If your token expired (assuming errorCode 2000 handles expired tokens)
        if (response.data?.errorCode === 2000 && !response.config._retry) {
            response.config._retry = true;

            try {
                // Call your refresh endpoint directly
                const refreshResponse = await axios.post(
                    "http://localhost:8085/api/refresh", 
                    {}, 
                    { withCredentials: true }
                );

                if (!refreshResponse.data?.success) {
                    window.location.href = "/";
                    return Promise.reject("refresh failed");
                }

                // Retry the original request now that the cookie is refreshed
                return api(response.config);
            } catch (refreshError) {
                window.location.href = "/";
                return Promise.reject(refreshError);
            }
        }

        return response;
    },
    (err) => {
        return Promise.reject(err);
    }
);

export default api;