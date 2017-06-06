package com.theah64.frenemy.web.utils;


import com.theah64.frenemy.web.database.tables.Frenemies;
import com.theah64.frenemy.web.exceptions.RequestException;

/**
 * Created by shifar on 31/12/15.
 */
public class HeaderSecurity {

    public static final String KEY_AUTHORIZATION = "Authorization";
    private static final String REASON_API_KEY_MISSING = "API key is missing";
    private static final String REASON_INVALID_API_KEY = "Invalid API key";
    private final String authorization;
    private String frenemyId;

    public HeaderSecurity(final String authorization) throws RequestException {
        //Collecting header from passed request
        this.authorization = authorization;
        isAuthorized();
    }

    /**
     * Used to identify if passed API-KEY has a valid victim.
     */
    protected void isAuthorized() throws RequestException {

        if (this.authorization == null) {
            //No api key passed along with request
            throw new RequestException("Unauthorized access");
        }

        final Frenemies frenemies = Frenemies.getInstance();
        this.frenemyId = frenemies.get(Frenemies.COLUMN_API_KEY, this.authorization, Frenemies.COLUMN_ID, true);
        if (this.frenemyId == null) {
            throw new RequestException("No frenemy found with the api_key " + this.authorization);
        }

    }

    public String getFrenemyId() {
        return this.frenemyId;
    }

    public String getFailureReason() {
        return this.authorization == null ? REASON_API_KEY_MISSING : REASON_INVALID_API_KEY;
    }

    public String getAuthorization() {
        return authorization;
    }
}
