package be.sibelga.security;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

public class SimpleEndUserEncryptionTool {

	public static void main(String[] args) {
		
		if ((args != null)&&(args.length >= 1)) {
			
			try {
					
				String sOutputPath = new String();
				
				if (args.length == 1) {
					sOutputPath = (new File(args[0])).getAbsolutePath();
				} else {
					sOutputPath = (new File(args[0])).getParent() + File.separator + "New encrypted archive";
				}
				
				sOutputPath += " " + (new SimpleDateFormat("yyyyMMdd_HHmmss")).format(new Date());
				
				System.out.println("Creating new archive: '" + sOutputPath + ".encrypted.zip'");

				File of = new File(sOutputPath + ".encrypted.zip");

				ZipFile zofOutput = new ZipFile(of);
				ZipParameters params = new ZipParameters();
				params.setCompressionLevel(Zip4jConstants.COMP_STORE);
				params.setEncryptFiles(true);
				params.setEncryptionMethod(Zip4jConstants.ENC_METHOD_AES);
				params.setAesKeyStrength(Zip4jConstants.AES_STRENGTH_256);
				String sPasswd = RandomString.generate(32, 64);
				params.setPassword(sPasswd);
				
				try {

					for (int i=0; i<args.length; i++) {
						File f = new File(args[i]);
						if (f.isDirectory()) {
							zofOutput.addFolder(f, params);
						} else {
							zofOutput.addFile(f, params);
						}
					}

				} finally {
					zofOutput = null;
					BufferedWriter bw;
					try {
						bw = new BufferedWriter(new FileWriter(new File(sOutputPath + ".encrypted.key.txt")));
						bw.write(sPasswd);
						bw.close();
						System.out.println("Storing key in: '" + sOutputPath + ".encrypted.key.txt'");
					} catch (IOException e) {
						logError("Error: Impossible to create output key file");
					}
				}
					
			} catch (ZipException e) {
				e.printStackTrace();
				logError("Error: Impossible to create output encrypted file");
			}
			
		} else {
			logError("Error: Please provide at least one valid argument");
		}

	}
	
	private static void logError(String sMessage) {
		System.err.println(sMessage);
		System.exit(1);
	}

}