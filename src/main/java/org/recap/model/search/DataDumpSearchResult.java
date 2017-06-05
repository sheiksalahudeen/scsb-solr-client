package org.recap.model.search;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by angelind on 26/10/16.
 */
@ApiModel(value="DataDumpSearchResult", description="Model for Displaying Search Result")
public class DataDumpSearchResult {

    @ApiModelProperty(name= "bibId", value= "Bibliographic Id",position = 0)
    private Integer bibId;
    @ApiModelProperty(name= "itemIds", value= "Item Ids",position = 1)
    private List<Integer> itemIds = new ArrayList<>();

    /**
     * Gets bib id.
     *
     * @return the bib id
     */
    public Integer getBibId() {
        return bibId;
    }

    /**
     * Sets bib id.
     *
     * @param bibId the bib id
     */
    public void setBibId(Integer bibId) {
        this.bibId = bibId;
    }

    /**
     * Gets item ids.
     *
     * @return the item ids
     */
    public List<Integer> getItemIds() {
        return itemIds;
    }

    /**
     * Sets item ids.
     *
     * @param itemIds the item ids
     */
    public void setItemIds(List<Integer> itemIds) {
        this.itemIds = itemIds;
    }
}
