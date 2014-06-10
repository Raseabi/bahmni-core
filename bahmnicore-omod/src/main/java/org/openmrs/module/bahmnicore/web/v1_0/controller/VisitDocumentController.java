package org.openmrs.module.bahmnicore.web.v1_0.controller;

import org.bahmni.module.bahmnicore.contract.visitDocument.VisitDocumentRequest;
import org.bahmni.module.bahmnicore.contract.visitDocument.VisitDocumentResponse;
import org.bahmni.module.bahmnicore.model.DocumentImage;
import org.bahmni.module.bahmnicore.service.PatientImageService;
import org.bahmni.module.bahmnicore.service.VisitDocumentService;
import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.WSDoc;
import org.openmrs.module.webservices.rest.web.v1_0.controller.BaseRestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class VisitDocumentController extends BaseRestController {
    private final String baseVisitDocumentUrl = "/rest/" + RestConstants.VERSION_1 + "/bahmnicore/visitDocument";
    @Autowired
    private VisitDocumentService visitDocumentService;
    @Autowired
    private PatientImageService patientImageService;

    @RequestMapping(method = RequestMethod.POST, value = baseVisitDocumentUrl)
    @WSDoc("Save Patient Document")
    @ResponseBody
    public VisitDocumentResponse save(@RequestBody VisitDocumentRequest visitDocumentUpload) {
        final Visit visit = visitDocumentService.upload(visitDocumentUpload);
        return new VisitDocumentResponse(visit.getUuid());
    }

    @RequestMapping(method = RequestMethod.POST, value = baseVisitDocumentUrl + "/uploadImage")
    @ResponseBody
    public String saveImage(@RequestBody DocumentImage image) {
        Patient patient = Context.getPatientService().getPatientByUuid(image.getPatientUuid());
        return patientImageService.saveDocument(patient.getId(), image.getEncounterTypeName(), image.getImage(), image.getFormat());
    }
}
