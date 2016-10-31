package opinions.model.opinion;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;

/*
 * We are just creating the class so that we have someplace to hold the mergeSubcodes routine.
 * It gets called by a couple difference classes, it
 */
public class QueueUtility extends ArrayDeque<OpinionReference> {
	private static final long serialVersionUID = 1L;
	
	private boolean compressCodeReferences;

	public QueueUtility(boolean compressCodeReferences) {
		this.compressCodeReferences = compressCodeReferences;
	}

	/*
	 * public void mergeSubcode( ArrayDeque<OpinionReference> queue,
	 * ArrayList<OpinionReference> subcodes ) { OpinionReference subcode =
	 * queue.pop(); for ( int i=0, j = subcodes.size(); i<j; ++i ) {
	 * OpinionReference listSubcode = subcodes.get(i); // Right here we are
	 * checking whether the codeSections are the same // and if so, then we
	 * "merge" them, which means only to discard // one and update the reference
	 * count .. if ( listSubcode.getCodeReference() == subcode.getCodeReference() )
	 * { listSubcode.incorporateSubcode( subcode, queue ); return; } }
	 * subcodes.add(subcode); subcode.addToChildren(queue); }
	 */
	public OpinionReference mergeSubcodes(ArrayList<OpinionReference> references) {
		OpinionReference opReference = pop();
		Iterator<OpinionReference> lit = references.iterator();
		while ( lit.hasNext() ) {
			OpinionReference listSubcode = lit.next();
			// Right here we are checking whether the codeSections are the same
			// and if so, then we "merge" them, which means only to discard
			// one and update the reference count ..
			// if ( listSubcode.getCodeReference() == subcode.getCodeReference() ) {
			// well, yea, the codeSection has to match, or don't even consider
			if (listSubcode.getCodeReference() == opReference.getCodeReference()) {

				listSubcode.incorporateOpinionReference( opReference, this );
				return opReference;
			}
		}
		references.add(opReference);
		opReference.addToChildren(this);
		return opReference;
	}

	public boolean isCompressCodeReferences() {
		return compressCodeReferences;
	}
	
/*	
	public void mergeSubcodes(ArrayList<OpinionReference> subcodes) {
		OpinionReference subcode = pop();
		for (int i = 0, j = subcodes.size(); i < j; ++i) {
			OpinionReference listSubcode = subcodes.get(i);
			// Right here we are checking whether the codeSections are the same
			// and if so, then we "merge" them, which means only to discard
			// one and update the reference count ..
			// if ( listSubcode.getCodeReference() == subcode.getCodeReference() ) {
			// well, yea, the codeSection has to match, or don't even consider
			if (listSubcode.getCodeReference() == subcode.getCodeReference()) {
                listSubcode.incorporateSubcode( subcode, this );
                return;
			}
		}
		subcodes.add(subcode);
		subcode.addToChildren(this);
	}

 */
}
