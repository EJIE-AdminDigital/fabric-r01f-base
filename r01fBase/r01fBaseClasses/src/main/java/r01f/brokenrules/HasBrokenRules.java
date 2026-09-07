package r01f.brokenrules;

import java.util.Collection;


/**
 * Interface for view components that can show a label
 * @param <L> label type
 */
public interface HasBrokenRules {
	/**
	 * Sets the broken rules text
	 * @param brokenRulesText
	 */
	public void setBrokenRules(final Collection<BrokenRule> brokenRulesText);
	/**
	 * @return the brokenRulesText
	 */
	public String getBrokenRules();
}
