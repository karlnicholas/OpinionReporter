package gsearch;

import gsearch.viewmodel.*;
import gsearch.viewmodel.ViewModel.STATES;

import java.io.*;
import java.util.*;
import java.util.logging.Logger;

import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.facet.*;
import org.apache.lucene.facet.taxonomy.*;
import org.apache.lucene.facet.taxonomy.directory.DirectoryTaxonomyReader;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import org.apache.lucene.queryparser.flexible.standard.StandardQueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.search.highlight.*;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import codesparser.*;

/*
 * Changes to match spec and make thread safe .. 
 */
public class GSearch {
	private static final Logger logger = Logger.getLogger(GSearch.class.getName());
	
	// Application scoped variables ..
	
	// The parsed statute hierarchy
	private CodesInterface codes;
	private IndexReader indexReader;
	private TaxonomyReader taxoReader;
	private IndexSearcher searcher;
	private FacetsConfig facetsConfig;
	private Analyzer analyzer;
	private StandardQueryParser parser;
	private int maxTopDocs;


	// section that is at the end of the path, if any
	Section termSection;
	
//	private States state;
	
	// This is meant to be put into an application scope
	// after instantiation .. 
	public GSearch(CodesInterface codes, File index, File indexTaxo) throws Exception {
		this.codes = codes;
		indexReader = DirectoryReader.open( FSDirectory.open(index));
        taxoReader = new DirectoryTaxonomyReader(FSDirectory.open(indexTaxo));
        searcher = new IndexSearcher(indexReader);
        facetsConfig = new FacetsConfig();
	    facetsConfig.setHierarchical(FacetsConfig.DEFAULT_INDEX_FIELD_NAME, true);
//	    facetsConfig.setRequireDimCount(FacetsConfig.DEFAULT_INDEX_FIELD_NAME, true);
	    analyzer = new EnglishAnalyzer(Version.LUCENE_4_9);
	    parser = new StandardQueryParser(analyzer);
	    maxTopDocs = 1000;
	}
	
	public void destroy() throws IOException {
		indexReader.close();
		taxoReader.close();
	}

/*		
	public boolean viewModelIsTerminatingSubcode(viewModel viewModel) {
		return viewModel.pathList.get(viewModel.pathList.size()-1).section;
		List<EntryReference> entryList = viewModel.codeList;
		while ( entryList != null  ) {
			if ( entryList.size() == 0 ) return false;
			EntryReference eRef = entryList.get(0);
			if ( eRef instanceof CodeEntry ) {
				if ( entryList.size() > 1 ) return false;				
			} else if ( eRef instanceof SubcodeEntry ) {
				if ( entryList.size() > 1 ) return false;				
			} else if ( eRef instanceof TextEntry ) {
				return true;				
			}
		}
		return false;
    }
*/		
	
	public ViewModel handleRequest( String path, String term, boolean highlights) throws IOException {
		logger.fine("get:" + path + ":" + term + ":" + highlights );
		ViewModel viewModel = new ViewModel(path, term, highlights);
		
		// set state
		viewModel.setState(STATES.START);
		if ( !viewModel.getPath().isEmpty() ) viewModel.setState(STATES.BROWSE);	// path overrides select

		termSection = null;
		
		// This section is building a list of the codes available .. 
		// it should be called whenever there is no path, at least
		// it can also, presumably, be built if the codeselect 
		// 
		if ( viewModel.getState() == STATES.START ) {
			ArrayList<Code> codeList = codes.getCodes();
			for ( int i=0, l=codeList.size(); i<l; ++i ) {
				Code c = codeList.get(i);
				CodeEntry cEntry = new CodeEntry( c );
				cEntry.setPathPart(false);
		    	viewModel.getEntries().add( cEntry );
			}
		} else {
			processPathAndSubcodeList( viewModel );			
		}
		
		if ( viewModel.getState() == STATES.TERMINATE || viewModel.isFragments() || !viewModel.getTerm().isEmpty() ) { 
			processTerm(viewModel);
		}
		// return the processing results

		return viewModel;
	}

