package com.study.userservice;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.study.userservice.auth.Role;
import com.study.userservice.dto.CardRequestDTO;
import com.study.userservice.dto.CardResponseDTO;
import com.study.userservice.dto.UserRequestDTO;
import com.study.userservice.entity.Card;
import com.study.userservice.entity.User;
import com.study.userservice.mappers.CardMapper;
import com.study.userservice.mappers.UserMapper;
import com.study.userservice.repository.CardRepository;
import com.study.userservice.repository.UserRepository;
import com.study.userservice.service.CardServiceImpl;
import com.study.userservice.service.UserServiceImpl;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ExtendWith(MockitoExtension.class)
public class CardServiceTest {

  @Mock private UserRepository userRepository;
  @Mock private CardRepository cardRepository;

  @InjectMocks private UserServiceImpl userServiceImpl;

  @InjectMocks private CardServiceImpl cardServiceImpl;

  @Spy private UserMapper userMapper;
  @Spy private CardMapper cardMapper;

  //  private static final Logger log = LoggerFactory.getLogger(CardServiceTest.class);

  User existingUser;
  LocalDateTime stamp;
  UserRequestDTO userRequestDTO;
  Card existingCard;
  CardRequestDTO cardRequestDTO;

  @BeforeEach
  void setUp() {
    stamp = LocalDateTime.of(2027, 11, 25, 10, 30);
    existingUser = new User(1L, "John", "Connor", stamp, "john@mail.com", true, Role.USER);
    userRequestDTO = new UserRequestDTO(1L, "John", "Connor", stamp, "john@mail.com", true);
    existingCard = new Card(1L, 1L, 1234L, "CardHolder", stamp, true);
    cardRequestDTO = new CardRequestDTO(1L, 1L, 1234L, "CardHolder", stamp, true);
  }

  @Test
  void saveOneTest() {
    List<Card> cards = new ArrayList<>();
    cards.add(existingCard);
    when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
    when(cardRepository.getByNumber(1234L)).thenReturn(Optional.empty());
    when(cardRepository.findByUserId(1L)).thenReturn(Optional.of(cards));
    when(cardRepository.save(existingCard)).thenReturn(existingCard);
    CardResponseDTO result = cardServiceImpl.saveOne(cardRequestDTO);
    //    log.info("___ Result: {}", result);
    assertNotNull(result);
    assertEquals(existingCard.getNumber(), result.getNumber());
  }

  @Test
  void saveManyTest() {
    List<User> users = new ArrayList<>();
    users.add(existingUser);
    List<Card> cards = new ArrayList<>();
    List<CardRequestDTO> dtos = new ArrayList<>();
    for (Long i = 0L; i < 2; i++) {
      Card card = new Card(i, 1L, 1234L + i, "CardHolder", stamp, true);
      cards.add(card);
      CardRequestDTO cardRequestDTO =
          new CardRequestDTO(i, 1L, 1234L + i, "CardHolder", stamp, true);
      dtos.add(cardRequestDTO);
    }
    Set<Long> ids = Set.of(1L);
    when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
    when(userRepository.findByIdIn(ids)).thenReturn(Optional.of(users));
    //    when(cardRepository.getByNumber(1234L)).thenReturn(Optional.empty());
    //    when(cardRepository.getByNumber(1235L)).thenReturn(Optional.empty());
    when(cardRepository.findByUserId(1L)).thenReturn(Optional.of(cards));
    log.info("___ users: {}", users);
    log.info("___ cards: {}", cards);
    log.info("___ card dtos: {}", dtos);
    List<CardResponseDTO> result = cardServiceImpl.saveMany(dtos);
    log.info("___ Result: {}", result);
    assertEquals(cards.getFirst().getNumber(), result.getFirst().getNumber());
  }

  @Test
  void getByIdTest() {
    Long cardId = 1L;
    when(cardRepository.findById(cardId)).thenReturn(Optional.of(existingCard));
    CardResponseDTO result = cardServiceImpl.getById(cardId);
    log.info("___ Result: {}", result);
    assertNotNull(result);
    assertEquals(cardId, result.getId());
    assertEquals(existingCard.getNumber(), result.getNumber());
  }

  @Test
  void getByIdsTest() {
    Set<Long> ids = Set.of(0L, 1L);
    List<Card> cards = new ArrayList<>();
    for (Long i = 0L; i < 2; i++) {
      Card card = new Card(i, 1L, 1234L + i, "CardHolder", stamp, true);
      cards.add(card);
    }
    when(cardRepository.findByIdIn(ids)).thenReturn(Optional.of(cards));
    List<CardResponseDTO> result = cardServiceImpl.getByIds(ids);
    log.info("___ Cards: {}", cards);
    log.info("___ Result: {}", result);
    assertEquals(cards.size(), result.size());
    assertEquals(cards.getFirst().getNumber(), result.getFirst().getNumber());
  }

  @Test
  void getAllCardsTest() {
    List<Card> cards = new ArrayList<>();
    for (Long i = 0L; i < 2; i++) {
      Card card = new Card(i, 1L, 1234L + i, "CardHolder", stamp, true);
      cards.add(card);
    }
    Pageable pageable = PageRequest.of(0, 10, Sort.by("Id"));
    Page<Card> mockPage = new PageImpl<>(cards, pageable, 0);
    when(cardRepository.findAll(pageable)).thenReturn(mockPage);
    List<CardResponseDTO> result = cardServiceImpl.getAllCards(0, 10);
    log.info("___ Result: {}", result);
    assertEquals(cards.size(), result.size());
    assertEquals(cards.getFirst().getNumber(), result.getFirst().getNumber());
  }

  @Test
  void getByUserIdTest() {
    Long userId = 1L;
    List<Card> cards = new ArrayList<>();
    cards.add(existingCard);
    when(cardRepository.findByUserId(userId)).thenReturn(Optional.of(cards));
    List<CardResponseDTO> result = cardServiceImpl.getByUserId(userId);
    log.info("___ Result: {}", result);
    assertNotNull(result);
    assertEquals(existingCard.getNumber(), result.getFirst().getNumber());
  }

  @Test
  void deleteByIdTest() {
    when(cardRepository.findById(1L)).thenReturn(Optional.of(existingCard));
    assertThatCode(() -> cardServiceImpl.deleteById(1L)).doesNotThrowAnyException();
    verify(cardRepository).deleteById(1L);
  }

  @Test
  void delCardLastTest() {
    when(cardRepository.findTopByOrderByIdDesc()).thenReturn(Optional.of(existingCard));
    assertThatCode(() -> cardServiceImpl.delCardLast()).doesNotThrowAnyException();
    verify(cardRepository).delete(existingCard);
  }

  @Test
  void updateCardTest() {
    List<Card> cards = new ArrayList<>();
    cards.add(existingCard);
    when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
    when(cardRepository.findById(1L)).thenReturn(Optional.of(existingCard));
    CardResponseDTO result = cardServiceImpl.updateCard(1L, cardRequestDTO);
    assertNotNull(result);
    assertEquals(existingCard.getNumber(), result.getNumber());
  }

  //  @Test
  void addRandomCardTest() {
    // generating card with random number
  }

  @Test
  void changeActiveTest() {
    when(cardRepository.findById(1L)).thenReturn(Optional.of(existingCard));
    CardResponseDTO result = cardServiceImpl.changeActive(1L);
    log.info("___ Is user active? {}", result);
    assertEquals(existingCard.isActive(), false);
  }
}
