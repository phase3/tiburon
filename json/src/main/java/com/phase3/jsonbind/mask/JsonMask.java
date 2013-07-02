package com.phase3.jsonbind.mask;

import com.fasterxml.jackson.databind.*;

import java.io.*;
import java.util.*;

public class JsonMask {

	Set<String> includeSet = new HashSet<String>();

	public JsonMask(String jsonMask) throws IOException {
		build(jsonMask);
	}

	private void build(String jsonMask) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		Map<String,Object> maskMap = mapper.readValue(jsonMask, Map.class);

		ArrayList<String> includes = (ArrayList<String>) maskMap.get("include");
		if (includes != null) {
			for(String s : includes) {
				String[] parts = s.split("\\.");
				String combined = "";
				for (int x=0;x<parts.length-1;x++) {
					String part = parts[x];
					combined += (combined.length()==0?"":".") + part;
					includeSet.add(combined);
				}
				includeSet.add(s);
			}
		}

	}
	public boolean isIncluded(String value) {
		return includeSet.contains(value);
	}
}