	private void processPathAndSubcodeList( ViewModel viewModel ) throws IOException {
		// at this point, only exhange.path is filled out ..
		Code code = codes.findCodeFromFacet(viewModel.getPath());
		EntryReference entryReference = new CodeEntry(code);
		viewModel.getEntries().add( entryReference );
		List<EntryReference> entries = entryReference.getEntries();

		ArrayList<CodeReference> subPaths = code.getReferences();

		// ok .. now we are building parent paths ..
		StringTokenizer tokenizer = new StringTokenizer(viewModel.getPath(), String.valueOf(FacetUtils.DELIMITER) );
		// burn the first token
		String token = tokenizer.nextToken();
		while ( tokenizer.hasMoreTokens() ) {
			token = tokenizer.nextToken();
			for (CodeReference reference: subPaths ) {				
				String subPart = reference.getReferenceFacet();
				if ( subPart.equals(token) ) {
					entryReference = new SubcodeEntry(reference);
					entries.add(entryReference);
					entries = entryReference.getEntries();
					subPaths = reference.getReferences();
					// check terminating
					if ( reference.getSection() != null ) {
						viewModel.setState(STATES.TERMINATE);
						termSection = reference.getSection();
					}
					break;	// out of for loop
				}
			}
		}
	    if ( subPaths != null ) {
	    	for ( CodeReference reference: subPaths ) {
	    		SubcodeEntry subcode = new SubcodeEntry( reference );
	    		subcode.setPathPart(false);
	    		entries.add( subcode );
		    }
	    }
	}
		
