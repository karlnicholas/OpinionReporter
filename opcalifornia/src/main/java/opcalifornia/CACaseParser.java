package opcalifornia;

import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.*;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.*;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFFootnote;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

import opinions.model.courtcase.CourtCase;
import opinions.parsers.CaseParserInterface;
import opinions.parsers.ParserDocument;

public class CACaseParser implements CaseParserInterface {

	private static final Logger logger = Logger.getLogger(CACaseParser.class.getName());

	@Override
	public Reader getCaseList() throws Exception {
		DefaultHttpClient httpclient = new DefaultHttpClient();
		try {
			HttpGet httget = new HttpGet("http://www.courts.ca.gov/cms/opinions.htm?Courts=Y");
			HttpResponse response = httpclient.execute(httget);
			HttpEntity entity = response.getEntity();
			logger.info("HTTP Response: " + response.getStatusLine());
			// need to read into memory here because 
			// we are going to shut down the connection manager before leaving
            BufferedReader reader = new BufferedReader( new InputStreamReader(entity.getContent(), "UTF-8" ));
            CharArrayWriter writer = new CharArrayWriter(); 
        	char[] cbuf = new char[2^13];
        	int len;
        	while ( (len = reader.read(cbuf, 0, cbuf.length)) != -1 ) {
        		writer.write(cbuf, 0, len);
        	}
        	reader.close();
        	writer.close();
            return new BufferedReader( new CharArrayReader(writer.toCharArray()) );
		} finally {
			httpclient.getConnectionManager().shutdown();
		}
	}

/*	
	@Override
	public ParserDocument getCaseFile(CourtCase ccase, boolean debugCopy) throws Exception {
		DefaultHttpClient httpclient = new DefaultHttpClient();
		try {
			HttpGet httpget = new HttpGet("http://www.courts.ca.gov/opinions/documents/" + ccase.getName() + ".DOC");
			HttpResponse response = httpclient.execute(httpget);
			HttpEntity entity = response.getEntity();
			return new ParserDocument(new HWPFDocument(entity.getContent())); 
			// we are going to shut down the connection manager before leaving
		} finally {
			httpclient.getConnectionManager().shutdown();
		}
	}
*/
	@Override
	public ParserDocument getCaseFile(CourtCase ccase, boolean debugCopy) throws Exception {
		DefaultHttpClient httpclient = new DefaultHttpClient();
		ParserDocument parserDocument = new ParserDocument();			
		try {
	
			HttpGet httpget = new HttpGet("http://www.courts.ca.gov/opinions/documents/" + ccase.getName() + ccase.getExtension());
			HttpResponse response = httpclient.execute(httpget);
			HttpEntity entity = response.getEntity();
/*			
			// testing
			ByteArrayInputStream bais = convertInputStream(entity.getContent());
			saveCopyOfCase("c:/users/karl/op/op/cases", ccase.getName() + ccase.getExtension(), bais);
			bais.reset();
*/
			if ( ccase.getExtension().equalsIgnoreCase(".DOC")) {
				HWPFDocument document = new HWPFDocument(entity.getContent());
				WordExtractor extractor = new WordExtractor(document);
				parserDocument.paragraphs = Arrays.asList(extractor.getParagraphText()); 
				parserDocument.footnotes = Arrays.asList(extractor.getFootnoteText());
		        extractor.close();
			} else if ( ccase.getExtension().equalsIgnoreCase(".DOCX")) {
				parserDocument.paragraphs = new ArrayList<String>();
				parserDocument.footnotes = new ArrayList<String>();
				try ( XWPFDocument document = new XWPFDocument(entity.getContent()) ) {
					Iterator<XWPFParagraph> pIter = document.getParagraphsIterator();
					while ( pIter.hasNext() ) {
						XWPFParagraph paragraph = pIter.next();
						parserDocument.paragraphs.add(paragraph.getParagraphText() ); 
					}
					for ( XWPFFootnote footnote: document.getFootnotes() ) {
						for ( XWPFParagraph p: footnote.getParagraphs()  ) {
							parserDocument.footnotes.add(p.getText());
						}
					}
					document.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				throw new IllegalArgumentException("Unknown File Type: " + ccase);
			}
		} finally {
			httpclient.getConnectionManager().shutdown();
		}
		return parserDocument;
	}

	private ByteArrayInputStream convertInputStream(InputStream inputStream) {
		ByteArrayInputStream bais = null;
        try ( ByteArrayOutputStream outputStream = new ByteArrayOutputStream() ) {
	    	byte[] bbuf = new byte[8192];
	    	int len;
	    	while ( (len = inputStream.read(bbuf, 0, bbuf.length)) != -1 ) {
	    		outputStream.write(bbuf, 0, len);
	    	}
	    	outputStream.close();
	    	bais = new ByteArrayInputStream(outputStream.toByteArray());
        } catch (IOException ex) {
	    	logger.log(Level.SEVERE, null, ex);
		}
        return bais; 
	}
	
	private void saveCopyOfCase(String directory, String fileName, InputStream inputStream ) {
		try {
			BufferedOutputStream out = new BufferedOutputStream( new FileOutputStream(directory + "/" + fileName));	    
		    ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
	    	byte[] bytes = new byte[8192];
	    	int len;
	    	while ( (len = inputStream.read(bytes, 0, bytes.length)) != -1 ) {
	    		out.write(bytes, 0, len);
	    		baos.write(bytes, 0, len);
	    	}
	    	out.close();
	    	baos.close();
	    	inputStream.close();
	    } catch( IOException ex) {
	    	logger.log(Level.SEVERE, null, ex);
	    }
	}

	@Override
	public List<CourtCase> parseCaseList(Reader reader) throws Exception {
		List<String> lines = new ArrayList<String>();
		String tmpString;
		BufferedReader bReader = new BufferedReader(reader);
		while ((tmpString = bReader.readLine()) != null) {
			lines.add(tmpString);
		}
		bReader.close();
		ArrayList<CourtCase> cases = new ArrayList<CourtCase>();
		Iterator<String> si = lines.iterator();

		DateFormat dfs = DateFormat.getDateInstance(DateFormat.SHORT);
        DateFormat dfm = DateFormat.getDateInstance(DateFormat.MEDIUM);
		
		while (si.hasNext()) {
			String line = si.next();
			// System.out.println(line);
			if (line.contains("/opinions/documents/")) {
				String fileExtension = ".DOCX"; 
				int loc = line.indexOf(fileExtension);
				if ( loc == -1) {
					fileExtension = ".DOC";
					loc = line.indexOf(fileExtension);
				}
				String fileName = line.substring(loc - 8, loc + 4);
				if (fileName.charAt(0) == '/') fileName = fileName.substring(1);
				loc = line.indexOf("<td valign=\"top\">");
				// String publishDate = line.substring(loc+17, loc+23 ) + "," +
				// line.substring(loc+23, loc+28 );
				// System.out.println( name + ":" + date);
				int locEnd = line.indexOf("</td>", loc);
				// String publishDate = line.substring(loc+17, loc+23 ) + "," +
				// line.substring(loc+23, loc+28 );
				String publishDate = line.substring(loc + 17, locEnd);
				// System.out.println( name + ":" + date);

				// find some useful information at the end of the string
				loc = line.indexOf("<br/><br/></td><td valign=\"top\">");
				String temp = line.substring(loc + 32, line.length());
				// System.out.println(temp);
				// such as date of opinion .. found by regex
				// also the title of the case .. now stored in tempa[0]
				String[] tempa = temp.split("\\b\\d{1,2}[/]\\d{1,2}[/]\\d{2}");
				String opinionDate = null;
				String court = null;
				if (tempa.length == 2) {
					// get out the date of
					opinionDate = temp.substring(tempa[0].length(),temp.length() - tempa[1].length());
					// and the court designation
					court = tempa[1].trim();
				} else {
					// sometimes no court designation
					opinionDate = temp.substring(tempa[0].length());
				}
				// store all this in a class
				fileName = fileName.replace(".DOC", "");
				Date opDate;
				Date pubDate;		        
				try {
					opDate = dfs.parse(opinionDate);
				} catch (ParseException e ) {
					// Default to current date.
					// not very good, but best that can be done, I suppose.
					Calendar cal = Calendar.getInstance();
					cal.setTime(new Date());
					cal.set(Calendar.HOUR_OF_DAY, 0);
					cal.set(Calendar.MINUTE, 0);
					cal.set(Calendar.SECOND, 0);
					cal.set(Calendar.MILLISECOND, 0);
					opDate = cal.getTime();
				}
		        try {
		        	pubDate = dfm.parse(publishDate);
			    } catch (ParseException e ) {
		        	// Default to current date.
		        	// not very good, but best that can be done, I suppose.
					Calendar cal = Calendar.getInstance();
					cal.setTime(new Date());
					cal.set(Calendar.HOUR_OF_DAY, 0);
					cal.set(Calendar.MINUTE, 0);
					cal.set(Calendar.SECOND, 0);
					cal.set(Calendar.MILLISECOND, 0);
					pubDate = cal.getTime();
			    }
				CourtCase courtCase = new CourtCase(fileName, fileExtension, tempa[0].trim(), opDate, pubDate, court);
// CourtCase(String name, String extension, String title, Date opinionDate, Date publishDate, String court) throws ParseException {

				cases.add(courtCase);
			//
			}
		}
		return cases;
	}
}

/*

			if (line.contains("/opinions/documents/")) {
				int loc = line.indexOf(".DOC");
				String name = line.substring(loc - 8, loc + 4);
				if (name.charAt(0) == '/') name = name.substring(1);
				loc = line.indexOf("<td valign=\"top\">");
				int locEnd = line.indexOf("</td>", loc);
				// String publishDate = line.substring(loc+17, loc+23 ) + "," +
				// line.substring(loc+23, loc+28 );
				String publishDate = line.substring(loc + 17, locEnd);
				// System.out.println( name + ":" + date);

				// find some useful information at the end of the string
				loc = line.indexOf("<br/><br/></td><td valign=\"top\">");
				String temp = line.substring(loc + 32, line.length());
				// System.out.println(temp);
				// such as date of opinion .. found by regex
				// also the title of the case .. now stored in tempa[0]
				String[] tempa = temp.split("\\b\\d{1,2}[/]\\d{1,2}[/]\\d{2}");
				String opinionDate = null;
				String court = null;
				if (tempa.length == 2) {
					// get out the date of
					opinionDate = temp.substring(tempa[0].length(),temp.length() - tempa[1].length());
					// and the court designation
					court = tempa[1].trim();
				} else {
					// sometimes no court designation
					opinionDate = temp.substring(tempa[0].length());
				}
				// store all this in a class
				name = name.replace(".DOC", "");
				Date opDate;
				Date pubDate;		        
		        try {
		        	opDate = dfs.parse(opinionDate);
		        } catch (ParseException e ) {
		        	// Default to current date.
		        	// not very good, but best that can be done, I suppose.
					Calendar cal = Calendar.getInstance();
					cal.setTime(new Date());
					cal.set(Calendar.HOUR_OF_DAY, 0);
					cal.set(Calendar.MINUTE, 0);
					cal.set(Calendar.SECOND, 0);
					cal.set(Calendar.MILLISECOND, 0);
					opDate = cal.getTime();
		        }
		        try {
		        	pubDate = dfm.parse(publishDate);
			    } catch (ParseException e ) {
		        	// Default to current date.
		        	// not very good, but best that can be done, I suppose.
					Calendar cal = Calendar.getInstance();
					cal.setTime(new Date());
					cal.set(Calendar.HOUR_OF_DAY, 0);
					cal.set(Calendar.MINUTE, 0);
					cal.set(Calendar.SECOND, 0);
					cal.set(Calendar.MILLISECOND, 0);
					pubDate = cal.getTime();
			    }
				CourtCase courtCase = new CourtCase(name, tempa[0].trim(),opDate, pubDate, court);
				cases.add(courtCase);
			}
*/

/*

if (line.contains("/opinions/documents/")) {
String fileExtension = ".DOCX"; 
int loc = line.indexOf(fileExtension);
if ( loc == -1) {
	fileExtension = ".DOC";
	loc = line.indexOf(fileExtension);
}
String fileName = line.substring(loc - 8, loc + 4);
if (fileName.charAt(0) == '/') fileName = fileName.substring(1);
loc = line.indexOf("<td valign=\"top\">");
// String publishDate = line.substring(loc+17, loc+23 ) + "," +
// line.substring(loc+23, loc+28 );
// System.out.println( name + ":" + date);

// find some useful information at the end of the string
loc = line.indexOf("<br/><br/></td><td valign=\"top\">");
String temp = line.substring(loc + 32, line.length());
// System.out.println(temp);
// such as date of opinion .. found by regex
// also the title of the case .. now stored in tempa[0]
String[] tempa = temp.split("\\b\\d{1,2}[/]\\d{1,2}[/]\\d{2}");
String opinionDate = null;
String court = null;
if (tempa.length == 2) {
	// get out the date of
	opinionDate = temp.substring(tempa[0].length(),temp.length() - tempa[1].length());
	// and the court designation
	court = tempa[1].trim();
} else {
	// sometimes no court designation
	opinionDate = temp.substring(tempa[0].length());
}
// store all this in a class
fileName = fileName.replace(".DOC", "");
Date opDate;
try {
	opDate = dfs.parse(opinionDate);
} catch (ParseException e ) {
	// Default to current date.
	// not very good, but best that can be done, I suppose.
	Calendar cal = Calendar.getInstance();
	cal.setTime(new Date());
	cal.set(Calendar.HOUR_OF_DAY, 0);
	cal.set(Calendar.MINUTE, 0);
	cal.set(Calendar.SECOND, 0);
	cal.set(Calendar.MILLISECOND, 0);
	opDate = cal.getTime();
}
SlipOpinion slipOpinion = new SlipOpinion(fileName, fileExtension, tempa[0].trim(),opDate, court);
// test for duplicates
if ( cases.contains(slipOpinion)) {
	logger.warning("Duplicate Detected:" + slipOpinion);
} else {
	cases.add(slipOpinion);
}
//
}

*/