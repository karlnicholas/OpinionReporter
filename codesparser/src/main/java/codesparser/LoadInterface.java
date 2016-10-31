package codesparser;

import java.io.File;

public interface LoadInterface {
	public void loadCode(File codesDir, File xmlcodes, File index, File indextaxo) throws Exception;
	public void createXMLCodes(File codesDir, File loadPath) throws Exception;
}
