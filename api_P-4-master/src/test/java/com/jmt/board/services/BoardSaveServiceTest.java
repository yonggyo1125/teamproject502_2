package com.jmt.board.services;

import com.jmt.board.controllers.RequestBoard;
import com.jmt.board.entities.Board;
import com.jmt.board.entities.BoardData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
//@ActiveProfiles("test")
public class BoardSaveServiceTest {

    @Autowired
    private BoardSaveService saveService;

    private Board board;

    @BeforeEach
    void init() {
        board = new Board();
        board.setBid("freeBoard1");
        board.setBName("자유게시판");
/*
         board = Board.builder()
                .bid("freetalk")
                .bName("자유게시판")
                 .gid(UUID.randomUUID().toString())
                 .listAccessType(Authority.ALL)
                 .writeAccessType(Authority.ALL)
                 .commentAccessType(Authority.ALL)
                 .viewAccessType(Authority.ALL)
                 .replyAccessType(Authority.ALL)
                 .locationAfterWriting("list")
                .build();
*/

    }

    @Test
    void saveTest() {
        RequestBoard form = new RequestBoard();
        form.setBid(board.getBid());
        form.setCategory("분류2");
        form.setPoster("작성자2");
        form.setSubject("제목2");
        form.setContent("내용2");
        form.setGuestPw("1234ab");

        BoardData data = saveService.save(form);
        System.out.println(data);

        form.setMode("update");
        form.setSeq(data.getSeq());

        BoardData data2 = saveService.save(form);
        System.out.println(data2);
    }
}