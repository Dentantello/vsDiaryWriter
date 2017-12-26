package net.viperfish.journal2.crypt;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import net.viperfish.framework.compression.Compressor;
import net.viperfish.framework.compression.Compressors;
import net.viperfish.journal2.core.CryptoInfo;
import net.viperfish.journal2.core.CryptoInfoGenerator;
import net.viperfish.journal2.core.JournalConfiguration;
import net.viperfish.journal2.core.Processor;
import net.viperfish.journal2.error.CipherException;
import net.viperfish.journal2.error.CompromisedDataException;

public class CompressionProccessor implements Processor {

	public static final String CONFIG_COMPRESSION = "compressor.algorithm";

	private Compressor compressor;

	@Override
	public Map<String, byte[]> doProccess(Map<String, byte[]> data, CryptoInfo info) throws CipherException {
		try {
			compressor = Compressors.getCompressor(info.getAlgorithm());
		} catch (NoSuchAlgorithmException e) {
			CipherException e1 = new CipherException(e);
			throw e1;
		}

		Map<String, byte[]> result = new HashMap<>();
		result.put("content", compressor.compress(data.get("content")));
		return result;
	}

	@Override
	public Map<String, byte[]> undoProccess(Map<String, byte[]> data, CryptoInfo info)
			throws CipherException, CompromisedDataException {
		try {
			compressor = Compressors.getCompressor(info.getAlgorithm());
		} catch (NoSuchAlgorithmException e) {
			CipherException e1 = new CipherException(e);
			throw e1;
		}

		Map<String, byte[]> result = new HashMap<>();
		result.put("content", compressor.deflate(data.get("content")));
		return result;
	}

	@Override
	public String getId() {
		return "compression";
	}

	@Override
	public CryptoInfoGenerator generator() {
		return new CryptoInfoGenerator() {

			@Override
			public void generate(CryptoInfo info) {
				String algorithm = JournalConfiguration.getString(CONFIG_COMPRESSION, "GZ");
				info.setAlgorithm(algorithm);
			}
		};
	}

}