	private void processTerm( ViewModel viewModel) throws IOException {
	    // is this really the best way???
	    // or .. let's see if this works 

		Query query = makeQuery(viewModel);
		
//		FacetSearchParams facetSearchParams = null;
		DrillDownQuery ddQuery;
		FacetsCollector facetsCollector = null;
		TopScoreDocCollector topScoreDocCollector = null;
		Collector collector = null;
		String[] catPath = null;
		int totalCount = 0;
		
    	// need to have the current list of entries that will be displayed
		List<EntryReference> entries = viewModel.getEntries();
		while ( entries.size() != 0 && entries.get(0).isPathPart() ) {
			entries = entries.get(0).getEntries();
		}
		
		// 2
		if ( viewModel.getState() == STATES.START ) {
			// search for all root level facets
			// CODE  -- facet and category : combined
			// Create facet search parameter object and put the path(s) into it
			ddQuery = new DrillDownQuery(facetsConfig, query);
			
			for ( EntryReference eRef: entries ) {
				ddQuery.add(FacetsConfig.DEFAULT_INDEX_FIELD_NAME, eRef.getFullFacet());
			}
		} 
		// 3 or 4
		else if ( viewModel.getState() == STATES.BROWSE) {
			catPath = FacetUtils.fromString(viewModel.getPath());
		    DrillDownQuery drillDownQuery = new DrillDownQuery(facetsConfig, query);
		    drillDownQuery.add(FacetsConfig.DEFAULT_INDEX_FIELD_NAME, catPath);
		    query = drillDownQuery; 

		}
		// 5
		else if ( viewModel.getState() == STATES.TERMINATE || viewModel.isFragments() ) {
			// actual searching of text fields .. 
			// again, drill down query .. 
			catPath = FacetUtils.fromString(viewModel.getPath());
		    DrillDownQuery drillDownQuery = new DrillDownQuery(facetsConfig, query);
		    drillDownQuery.add(FacetsConfig.DEFAULT_INDEX_FIELD_NAME, catPath);
		    query = drillDownQuery; 
		}

		// 2
		if ( viewModel.getState() == STATES.START) {
			// collect facts .. 
			facetsCollector = new FacetsCollector(); 
			// maybe wrap a topScoreDocCollector ..
			if ( viewModel.isFragments() ) {
				topScoreDocCollector = TopScoreDocCollector.create(maxTopDocs, true);
				collector = MultiCollector.wrap( topScoreDocCollector, facetsCollector );
			} else {
				collector = facetsCollector;
			}
		}
		//3 and 4
		else if ( viewModel.getState() == STATES.BROWSE ) {
			// collect facts .. 
			facetsCollector = new FacetsCollector(); 
			// maybe wrap a topScoreDocCollector ..
			if ( viewModel.isFragments() ) {
				topScoreDocCollector = TopScoreDocCollector.create(maxTopDocs, true);
				collector = MultiCollector.wrap( topScoreDocCollector, facetsCollector );
			} else {
				collector = facetsCollector;				
			}
		}
		// 5
		else if ( viewModel.getState() == STATES.TERMINATE || viewModel.isFragments() ) {
			collector = topScoreDocCollector = TopScoreDocCollector.create(maxTopDocs, true);
		}
		
    	searcher.search(query, collector);
    	
    	// 2
    	if ( viewModel.getState() == STATES.START ) {
	    	// Here have used multiple CategoryPaths, so
	    	// ignore the subresults and get the various first level facet results
    	    Facets facets = new FastTaxonomyFacetCounts(taxoReader, facetsConfig, facetsCollector);
    	    FacetResult result = facets.getTopChildren(maxTopDocs, FacetsConfig.DEFAULT_INDEX_FIELD_NAME );
    	    
    	    if ( result != null ) {
		    	for ( LabelAndValue labelAndValue: result.labelValues ) {
	    	    	for ( EntryReference eRef: entries ) {
	    	    		if ( eRef.getFullFacet().equals(labelAndValue.label) ) {
	    	    			int count = labelAndValue.value.intValue();
	    	    			totalCount += count;
	    	    			eRef.setCount( count );
	    	    			break;
	    	    		}
	    	    	}
		    	}
    	    }
    	}
    	// 3 or 4 
    	else if ( viewModel.getState() == STATES.BROWSE) {
        	// using the path, so only one CategoryPath ... (with some number of sub-results)
    	    Facets facets = new FastTaxonomyFacetCounts(taxoReader, facetsConfig, facetsCollector);
    	    FacetResult result = facets.getTopChildren(maxTopDocs, FacetsConfig.DEFAULT_INDEX_FIELD_NAME, catPath );
    		
	    	// specific path, so get the result
    	    if ( result != null ) {
	    	    String pathStart = FacetUtils.toString(result.path) + FacetUtils.DELIMITER;
		    	for ( LabelAndValue labelAndValue: result.labelValues ) {
	    	    	for ( EntryReference eRef: entries ) {
	    	    		if ( eRef.getFullFacet().equals( pathStart + labelAndValue.label ) ) {
	    	    			int count = labelAndValue.value.intValue();
	    	    			totalCount += count;
	    	    			eRef.setCount( count );
	    	    			break;
	    	    		}
	    	    	}
	    	    }
    	    }
    	}
    	// 5 or highlights
    	if ( viewModel.getState() == STATES.TERMINATE || viewModel.isFragments() ) {
    		
	    	TopDocs docResults = topScoreDocCollector.topDocs();
	        ScoreDoc[] hits = docResults.scoreDocs;
	
	        int numTotalHits = docResults.totalHits;

	        int start = 0;
	        int end = Math.min(numTotalHits, maxTopDocs);
	        if ( end < numTotalHits ) totalCount = -maxTopDocs;
	        else totalCount = end;

	        SimpleHTMLFormatter htmlFormatter = new SimpleHTMLFormatter();
	        Highlighter highlighter = new Highlighter(htmlFormatter, new QueryScorer(query));
	        highlighter.setTextFragmenter(new GSearchFragmenter() );
	    	
	    	// create a temporary holding list of sectionText entries
	    	ArrayList<SectionText> sectionTextList = new ArrayList<SectionText>();
	    	
	    	for (int i = start; i < end; i++) {
	
	            int docId = hits[i].doc;
	            Document doc = searcher.doc(docId);
	            
	            IndexableField textField = doc.getField("sectiontext");
	            SectionNumber sectionNumber = new SectionNumber( 
	            		Integer.parseInt(doc.getField("position").stringValue()), 
	            		doc.getField("sectionnumber").stringValue() 
	            	);
	            String docFacet = doc.getField("path").stringValue();

	            String text = textField.stringValue();      

	            if ( viewModel.isFragments() ) { 
	                
	    	    	TokenStream tokenStream = TokenSources.getAnyTokenStream(searcher.getIndexReader(), docId, "sectiontext", analyzer);
	            	TextFragment[] frag = null;
	    			try {
	    				frag = highlighter.getBestTextFragments(tokenStream, text, false, 1);
	    			} catch (InvalidTokenOffsetsException e) {
	    				throw new RuntimeException( e );
	    			}
	    			for (int j = 0; j < frag.length; j++) {
	    				if ((frag[j] != null) && (frag[j].getScore() > 0)) {

	    					sectionTextList.add( new SectionText(
								sectionNumber.getSectionNumber(), 
								sectionNumber.getPosition(), 
    							sectionNumber.toString() + " ... " + frag[j].toString() + " ... ",
    							docFacet
    							) 
    						);
	    					break;
//	    	                  System.out.println((frag[j].toString()));
	    				}
	    			}
	            } else {
	            	sectionTextList.add( 
            			new SectionText(
							sectionNumber.getSectionNumber(), 
							sectionNumber.getPosition(), 
	            			text, 
	            			docFacet
	            		) 
	            	);
	            }
	    	}
	        // so .. getting last one .. getting section .. getting sectionNumbers .. iterating results ..
	        // this part fills in the missing sectionTexts if there was a search term used
	        // don't need to do this if the term is empty ..
	        // though, you might want to do it anyway so as to fall through to the sort call
	    	if ( sectionTextList != null || !viewModel.getPath().isEmpty() ) {
//		        Section section = viewModel.terminatingSubcode();
	    		// not called in a loop, so, suffer through it once ...

	    		if ( termSection != null ) {
	    			String fullFacet = FacetUtils.toString(termSection.getFullFacet()); 
		        	for ( SectionNumber sectionNumber: termSection.getSectionNumbers() ) {
			        	boolean found = false;
		        		for (SectionText sectionText: sectionTextList ) {
		        			// searching for a specific entry, so don't do lexical comparisons
        					if ( sectionText.getSectionNumber().equals( sectionNumber.getSectionNumber() ) ) {
				        		found = true;
				        		break;
				        	}
			        	}
			        	if ( !found ) {
			        		sectionTextList.add(
			        			new SectionText(
									sectionNumber.getSectionNumber(),
									sectionNumber.getPosition(), 
									sectionNumber + ". NO TERMS FOUND.", 
									fullFacet
								)
	        				);
			        	}
			        }
		        	// need to get the list from the section 
		        	// and sort if by that ... 
		        }
	        }

	        Collections.sort(sectionTextList);
			List<EntryReference> entries1 = viewModel.getEntries();

	        for ( SectionText textEntry: sectionTextList ) {
				boolean placed = putTextEntry(entries1, textEntry);
				if ( !placed ) {
					throw new RuntimeException("TextEntry not placed.");
				}
	        }			
    	}
    	// update total counts
    	viewModel.setTotalCount(totalCount);
    }
	
