package com.psl.util;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class EncryptionService {
	private static Options options = new Options();
	static{
		options.addOption("epwd", true, "password to encrypt");
		
	}

	public static void main(String[] args) throws ParseException {
		CommandLineParser parser = new DefaultParser();
		CommandLine cmd = parser.parse( options, args);
		
		if(cmd.hasOption("epwd")){
			String pwd = cmd.getOptionValue("epwd");
			System.out.println("Encrpted password = " + EncryptionUtils.encryptPassword(pwd));
		}
		
		
	}

}
