package org.recap.controller;

import org.apache.commons.lang3.StringUtils;
import org.recap.RecapConstants;
import org.recap.model.search.BibliographicMarcForm;
import org.recap.util.MarcRecordViewUtil;
import org.recap.util.CollectionServiceUtil;
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

/**
 * Created by rajeshbabuk on 17/10/16.
 */
@Controller
public class CollectionUpdateController {

    Logger logger = LoggerFactory.getLogger(CollectionUpdateController.class);

    @Autowired
    MarcRecordViewUtil marcRecordViewUtil;

    @Autowired
    CollectionServiceUtil collectionServiceUtil;

    @RequestMapping("/collectionUpdate")
    public String openMarcRecord(@Valid @ModelAttribute("bibId") Integer bibId, @Valid @ModelAttribute("itemId") Integer itemId, Model model) {
        BibliographicMarcForm bibliographicMarcForm = marcRecordViewUtil.buildBibliographicMarcForm(bibId, itemId);
        model.addAttribute("bibliographicMarcForm", bibliographicMarcForm);
        if (null != bibliographicMarcForm && StringUtils.isNotBlank(bibliographicMarcForm.getErrorMessage())) {
            return "marcRecordErrorMessage";
        } else {
            return "collectionUpdateView";
        }
    }

    @ResponseBody
    @RequestMapping(value = "/collectionUpdate", method = RequestMethod.POST, params = "action=collectionUpdate")
    public ModelAndView collectionUpdate(@Valid @ModelAttribute("bibliographicMarcForm") BibliographicMarcForm bibliographicMarcForm,
                                  BindingResult result,
                                  Model model) {
        if (RecapConstants.UPDATE_CGD.equalsIgnoreCase(bibliographicMarcForm.getCollectionAction())) {
            collectionServiceUtil.updateCGDForItem(bibliographicMarcForm);
        } else if (RecapConstants.DEACCESSION.equalsIgnoreCase(bibliographicMarcForm.getCollectionAction())) {
            collectionServiceUtil.deaccessionItem(bibliographicMarcForm);
        }
        return new ModelAndView("collectionUpdateView", "bibliographicMarcForm", bibliographicMarcForm);
    }
}
