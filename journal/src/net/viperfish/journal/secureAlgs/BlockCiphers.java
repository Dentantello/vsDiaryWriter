package net.viperfish.journal.secureAlgs;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.engines.BlowfishEngine;
import org.bouncycastle.crypto.engines.CAST5Engine;
import org.bouncycastle.crypto.engines.CAST6Engine;
import org.bouncycastle.crypto.engines.CamelliaEngine;
import org.bouncycastle.crypto.engines.DESEngine;
import org.bouncycastle.crypto.engines.DESedeEngine;
import org.bouncycastle.crypto.engines.GOST28147Engine;
import org.bouncycastle.crypto.engines.IDEAEngine;
import org.bouncycastle.crypto.engines.NoekeonEngine;
import org.bouncycastle.crypto.engines.RC2Engine;
import org.bouncycastle.crypto.engines.RC532Engine;
import org.bouncycastle.crypto.engines.RC6Engine;
import org.bouncycastle.crypto.engines.SEEDEngine;
import org.bouncycastle.crypto.engines.SM4Engine;
import org.bouncycastle.crypto.engines.SerpentEngine;
import org.bouncycastle.crypto.engines.Shacal2Engine;
import org.bouncycastle.crypto.engines.SkipjackEngine;
import org.bouncycastle.crypto.engines.TEAEngine;
import org.bouncycastle.crypto.engines.ThreefishEngine;
import org.bouncycastle.crypto.engines.TwofishEngine;
import org.bouncycastle.crypto.engines.XTEAEngine;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.modes.CFBBlockCipher;
import org.bouncycastle.crypto.modes.OFBBlockCipher;
import org.bouncycastle.crypto.modes.SICBlockCipher;
import org.bouncycastle.crypto.paddings.BlockCipherPadding;
import org.bouncycastle.crypto.paddings.ISO10126d2Padding;
import org.bouncycastle.crypto.paddings.ISO7816d4Padding;
import org.bouncycastle.crypto.paddings.PKCS7Padding;
import org.bouncycastle.crypto.paddings.TBCPadding;
import org.bouncycastle.crypto.paddings.X923Padding;
import org.bouncycastle.crypto.paddings.ZeroBytePadding;

import net.viperfish.journal.secureAlgs.BCBlockCipherEncryptor.BCBlockCipherBuilder;

/**
 * utils for blockcipher
 * 
 * @author sdai
 *
 */
public final class BlockCiphers {

	private BlockCiphers() {
		// TODO Auto-generated constructor stub
	}

	private static final int DES_KEY_SIZE = 64;
	private static final int AES_KEY_SIZE = 256;
	private static final int DESEDE_KEY_SIZE = 192;
	private static final int CAST5_KEY_SIZE = 128;
	private static final int CAST6_KEY_SIZE = 256;
	private static final int DEFAULT = 256;
	private static final int BLOWFISH_KEY_SIZE = 448;
	private static final int CAMELLIA_KEY_SIZE = 256;
	private static final int GOST28147_KEY_SIZE = 256;
	private static final int IDEA_KEY_SIZE = 128;
	private static final int NOEKEON_KEY_SIZE = 128;
	private static final int RC5_KEYSIZE = 128;
	private static final int RC6_KEY_SIZE = 256;
	private static final int RC2_KEY_SIZE = 512;
	private static final int SEED_KEY_SIZE = 128;
	private static final int SHACAL2_KEY_SIZE = 512;
	private static final int SERPENT_KEY_SIZE = 256;
	private static final int SKIPJACK_KEY_SIZE = 128;
	private static final int TEA_KEY_SIZE = 128;
	private static final int TWOFISH_KEY_SIZE = 256;
	private static final int XTEA_KEY_SIZE = 128;
	private static final int SM4_KEY_SIZE = 128;
	private static final int THREEFISH_KEY_SIZE = 512;

	private static Map<String, Class<? extends BlockCipher>> blockCipherEngines;
	private static Map<String, Class<? extends BlockCipher>> blockCipherMode;
	private static Map<String, Class<? extends BlockCipherPadding>> blockCipherPadding;
	private static Map<String, BlockCipherEncryptor> cache;
	private static Set<String> gcmCiphers;

	static {
		blockCipherEngines = new TreeMap<String, Class<? extends BlockCipher>>(String.CASE_INSENSITIVE_ORDER);
		blockCipherMode = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
		blockCipherPadding = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
		cache = new HashMap<>();
		gcmCiphers = new TreeSet<>();
		initBlockCipherEngines();
		initBlockCipherModes();
		initBlockCipherPaddings();
		initGCMCiphers();
	}

	private static void initGCMCiphers() {
		gcmCiphers.add("AES");
		gcmCiphers.add("Camellia");
		gcmCiphers.add("CAST6");
		gcmCiphers.add("Noekeon");
		gcmCiphers.add("RC6");
		gcmCiphers.add("SEED");
		gcmCiphers.add("Serpent");
		gcmCiphers.add("Twofish");
	}

