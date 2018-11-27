package br.edu.ufcg.ccc.leda.submission.util;

import java.util.List;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;

public class Auth {

    private final Logger LOGGER = Logger.getLogger(Auth.class.getName());

    private final static String CLIENT_ID1 = "93476930298-dcdjdcu4uk9kb9892isecsl80go05mlo.apps.googleusercontent.com";
    private final static String CLIENT_ID2 = "93476930298-j8k9746bc7kiqk82npbs0hkiillvat1a.apps.googleusercontent.com";
    private final static String CLIENT_ID3 = "93476930298-grmvro192611ftfnhbgs7ll3ivpdqgip.apps.googleusercontent.com";

    private Payload getPayload(List<String> ClientIds, String idTokenString) {

        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), JacksonFactory.getDefaultInstance())
                .setAudience(ClientIds).build();

        GoogleIdToken idToken;

        try {
            System.out.println(idTokenString);
            idToken = verifier.verify(idTokenString);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            return null;
        }
        if (idToken != null) {
            return idToken.getPayload();
        } else {
            LOGGER.log(Level.WARNING, "Invalid ID token.");
            return null;
        }
    }

/*    public static void main(String args[]) {
        Auth a = new Auth();
        String idTokenString = "Id Token received by POST";
        Payload p = a.getPayload(Arrays.asList(CLIENT_ID1, CLIENT_ID2, CLIENT_ID3), idTokenString);
        System.out.println(Boolean.valueOf(p.getEmailVerified()));
    } For Testing */
}