package fr.oceaneconsulting.dashboard.connector.redmine;

import java.io.IOException;
import java.time.ZonedDateTime;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import fr.oceaneconsulting.dashboard.connector.redmine.model.RedmineTreatment;
import fr.oceaneconsulting.dashboard.connector.redmine.model.enums.RedmineTreatmentStatus;

public class RedmineTreatmentDeserializer extends StdDeserializer<RedmineTreatment>{

	private static final long serialVersionUID = 839201321959862178L;

	public RedmineTreatmentDeserializer() {
		this(null);
	}

	protected RedmineTreatmentDeserializer(Class<?> vc) {
		super(vc);
	}

	@Override
	public RedmineTreatment deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
		JsonNode node = p.getCodec().readTree(p);
		RedmineTreatment treatment = new RedmineTreatment();
		treatment.setId(node.get("id").asInt());
		treatment.setCreatedOn(ZonedDateTime.parse(node.get("created_on").asText()));

		if (node.has("details")) {
			if (node.get("details").isArray()) {
				for (JsonNode jsonNode : node.get("details")) {
					if (jsonNode.has("name")) {
						if (jsonNode.get("name").asText().equals("status_id")) {
							treatment.setStatus(RedmineTreatmentStatus.valueOfId(jsonNode.get("new_value").asInt()));
							return treatment;
						}
					}
				}
			}
		}

		return null;
	}
}
