package org.recap.model.search.resolver;

/**
 * Created by peris on 9/29/16.
 */
public interface ValueResolver {
    /**
     * Checks whether the given field value is valid or not.
     *
     * @param field the field
     * @return the boolean
     */
    public Boolean isInterested(String field);
}
