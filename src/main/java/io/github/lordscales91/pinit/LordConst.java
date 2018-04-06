package io.github.lordscales91.pinit;

import java.util.Arrays;
import java.util.List;

/**
 * A constants container with a fancy name to avoid name clashes
 */
public class LordConst {
	public static final String IMG_URL = "img.url";
	public static final String PIN_NOTE = "pin.note";
	public static final String PIN_LINK = "pin.link";
	public static final String BOARD_NAME = "board.name";
	public static final String BOARD_ID = "board.id";
	public static final String IMG_PATH = "img.path";
	
	/**
	 * Unofficial list of domains blacklisted by Pinterest. There is no public official list
	 */
	public static final List<String> BLACKLISTED_DOMAINS  = Arrays.asList(new String[]{
			"danbooru.donmai.us"
	});
	
	/**
	 * Some domains whilst they are not blacklisted, due to some reason such as a restricted access
	 * policy can't be pinned since the image can't be extracted by Pinterest. 
	 */
	public static final List<String> FAULTY_DOMAINS = Arrays.asList(new String[]{
			"yande.re"
	});
}
