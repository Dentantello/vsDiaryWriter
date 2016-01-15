package test.java;

import java.io.File;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import net.viperfish.journal.framework.Configuration;
import net.viperfish.journal.framework.Journal;
import net.viperfish.journal.secureProvider.BlockCipherMacTransformer;

public class SecurityWrapperTest {
	private BlockCipherMacTransformer wrapper;

	private void setupConfig() {
		Configuration.setProperty(BlockCipherMacTransformer.ENCRYPTION_ALG_NAME, "AES");
		Configuration.setProperty(BlockCipherMacTransformer.ENCRYPTION_MODE, "CFB");
		Configuration.setProperty(BlockCipherMacTransformer.ENCRYPTION_PADDING, "PKCS7PADDING");
		Configuration.setProperty(BlockCipherMacTransformer.MAC_ALGORITHM, "MD5");
		Configuration.setProperty(BlockCipherMacTransformer.MAC_TYPE, "HMAC");
		Configuration.setProperty(BlockCipherMacTransformer.KDF_HASH, "SHA256");
	}

	public SecurityWrapperTest() {
		setupConfig();
		File testDir = new File("test");
		if (!testDir.exists()) {
			testDir.mkdir();
		}
		try {
			wrapper = new BlockCipherMacTransformer(new File(testDir.getCanonicalPath() + "/salt"));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		wrapper.setPassword("password");

	}

	@Test
	public void testEncrypt() {
		Journal j = new Journal();
		j.setSubject("test get");
		j.setContent("test get content");
		Journal result = wrapper.encryptJournal(j);
		result = wrapper.decryptJournal(result);
		String plainContent = result.getContent();
		Assert.assertEquals("test get content", plainContent);
		Assert.assertEquals("test get", result.getSubject());
	}

}
