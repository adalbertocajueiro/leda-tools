package br.edu.ufcg.leda.util;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.hc.client5.http.ClientProtocolException;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.support.ClassicRequestBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.edu.ufcg.leda.commons.user.Student;
import br.edu.ufcg.leda.commons.util.Semester;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

public class Util {
	public static final String AUTOR_MATRICULA = "autor.matricula";
	public static final String AUTOR_NOME = "autor.nome";

	private static final Logger logger = LogManager.getLogger(Util.class);

	public static String getCurrentSemester(String url)
			throws ClientProtocolException, IOException, URISyntaxException {
		String currentSemester = null;

		CloseableHttpClient client = HttpClientBuilder.create().build();

		try {
			ClassicHttpRequest httpGet = ClassicRequestBuilder.get(url).build();

			HttpClientResponseHandler<List<Semester>> handler = response -> {
				List<Semester> singleList = new ArrayList<Semester>();
				int statusCode = response.getCode(); // e.g., 400, 404, 500
				String reason = response.getReasonPhrase();

				// TODO fazer a logica de ver primeiro do status code.
				if (statusCode != 200) {
					logger.warn("HTTP STATUS CODE: " + statusCode);

					// 3. Extract the error payload from the body
					HttpEntity entity = response.getEntity();
					if (entity != null) {
						try {
							// Convert the entity stream into a readable String
							String errorBody = EntityUtils.toString(entity);
							throw new SenderException("Server responded with error: " + statusCode + " - " + reason
									+ ". Error body: " + errorBody);
						} catch (IOException e) {
							throw new SenderException("Failed to read error body", e);
						} finally {
							// Always ensure the entity is fully consumed or closed
							try {
								EntityUtils.consume(entity);
							} catch (IOException ignored) {

							}
						}
					} else {
						throw new SenderException(
								"Server response with no content for error status code: " + statusCode);
					}
				} else {
					HttpEntity entity = response.getEntity();
					if (entity != null) {
						ObjectMapper mapper = new ObjectMapper();
						// Read directly from the input stream of the response entity
						singleList = Arrays.asList(mapper.readValue(entity.getContent(), Semester[].class));
						EntityUtils.consume(entity);
					} else {
						throw new SenderException(
								"Server response with no content for success status code: " + statusCode);
					}
					// logger.debug("HTTP STATUS CODE: " + statusCode);
				}
				return singleList;
			};

			currentSemester = client.execute(httpGet, handler).stream()
					.filter(s -> s.isCurrent())
					.findFirst().get().getId();

		} finally {
			client.close();
		}
		return currentSemester;
	}

	public static Map<Integer, List<Student>> getAllStudents(String semestre, String url)
			throws ClientProtocolException, IOException, URISyntaxException {
		Map<Integer, List<Student>> students = new HashMap<Integer, List<Student>>();

		CloseableHttpClient client = HttpClientBuilder.create().build();
		try {
			ClassicHttpRequest httpGet = ClassicRequestBuilder.get(url).addParameter("semester", semestre).build();

			HttpClientResponseHandler<Map<Integer, List<Student>>> handler = response -> {
				Map<Integer, List<Student>> returnedMap = new HashMap<Integer, List<Student>>();
				int statusCode = response.getCode(); // e.g., 400, 404, 500
				String reason = response.getReasonPhrase();

				if (statusCode != 200) {
					logger.warn("HTTP STATUS CODE: " + statusCode);
					// 3. Extract the error payload from the body
					HttpEntity entity = response.getEntity();
					if (entity != null) {
						try {
							// Convert the entity stream into a readable String
							String errorBody = EntityUtils.toString(entity);
							throw new SenderException("Server responded with error: " + statusCode + " - " + reason
									+ ". Error body: " + errorBody);
						} catch (IOException e) {
							throw new SenderException("Failed to read error body", e);
						} finally {
							// Always ensure the entity is fully consumed or closed
							try {
								EntityUtils.consume(entity);
							} catch (IOException ignored) {

							}
						}
					} else {
						throw new SenderException(
								"Server response with no content for error status code: " + statusCode);
					}

				} else {
					HttpEntity entity = response.getEntity();
					if (entity != null) {
						ObjectMapper mapper = new ObjectMapper();
						// Read directly from the input stream of the response entity
						returnedMap = mapper.readValue(
								entity.getContent(),
								new TypeReference<Map<Integer, List<Student>>>() {
								});
						EntityUtils.consume(entity);
					} else {
						throw new SenderException(
								"Server response with no content for success status code: " + statusCode);
					}
				}
				return returnedMap;
			};
			students = client.execute(httpGet, handler);

		} finally {
			client.close();
		}
		return students;
	}

}
