package opinions.parsers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.TreeSet;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: karl
 * Date: 6/24/12
 * Time: 7:47 AM
 * To change this template use File | Settings | File Templates.
 */
public class SentenceParser {
	private static final Logger logger = Logger.getLogger(SentenceParser.class.getName());
    ///
    private static final boolean DEBUGFINE = false;
    private static final boolean DEBUGFINER = false;
    private static final boolean DEBUGFINEST = false;
//    private static final boolean GENDEBUG = false;
//    private static final String DEBUGFILE = null; // set to null if not wanted
    private static final int pnum = 0;  // set to no-zero for a specific paragraph number

    private char ch;
    private int nestCount;
    private int priorNestCount;
    //    private StringCharacterIterator sci;
    private int sci;
    private int plength;
    private char[] paragraph;

    private static String[] abbvrs = {
        "v.",
        "seq.",
        "p.m.",
        "a.m.",
        "Cal.",
        "Corp.",
        "Inc.",
        "Bd.",
        "No.",
        "p.",
        "Mr.",
        "Jr.",
        "Mrs.",
        "Ms.",
        "Dr.",
        "PhD.",
        "Ct.",
        "Super.",
	    "Dist.",
	};
	private TreeSet<String> hashAbb;


    public SentenceParser() {
    	    hashAbb = new TreeSet<String>();
    	    Collections.addAll(hashAbb, abbvrs);
    }

    @SuppressWarnings("unused")
	public ArrayList<String> stripSentences(ArrayList<String> paragraphs) {
        ArrayList<String> sentences = new ArrayList<String>();
        int p, pl;
        if (pnum > 0) {
            p = pnum;
            pl = pnum + 1;
        } else {
            p = 0;
            pl = paragraphs.size();
        }
        for (; p < pl; ++p) {
            if ( DEBUGFINE) logger.fine( "========================= Paragraph ======================" + p);
        	sentences.addAll(stripSentences(paragraphs.get(p)));
        }
        return sentences;
    }
    
    public ArrayList<String> stripSentences(String pgraph) {
        ArrayList<String> sentences = new ArrayList<String>();
        String pg = pgraph.trim()
                .replace("[Citation.]", "")
                .replace("[Citations.]", "")
                .replace("[citation.]", "")
                .replace("[citations.]", "")
                .replace('\u0002', ' ')
                .replace('\u00A0', ' ')
                .replace("    ", " ")
                .replace("   ", " ")
                .replace("  ", " ")
                .replace("  ", " ")
                .replace("  ", " ")
                .replace("  ", " ")
                .replace("  ", " ")
                .replace('\u201D', '"')
                .replace('\u201C', '"')
                .replace('\u2018', '\'')
                .replace('\u2019', '\'')
                .replace('\u001E', '-')
//                .replace('�', '-')
//                .replace(". \"", ".\"")
//                .replace(". \'", ".\'")
                .replace("\' \"", "\'\"")
                .replace("\" \'", "\"\'");
        plength = pg.length();
        if (plength < 2) {
            return sentences;
        }
        paragraph = new char[plength];
        paragraph = pg.toCharArray();

        sci = 0;
        int start = 0;
        nestCount = 0;
        priorNestCount = nestCount;
//        sci = new StringCharacterIterator(paragraph);

        while (sci < plength) {
            ch = paragraph[sci++];
            if ( DEBUGFINER) logger.finer( "" + ch);
            int ni = testNestOpen();
            if (ni >= 0) {
                nestCount++;
                if (DEBUGFINEST) printNChars(' ');
                nextNester(ni);
                nestCount--;
                if (DEBUGFINEST) printNChars(' ');
            }
            if (testQuote()) {
                nestCount++;
                if (DEBUGFINEST) printNChars(' ');
                nextQuote();
                nestCount--;
                if (DEBUGFINEST) printNChars(' ');
            }
            if (testSingleQuote(false)) {
                nestCount++;
                if (DEBUGFINEST) printNChars(' ');
                nextSingleQuote();
                nestCount--;
                if (DEBUGFINEST) printNChars(' ');
            }
            if (testStoppers()) {
                if (DEBUGFINER) logger.finer("" + ch);
                int end = sci;
                if (ch == ' ') end--;
                String sentence = new String(paragraph, start, end - start);
                sentences.add(sentence);
                if (DEBUGFINER) logger.finer("|" + sentence + "|");
                // artificially advance one, depending on the stopper ...
                if (ch != ' ' && sci < plength) {
                    ch = paragraph[sci++];
                }
                start = sci;
//                System.out.println("Start = " + start );
            }
        };
        if (start != plength) {
            String sentence = new String(paragraph, start, plength - start);
            sentences.add(sentence);
            if (DEBUGFINER) logger.finer( "|" + sentence + "|");
        }
        return sentences;
    }

    private boolean testForAbbreviation(int farback) {
        // let's make this a little smarter ...
        int idx = sci+farback;
        int end = idx+1;
        boolean allUpper = true;
        for ( ; idx > 0; --idx ) {
            if ( paragraph[idx] == ' ' ) break;
            if ( allUpper ) {
                if ( Character.isDigit(paragraph[idx]) ) allUpper = false;
                if ( !Character.isLetter(paragraph[idx]) && !(paragraph[idx]=='.') ) allUpper = false;
                if ( Character.isLetter(paragraph[idx]) && Character.isLowerCase(paragraph[idx]) ) allUpper = false;
            }
        }
        if ( allUpper ) return true;
        if ( idx != 0 ) idx++;
        String word = new String(paragraph, idx, end-idx);
        if (DEBUGFINER) logger.finer(word);
        return hashAbb.contains(word);
    }


