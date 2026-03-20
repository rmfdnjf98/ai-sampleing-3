-- 유저 더미 데이터
INSERT INTO user_tb (username, password, created_at) VALUES ('ssar', '$2a$10$v2smN3fzz4YAwUyxTtcBN.iMIsgi0BZUUMgnqnSvndLp2LheBprVm', NOW());
INSERT INTO user_tb (username, password, created_at) VALUES ('cos', '$2a$10$v2smN3fzz4YAwUyxTtcBN.iMIsgi0BZUUMgnqnSvndLp2LheBprVm', NOW());

-- 게시글 더미 데이터
INSERT INTO board_tb (title, content, user_id, created_at) VALUES ('첫 번째 게시글', '내용1', 1, NOW());
INSERT INTO board_tb (title, content, user_id, created_at) VALUES ('두 번째 게시글', '내용2', 1, NOW());
INSERT INTO board_tb (title, content, user_id, created_at) VALUES ('세 번째 게시글', '내용3', 2, NOW());
INSERT INTO board_tb (title, content, user_id, created_at) VALUES ('네 번째 게시글', '내용4', 1, NOW());
INSERT INTO board_tb (title, content, user_id, created_at) VALUES ('다섯 번째 게시글', '내용5', 2, NOW());
INSERT INTO board_tb (title, content, user_id, created_at) VALUES ('여섯 번째 게시글', '내용6', 1, NOW());
INSERT INTO board_tb (title, content, user_id, created_at) VALUES ('일곱 번째 게시글', '내용7', 2, NOW());
INSERT INTO board_tb (title, content, user_id, created_at) VALUES ('여덟 번째 게시글', '내용8', 1, NOW());
INSERT INTO board_tb (title, content, user_id, created_at) VALUES ('아홉 번째 게시글', '내용9', 2, NOW());
INSERT INTO board_tb (title, content, user_id, created_at) VALUES ('열 번째 게시글', '내용10', 1, NOW());
INSERT INTO board_tb (title, content, user_id, created_at) VALUES ('열한 번째 게시글', '내용11', 2, NOW());
INSERT INTO board_tb (title, content, user_id, created_at) VALUES ('열두 번째 게시글', '내용12', 1, NOW());
INSERT INTO board_tb (title, content, user_id, created_at) VALUES ('열세 번째 게시글', '내용13', 2, NOW());
INSERT INTO board_tb (title, content, user_id, created_at) VALUES ('열네 번째 게시글', '내용14', 1, NOW());
INSERT INTO board_tb (title, content, user_id, created_at) VALUES ('열오 번째 게시글', '내용15', 2, NOW());
INSERT INTO board_tb (title, content, user_id, created_at) VALUES ('열육 번째 게시글', '내용16', 1, NOW());
INSERT INTO board_tb (title, content, user_id, created_at) VALUES ('열칠 번째 게시글', '내용17', 2, NOW());
INSERT INTO board_tb (title, content, user_id, created_at) VALUES ('열팔 번째 게시글', '내용18', 1, NOW());
INSERT INTO board_tb (title, content, user_id, created_at) VALUES ('열아홉 번째 게시글', '내용19', 2, NOW());
INSERT INTO board_tb (title, content, user_id, created_at) VALUES ('스무 번째 게시글', '내용20', 1, NOW());
INSERT INTO board_tb (title, content, user_id, created_at) VALUES ('스물한 번째 게시글', '내용21', 2, NOW());
INSERT INTO board_tb (title, content, user_id, created_at) VALUES ('스물두 번째 게시글', '내용22', 1, NOW());
INSERT INTO board_tb (title, content, user_id, created_at) VALUES ('스물세 번째 게시글', '내용23', 2, NOW());

-- 댓글 더미 데이터
INSERT INTO reply_tb (comment, user_id, board_id, created_at) VALUES ('첫 번째 게시글에 ssar이 작성한 댓글입니다.', 1, 1, NOW());
INSERT INTO reply_tb (comment, user_id, board_id, created_at) VALUES ('첫 번째 게시글에 cos가 작성한 댓글입니다.', 2, 1, NOW());
INSERT INTO reply_tb (comment, user_id, board_id, created_at) VALUES ('두 번째 게시글에 cos가 작성한 댓글입니다.', 2, 2, NOW());
