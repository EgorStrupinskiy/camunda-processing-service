package com.azati.warshipprocessing.mapper;

import com.azati.warshipprocessing.dto.SessionDTO;
import com.azati.warshipprocessing.entity.Session;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface SessionMapper {

    SessionDTO toDTO(Session session);

    List<SessionDTO> toDTOList(List<Session> sessions);
}
