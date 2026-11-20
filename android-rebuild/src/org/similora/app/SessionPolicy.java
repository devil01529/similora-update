package org.similora.app;

final class SessionPolicy {
    static boolean hasIdentity(java.net.HttpURLConnection connection) throws java.io.IOException {
        int status = connection.getResponseCode();
        if (status == 401 || status == 403) return false;
        if (status != 200) throw new java.io.IOException("Session service unavailable");
        return true;
    }

    static boolean accepts(String id, String role, String status) {
        return id != null && id.matches("[a-f0-9]{32}") && "approved".equals(status)
                && ("member".equals(role) || "owner".equals(role));
    }
}
