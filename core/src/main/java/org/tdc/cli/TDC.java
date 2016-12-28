package org.tdc.cli;

/**
 * 'Main' class for launching primary command-line interface.
 */
public class TDC {
	public static void main(String[] args) {
		CLIOperations ops = new CLIOperations(false); // non-admin mode
		ops.execute(args);
	}
}
