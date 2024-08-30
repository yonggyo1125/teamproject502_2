package com.jmt.board.controllers;

import com.jmt.board.entities.BoardData;
import com.jmt.board.services.BoardInfoService;
import com.jmt.global.ListData;
import com.jmt.global.constants.DeleteStatus;
import com.jmt.global.rests.JSONData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/board_data")
@RequiredArgsConstructor
public class BoardDataController {

    private final BoardInfoService boardInfoService;
    // 게시글 조회
    @GetMapping("/list")
    public JSONData list() {
        List<BoardData> items = boardInfoService.getAllBoardData();
        System.out.println(items);
        return new JSONData(items);
    }

    @GetMapping
    public JSONData getList(BoardDataSearch search) {
        ListData<BoardData> data = boardInfoService.getList(search, DeleteStatus.ALL);

        return new JSONData(data);
    }
}
