package com.study.userservice.controllers;

import java.util.List;
import java.util.Set;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.study.userservice.dto.CardRequestDTO;
import com.study.userservice.dto.CardResponseDTO;
import com.study.userservice.dto.UserResponseDTO;
import com.study.userservice.service.interfaces.CardService;
import com.study.userservice.service.interfaces.UserService;

@RestController
@RequestMapping(path = "/api/v1")
public class CardController {

  private final UserService userService;
  private final CardService cardService;

  public CardController(UserService userService, CardService cardService) {
    this.userService = userService;
    this.cardService = cardService;
  }

  @PostMapping(path = "/card")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<CardResponseDTO> createCard(@RequestBody CardRequestDTO cardRequestDTO) {
    CardResponseDTO cardResponseDTO = cardService.saveOne(cardRequestDTO);
    return ResponseEntity.ok(cardResponseDTO);
  }

  @PostMapping(path = "/cards")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<List<CardResponseDTO>> createCards(
      @RequestBody List<CardRequestDTO> cardRequestDTOs) {
    List<CardResponseDTO> cardResponseDTO = cardService.saveMany(cardRequestDTOs);
    return ResponseEntity.ok(cardResponseDTO);
  }

  @GetMapping("/card/{id}")
  public ResponseEntity<CardResponseDTO> getCardById(@PathVariable Long id) {
    CardResponseDTO cardResponseDTO = cardService.getById(id);
    return ResponseEntity.ok(cardResponseDTO);
  }

  @GetMapping("/cards/{ids}")
  public ResponseEntity<List<CardResponseDTO>> getCardsByIds(@PathVariable Set<Long> ids) {
    List<CardResponseDTO> cardResponseDTOs = cardService.getByIds(ids);
    return ResponseEntity.ok(cardResponseDTOs);
  }

  @PutMapping("/card/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<CardResponseDTO> updateCard(
      @PathVariable Long id, @RequestBody CardRequestDTO cardRequestDTO) {
    CardResponseDTO cardResponseDTO = cardService.updateCard(id, cardRequestDTO);
    return ResponseEntity.ok(cardResponseDTO);
  }

  @GetMapping(path = "/card/random")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<CardResponseDTO> addCard() {
    UserResponseDTO userResponseDTO = userService.getRandomUser();
    CardResponseDTO cardResponseDTO = cardService.addRandomCard(userResponseDTO);
    return ResponseEntity.ok(cardResponseDTO);
  }

  @GetMapping(path = "/cards/{page}/{itemsPerPage}")
  public ResponseEntity<List<CardResponseDTO>> getAllCards(
      @PathVariable Integer page, @PathVariable Integer itemsPerPage) {
    List<CardResponseDTO> cardResponseDTOs = cardService.getAllCards(page, itemsPerPage);
    return ResponseEntity.ok(cardResponseDTOs);
  }

  @DeleteMapping("/card/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<CardResponseDTO> deleteCardById(@PathVariable Long id) {
    CardResponseDTO cardResponseDTO = cardService.deleteById(id);
    return ResponseEntity.ok(cardResponseDTO);
  }

  @GetMapping(path = "/card/last")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<CardResponseDTO> deleteLastCard() {
    CardResponseDTO cardResponseDTO = cardService.delCardLast();
    return ResponseEntity.ok(cardResponseDTO);
  }

  @GetMapping(path = "/card/user/{id}")
  public ResponseEntity<List<CardResponseDTO>> getCardsByUserId(@PathVariable Long id) {
    List<CardResponseDTO> cardResponseDTOs = cardService.getByUserId(id);
    return ResponseEntity.ok(cardResponseDTOs);
  }

  @GetMapping("/card/active/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<CardResponseDTO> changeActiveStatus(@PathVariable Long id) {
    CardResponseDTO cardResponseDTO = cardService.changeActive(id);
    return ResponseEntity.ok(cardResponseDTO);
  }
}