	private static void initBlockCipherEngines() {
		blockCipherEngines.put("AES", AESEngine.class);
		blockCipherEngines.put("Blowfish", BlowfishEngine.class);
		blockCipherEngines.put("Camellia", CamelliaEngine.class);
		blockCipherEngines.put("CAST5", CAST5Engine.class);
		blockCipherEngines.put("CAST6", CAST6Engine.class);
		blockCipherEngines.put("DESede", DESedeEngine.class);
		blockCipherEngines.put("DES", DESEngine.class);
		blockCipherEngines.put("GOST28147", GOST28147Engine.class);
		blockCipherEngines.put("IDEA", IDEAEngine.class);
		blockCipherEngines.put("Noekeon", NoekeonEngine.class);
		blockCipherEngines.put("RC2", RC2Engine.class);
		blockCipherEngines.put("RC5", RC532Engine.class);
		blockCipherEngines.put("RC6", RC6Engine.class);
		blockCipherEngines.put("SEED", SEEDEngine.class);
		blockCipherEngines.put("Serpent", SerpentEngine.class);
		blockCipherEngines.put("Shacal2", Shacal2Engine.class);
		blockCipherEngines.put("Skipjack", SkipjackEngine.class);
		blockCipherEngines.put("SM4", SM4Engine.class);
		blockCipherEngines.put("TEA", TEAEngine.class);
		blockCipherEngines.put("Twofish", TwofishEngine.class);
		blockCipherEngines.put("XTEA", XTEAEngine.class);
		blockCipherEngines.put("Threefish", ThreefishEngine.class);
	}

	private static void initBlockCipherModes() {
		blockCipherMode.put("CBC", CBCBlockCipher.class);
		blockCipherMode.put("CFB", CFBBlockCipher.class);
		blockCipherMode.put("OFB", OFBBlockCipher.class);
		blockCipherMode.put("CTR", SICBlockCipher.class);
	}

	private static void initBlockCipherPaddings() {
		blockCipherPadding.put("ISO10126d2Padding", ISO10126d2Padding.class);
		blockCipherPadding.put("ISO7816d4Padding", ISO7816d4Padding.class);
		blockCipherPadding.put("PKCS7Padding", PKCS7Padding.class);
		blockCipherPadding.put("TBCPadding", TBCPadding.class);
		blockCipherPadding.put("X923Padding", X923Padding.class);
		blockCipherPadding.put("ZeroBytePadding", ZeroBytePadding.class);
	}

	/**
	 * get the selected key size, usually max, for an encryption algorithm
	 * 
	 * @param algorithm
	 *            the name of the algorithm, case insensitive
	 * @return the key size
	 */
	public static int getKeySize(String algorithm) {
		if (algorithm.compareToIgnoreCase("des") == 0) {
			return DES_KEY_SIZE;
		}
		if (algorithm.compareToIgnoreCase("desede") == 0 || algorithm.compareToIgnoreCase("tripledes") == 0) {
			return DESEDE_KEY_SIZE;
		}
		if (algorithm.compareToIgnoreCase("aes") == 0) {
			return AES_KEY_SIZE;
		}
		if (algorithm.compareToIgnoreCase("twofish") == 0) {
			return DEFAULT;
		}
		if (algorithm.compareToIgnoreCase("cast5") == 0) {
			return CAST5_KEY_SIZE;
		}
		if (algorithm.compareToIgnoreCase("cast6") == 0) {
			return CAST6_KEY_SIZE;
		}
		if (algorithm.equalsIgnoreCase("blowfish")) {
			return BLOWFISH_KEY_SIZE;
		}
		if (algorithm.equalsIgnoreCase("CAMELLIA")) {
			return CAMELLIA_KEY_SIZE;
		}
		if (algorithm.equalsIgnoreCase("GOST28147")) {
			return GOST28147_KEY_SIZE;
		}
		if (algorithm.equalsIgnoreCase("IDEA")) {
			return IDEA_KEY_SIZE;
		}
		if (algorithm.equalsIgnoreCase("NOEKEON")) {
			return NOEKEON_KEY_SIZE;
		}
		if (algorithm.equalsIgnoreCase("RC5")) {
			return RC5_KEYSIZE;
		}
		if (algorithm.equalsIgnoreCase("RC6")) {
			return RC6_KEY_SIZE;
		}
		if (algorithm.equalsIgnoreCase("SEED")) {
			return SEED_KEY_SIZE;
		}
		if (algorithm.equalsIgnoreCase("SHACAL2")) {
			return SHACAL2_KEY_SIZE;
		}

		if (algorithm.equalsIgnoreCase("SERPENT")) {
			return SERPENT_KEY_SIZE;
		}
		if (algorithm.equalsIgnoreCase("SKIPJACK")) {
			return SKIPJACK_KEY_SIZE;
		}
		if (algorithm.equalsIgnoreCase("TEA")) {
			return TEA_KEY_SIZE;
		}
		if (algorithm.equalsIgnoreCase("TWOFISH")) {
			return TWOFISH_KEY_SIZE;
		}
		if (algorithm.equalsIgnoreCase("XTEA")) {
			return XTEA_KEY_SIZE;
		}
		if (algorithm.equalsIgnoreCase("SM4")) {
			return SM4_KEY_SIZE;
		}
		if (algorithm.equalsIgnoreCase("RC2")) {
			return RC2_KEY_SIZE;
		}
		if (algorithm.equalsIgnoreCase("Threefish")) {
			return THREEFISH_KEY_SIZE;
		}
		if (algorithm.matches("\\w+(\\W|\\S)?\\d+")) {
			Matcher num = Pattern.compile("\\d+").matcher(algorithm);
			while (num.find()) {
				return Integer.parseInt(num.group());
			}
		}
		return DEFAULT;
	}

