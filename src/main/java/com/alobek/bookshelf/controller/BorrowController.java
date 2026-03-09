package com.alobek.bookshelf.controller;

import com.alobek.bookshelf.dto.AppResponse;
import com.alobek.bookshelf.dto.borrow.AdminBorrowDTO;
import com.alobek.bookshelf.dto.borrow.BorrowHistoryDTO;
import com.alobek.bookshelf.dto.borrow.BorrowResponseDTO;
import com.alobek.bookshelf.dto.borrow.ReturnBookResponseDTO;
import com.alobek.bookshelf.enums.AppLanguage;
import com.alobek.bookshelf.service.BorrowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/borrow")
@Tag(name = "Borrow Controller", description = "Controller for working with borrowing")
@Slf4j
public class BorrowController {

    @Autowired
    private BorrowService borrowService;


    @PostMapping("/book-borrow/{bookId}")
    @Operation(summary = "borrow a book", description = "API using for borrowing a book ")
    public ResponseEntity<AppResponse<BorrowResponseDTO>> borrow(@PathVariable("bookId") Integer bookId,
                                                                 @RequestHeader(value = "Accept-Language", defaultValue = "EN")AppLanguage lang) {

        return ResponseEntity.ok(borrowService.borrowBook(bookId, lang));
    }

    @PutMapping("/book-return/{bookId}")
    @Operation(summary = "Return book", description = "API using for return book")
    public ResponseEntity<AppResponse<ReturnBookResponseDTO>> returnBook(@PathVariable("bookId") Integer bookId,
                                                            @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage lang) {
        return ResponseEntity.ok(borrowService.returnBook(bookId, lang));
    }

    @GetMapping("/borrow-history")
    @Operation(summary = "Borrow history", description = "API using for getting borrow history")
    public ResponseEntity<AppResponse<List<BorrowHistoryDTO>>> borrowHistory(@RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage lang) {
        return ResponseEntity.ok(borrowService.borrowHistory(lang));
    }

    @GetMapping("/admin/borrows")
    @Operation(summary = "All borrows", description = "API using for get all borrowings")
    public ResponseEntity<AppResponse<Page<AdminBorrowDTO>>> allBorrowings(@RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage lang,
                                                               @RequestParam(value = "page", defaultValue = "1") int page,
                                                               @RequestParam(value = "size", defaultValue = "10") int size){
        return ResponseEntity.ok(borrowService.allBorrowings(page, size,lang));
    }

}
