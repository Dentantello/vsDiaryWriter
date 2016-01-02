package net.viperfish.journal.secureAlgs;

import org.bouncycastle.crypto.Digest;

import net.viperfish.journal.secureProvider.AlgorithmSpec;
import net.viperfish.journal.secureProvider.Digester;


public class BCDigester implements Digester {

	private String mode;
	
	public BCDigester() {
		mode = new String("SHA512");
	}
	
	@Override
	public String getMode() {
		return this.mode;
	}

	@Override
	public void setMode(String mode) {
		this.mode = mode;

	}

	
	@Override
	public byte[] digest(byte[] text) {
		Digest dig = AlgorithmSpec.getDigester(mode);
		byte[] data = new byte[dig.getDigestSize()];
		for(byte i : text) {
			dig.update(i);
		}
		dig.doFinal(data, 0);
		return data;
	}

}
