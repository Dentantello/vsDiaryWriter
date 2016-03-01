package net.viperfish.journal.authProvider;

import java.io.File;

import net.viperfish.journal.framework.AuthenticationManager;
import net.viperfish.journal.framework.Provider;
import net.viperfish.utils.file.CommonFunctions;

public class ViperfishAuthProvider implements Provider<AuthenticationManager> {

	private HashAuthManager auth;
	private File dataDir;

	public ViperfishAuthProvider() {
		File homeDir = new File(System.getProperty("user.home"));
		File vDiaryDir = new File(homeDir, ".vsDiary");
		CommonFunctions.initDir(vDiaryDir);
		dataDir = new File(vDiaryDir, "secure");
		CommonFunctions.initDir(dataDir);
		auth = null;
	}

	private AuthenticationManager lazyLoadAuth() {
		if (auth == null) {
			auth = new HashAuthManager(dataDir);
		}
		return auth;
	}

	@Override
	public AuthenticationManager newInstance() {
		return new HashAuthManager(dataDir);
	}

	@Override
	public AuthenticationManager getInstance() {
		return lazyLoadAuth();
	}

	@Override
	public AuthenticationManager newInstance(String instance) {
		if (instance.equals("HashAuthentication")) {
			return new HashAuthManager(dataDir);
		}
		return null;
	}

	@Override
	public AuthenticationManager getInstance(String instance) {
		if (instance.equals("HashAuthentication")) {
			return lazyLoadAuth();
		}
		return null;
	}

	@Override
	public String[] getSupported() {
		return new String[] { "HashAuthentication" };
	}

	@Override
	public String getName() {
		return "viperfish";
	}

	@Override
	public void dispose() {

	}

	@Override
	public void setDefaultInstance(String instance) {

	}

	@Override
	public String getDefaultInstance() {
		return "HashAuthentication";
	}

	@Override
	public void delete() {
		CommonFunctions.delete(dataDir);

	}

}
