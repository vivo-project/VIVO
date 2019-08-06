/* $This file is distributed under the terms of the license in LICENSE$ */

package edu.cornell.mannlib.vitro.webapp.visualization.valueobjects;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Right now this is just acting as a hashmap but in future we would want to provide
 * more detailed info other than just what properties had what values. E.g. we
 * could parse properties (& its values) to look for what namespaces are used.
 *
 * @author cdtank
 */
@SuppressWarnings("serial")
public class GenericQueryMap extends HashMap<String, Set<String>> {

	public GenericQueryMap() {
		super();
	}

	public void addEntry(String property, String value) {

		Set<String> values;

		if (this.containsKey(property)) {

			values = this.get(property);
			values.add(value);

		} else {

			values = new HashSet<String>();
			values.add(value);
			this.put(property, values);

		}
	}

}
