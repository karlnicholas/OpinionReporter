package code;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.TreeSet;

import codesparser.Code;
import codesparser.CodeRange;
import codesparser.CodeReference;
import codesparser.CodesInterface;
import codesparser.Section;
import codesparser.SectionNumber;
import codesparser.Subcode;

public class CodeParser {
	
	private String part;
	private String partNumber;
	private String title;
	private CodeRange range;

   
   public Code parse(CodesInterface codesInterface, String encoding, File file) throws FileNotFoundException  { 

	   Stack<String> secStack = new Stack<String>();
       boolean veryFirst = true;
       Scanner scanner = new Scanner( file, encoding );

       try {
	       CodeReference codeMember = null;
	       // This is the top level, it is not a section
	       String codeTitle = changeTitleCase(scanner.nextLine());
	       Code code = new Code ( codeTitle, codesInterface.getShortTitle(codeTitle), codesInterface.mapCodeToFacetHead(codeTitle) );
	//       s = new Section(title, false, 0);
	//       sections.add(s);
	
	       // use Title as top level parent
	       CodeReference parent = code;
	       // save the most recent good parent here, so as to skip over lines that don't constitute a level
	       CodeReference goodParent = code;

           // the current lined, combined from as many next lines as required
           String cline = null;
           // temp buffer to hold the next line process
           String nline;
           // To be used to look at the most recent line read, to see if it is 79 chars long, and therefor
           // had code sections in it.
           String eline;
           // save the results of eline test, if there is a section range to decode
           boolean codeRange;

           //first use a reader to get each line
           while ( scanner.hasNextLine() ){
               cline = scanner.nextLine();
               if ( cline.contains("TABLE OF CONTENTS") ) break;
           }

           // Skip a line
           scanner.nextLine();

           // Initialize algorithm with first data line
           cline = scanner.nextLine();
           eline = cline;

           do {
               // Get indent level by counting spaces
               int cindent = findIndent(cline);
               nline = null;
               codeRange = false;
               // loop and concatenate partial lines
               while ( scanner.hasNextLine() ) {
                   nline = scanner.nextLine();
                   // measure distance from last line to see if it's a continuation of last line
                   int nindent = findIndent(nline);
                   if ( cindent + 4 < nindent ) {
                       // if continuation, the combine the lines
                       cline = cline + " " + nline.trim();
                       eline = nline;
                       // clear this since we used it.
                       nline = null;
                   } else {
                       // else, break and process this line
                       break;
                   }
               }
               // end the loop here .. 
//               if ( cline == null && nline == null ) break;
               
               if ( cline.length() != 0 ) {
                   if ( cline.trim().charAt(0) != '.' ) {
                       // check to see if we need to set a new parent based on indent level
                       int level = checkLevel( cline, secStack);
                       
                       // but first, see if this is the very first level, and so don't indent
                       // special case ... reset the level .. this needs to be pushed into the checkLevel routine, no?
                       if ( level > 0 && veryFirst ) {
                           level = 0;
                           veryFirst = false;
                       }
                       // if this is a different level, then adjust the parents
                       while ( level != 0 && veryFirst != true ) {
                           if ( level > 0 ) {
                               // if a deeper level, set parent to the last good parent
                               parent = goodParent;
                               // preserve the hierarchy in a list
                               --level;
                           } else {
                               // if shallower level, then set parent to this parent's parent
                               parent = parent.getParent();
                               goodParent = parent;
                               ++level;
                           }
                       }
                       // Check to see if this line has section numbers in it
                       if ( eline.length() == 79 ) {
                           codeRange = true;
                       }
                       // Create a new section
                	   parseLine( cline.trim(), codeRange );
                	   
                	   String adjTitle = changeTitleCase(title);

                	   if ( range == null ) {
                    	   // this is related to the temp-fix of veryFirst from above ... 
//                    	   codeMember = new Subcode( parent, part, partNumber, title, (secStack.size()>0?secStack.size():1) );
                    	   codeMember = new Subcode( parent, part, partNumber, adjTitle, secStack.size() );
                       } else {
                    	   // this is related to the temp-fix of veryFirst from above ...(and this too) 
//                    	   codeMember = new Section(parent, part, partNumber, title, range, (secStack.size()>0?secStack.size():1) );
                    	   codeMember = new Section(parent, part, partNumber, adjTitle, range, secStack.size() );
                       }
                       

                	   if ( 
	        			   codeMember.getPart() == null &&
	        			   codeMember.getPartNumber() == null &&
	        			   codeMember.getCodeRange().getsNumber() == null &&
	        			   codeMember.getCodeRange().geteNumber() == null
        			   ) { 
                		   System.out.println("Invalid codeMember: " + codeMember);
                	   } else {
	
	            		   parent.addReference(codeMember);
	
	                       // check if this section is a constitutes a level
	                       if ( part == null ) {
	                           // if not, adjust parent
	                    	   codeMember.setParent(goodParent);
	                       } else {
	                           // else, save this as the most recent good parent
	                           goodParent = codeMember;
	                       }
                	   }
                   }
                   // do while there are lines to process
               }
               cline = nline;
               eline = cline;
//             } while ( cline != null );
           } while ( scanner.hasNext() || cline != null );

//         log("Done.");
           return code;
       }
       finally {
           //ensure the underlying stream is always closed
           //this only has any effect if the item passed to the Scanner
           //constructor implements Closeable (which it does in this case).
           scanner.close();
       }
   }

