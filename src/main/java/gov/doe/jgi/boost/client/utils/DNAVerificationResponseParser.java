package gov.doe.jgi.boost.client.utils;

import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONObject;

public class DNAVerificationResponseParser {

	private JSONObject responseObject = null;

	public DNAVerificationResponseParser(JSONObject jsonResponse) {
		this.responseObject = jsonResponse;
	}

	public String dnaVeriOutputDoc() {
		String outputSequence = null;
		if (responseObject.has("provenenance") && responseObject.has("output-document")) {
			outputSequence = responseObject.getJSONObject("provenance").getString("output-document");
		}
		return outputSequence;
	}

	public JSONObject constraintsViolations() {
		JSONArray responseArray = responseObject.getJSONArray("response");
		JSONArray violationArray = responseArray.getJSONObject(0).getJSONArray("violations");

		JSONObject violationObject = null;
		if (violationArray.length() >= 0 && violationObject.has("sequence-violations")) {
			violationObject = violationArray.getJSONObject(0).getJSONObject("sequence-violations");
		} else {
			violationObject = null;
		}
		return violationObject;
	}

	public HashMap<String, String> verificationStatistics() {

		HashMap<String, String> veificationStatists = new HashMap<>();

		JSONArray staticsArray = null;
		if (responseObject.has("statistics")) {
			staticsArray = responseObject.getJSONArray("statistics");
		}

		if (null != staticsArray) {
			for (int index = 0; index < staticsArray.length(); index++) {
				JSONObject staticsObject = staticsArray.getJSONObject(index);
				String measurementName = staticsObject.getString("measurement-name");
				String measurementValue = staticsObject.getString("measurement-value");
				veificationStatists.put(measurementName, measurementValue);
			}
		}
		return veificationStatists;
	}
}