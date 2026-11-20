package org.similora.app;

public class SessionPolicyCheck {
    public static void main(String[] args) throws Exception {
        for (final int code : new int[]{200, 401, 403, 302, 500, -1}) {
            java.net.HttpURLConnection response = new java.net.HttpURLConnection(null) {
                public int getResponseCode() throws java.io.IOException {
                    if (code == -1) throw new java.io.IOException("Synthetic offline fixture");
                    return code;
                }
                public void connect() {}
                public void disconnect() {}
                public boolean usingProxy() { return false; }
            };
            try {
                boolean hasIdentity = SessionPolicy.hasIdentity(response);
                if (hasIdentity != (code == 200) || (code != 200 && code != 401 && code != 403))
                    throw new AssertionError("Unexpected HTTP decision");
            } catch (java.io.IOException unavailable) {
                if (code != -1 && code != 500 && code != 302) throw unavailable;
            }
        }
        String id = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
        for (String role : new String[]{"member", "owner", "", "administrator"}) {
            for (String status : new String[]{"pending", "approved", "rejected", "revoked", ""}) {
                boolean expected = status.equals("approved") && (role.equals("owner") || role.equals("member"));
                if (SessionPolicy.accepts(id, role, status) != expected) throw new AssertionError();
            }
        }
        if (SessionPolicy.accepts("", "owner", "approved")) throw new AssertionError();
        // Optional input from the other repository's in-memory Worker test.
        if (args.length == 4 && SessionPolicy.accepts(args[0], args[1], args[2]) != Boolean.parseBoolean(args[3]))
            throw new AssertionError("Worker decision differs from Android policy");
    }
}
