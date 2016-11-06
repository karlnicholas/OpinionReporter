package op.services;

import java.io.File;
import java.util.ResourceBundle;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import opinions.parsers.CaseParserInterface;
import codesparser.*;

/**
 * Servlet implementation class Load4Web
 */

@Service
public class CodeInterfacesService {

	private static final String interfaces = "application";
	private static final String codesinterfaceKey = "codesparser.codesinterface";
	private static final String loadinterfaceKey = "codesparser.loadinterface";
	private static final String caseparserinterfaceKey = "opinions.caseparserinterface";	

	private CodesInterface codesInterface = null;
	private CaseParserInterface caseParserInterface = null;
	
	@PostConstruct
	protected CodeInterfacesService initialize() throws Exception {
		try {
			ResourceBundle rb = ResourceBundle.getBundle(interfaces);
			String iface = rb.getString(codesinterfaceKey);
			codesInterface = (CodesInterface) Class.forName(iface).newInstance();
			codesInterface.loadCodes();
			iface = rb.getString(caseparserinterfaceKey);
			caseParserInterface = (CaseParserInterface) Class.forName(iface).newInstance();
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			throw new IllegalStateException(e);
		}
		return this;
	}
	
	public CodesInterface getCodesInterface() {
		return codesInterface;
	}
	
	public CodeTitles[] getCodeTitles() {
		return codesInterface.getCodeTitles();
	}

	public LoadInterface getLoadInterface() throws Exception {		
		ResourceBundle rb = ResourceBundle.getBundle(interfaces);
		String iface = rb.getString(loadinterfaceKey);
		return (LoadInterface) Class.forName(iface).newInstance();
	}

	public CaseParserInterface getCaseParserInterface() {
		return caseParserInterface;
	}
	/*
	 * Reqired to run this to create xml files in the resources folder that describe the code hierarchy 
	 */
	public static void main(String... args) throws Exception {
		
		final class Run {
			public void run(CodeInterfacesService interfacesFactory) throws Exception {
				LoadInterface loader = interfacesFactory.getLoadInterface();

				// For gscalifornia
				File codesDir = new File("c:/users/karl/code");

				File xmlcodes = new File("c:/users/karl/op/op/src/main/resources/xmlcodes");
				
				loader.createXMLCodes(codesDir, xmlcodes );
			}
		}
		Run run = new Run();
		run.run(new CodeInterfacesService().initialize());
	}
    
}
