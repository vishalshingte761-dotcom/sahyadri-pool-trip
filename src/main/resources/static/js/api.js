// =====================================================
// Sahyadri Pool & Trip — API client
// Talks to the Spring Boot backend (default: localhost:8080)
// =====================================================

const API_BASE_URL = window.SPT_API_BASE_URL || "http://localhost:8080";

const API = {

  async request(endpoint, options = {}) {

    const token = localStorage.getItem("spt_token");

    const config = {
      method: options.method || "GET",
      headers: {
        "Content-Type": "application/json",
        ...(options.headers || {})
      }
    };

    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }

    if (options.body !== undefined) {
      config.body = JSON.stringify(options.body);
    }

    let response;

    try {
      response = await fetch(`${API_BASE_URL}${endpoint}`, config);
    } catch (networkError) {
      console.error("Network error calling backend:", networkError);
      throw new Error(
        "Can't reach the backend. Is the Spring Boot server running on " +
        API_BASE_URL + "?"
      );
    }

    let data = null;

    try {
      data = await response.json();
    } catch (error) {
      data = null;
    }

    if (response.status === 401) {
      localStorage.removeItem("spt_token");
      localStorage.removeItem("spt_user");

      throw new Error(
        data?.message || "Session expired. Please login again."
      );
    }

    if (response.status === 403) {
      throw new Error(
        data?.message ||
        "Access denied. Your account role can't do this."
      );
    }

    if (!response.ok) {
      // Validation errors come back as { success:false, message, errors:{field: msg} }
      if (data?.errors) {
        const first = Object.values(data.errors)[0];
        throw new Error(first || data.message || `Request failed (${response.status})`);
      }
      throw new Error(
        data?.message || `Request failed (${response.status})`
      );
    }

    return data;
  },

  get(endpoint) {
    return this.request(endpoint, { method: "GET" });
  },

  post(endpoint, body) {
    return this.request(endpoint, { method: "POST", body });
  },

  put(endpoint, body) {
    return this.request(endpoint, { method: "PUT", body });
  },

  delete(endpoint) {
    return this.request(endpoint, { method: "DELETE" });
  },

  async multipart(endpoint, formData, method = "POST") {
    const token = localStorage.getItem("spt_token");
    const headers = {};
    if (token) headers.Authorization = `Bearer ${token}`;
    const response = await fetch(`${API_BASE_URL}${endpoint}`, { method, headers, body: formData });
    let data = null; try { data = await response.json(); } catch (_) {}
    if (response.status === 401) { localStorage.removeItem("spt_token"); localStorage.removeItem("spt_user"); throw new Error(data?.message || "Session expired. Please login again."); }
    if (!response.ok) throw new Error(data?.message || `Request failed (${response.status})`);
    return data;
  }
};

// Builds a query string, skipping empty/undefined/null values.
function qs(params) {
  const usp = new URLSearchParams();
  Object.entries(params || {}).forEach(([key, value]) => {
    if (value !== undefined && value !== null && value !== "") {
      usp.append(key, value);
    }
  });
  const s = usp.toString();
  return s ? `?${s}` : "";
}

async function testBackendConnection() {
  try {
    const stats = await API.get("/api/admin/stats");
    console.log("BACKEND CONNECTION OK", stats);
    return true;
  } catch (error) {
    console.warn("Backend not reachable yet:", error.message);
    return false;
  }
}
