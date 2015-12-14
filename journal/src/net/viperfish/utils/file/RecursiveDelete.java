package net.viperfish.utils.file;

import java.io.File;

public class RecursiveDelete {

	public void recursiveRm(File dir) {
		if (dir.isDirectory()) {
			for (File i : dir.listFiles()) {
				recursiveRm(i);
			}
		}
		dir.delete();
	}

}
