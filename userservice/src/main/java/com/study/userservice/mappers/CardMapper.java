package com.study.userservice.mappers;

import java.util.List;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;

import com.study.userservice.dto.CardRequestDTO;
import com.study.userservice.dto.CardResponseDTO;
import com.study.userservice.entity.Card;

@Mapper(componentModel = "spring")
public interface CardMapper {

  public CardResponseDTO toDTO(Card card);

  public List<CardResponseDTO> toDTOs(List<Card> cards);

  @InheritInverseConfiguration
  public Card toEntity(CardRequestDTO cardRequestDTO);

  @InheritInverseConfiguration
  public List<Card> manyToEntity(List<CardRequestDTO> cardDTOs);
}
