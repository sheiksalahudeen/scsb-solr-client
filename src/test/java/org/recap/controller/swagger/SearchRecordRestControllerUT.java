package org.recap.controller.swagger;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.recap.controller.BaseControllerUT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

/**
 * Created by premkb on 19/8/16.
 */
public class SearchRecordRestControllerUT extends BaseControllerUT {

    private static final Logger logger = LoggerFactory.getLogger(SearchRecordRestController.class);

    @Autowired
    private SearchRecordRestController searchRecordRestController;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(searchRecordRestController).build();
    }

    @Test
    public void searchRestfulApiEmpty() throws Exception {
        String paramString="sdaasdadad{}{[[[]]";
        MvcResult mvcResult = this.mockMvc.perform(get("/searchService/search")
                .param("requestJson",paramString))
                .andReturn();
        int status = mvcResult.getResponse().getStatus();
        assertTrue(status == 200);
    }

    @Test
    public void searchRestfulApi() throws Exception {
        String paramString="{\"owningInstitutions\" : [\"PUL\" ],\"materialTypes\" : [ \"Other\" ],\"useRestrictions\" : [\"NoRestrictions\" ,\"InLibraryUse\", \"SupervisedUse\" ] ,\"pageSize\": 10}";
        MvcResult mvcResult = this.mockMvc.perform(get("/searchService/search").param("requestJson",paramString)).andReturn();
        int status = mvcResult.getResponse().getStatus();

        assertTrue(status == 200);
    }

    @Test
    public void searchRestfulApiSupervisedUse() throws Exception {
        String paramString="{\"owningInstitutions\" : [\"PUL\" ],\"materialTypes\" : [ \"Other\" ],\"useRestrictions\" : [\"SupervisedUse\" ] ,\"pageSize\": 10}";
        MvcResult mvcResult = this.mockMvc.perform(get("/searchService/search")
                .param("requestJson",paramString))
                .andReturn();
        int status = mvcResult.getResponse().getStatus();
        assertTrue(status == 200);
    }

    @Test
    public void searchRestfulApiUseRestriction() throws Exception {
        String paramString="{\"owningInstitutions\" : [\"PUL\" ],\"materialTypes\" : [ \"Other\" ],\"useRestrictions\" : [\"NoRestrictions\" , \"SupervisedUse\" ] ,\"pageSize\": 10}";
        MvcResult mvcResult = this.mockMvc.perform(get("/searchService/search")
                .param("requestJson",paramString))
                .andReturn();
        int status = mvcResult.getResponse().getStatus();
        assertTrue(status == 200);
    }
}
