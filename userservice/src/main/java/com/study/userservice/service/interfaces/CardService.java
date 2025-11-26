package com.study.userservice.service.interfaces;

import java.util.List;
import java.util.Set;

import com.study.userservice.dto.CardRequestDTO;
import com.study.userservice.dto.CardResponseDTO;
import com.study.userservice.dto.UserResponseDTO;

public interface CardService {

  public CardResponseDTO saveOne(CardRequestDTO cardRequestDTO);

  public List<CardResponseDTO> saveMany(List<CardRequestDTO> cardRequestDTOs);

  public CardResponseDTO getById(Long id);

  public List<CardResponseDTO> getByIds(Set<Long> ids);

  public CardResponseDTO updateCard(Long id, CardRequestDTO cardRequestDTO);

  public CardResponseDTO deleteById(Long id);

  public List<CardResponseDTO> getAllCards(Integer page, Integer itemsPerPage);

  public CardResponseDTO delCardLast();

  public CardResponseDTO addRandomCard(UserResponseDTO u);

  public List<CardResponseDTO> getByUserId(Long id);

  public CardResponseDTO changeActive(Long id);
}
