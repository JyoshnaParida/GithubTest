package tfWebControllers;

import org.apache.commons.lang3.RandomStringUtils;

public class RestUtils extends Tfwebflow{
	private static final RandomStringUtils RandomIntUtils = null;
	public static String generateRandomName() {
        String generatedString = RandomStringUtils.randomAlphabetic(8); // Generate an 8-character random string
        return "ANIL" + generatedString;
    }
}