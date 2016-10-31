package codesparser;

import java.util.Arrays;
import java.util.regex.Pattern;

public class FacetUtils {
	public static final char DELIMITER = '/';

	private static void hasDelimiter(String offender, char delimiter) {
		throw new IllegalArgumentException("delimiter character '" + delimiter
				+ "' (U+" + Integer.toHexString(delimiter)
				+ ") appears in path component \"" + offender + "\"");
	}

	public static String[] fromString(final String facetString) {
		return fromString(facetString, DELIMITER);
	}

	public static boolean facetMatch(final String fullFacet, final String partFacet) {
		String[] compp = partFacet.split(Pattern.quote(Character.toString(DELIMITER)));
		String[] compf = fullFacet.split(Pattern.quote(Character.toString(DELIMITER)));
		for (int i = 0, j=compp.length; i<j; ++i ) {
			if ( !compp[i].equals(compf[i])) return false;
		}
		return true;
	}

	public static String[] fromString(final String pathString, final char delimiter) {
		String[] comps = pathString.split(Pattern.quote(Character.toString(delimiter)));
		for (String comp : comps) {
			if (comp == null || comp.isEmpty()) {
				throw new IllegalArgumentException(
						"empty or null components not allowed: "
								+ Arrays.toString(comps));
			}
		}
		return comps;
	}

	public static String toString(String[] components) {
		return toString(components, DELIMITER);
	}

	public static String toString(String[] components, char delimiter) {

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < components.length; i++) {
			if (components[i].indexOf(delimiter) != -1) {
				hasDelimiter(components[i], delimiter);
			}
			sb.append(components[i]).append(delimiter);
		}
		sb.setLength(sb.length() - 1); // remove last delimiter
		return sb.toString();
	}

}
