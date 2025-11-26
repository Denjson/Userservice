package com.study.userservice.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections4.IterableUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.study.userservice.dto.CardRequestDTO;
import com.study.userservice.dto.CardResponseDTO;
import com.study.userservice.dto.UserResponseDTO;
import com.study.userservice.entity.Card;
import com.study.userservice.entity.User;
import com.study.userservice.exceptions.CardQuantityLimitException;
import com.study.userservice.exceptions.DuplicatedValueException;
import com.study.userservice.exceptions.IdNotFoundException;
import com.study.userservice.mappers.CardMapper;
import com.study.userservice.repository.CardRepository;
import com.study.userservice.repository.UserRepository;
import com.study.userservice.service.interfaces.CardService;

@Service
public class CardServiceImpl implements CardService {

  private static final Logger log = LoggerFactory.getLogger(CardServiceImpl.class);
  private final CardRepository cardRepository;
  private final UserRepository userRepository;
  private final CardMapper cardMapper;

  public CardServiceImpl(
      CardRepository cardRepository, UserRepository userRepository, CardMapper cardMapper) {
    this.cardRepository = cardRepository;
    this.userRepository = userRepository;
    this.cardMapper = cardMapper;
  }

  @CacheEvict(value = "allcards", allEntries = true)
  public CardResponseDTO saveOne(CardRequestDTO cardRequestDTO) {
    checkCard(cardRequestDTO);
    Card card = cardRepository.save(cardMapper.toEntity(cardRequestDTO));
    log.info("___User found. Card added: {}", cardRequestDTO.toString());
    return cardMapper.toDTO(card);
  }

  public void checkCard(CardRequestDTO cardRequestDTO) {
    userRepository
        .findById(cardRequestDTO.getUserId())
        .orElseThrow(
            () -> new IdNotFoundException("User not found with id: " + cardRequestDTO.getUserId()));
    if (cardRepository.getByNumber(cardRequestDTO.getNumber()).isPresent()) {
      throw new DuplicatedValueException(
          "Card number is duplicated: " + cardRequestDTO.getNumber());
    }
    Optional<List<Card>> cards = cardRepository.findByUserId(cardRequestDTO.getUserId());
    if (cards.get().size() > 4) {
      throw new CardQuantityLimitException("Users are not allowed to have more than 5 cards");
    }
  }

  @CacheEvict(value = "allcards", allEntries = true)
  public List<CardResponseDTO> saveMany(List<CardRequestDTO> cardRequestDTOs) {
    for (CardRequestDTO cardRequestDTO : cardRequestDTOs) {
      checkCard(cardRequestDTO);
    }
    List<Card> cards = cardMapper.manyToEntity(cardRequestDTOs);
    Set<Long> usersFromCards = cards.stream().map(Card::getUserId).collect(Collectors.toSet());
    Optional<List<User>> usersFound = userRepository.findByIdIn(usersFromCards);
    if (usersFound.get().isEmpty()) {
      throw new IdNotFoundException("Users not found with ids: " + usersFromCards);
    }
    if (usersFound.get().size() == usersFromCards.size()) {
      cardRepository.saveAll(cards);
      log.info("___All users found. Saved Cards: {}", cards);
      return cardMapper.toDTOs(cards);
    } else {
      throw new IdNotFoundException("Users not found with ids: " + usersFromCards);
    }
  }

  @Cacheable(value = "cards", key = "#id")
  public CardResponseDTO getById(Long id) {
    Card card =
        cardRepository
            .findById(id)
            .orElseThrow(() -> new IdNotFoundException("Card not found with id: " + id));
    return cardMapper.toDTO(card);
  }

  public List<CardResponseDTO> getByIds(Set<Long> ids) {
    List<Card> cards = cardRepository.findByIdIn(ids).get();
    if (cards.isEmpty()) {
      throw new IdNotFoundException("Cards not found with ids: " + ids);
    }
    log.info("___Cards found: {}", cards);
    return cardMapper.toDTOs(cards);
  }

