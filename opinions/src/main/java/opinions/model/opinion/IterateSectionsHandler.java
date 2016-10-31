package opinions.model.opinion;

// This interface allows for the iteration
// of each Section. It implements a CallBack
public interface IterateSectionsHandler {
	public boolean handleOpinionSection(OpinionSection section) throws Exception;
}
