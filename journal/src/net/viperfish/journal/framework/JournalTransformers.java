package net.viperfish.journal.framework;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * a provider manager for journal transformers
 * 
 * @author sdai
 *
 */
public enum JournalTransformers {
	INSTANCE;
	private Map<String, Provider<JournalTransformer>> secureProviders;
	private String defaultTransformerProvider;

	private JournalTransformers() {
		secureProviders = new HashMap<>();
		defaultTransformerProvider = "viperfish";
	}

	public void registerTransformerProvider(Provider<JournalTransformer> p) {
		secureProviders.put(p.getName(), p);
		if (p.getConfigPages() != null) {
			ConfigPages.registerConfig(p.getConfigPages());
		}
	}

	public String getDefaultTransformerProvider() {
		return defaultTransformerProvider;
	}

	public void setDefaultTransformerProvider(String defaultTransformerProvider) {
		this.defaultTransformerProvider = defaultTransformerProvider;
	}

	public JournalTransformer newTransformer() {
		return newTransformer(Configuration.getString(ConfigMapping.TRANSFORMER_COMPONENT));
	}

	public JournalTransformer newTransformer(String instance) {
		JournalTransformer t = secureProviders.get(defaultTransformerProvider).newInstance(instance);
		if (t != null) {
			return t;
		}
		for (Entry<String, Provider<JournalTransformer>> iter : secureProviders.entrySet()) {
			JournalTransformer tmp = iter.getValue().newInstance(instance);
			if (tmp != null) {
				return tmp;
			}
		}
		return null;
	}

	public JournalTransformer newTransformer(String provider, String instance) {
		return secureProviders.get(provider).newInstance(instance);
	}

	public JournalTransformer getTransformer() {
		return getTransformer(Configuration.getString(ConfigMapping.TRANSFORMER_COMPONENT));
	}

	public JournalTransformer getTransformer(String instance) {
		JournalTransformer t = secureProviders.get(defaultTransformerProvider).getInstance(instance);
		if (t != null) {
			return t;
		}
		for (Entry<String, Provider<JournalTransformer>> iter : secureProviders.entrySet()) {
			JournalTransformer tmp = iter.getValue().getInstance(instance);
			if (tmp != null) {
				return tmp;
			}
		}
		return null;
	}

	public JournalTransformer getTransformer(String provider, String instance) {
		return secureProviders.get(provider).getInstance(instance);
	}

	public void dispose() {
		for (Entry<String, Provider<JournalTransformer>> iter : secureProviders.entrySet()) {
			iter.getValue().dispose();
			System.err.println("disposed " + iter.getKey());
		}
		secureProviders.clear();
		System.err.println("disposed secure providers");
	}

	public Map<String, Provider<JournalTransformer>> getSecureProviders() {
		return secureProviders;
	}

	public void refreshAll() {
		for (Entry<String, Provider<JournalTransformer>> iter : secureProviders.entrySet()) {
			iter.getValue().refresh();
		}
		EntryDatabases.INSTANCE.refreshAdapter();
	}

}
