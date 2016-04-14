package net.viperfish.journal.authProvider;

import java.io.File;
import java.io.IOException;

import net.viperfish.journal.framework.AuthTest;
import net.viperfish.journal.framework.AuthenticationManager;
import net.viperfish.utils.file.CommonFunctions;

public class SCryptTest extends AuthTest {

	@Override
	protected AuthenticationManager getAuth(File dataDir) {
		CommonFunctions.initDir(dataDir);
		File passwdFile = new File(dataDir, "passwd");
		try {
			CommonFunctions.initFile(passwdFile);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return new SCryptAuthManager(passwdFile);
	}

}
