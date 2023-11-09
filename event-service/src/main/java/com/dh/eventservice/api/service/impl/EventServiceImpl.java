package com.dh.eventservice.api.service.impl;

import com.dh.eventservice.api.Exceptions.ResourceNotFoundExceptions;
import com.dh.eventservice.api.config.ModelMapperConfig;
import com.dh.eventservice.api.service.EventService;


import com.dh.eventservice.domain.DTO.EventDTO;
import com.dh.eventservice.domain.model.Category;
import com.dh.eventservice.domain.model.Event;
import com.dh.eventservice.domain.repository.CategoryRepository;
import com.dh.eventservice.domain.repository.DateTimeRepository;
import com.dh.eventservice.domain.repository.EventRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;


@Service
public class EventServiceImpl implements EventService {

    private static final Logger logger = LoggerFactory.getLogger(EventServiceImpl.class);

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private DateTimeRepository dateTimeRepository;

    @Autowired
    private ModelMapperConfig mapper;

    @Autowired
    private ObjectMapper obmapper;


    @Override
    public List<Event> getListByCategory(String category) {
        Category category1 = categoryRepository.findAllByDescription(category);

        List<Event> events = eventRepository.findAllByCategoryDescription(category1.getDescription());

        logger.info("Se listaron los eventos de categoria: {}", category);

        return events;
    }

    /*voy a traer una lista de eventos de auerdo a una fecha*/
    @Override
    public List<Event> getListByDate(String date) throws ResourceNotFoundExceptions {
		/*DateTime dateTime1 = dateTimeRepository.findByDateTime(LocalDateTime.parse(dateTime));
		System.out.println(dateTime1);*/
        String fechaSinHora = date.substring(0, 10); // Extrae los primeros 10 caracteres (yyyy-MM-dd)
        List<Event> events = eventRepository.findAllByDateList(fechaSinHora);
        logger.info("Se listaron los eventos de la fecha: {}", date);
        return events;
    }

    @Override
    public List<Event> getListByCountry(String country) {
        return eventRepository.findEventsByCountry(country);
    }

    @Override
    public List<Event> getListByCountryAndCity(String city, String country) {
        return eventRepository.findEventsByCityAndCountry(city, country);
    }

    @Override
    public List<EventDTO> getListByCategoryId(Integer id) {
        List<Event> events = eventRepository.findAllByCategoryId(id);

        logger.info("Se listaron los eventos de categoria: {}", id);

        return mapper.getModelMapper().map(events, List.class);

    }


    @Override
    public List<EventDTO> getListByName(String name) {
        List<EventDTO> events = eventRepository.findAllByName(name);

        logger.info("Se listaron los eventos del recinto: {}", name);
        return events;
    }


    @Override
    public List<Event> getEventsByVenue(String venue) {
        return eventRepository.findEventsByVenue(venue);
    }

    @Override
    public List<EventDTO> getAllEvents() {
        List<Event> events = eventRepository.findAll();

        logger.info("Se listaron todos los eventos");

        return mapper.getModelMapper().map(events, List.class);

    }

    @Override
    public EventDTO save(EventDTO eventDto) {

        Event event = mapper.getModelMapper().map(eventDto, Event.class);

        logger.info("Se guardó el evento: {}", event);

        Event guardado = eventRepository.save(event);

        System.out.println(guardado);
        return mapper.getModelMapper().map(guardado, EventDTO.class);

    }

    @Override
    public void delete(Integer id) throws ResourceNotFoundExceptions {
        if (eventRepository.findById((id)) == null) {
            logger.error("No existe el elmento a eliminar");
            throw new ResourceNotFoundExceptions("No existe el elmento a eliminar");
        } else {
            eventRepository.deleteById(id);
            logger.info("Se elimino correctamente el elemento con id: " + id);
        }

    }

    @Override
    public EventDTO findById(Integer id) throws ResourceNotFoundExceptions {
        Optional<Event> event = eventRepository.findById((id));
        EventDTO eventDTO = null;
        if (event.isPresent()) {
            eventDTO = obmapper.convertValue(event, EventDTO.class);
        } else {
            throw new ResourceNotFoundExceptions("No existe el elemento buscado con id " + id);
        }
        return eventDTO;
    }


    @Override
    public String update(EventDTO eventDTO) throws ResourceNotFoundExceptions {
        Optional<Event> event = eventRepository.findById(eventDTO.getId());
        String response;
        if (event.isPresent()) {
            eventRepository.save(this.updateDb(event.get(), eventDTO));
            mapper.getModelMapper().map(eventDTO, EventDTO.class);
            response = "Successful update";
        } else {
            throw new ResourceNotFoundExceptions("El evento no pudo ser actualizado");
        }
        return response;
    }


    private Event updateDb(Event event, EventDTO eventDTO) {
        if (eventDTO.getDateList() != null) {
            event.setDateList(eventDTO.getDateList());
        }

        if (eventDTO.getDescription() != null) {
            event.setDescription(eventDTO.getDescription());
        }

        if (eventDTO.getName() != null) {
            event.setName(eventDTO.getName());
        }

        if (eventDTO.getMiniImageUrl() != null) {
            event.setMiniImageUrl(eventDTO.getMiniImageUrl());
        }

        if (eventDTO.getBannerImageUrl() != null) {
            event.setBannerImageUrl(eventDTO.getBannerImageUrl());
        }

        if (eventDTO.getDetailImageUrl() != null) {
            event.setDetailImageUrl(eventDTO.getDetailImageUrl());
        }

        if (eventDTO.getVenue() != null) {
            event.setVenue(eventDTO.getVenue());
        }

        if (eventDTO.getCategory() != null) {
            event.setCategory(mapper.getModelMapper().map(eventDTO.getCategory(), Category.class));
        }

        return event;
    }
}