   private final static TreeSet<String> wordSet; 
   static {
   	wordSet = new TreeSet<String>();
   	wordSet.add("egg");
   	wordSet.add("bay");
   	wordSet.add("act");
   	wordSet.add("san");
   	wordSet.add("aid");
   	wordSet.add("law");
   	}
   
   private String changeTitleCase(String title ) {
   	StringBuffer buffer = new StringBuffer();
   	StringTokenizer tokenizer = new StringTokenizer(title);
   	boolean first = true;
   	while ( tokenizer.hasMoreTokens() ) {
   		String word = tokenizer.nextToken().toLowerCase();
   		if( word.contains("-") ) {
   			StringTokenizer strk2 = new StringTokenizer(word, "-");
   			String newWord = new String();
   			boolean first2 = true;
   			if ( strk2.countTokens() == 1 ) {
   				buffer.append(word);
   	   			continue;
   			}
   			while (  strk2.hasMoreTokens() ) {
   				if ( first2 ) {
   					first2 = false;
   				} else {
   					if ( word.contains("--")) {
   	   					newWord = newWord.concat("--");
   					} else {
   	   					newWord = newWord.concat("-");
   					}
   				}
   				String tk = strk2.nextToken();
   				if ( tk != null && tk.length() > 0 ) {
   					newWord = newWord.concat( Character.toUpperCase(tk.charAt(0)) + tk.substring(1)  );
   				}
   			}
   			buffer.append(newWord);
   			continue;
   		}
   		if ( word.length() <= 3 && !(wordSet.contains(word)) && first == false) {
   			buffer.append(word + " ");
   		} else {
   			first = false;
   			buffer.append(Character.toUpperCase(word.charAt(0)) + word.substring(1) + " " );
   		}
   	}
   	return buffer.toString().trim();
   }
   
   protected void parseLine(String line, boolean codeRange ) {

	   part = null;
	   partNumber = null;
	   range = null;
	   title = null;
	   
       String codeSection = null;
       int tsindex = 0;
       int teindex = line.length()-1;

       // Check if there are section numbers to be parsed into a SectionRange
       if ( teindex > 2 && (
               Character.isDigit(line.charAt(teindex)) || (
                       ( Character.isLetter(line.charAt(teindex)) || (line.charAt(teindex) >= 188 && line.charAt(teindex) <= 190)
                    		   ) &&
                       Character.isDigit(line.charAt(teindex-1))
                       )
                   )
               && codeRange
               ) {
           tsindex = teindex;
           while (line.charAt(tsindex--) != ' ' );
           codeSection = line.substring(tsindex+2, teindex+1);
           while ( line.charAt(tsindex) == ' ' || line.charAt(tsindex) == '.' ) tsindex--;
           teindex = tsindex+1;
           tsindex = 0;
       } else {
           teindex++;
       }

       // look for
       String ln = line.toLowerCase();
       if ( ln.contains(CACodes.sectionTitles[0]) ||
           ln.contains(CACodes.sectionTitles[1]) ||
           ln.contains(CACodes.sectionTitles[2]) ||
           ln.contains(CACodes.sectionTitles[3]) ||
           ln.contains(CACodes.sectionTitles[4]) ) {

           tsindex = 0;
           int eindex = 0;

           while ( line.charAt(eindex++) != ' ' );
               part = line.substring(tsindex, eindex-1);

               tsindex = eindex;
               while ( line.charAt(eindex++) != ' ' );
               partNumber = line.substring(tsindex, eindex-2);
               if ( Character.isDigit( partNumber.charAt(0)) ) {
                   tsindex = eindex+1;
               } else {
                   part = null;
                   partNumber = null;
                   tsindex = 0;
               }
       }

       title = line.substring(tsindex, teindex);

       if ( codeSection != null && codeRange ) {
    	   String sNumber = null;
    	   String eNumber = null;
	       	if ( codeSection != null && !codeSection.isEmpty()  ) {
		        int idx = codeSection.indexOf('-');
		        if ( idx != -1 ) {
		        	sNumber = codeSection.substring(0, idx);
		            eNumber = codeSection.substring(idx+1, codeSection.length());
		        } else {
		        	sNumber = codeSection.substring(0, codeSection.length());
		            eNumber = null;
		        }
			}
//           range = new CodeRange(codeSection.substring(0, codeSection.length()) );
           range = new CodeRange(
        		   new SectionNumber(-1, sNumber), 
        		   new SectionNumber(-1, eNumber) 
        		   );

       }
   }
   

   
   /*
       Use findSection to get a pointer to the Section depending on
       the "Number" of the section within the list of SectionRanges.
       Returns null if section not found

   public int compareTo(SectionNumber sectionNumber) {
       if ( range == null ) return -1;
       return range.compareTo(sectionNumber);
   }

    */

