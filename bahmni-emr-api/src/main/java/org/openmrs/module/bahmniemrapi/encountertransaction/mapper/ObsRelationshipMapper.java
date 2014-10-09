package org.openmrs.module.bahmniemrapi.encountertransaction.mapper;

import org.bahmni.module.obsrelationship.api.ObsRelationService;
import org.bahmni.module.obsrelationship.model.ObsRelationship;
import org.openmrs.module.bahmniemrapi.encountertransaction.contract.BahmniObservation;
import org.openmrs.module.emrapi.encounter.*;
import org.openmrs.module.emrapi.encounter.domain.EncounterTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class ObsRelationshipMapper {
    private ObsRelationService obsRelationService;
    private ObservationMapper observationMapper;
    private EncounterProviderMapper encounterProviderMapper;

    @Autowired
    public ObsRelationshipMapper(ObsRelationService obsRelationService, ObservationMapper observationMapper, EncounterProviderMapper encounterProviderMapper) {
        this.obsRelationService = obsRelationService;
        this.observationMapper = observationMapper;
        this.encounterProviderMapper = encounterProviderMapper;
    }

    public List<BahmniObservation> map(List<BahmniObservation> bahmniObservations, String encounterUuid, Set<EncounterTransaction.Provider> providers) {
        List<ObsRelationship> obsRelationshipsInEncounter = obsRelationService.getRelationsWhereSourceObsInEncounter(encounterUuid);
        for (BahmniObservation bahmniObservation : bahmniObservations) {
            for (ObsRelationship obsRelationship : obsRelationshipsInEncounter) {
                if (bahmniObservation.isSameAs(obsRelationship.getSourceObs())) {
                    org.openmrs.module.bahmniemrapi.obsrelation.contract.ObsRelationship targetObsRelation =
                            new org.openmrs.module.bahmniemrapi.obsrelation.contract.ObsRelationship();
                    targetObsRelation.setRelationshipType(obsRelationship.getObsRelationshipType().getName());
                    targetObsRelation.setUuid(obsRelationship.getUuid());
                    EncounterTransaction.Observation etObservation = observationMapper.map(obsRelationship.getTargetObs());
                    targetObsRelation.setTargetObs(new BahmniObservation(etObservation));
                    bahmniObservation.setTargetObsRelation(targetObsRelation);
                    bahmniObservation.setProviders(providers);
                }
            }
        }
        return bahmniObservations;
    }

    public List<BahmniObservation> map(List<ObsRelationship> obsRelationships) {
        List<BahmniObservation> bahmniObservations = new ArrayList<>();
        for (ObsRelationship obsRelationship : obsRelationships) {
            BahmniObservation sourceObservation = new BahmniObservation(observationMapper.map(obsRelationship.getSourceObs()));
            sourceObservation.setProviders(encounterProviderMapper.convert(obsRelationship.getSourceObs().getEncounter().getEncounterProviders()));

            BahmniObservation targetObservation = new BahmniObservation(observationMapper.map(obsRelationship.getTargetObs()));

            org.openmrs.module.bahmniemrapi.obsrelation.contract.ObsRelationship targetObsRelation =
                    new org.openmrs.module.bahmniemrapi.obsrelation.contract.ObsRelationship(targetObservation, obsRelationship.getUuid(), obsRelationship.getObsRelationshipType().getName());
            sourceObservation.setTargetObsRelation(targetObsRelation);
            bahmniObservations.add(sourceObservation);

        }
        return bahmniObservations;
    }
}