	private boolean putTextEntry(List<EntryReference> entries, SectionText textEntry) {
		boolean retVal = false;
		int textPosition = textEntry.getPosition();
		for ( EntryReference entryReference: entries ) {
			if ( FacetUtils.facetMatch(textEntry.getFullFacet(), entryReference.getFullFacet()) ) {
				int posBegin = entryReference.getCodeReference().getCodeRange().getsNumber().getPosition();
				int posEnd = entryReference.getCodeReference().getCodeRange().geteNumber().getPosition();
				if ( textPosition >= posBegin && textPosition <= posEnd ) {
					List<EntryReference> nextEntries = entryReference.getEntries();
					if ( nextEntries.size() == 0 || nextEntries.get(0) instanceof SectionText ) {
						entryReference.getEntries().add(textEntry);
						return true;
					} else {
						retVal = putTextEntry(entryReference.getEntries(), textEntry);
					}
				}
			}
		}
		return retVal;
	}

	private Query makeQuery(ViewModel viewModel ) {
		Query q;
	    if ( viewModel.getTerm().isEmpty() ) {
	    	q = new MatchAllDocsQuery();
	    } else {
//	    	Analyzer analyzer = new EnglishAnalyzer(Version.LUCENE_4_9);
//	    	QueryParser parser = new QueryParser(Version.LUCENE_4_9, "sectiontext", analyzer);
//	    	StandardQueryParser parser = new StandardQueryParser(new EnglishAnalyzer(Version.LUCENE_4_9));
	    	try {
	    		q = parser.parse(viewModel.getTerm(), "sectiontext");
	    	} catch (Throwable t) {
				// TODO Auto-generated catch block
				logger.severe("Parser error for term |"+viewModel.getTerm()+"| = " + t.getMessage());
				q = new MatchAllDocsQuery();
			}
	    }
		return q;
	}
	
}