   private int findIndent( String line ) {
       int indent = 0;
       if ( line.length() == 0 ) {
           return 0;
       }
       while ( line.charAt(indent++) == ' '){
           if ( indent >= line.length() ) break;
       };
       return indent-1;
   }

       
   /*
    * return 0 if no new level, 1 if additional level, or -1 through -n for number of levels up
    * the rule of levels is such ..
    * There is one and only one level 0, after that, the first entry would be level 1 
    * and everything else would be level one until ...
    * (one of the keywords is detected .. which doesn't work, but does ..)
    * if one of the keywords detected, then it's down a level
    * unless the word is already on the stack, then it's up a level .. (or more) 
    * the unique exception to this rule is that if it's not a keyword ..
    * then it's down a level .... as long as the last line/header 
    * was either a detected keyword or level 0 ... 
    * .. if not, then maintain the current level 
    * .. don't put non-detected-keywords on the stack because they can't be popped
    * 
    * let's put the routine into pseudo-code
    * strip tokens
    * get first-word-header
    * get second word number
    * insure two numbers
    * insure number length > 0
    * insure first character of number is a digit
    * search sectionTitles, find match
    * if stack is-empty, just push it on the stack, return 1
    * if top-of-stack not empty, return 0
    * if word-header at the top-of-stack, then return 0
    *  loop .. while word-header is not word-prior-top-of stack
    *    
    *    decrement to negative the return count ..
    *    if word-matches, loop 
    *       and pop everthing above the word the matches
    *    	return negative reurn-count
    *    
    *  
    *  pop stack
    * ...
    */
   private int checkLevel( String line, Stack<String> secStack ) {
//	   logger.fine(line);
	   // first off, we don't care if there is a number .. 
	   // we only care about matcing the first word .. 
	   // so let's get the first word as header
       // if true, then we have a bona fida header
	   String sectionTitle = findSectionTitle(line);
	   if ( sectionTitle == null ) {
		   // if we are already pending .. do nothing
		   // jasva evaluates left to right, right?
	       if (!secStack.empty() && secStack.peek() == "pending" ) return 0;
	       // else, go into pending state
	       secStack.push("pending");
		   // for the pending logic
           return 1;
	   }
       // so, here we have a legit title
       if ( !secStack.empty() ) {
           String top = secStack.peek();
           // if we are lready at this level, ie, this header is on the stack, 
           // then do nothing
           if ( top.equals(sectionTitle)) {
               return 0;
           } else {
        	   // work our way up the stack 
        	   // to see if we find a match
        	   // otherwise, fall through to the push at the bottom .. 
               int cbc = secStack.size()-1;
               if ( cbc >= 0 ) {
                   int retLevel = 0;
                   while ( cbc >= 0 ) {
                       String prior = secStack.elementAt(cbc);
                       if ( prior.equals( sectionTitle ) ) {
                           do {
                               secStack.pop();
                           } while ( secStack.size() > cbc+1 );
                           return retLevel;
                       } else {
                           retLevel--;
                       }
                       cbc--;
                   }
                   // fall through to new item and push on the stack.
               }
               // fall through
           }
           // fall through
       }
       // push current onto stack
       if (!secStack.empty() && secStack.peek() == "pending" ) {
    	   secStack.remove(secStack.indexOf(secStack.lastElement()) );
           secStack.push(sectionTitle);
           return 0;
       }
       secStack.push(sectionTitle);
       return 1;
   }

   private String findSectionTitle(String line ) {
	   // we check for "numbers" because there are strings that have matching
	   // titles but no numbers, eg "TITLE OF ACT"
	   // and hence are not proper sections titles
	   StringTokenizer tokenizer = new StringTokenizer(line.trim().toLowerCase());
	   if( !tokenizer.hasMoreTokens() ) return null;
	   String header = tokenizer.nextToken();
	   if( !tokenizer.hasMoreTokens() ) return null;
	   String num = tokenizer.nextToken();
	   if ( num.length() < 1) return null;
	   if ( !Character.isDigit(num.charAt(0))) return null;

       for ( int tc=0; tc<CACodes.sectionTitles.length; ++tc ) {
    	   String sectionTitle = CACodes.sectionTitles[tc];
           if ( header.equals(sectionTitle) ) return sectionTitle;
       }
       return null;
   }

}