    private boolean testStoppers() {
        if (sci + 1 >= plength) return false;
        if (paragraph[sci] != ' ' ) return false;
        if (
                paragraph[sci - 1] == '.' &&
                        paragraph[sci] == ' '
                ) {
            if (testForAbbreviation(-1)) return false;
            return true;
        }
        if (sci < 2) return false;
        if (
                paragraph[sci - 2] == '.' &&
                        paragraph[sci - 1] == ')'
                ) {
            if (testForAbbreviation(-2)) return false;
            return true;
        }
        if (
                paragraph[sci - 2] == '.' &&
                        paragraph[sci - 1] == '"'
                ) return true;
        if (
                paragraph[sci - 2] == '.' &&
                        paragraph[sci - 1] == '\''
                ) return true;
        if (sci < 3) return false;
        if (
                paragraph[sci - 3] == '.' &&
                        paragraph[sci - 2] == '\'' &&
                        paragraph[sci - 1] == '"'
                ) return true;

        if (sci < 4) return false;
        if (
                paragraph[sci - 4] == '.' &&
                        paragraph[sci - 3] == '"' &&
                        paragraph[sci - 2] == '\'' &&
                        paragraph[sci - 1] == '"'
                ) return true;

        return false;


    }

    private static char[][] nesters = {
            {'(', ')'},
            {'[', ']'},
    };

    private int testNestOpen() {
        int ni = 0;
        for (; ni < nesters.length; ++ni) {
            if (ch == nesters[ni][0]) {
                return ni;
            }
        }
        return -1;
    }

    private void nextNester(int oni) {
        while (sci < plength) {
            ch = paragraph[sci++];
            if (DEBUGFINER) logger.finer("" + ch);
            if (ch == nesters[oni][1]) {
                return;
            }

            int ni = testNestOpen();
            if (ni >= 0) {
                nestCount++;
                if (DEBUGFINEST) printNChars(nesters[oni][0]);
                nextNester(ni);
                nestCount--;
                if (DEBUGFINEST) printNChars(nesters[oni][1]);
            }
            if (testQuote()) {
                nestCount++;
                if (DEBUGFINEST) printNChars(nesters[oni][0]);
                nextQuote();
                nestCount--;
                if (DEBUGFINEST) printNChars(nesters[oni][1]);
            }
            if (testSingleQuote(false)) {
                nestCount++;
                if (DEBUGFINEST) printNChars(nesters[oni][0]);
                nextSingleQuote();
                nestCount--;
                if (DEBUGFINEST) printNChars(nesters[oni][1]);
            }
        };
        return;
    }

    private boolean testSingleQuote(boolean lookingForMatch) {

        if (ch == '\'') {
//            System.out.println("" + c1 + Character.getNumericValue(c1) + " " + Character.isAlphabetic(c1));
//            System.out.println("" + c2 + Character.getNumericValue(c2) + " " + Character.isAlphabetic(c2));
//            System.out.println("" + c3 + Character.getNumericValue(c3) + " " + Character.isAlphabetic(c3));
            if (sci >= plength) return true;
            if (sci < 2) return true;
            if (paragraph[sci] == '"') return true;
            if (paragraph[sci - 2] == '"') return true;
            if (!Character.isWhitespace(paragraph[sci - 2]) && !Character.isWhitespace(paragraph[sci])) return false;
            if ( !lookingForMatch ) if (paragraph[sci - 2] == 's' && paragraph[sci] == ' ') return false;
            if (sci < 2) return true;
            if ( !lookingForMatch ) if (paragraph[sci - 3] == 'i' && paragraph[sci - 2] == 'n' && paragraph[sci] == ' ') return false;
            return true;
        }
        return false;
    }

    private void nextSingleQuote() {
        while (sci < plength) {
            ch = paragraph[sci++];
            if (DEBUGFINER) logger.finer("" + ch);
            if (testSingleQuote(true)) {
                return;
            }
            if (testQuote()) {
                nestCount++;
                if (DEBUGFINEST) printNChars('\'');
                nextQuote();
                nestCount--;
                if (DEBUGFINEST) printNChars('\'');
            }
            int ni = testNestOpen();
            if (ni >= 0) {
                nestCount++;
                if (DEBUGFINEST) printNChars('\'');
                nextNester(ni);
                nestCount--;
                if (DEBUGFINEST) printNChars('\'');
            }
        };
        return;
    }

    private boolean testQuote() {
        if (ch == '"') return true;
        return false;
    }

    private void nextQuote() {
        while (sci < plength) {
            ch = paragraph[sci++];
            if (DEBUGFINER) logger.finer("" + ch);
            if (testQuote()) {
                return;
            }
            if (testSingleQuote(false)) {
                nestCount++;
                if (DEBUGFINEST) printNChars('"');
                nextSingleQuote();
                nestCount--;
                if (DEBUGFINEST) printNChars('"');
            }
            int ni = testNestOpen();
            if (ni >= 0) {
                nestCount++;
                if (DEBUGFINEST) printNChars('"');
                nextNester(ni);
                nestCount--;
                if (DEBUGFINEST) printNChars('"');
            }
        };
        return;
    }

    private void printNChars(char f) {
        String out = new String(Character.toString(f));
        if (nestCount > priorNestCount) {
            out = out.concat(Integer.toString(nestCount) + ">");
        } else {
            out = out.concat(Integer.toString(nestCount) + "<");
        }
        for (int i = 0; i < nestCount; ++i) out = out.concat(" ");
        out = out.concat("" + ch);
        if (sci < plength) out = out.concat("" + paragraph[sci]);
        priorNestCount = nestCount;
        if ( DEBUGFINEST) logger.finest(out);
    }

}
