package net.viperfish.journal2.core;

import java.io.Serializable;
import java.util.Arrays;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "Journal_CryptoInfo")
public class CryptoInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5696541382533899082L;
	@DatabaseField(generatedId = true)
	private long id;

	@DatabaseField
	private long journal;
	@DatabaseField(columnName = "CryptoInfo_Key", dataType = DataType.BYTE_ARRAY)
	private byte[] key;
	@DatabaseField(columnName = "CryptoInfo_Nounce", dataType = DataType.BYTE_ARRAY)
	private byte[] nounce;
	@DatabaseField(columnName = "CryptoInfo_Algorithm")
	private String algorithm;
	@DatabaseField(columnName = "CryptoInfo_Mode")
	private String mode;
	@DatabaseField(columnName = "Key")
	private String mapKey;

	public CryptoInfo() {
		journal = -1;
		key = new byte[0];
		nounce = new byte[0];
		algorithm = new String();
		mode = new String();
	}

	public long getJournal() {
		return journal;
	}

	public void setJournal(long journal) {
		this.journal = journal;
	}

	public byte[] getKey() {
		return key;
	}

	public void setKey(byte[] key) {
		this.key = key;
	}

	public byte[] getNounce() {
		return nounce;
	}

	public void setNounce(byte[] nounce) {
		this.nounce = nounce;
	}

	public String getAlgorithm() {
		return algorithm;
	}

	public void setAlgorithm(String algorithm) {
		this.algorithm = algorithm;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public String getMapKey() {
		return mapKey;
	}

	public void setMapKey(String mapKey) {
		this.mapKey = mapKey;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((algorithm == null) ? 0 : algorithm.hashCode());
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + (int) (journal ^ (journal >>> 32));
		result = prime * result + Arrays.hashCode(key);
		result = prime * result + ((mapKey == null) ? 0 : mapKey.hashCode());
		result = prime * result + ((mode == null) ? 0 : mode.hashCode());
		result = prime * result + Arrays.hashCode(nounce);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CryptoInfo other = (CryptoInfo) obj;
		if (algorithm == null) {
			if (other.algorithm != null)
				return false;
		} else if (!algorithm.equals(other.algorithm))
			return false;
		if (id != other.id)
			return false;
		if (journal != other.journal)
			return false;
		if (!Arrays.equals(key, other.key))
			return false;
		if (mapKey == null) {
			if (other.mapKey != null)
				return false;
		} else if (!mapKey.equals(other.mapKey))
			return false;
		if (mode == null) {
			if (other.mode != null)
				return false;
		} else if (!mode.equals(other.mode))
			return false;
		if (!Arrays.equals(nounce, other.nounce))
			return false;
		return true;
	}

}
