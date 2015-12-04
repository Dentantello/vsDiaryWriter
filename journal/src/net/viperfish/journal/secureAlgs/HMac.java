package net.viperfish.journal.secureAlgs;

import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.params.KeyParameter;

public class HMac extends BCMacDigester {

	private Mac mac;
	private String currentMode;

	public HMac() {
		currentMode = "SHA512";
	}

	@Override
	protected Mac getMac(String mode) {
		if (!currentMode.equals(mode) || mac == null) {
			Digest dig = AlgorithmSpec.getDigester(mode);
			mac = new org.bouncycastle.crypto.macs.HMac(dig);
			currentMode = mode;
		}
		return mac;
	}

	@Override
	protected int getKeySize() {
		return 32;
	}

	@Override
	protected void initMac(byte[] key, byte[] iv) {
		getMac(currentMode).init(new KeyParameter(key));

	}

}
