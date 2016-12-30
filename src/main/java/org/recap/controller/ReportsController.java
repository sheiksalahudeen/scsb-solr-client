package org.recap.controller;

import org.recap.RecapConstants;
import org.recap.model.search.DeaccessionItemResultsRow;
import org.recap.model.search.ReportsForm;
import org.recap.util.ReportsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by rajeshbabuk on 13/10/16.
 */

@Controller
public class ReportsController {

    Logger logger = LoggerFactory.getLogger(ReportsController.class);

    @Autowired
    ReportsUtil reportsUtil;

    @RequestMapping("/reports")
    public String collection(Model model) {
        ReportsForm reportsForm = new ReportsForm();
        model.addAttribute("reportsForm", reportsForm);
        model.addAttribute(RecapConstants.TEMPLATE, RecapConstants.REPORTS);
        return "searchRecords";
    }

    @ResponseBody
    @RequestMapping(value = "/reports", method = RequestMethod.POST, params = "action=submit")
    public ModelAndView reportCounts(@Valid @ModelAttribute("reportsForm") ReportsForm reportsForm,
                                     Model model) throws Exception {
        if (reportsForm.getRequestType().equalsIgnoreCase("Request")) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(RecapConstants.Simple_Date_Format_REPORTS);
            Date requestFromDate = simpleDateFormat.parse(reportsForm.getRequestFromDate());
            Date requestToDate = simpleDateFormat.parse(reportsForm.getRequestToDate());
            if (reportsForm.getShowBy().equalsIgnoreCase("IL_BD")) {
                reportsUtil.populateILBDCountsForRequest(reportsForm, requestFromDate, requestToDate);
            } else if (reportsForm.getShowBy().equalsIgnoreCase("Partners")) {
                reportsUtil.populatePartnersCountForRequest(reportsForm, requestFromDate, requestToDate);
            } else if (reportsForm.getShowBy().equalsIgnoreCase("RequestType")) {
                reportsUtil.populateRequestTypeInformation(reportsForm, requestFromDate, requestToDate);
            }
        } else if (reportsForm.getRequestType().equalsIgnoreCase("Accession/Deaccesion")) {
            reportsUtil.populateAccessionDeaccessionItemCounts(reportsForm, reportsForm.getAccessionDeaccessionFromDate(), reportsForm.getAccessionDeaccessionToDate());

        } else if (reportsForm.getRequestType().equalsIgnoreCase("CollectionGroupDesignation")) {
            reportsUtil.populateCGDItemCounts(reportsForm);
        }
        model.addAttribute(RecapConstants.TEMPLATE, RecapConstants.REPORTS);
        return new ModelAndView("searchRecords", "reportsForm", reportsForm);

    }

    @ResponseBody
    @RequestMapping(value = "/reports/collectionGroupDesignation", method = RequestMethod.GET)
    public ModelAndView cgdCounts(@Valid @ModelAttribute("reportsForm") ReportsForm reportsForm,
                                  Model model) throws Exception {
        reportsUtil.populateCGDItemCounts(reportsForm);
        model.addAttribute(RecapConstants.TEMPLATE, RecapConstants.REPORTS);
        return new ModelAndView("reports :: #cgdTable", "reportsForm", reportsForm);

    }

    @ResponseBody
    @RequestMapping(value = "/reports/deaccessionInformation", method = RequestMethod.GET)
    public ModelAndView deaccessionInformation(@Valid @ModelAttribute("fromDate") String fromDate,
                                               @Valid @ModelAttribute("toDate") String toDate,
                                               @Valid @ModelAttribute("owningInstitution") String owningInstitution,
                                               Model model) throws Exception {
        List<DeaccessionItemResultsRow> deaccessionItemResultsRowList= reportsUtil.deaccessionReportFieldsInformation(fromDate, toDate, owningInstitution);
        model.addAttribute(RecapConstants.TEMPLATE, RecapConstants.REPORTS);
        return new ModelAndView("reports :: #deaccessionInformation","deaccessionItemResultsRows",deaccessionItemResultsRowList);

    }

}
