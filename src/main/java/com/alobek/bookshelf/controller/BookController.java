package com.alobek.bookshelf.controller;

import com.alobek.bookshelf.dto.AppResponse;
import com.alobek.bookshelf.dto.book.*;
import com.alobek.bookshelf.enums.AppLanguage;
import com.alobek.bookshelf.service.BookService;
import com.alobek.bookshelf.util.PageUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/book")
@Tag(name = "Book Controller", description = "Controller for working on books")
@Slf4j
public class BookController {

    @Autowired
    private BookService bookService;

    @PostMapping("/admin/create-book")
    @Operation(summary = "Adding new book", description = "API using for adding new book")
    public ResponseEntity<AppResponse<String>> createBook(@Valid @RequestBody BookDTO dto,
                                                          @RequestHeader(value = "Accept-Language", defaultValue = "UZ")AppLanguage lang){
        log.info("Adding new book: title: {}, author: {}", dto.getTitle(), dto.getAuthor());
        return ResponseEntity.ok().body(bookService.create(dto, lang));
    }

    @GetMapping("/book-list/{query}")
    @Operation(summary = "Adding new book", description = "API using for adding new book")
    public ResponseEntity<AppResponse<Page<BookResponseDTO>>> bookbookList(@PathVariable("query")  String query,
                                                                       @RequestHeader(value = "lang", defaultValue = "EN") AppLanguage lang,
                                                                       @RequestParam(value = "page", defaultValue = "1") int page,
                                                                       @RequestParam(value = "size", defaultValue = "10") int size ) {

        return ResponseEntity.ok(bookService.bookList(query, page, size, lang));
    }

    @GetMapping("/book-detail/{bookId}")
    @Operation(summary = "Book details", description = "API using for registration")
    public ResponseEntity<AppResponse<BookDetailDTO>> bookDetail(@PathVariable("bookId") Integer bookId,
                                                    @RequestParam(value = "lang", defaultValue = "EN") AppLanguage lang){
        return ResponseEntity.ok(bookService.bookDetail(bookId, lang));
    }


    @PutMapping("/admin/update/{bookId}")
    public ResponseEntity<AppResponse<UpdateBookResponseDTO>> UpdateBook(@PathVariable("bookId") Integer bookId,
                                                                         @RequestBody UpdateBookRequestDTO dto,
                                                                         @RequestHeader(value = "lang", defaultValue = "UZ") AppLanguage lang){

        return ResponseEntity.ok(bookService.update(bookId, dto, lang)) ;
    }


    @DeleteMapping("/admin/delete/{bookId}")
    public ResponseEntity<AppResponse> delete(@PathVariable("bookId") Integer bookId,
                                    @RequestHeader(value = "lang", defaultValue = "EN") AppLanguage lang){
        return ResponseEntity.ok(bookService.deleteBook(bookId, lang));
    }


    @PostMapping("/admin/advanced-filter")
    public ResponseEntity<AppResponse<Page<BookResponseDTO>>> advancedFilter(@RequestBody AdvancedFilterDTO dto,
                                            @RequestHeader(value = "lang", defaultValue = "RU") AppLanguage lang,
                                            @RequestParam(value = "page", defaultValue = "1") int page,
                                            @RequestParam(value = "size", defaultValue = "10") int size){
        return ResponseEntity.ok(bookService.advancedBookFilter(dto, page, size, lang));
    }


}
