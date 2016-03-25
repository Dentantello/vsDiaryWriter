package net.viperfish.utils.compression;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.compress.compressors.xz.XZCompressorInputStream;
import org.apache.commons.compress.compressors.xz.XZCompressorOutputStream;

/**
 * compressor with XZ algorithm
 * 
 * @author sdai
 *
 */
class XZCompressor extends Compressor {

	@Override
	protected OutputStream createOutputStream(ByteArrayOutputStream out) throws IOException {
		return new XZCompressorOutputStream(out);
	}

	@Override
	protected InputStream createInputStream(ByteArrayInputStream in) throws IOException {
		return new XZCompressorInputStream(in);
	}

}
