/*package br.edu.ufcg.ccc.leda.submission.util;

import java.util.Collections;

import com.fasterxml.jackson.core.JsonFactory;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;

public class Auth {

	private final String CLIENT_ID = "93476930298-p85cf2038thmgh4dljbmb1ujmsppp5t5.apps.googleusercontent.com";

	HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
	JacksonFactory jsonFactory = JacksonFactory.getDefaultInstance();

	GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(httpTransport, jsonFactory)
			.setAudience(Collections.singletonList(CLIENT_ID)).build();

	// (Receive idTokenString by HTTPS POST)

	GoogleIdToken idToken = verifier.verify("idTokenString") 

	if(idToken != null) {
		Payload payload = idToken.getPayload();

		// Print user identifier
		String userId = payload.getSubject();
		System.out.println("User ID: " + userId);

		// Get profile information from payload
		String email = payload.getEmail();
		boolean emailVerified = Boolean.valueOf(payload.getEmailVerified());
		String name = (String) payload.get("name");
		String pictureUrl = (String) payload.get("picture");
		String locale = (String) payload.get("locale");
		String familyName = (String) payload.get("family_name");
		String givenName = (String) payload.get("given_name");

		// Use or store profile information
		// ...

	} else {
		System.out.println("Invalid ID token.");
	}
}*/