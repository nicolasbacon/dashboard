package fr.oceaneconsulting.dashboard.connector.mantis;

import java.io.IOException;
import java.time.ZonedDateTime;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import fr.oceaneconsulting.dashboard.connector.mantis.model.MantisTreatment;
import fr.oceaneconsulting.dashboard.connector.mantis.model.enums.MantisTreatmentStatus;

public class MantisTreatmentDeserializer extends StdDeserializer<MantisTreatment>{

	private static final long serialVersionUID = -4294400175011748459L;

	public MantisTreatmentDeserializer() {
		this(null);
	}

	protected MantisTreatmentDeserializer(Class<?> vc) {
		super(vc);
	}

	@Override
	public MantisTreatment deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
		JsonNode node = p.getCodec().readTree(p);
		MantisTreatment treatment = new MantisTreatment();
		treatment.setCreatedAt(ZonedDateTime.parse(node.get("created_at").asText()));
		// If the type ID is 1 then it is the creation of the issue
		if( node.get("type").get("id").asInt() == 1 ) {
			treatment.setStatus(MantisTreatmentStatus.NOUVEAU);
			return treatment;
			// Else if 0 then fields have been modified
		} else if (node.get("type").get("id").asInt() == 0) {
			// If the name of the field is status then the state of the issue have change
			if (node.get("field").get("name").asText().equals("status")) {
				// We read the new value to know the new state
				treatment.setStatus(MantisTreatmentStatus.valueOfId(node.get("new_value").get("id").asInt()));
				return treatment;
			}
		}
		return null;
	}

}
