const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;

export const apiFetch = (path, options = {}) => {
    return fetch(`${API_BASE_URL}${path}`, {
        ...options,
        headers: {
            'Content-Type': 'application/json',
            ...(options.headers || {})
        }
    }).then(res => res.json());
};