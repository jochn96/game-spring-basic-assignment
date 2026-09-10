package com.gamebasic.game.service;

import com.gamebasic.game.dto.CreateRequest;
import com.gamebasic.game.dto.GameDetailResponse;
import com.gamebasic.game.dto.GameSummaryResponse;
import com.gamebasic.game.dto.ProgressRequest;
import com.gamebasic.game.dto.RenameRequest;
import com.gamebasic.game.entity.Game;
import com.gamebasic.game.repository.GameRepository;
import com.gamebasic.runcard.dto.CardResponse;
import com.gamebasic.runcard.dto.RunCardRequest;
import com.gamebasic.runcard.entity.RunCard;
import com.gamebasic.runcard.repository.RunCardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GameService {

    private final GameRepository gameRepository;
    private final RunCardRepository runCardRepository;

    @Transactional
    public GameDetailResponse createGame(CreateRequest request) {
        Game game = gameRepository.save(new Game(request.getPlayerName()));
        saveDeck(game, request.getDeck());
        List<RunCard> cards = runCardRepository.findAllByGameOrderByIdAsc(game);
        List<CardResponse> deck = new ArrayList<>();
        for (RunCard card : cards) {
            deck.add(new CardResponse(card.getId(), card.getCardType(), card.getAcquiredFloor()));
        }
        return new GameDetailResponse(
            game.getId(),
            game.getPlayerName(),
            game.getCurrentHp(),
            game.getCurrentFloor(),
            game.getPhase(),
            game.getStatus(),
            deck
        );
    }

    private void saveDeck(Game game, List<RunCardRequest> deck) {
        List<RunCard> cards = new ArrayList<>();
        for (RunCardRequest card : deck) {
            cards.add(new RunCard(game, card.getCardType(), card.getAcquiredFloor()));
        }
        runCardRepository.saveAll(cards);
    }

    private Game findGame(Long gameId) {
        return gameRepository.findById(gameId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @Transactional
    public GameDetailResponse updateProgress(Long gameId, ProgressRequest request) {
        Game game = findGame(gameId);
        game.updateProgress(
            request.getCurrentHp(),
            request.getCurrentFloor(),
            request.getPhase(),
            request.getStatus()
        );
        // 요청의 deck은 저장할 덱 전체이므로 기존 카드를 모두 지우고 요청 순서대로 다시 저장합니다.
        runCardRepository.deleteAllByGame(game);
        saveDeck(game, request.getDeck());
        List<RunCard> cards = runCardRepository.findAllByGameOrderByIdAsc(game);
        List<CardResponse> deck = new ArrayList<>();
        for (RunCard card : cards) {
            deck.add(new CardResponse(card.getId(), card.getCardType(), card.getAcquiredFloor()));
        }
        return new GameDetailResponse(
            game.getId(),
            game.getPlayerName(),
            game.getCurrentHp(),
            game.getCurrentFloor(),
            game.getPhase(),
            game.getStatus(),
            deck
        );
    }

    @Transactional(readOnly = true)
    public List<GameSummaryResponse> getGames() {
        List<Game> games = gameRepository.findAllByOrderByIdDesc();
        List<GameSummaryResponse> result = new ArrayList<>();
        for (Game game : games) {
            result.add(new GameSummaryResponse(
                game.getId(),
                game.getPlayerName(),
                game.getCurrentFloor(),
                game.getCurrentHp(),
                game.getPhase(),
                game.getStatus()
            ));
        }
        return result;
    }

    @Transactional(readOnly = true)
    public GameDetailResponse getGame(Long gameId) {
        Game game = findGame(gameId);
        List<RunCard> cards = runCardRepository.findAllByGameOrderByIdAsc(game);
        List<CardResponse> deck = new ArrayList<>();
        for (RunCard card : cards) {
            deck.add(new CardResponse(card.getId(), card.getCardType(), card.getAcquiredFloor()));
        }
        return new GameDetailResponse(
            game.getId(),
            game.getPlayerName(),
            game.getCurrentHp(),
            game.getCurrentFloor(),
            game.getPhase(),
            game.getStatus(),
            deck
        );
    }

    @Transactional
    public void renameGame(Long gameId, RenameRequest request) {
        Game game = findGame(gameId);
        game.rename(request.getPlayerName());
        // save()를 호출하지 않아도, 트랜잭션 커밋 시점에 변경 감지(dirty checking)로 UPDATE 쿼리가 나간다.
    }

    @Transactional
    public void deleteGame(Long gameId) {
        Game game = findGame(gameId);
        // 단방향 연관관계라 자식(RunCard)을 Repository로 먼저 명시적으로 지운 뒤 부모(Game)를 지운다.
        runCardRepository.deleteAllByGame(game);
        gameRepository.delete(game);
    }
}
