package net.viperfish.journal.secure;

public interface Digester {

	public abstract String getMode();

	public abstract void setMode(String mode);

	public abstract byte[] digest(byte[] text);

}