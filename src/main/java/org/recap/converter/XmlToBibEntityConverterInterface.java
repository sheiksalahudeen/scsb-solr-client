package org.recap.converter;

import java.util.Map;

/**
 * Created by premkb on 15/12/16.
 */
@FunctionalInterface
public interface XmlToBibEntityConverterInterface {

    /**
     * This method is used to convert the given record and put them in a map.
     *
     * @param record          the record
     * @param institutionName the institution name
     * @param customerCode    the customer code
     * @return the map
     */
    public Map convert(Object record, String institutionName, String customerCode);
}