	/**
	 * gets a bouncy castle BlockCipher instance
	 * 
	 * @param alg
	 *            the name of the algorithm
	 * @return the engine
	 */
	public static BlockCipher getBlockCipherEngine(String alg) {
		try {
			BlockCipher result = null;
			if (alg.equalsIgnoreCase("Threefish")) {
				result = new ThreefishEngine(ThreefishEngine.BLOCKSIZE_512);
			} else {
				result = blockCipherEngines.get(alg).newInstance();
			}
			return result;
		} catch (InstantiationException |

				IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * gets an BlockCipher that is a combination of the Cipher Mode and Cipher
	 * Engine
	 * 
	 * @param engine
	 *            the engine to apply mode to
	 * @param mode
	 *            the name of the mode
	 * @return the wrapped instance
	 */
	static BlockCipher wrapBlockCipherMode(BlockCipher engine, String mode) {
		BlockCipher result = null;
		try {
			Class<? extends BlockCipher> modeClass = blockCipherMode.get(mode);
			Constructor<?>[] modeCtors = modeClass.getConstructors();
			Constructor<? extends BlockCipher> modeCtor;
			if (modeCtors[0].getParameterTypes().length == 1) {
				modeCtor = modeClass.getConstructor(BlockCipher.class);
			} else {
				modeCtor = modeClass.getConstructor(BlockCipher.class, int.class);
			}

			BlockCipher modedEngine;
			if (modeCtor.getParameterTypes().length == 1) {
				modedEngine = modeCtor.newInstance(engine);
			} else {
				modedEngine = modeCtor.newInstance(engine, engine.getBlockSize() * 8);
			}
			result = modedEngine;
		} catch (InstantiationException | IllegalAccessException | NoSuchMethodException | SecurityException
				| IllegalArgumentException | InvocationTargetException e) {
			e.fillInStackTrace();
			throw new RuntimeException(e);
		}
		return result;
	}

	/**
	 * get the padding for an blockcipher
	 * 
	 * @param paddingName
	 *            the name of the padding
	 * @return the padding
	 */
	static BlockCipherPadding getBlockCipherPadding(String paddingName) {
		BlockCipherPadding padding = null;
		try {
			padding = blockCipherPadding.get(paddingName).newInstance();
			return padding;
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * get usable block ciphers
	 * 
	 * @return names of usable block ciphers
	 */
	public static Set<String> getSupportedBlockCipher() {
		Set<String> result = new TreeSet<String>();
		for (Entry<String, Class<? extends BlockCipher>> iter : blockCipherEngines.entrySet()) {
			result.add(iter.getKey());
		}
		return result;
	}

	/**
	 * get usable block cipher modes
	 * 
	 * @return names of usable block cipher modes
	 */
	public static Set<String> getSupportedBlockCipherMode() {
		Set<String> result = new TreeSet<>();
		for (Entry<String, Class<? extends BlockCipher>> iter : blockCipherMode.entrySet()) {
			result.add(iter.getKey());
		}
		return result;
	}

	/**
	 * get usable block cipher paddings
	 * 
	 * @return names of usable block cipher paddings
	 */
	public static Set<String> getSupportedBlockCipherPadding() {
		Set<String> result = new TreeSet<>();
		for (Entry<String, Class<? extends BlockCipherPadding>> iter : blockCipherPadding.entrySet()) {
			result.add(iter.getKey());
		}
		return result;
	}

	/**
	 * get algorithms that has a 16 byte block size and thus usable for GMAC
	 * 
	 * @return names of algorithm usable with GMAC
	 */
	public static Set<String> getGmacAlgorithms() {
		return new TreeSet<>(gcmCiphers);
	}

	public static BlockCipherEncryptor getEncryptor(String cipher, String mode, String padding) {

		StringBuilder sb = new StringBuilder();
		sb.append(cipher).append("/").append(mode).append("/").append(padding);
		String type = sb.toString();

		BlockCipherEncryptor bc = cache.get(type);
		if (bc == null) {

			BlockCipher engine = getBlockCipherEngine(cipher);
			BlockCipher modeEngine = wrapBlockCipherMode(engine, mode);
			BlockCipherPadding pad = getBlockCipherPadding(padding);

			BCBlockCipherBuilder builder = new BCBlockCipherBuilder();
			builder.setBlockSize(engine.getBlockSize()).setKeySize(getKeySize(cipher)).setCipher(modeEngine)
					.setPadding(pad);
			bc = builder.build();
			cache.put(type, bc);
		}
		return bc;
	}

}
