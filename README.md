# Crimson Citadel — Game Spring Basic Assignment


- Lv 1. 설정 파일 작성: Docker MySQL 연결
    `application.properties`를 새로 작성해 MySQL 접속 정보를 설정하고, `ddl-auto=update`로 재시작 후에도 데이터가 유지되도록 함.
 
- Lv 2. 의존성 주입(DI)
    `GameService`에 빠져 있던 `@Service`를 추가해 의존성 주입 오류를 해결.
 
- Lv 3. RESTful API: 게임 목록 조회 
    컨트롤러의 목록 조회 매핑 경로를 명세(`GET /games`)에 맞게 수정.
  
- Lv 4. @Transactional 
    게임 생성 저장 메서드에 잘못 붙어 있던 `readOnly = true`를 제거해 저장 시 500 에러 해결.
 
- Lv 5. Bean Validation: 게임 생성
    `RunCardRequest`에 Bean Validation 애너테이션(`@NotBlank`, `@Min`/`@Max` 등)을 추가하고, `CardResponse`를 완성해 게임 생성 API 요청 검증과 응답을 완료.
 
- Lv 6. 보상 카드 선택과 진행 저장
    `PUT /games/{gameId}/progress`를 구현해 보상 선택·전투·층 이동 시 덱 전체를 저장하도록 함.

- Lv 7. 저장된 여정 이어하기 
    `GameSummaryResponse`를 새로 만들고 `GET /games`(id 내림차순), `GET /games/{gameId}`(덱은 id 오름차순)를 구현해 저장된 여정을 이어할 수 있게 함.

- Lv 8. 더티 체킹: 이름 수정, 자식부터 삭제
    변경 감지(dirty checking)로 이름을 수정하고, 연관관계가 단방향이라 `RunCard`를 먼저 삭제한 뒤 `Game`을 삭제하도록 구현.

