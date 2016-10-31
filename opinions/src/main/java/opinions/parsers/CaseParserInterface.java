package opinions.parsers;

import java.io.*;
import java.util.*;

import opinions.model.courtcase.CourtCase;

public interface CaseParserInterface {
	Reader getCaseList() throws Exception;
	ParserDocument getCaseFile(CourtCase ccase, boolean debugCopy) throws Exception;
	List<CourtCase> parseCaseList(Reader reader) throws Exception;
}