  @Cacheable(value = "allcards", key = "#page + '-' + #itemsPerPage")
  public List<CardResponseDTO> getAllCards(Integer page, Integer itemsPerPage) {
    Pageable pageable = PageRequest.of(page, itemsPerPage, Sort.by("Id"));
    List<Card> cards = IterableUtils.toList(cardRepository.findAll(pageable));
    if (cards.isEmpty()) {
      throw new IdNotFoundException("List of cards is empty");
    }
    List<CardResponseDTO> cardResponseDTOs = cardMapper.toDTOs(cards);
    return cardResponseDTOs;
  }

  //  @Cacheable(value = "cards", key = "#id")
  public List<CardResponseDTO> getByUserId(Long id) {
    List<Card> cards = cardRepository.findByUserId(id).get();
    if (cards.size() == 0) {
      throw new IdNotFoundException("User not found with id: " + id);
    }
    return cardMapper.toDTOs(cards);
  }

  @Caching(
      evict = {
        @CacheEvict(value = "cards", key = "#id"),
        @CacheEvict(value = "allcards", allEntries = true)
      })
  public CardResponseDTO deleteById(Long id) {
    Card card =
        cardRepository
            .findById(id)
            .orElseThrow(() -> new IdNotFoundException("Card not found with id: " + id));
    cardRepository.deleteById(id);
    return cardMapper.toDTO(card);
  }

  @CacheEvict(value = "allcards", allEntries = true)
  public CardResponseDTO delCardLast() {
    Card card =
        cardRepository
            .findTopByOrderByIdDesc()
            .orElseThrow(() -> new IdNotFoundException("Card not found"));
    log.info("___Delete last card: {}", card);
    CardResponseDTO сardResponseDTO = cardMapper.toDTO(card);
    cardRepository.delete(card);
    return сardResponseDTO;
  }

  @CachePut(value = "cards", key = "#id")
  @CacheEvict(value = "allcards", allEntries = true)
  public CardResponseDTO updateCard(Long id, CardRequestDTO cardDetails) {
    Optional<User> userOptional = userRepository.findById(cardDetails.getUserId());
    Card card =
        cardRepository
            .findById(id)
            .orElseThrow(() -> new IdNotFoundException("Card not found with id: " + id));
    if (userOptional.isPresent()) {
      card.setUserId(cardDetails.getUserId());
      card.setHolder(userOptional.get().getName());
      card.setNumber(cardDetails.getNumber());
      card.setExpirationDate(cardDetails.getExpirationDate());
      if (cardDetails.getExpirationDate().compareTo(LocalDateTime.now()) > 0) {
        card.setActive(cardDetails.isActive());
      } else {
        card.setActive(!cardDetails.isActive());
      }
      cardRepository.save(card);
      log.info("___Card updated: {}", card);
    } else {
      log.info("___User with ID: {} not found.", cardDetails.getUserId());
      throw new IdNotFoundException("User not found with id: " + cardDetails.getUserId());
    }
    return cardMapper.toDTO(card);
  }

  @CacheEvict(value = "allcards", allEntries = true)
  public CardResponseDTO addRandomCard(UserResponseDTO userResponseDto) {
    Card card = new Card();
    card.setUserId(userResponseDto.getId());
    card.setNumber((long) (Math.random() * 10000000) + 1000000);
    card.setHolder(userResponseDto.getName());
    card.setExpirationDate(LocalDateTime.now().plusYears(2));
    card.setActive(userResponseDto.isActive());
    log.info("___Card added to Random user: {}", card);
    cardRepository.save(card);
    return cardMapper.toDTO(card);
  }

  @CacheEvict(value = "allcards", allEntries = true)
  public CardResponseDTO changeActive(Long id) {
    Card card =
        cardRepository
            .findById(id)
            .orElseThrow(() -> new IdNotFoundException("Card not found with id: " + id));
    card.setActive(!card.isActive());
    cardRepository.save(card);
    log.info("___Card updated: {}", card.toString());
    return cardMapper.toDTO(card);
  }
}
