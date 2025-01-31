package io.gravitee.apim.infra.crud_service.dictionary;

import static io.gravitee.repository.management.model.Audit.AuditProperties.DICTIONARY;
import static io.gravitee.repository.management.model.Dictionary.AuditEvent.DICTIONARY_UPDATED;

import io.gravitee.apim.core.application_dictionary.crud_service.ApplicationDictionaryCrudService;
import io.gravitee.repository.exceptions.TechnicalException;
import io.gravitee.repository.management.api.DictionaryRepository;
import io.gravitee.repository.management.model.Dictionary;
import io.gravitee.repository.management.model.DictionaryType;
import io.gravitee.repository.management.model.LifecycleState;
import io.gravitee.rest.api.model.EventType;
import io.gravitee.rest.api.service.AuditService;
import io.gravitee.rest.api.service.EventService;
import io.gravitee.rest.api.service.common.ExecutionContext;
import io.gravitee.rest.api.service.exceptions.TechnicalManagementException;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ApplicationDictionaryCrudServiceImpl implements ApplicationDictionaryCrudService {

    private final DictionaryRepository dictionaryRepository;
    private final EventService eventService;
    private final AuditService auditService;

    @Autowired
    public ApplicationDictionaryCrudServiceImpl(
        @Lazy DictionaryRepository dictionaryRepository,
        EventService eventService,
        AuditService auditService
    ) {
        this.dictionaryRepository = dictionaryRepository;
        this.eventService = eventService;
        this.auditService = auditService;
    }

    @Override
    public Optional<Dictionary> findById(String applicationId) {
        try {
            return dictionaryRepository.findById(applicationId);
        } catch (TechnicalException e) {
            throw new TechnicalManagementException("An error occurs while trying to find a dictionary for application " + applicationId, e);
        }
    }

    @Override
    public void delete(ExecutionContext executionContext, Dictionary dictionary) {
        var workingDictionary = dictionary;
        if (dictionary.getType() == DictionaryType.DYNAMIC) {
            log.debug("Stop dictionary for application {}", dictionary.getId());
            workingDictionary = stop(executionContext, dictionary);
        }

        log.debug("Delete dictionary for application {}", workingDictionary.getId());
        try {
            dictionaryRepository.delete(workingDictionary.getId());
        } catch (TechnicalException e) {
            throw new TechnicalManagementException("An error occurs while trying to delete dictionary " + workingDictionary.getId(), e);
        }

        log.debug("Unpublish dictionary for application {}", workingDictionary.getId());
        eventService.createDictionaryEvent(
            executionContext,
            Collections.singleton(executionContext.getEnvironmentId()),
            executionContext.getOrganizationId(),
            EventType.UNPUBLISH_DICTIONARY,
            workingDictionary
        );
        auditService.createAuditLog(
            executionContext,
            Collections.singletonMap(DICTIONARY, workingDictionary.getName()),
            Dictionary.AuditEvent.DICTIONARY_DELETED,
            new Date(),
            workingDictionary,
            null
        );
    }

    @Override
    public Dictionary create(ExecutionContext executionContext, Dictionary dictionary) {
        dictionary.setEnvironmentId(executionContext.getEnvironmentId());

        dictionary.setCreatedAt(new Date());
        dictionary.setState(LifecycleState.STOPPED);
        dictionary.setUpdatedAt(dictionary.getCreatedAt());

        try {
            Dictionary createdDictionary = dictionaryRepository.create(dictionary);
            auditService.createAuditLog(
                executionContext,
                Collections.singletonMap(DICTIONARY, dictionary.getName()),
                DICTIONARY_UPDATED,
                createdDictionary.getCreatedAt(),
                null,
                createdDictionary
            );

            return createdDictionary;
        } catch (TechnicalException e) {
            throw new TechnicalManagementException("An error occurs while trying to create dictionary " + dictionary.getId(), e);
        }
    }

    @Override
    public Dictionary update(ExecutionContext executionContext, Dictionary dictionary) {
        dictionary.setEnvironmentId(executionContext.getEnvironmentId());

        dictionary.setUpdatedAt(new Date());
        Dictionary updatedDictionary;
        try {
            updatedDictionary = dictionaryRepository.update(dictionary);
        } catch (TechnicalException e) {
            throw new TechnicalManagementException("An error occurs while trying to update dictionary " + dictionary.getId(), e);
        }

        auditService.createAuditLog(
            executionContext,
            Collections.singletonMap(DICTIONARY, dictionary.getName()),
            DICTIONARY_UPDATED,
            updatedDictionary.getUpdatedAt(),
            dictionary,
            updatedDictionary
        );

        return updatedDictionary;
    }

    private Dictionary stop(ExecutionContext executionContext, Dictionary dictionary) {
        // add deployment date
        dictionary.setUpdatedAt(new Date());
        dictionary.setState(LifecycleState.STOPPED);

        Dictionary updatedDictionary;
        try {
            updatedDictionary = dictionaryRepository.update(dictionary);
        } catch (TechnicalException e) {
            throw new TechnicalManagementException("An error occurs while trying to stop dictionary " + dictionary.getId(), e);
        }

        // And create event
        eventService.createDynamicDictionaryEvent(
            executionContext,
            Collections.singleton(executionContext.getEnvironmentId()),
            executionContext.getOrganizationId(),
            EventType.STOP_DICTIONARY,
            dictionary.getId()
        );

        // Audit
        auditService.createAuditLog(
            executionContext,
            Collections.singletonMap(DICTIONARY, dictionary.getName()),
            DICTIONARY_UPDATED,
            updatedDictionary.getUpdatedAt(),
            dictionary,
            updatedDictionary
        );

        return updatedDictionary;
    }
}
