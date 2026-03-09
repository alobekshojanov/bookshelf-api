package com.alobek.bookshelf.service;

import com.alobek.bookshelf.dto.AppResponse;
import com.alobek.bookshelf.dto.book.*;
import com.alobek.bookshelf.entity.AttachEntity;
import com.alobek.bookshelf.entity.BookEntity;
import com.alobek.bookshelf.enums.AppLanguage;
import com.alobek.bookshelf.exps.AppBadException;
import com.alobek.bookshelf.mapper.BookMapper;
import com.alobek.bookshelf.repository.AttachRepository;
import com.alobek.bookshelf.repository.BookRepository;
import com.alobek.bookshelf.util.PageUtil;
import com.alobek.bookshelf.util.SpringSecurityUtil;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AttachRepository attachRepository;

    @Autowired
    private ResourceBundleService bundleService;



    public AppResponse<String> create(BookDTO dto, AppLanguage lang) {
        // check for published year...
        if (dto.getPublishedYear() > LocalDate.now().getYear()) {
            log.warn("Published year not valid title: {}, publishedYear: {}", dto.getTitle(), dto.getPublishedYear());
            throw new AppBadException(bundleService.getMessage("published.year.incorrect", lang));
        }

        // check title - is there any books with the same title and Author?
        Optional<BookEntity> optional = bookRepository
                .findByTitleIgnoreCaseAndAuthorIgnoreCaseAndActiveTrue(dto.getTitle(), dto.getAuthor());
        if (optional.isPresent()) {
            log.warn("Book already exists title: {}", dto.getTitle());
            throw new AppBadException(bundleService.getMessage("book.already.exists", lang));
        }

        // Does photo exists?
        Optional<AttachEntity> optionalAttach = attachRepository.findById(dto.getPhotoId());
        if (optionalAttach.isEmpty()) {
            log.warn("For coverage, photo not found photoId: {} ", dto.getPhotoId());
            throw new AppBadException(bundleService.getMessage("photo.not.found", lang));
        }
        AttachEntity entity = optionalAttach.get();
        // Prepare entity:
        BookEntity bookEntity = new BookEntity();
        bookEntity.setTitle(dto.getTitle());
        bookEntity.setAuthor(dto.getAuthor());
        bookEntity.setDescription(dto.getDescription());
        bookEntity.setGenre(dto.getGenre());
        bookEntity.setPublishedYear(dto.getPublishedYear());
        bookEntity.setActive(true);
        bookEntity.setQuantity(dto.getQuantity());
        //bookEntity.setAvailability(Boolean.TRUE);
        bookEntity.setPhotoId(entity.getId());
        bookEntity.setCreatedDate(LocalDateTime.now());

        // Save book
        bookRepository.save(bookEntity);

        // return success.
        return new AppResponse<>(bundleService.getMessage("book.added.successfully", lang));
    }

    public AppResponse<Page<BookResponseDTO>> bookList(String query, int page, int size, AppLanguage lang) {
        int currentPage = PageUtil.page(page);
        PageRequest pageRequest = PageRequest.of(currentPage, size);

        // 1. query validation ...
        if (query == null || query.isEmpty() || query.length() < 1 || query.length() > 255) {
            throw new AppBadException(bundleService.getMessage("invalid.query.given", lang));
        }

        // 2. check whether query title or author ...
        Page<BookEntity> bookPage = bookRepository.findByTitleContainingIgnoreCaseAndActiveTrueOrderByCreatedDateDesc(query, pageRequest); // searching by title
        // If no results, try searching by author
        if (bookPage.isEmpty()) {
            bookPage = bookRepository.findByAuthorContainingIgnoreCaseAndActiveTrueOrderByCreatedDateDesc(query, pageRequest);
        }

        if (bookPage.isEmpty()) {
            return new AppResponse<>(bundleService.getMessage("book.list.empty", lang));
        }

        // map entity page → dto page
        Page<BookResponseDTO> dtoPage = bookPage.map(this::toDTO); // map a list of BookEntity to list of BookResponseDTO
        //return new PageImpl<BookDTO>(bookList, pageRequest,bookList.getTotalElements());

        // response ...
        return new AppResponse<>(dtoPage, bundleService.getMessage("book.list", lang));

    }

    public AppResponse<BookDetailDTO> bookDetail(Integer bookId, AppLanguage lang) {
        // check bookID ...
        Optional<BookEntity> optional = bookRepository.findByIdAndActiveTrue(bookId);
        if (optional.isEmpty()) {
            throw new AppBadException(bundleService.getMessage("book.not.found", lang));
        }

        // map book to BookDetailDTO
        BookEntity book = optional.get();
        BookDetailDTO bookDetailDTO = toBookDetailDTO(book);

        //response
        return new AppResponse<>(bookDetailDTO, bundleService.getMessage("book.detail.was.responded", lang));
    }

    public AppResponse<UpdateBookResponseDTO> update(Integer bookId, UpdateBookRequestDTO dto, AppLanguage lang) {
        String baseURL = "http://localhost:8080/attach/open/";
        // Find the book by ID ...
        Optional<BookEntity> optional = bookRepository.findByIdAndActiveTrue(bookId);
        if (optional.isEmpty()) {
            throw new AppBadException(bundleService.getMessage("book.not.found", lang));
        }
        BookEntity book = optional.get();

        // map UpdateBookRequestDTO into bookEntity ...
            book.setTitle(dto.getTitle());
            book.setAuthor(dto.getAuthor());
            book.setDescription(dto.getDescription());
            book.setGenre(dto.getGenre());

        BookPhotoResponseDTO  photoDTO = null;
            if (dto.getPhoto() != null && dto.getPhoto().getId() != null) {
                book.setPhotoId(dto.getPhoto().getId());

                photoDTO = new BookPhotoResponseDTO();
                photoDTO.setId(dto.getPhoto().getId());
                photoDTO.setUrl(baseURL + dto.getPhoto().getId());
            }

            bookRepository.save(book);
        // Save the updated entity ...

        // map bookEntity into UpdateBookResponseDTO ...
            UpdateBookResponseDTO responseDTO = new UpdateBookResponseDTO();
            responseDTO.setId(book.getId());
            responseDTO.setTitle(book.getTitle());
            responseDTO.setAuthor(book.getAuthor());
            responseDTO.setPhoto(photoDTO); // safe, null if no photo update

        // Wrap it in AppResponse and return ...
        return new AppResponse<>(responseDTO, bundleService.getMessage("book.update.successfully", lang));

    }

    public AppResponse<String> deleteBook(Integer bookId, AppLanguage lang) {
        // check if the book exists and is active ...
        BookEntity bookEntity = bookRepository.findByIdAndActiveTrue(bookId).orElseThrow(() ->
                new AppBadException(bundleService.getMessage("book.not.found", lang)));

        // soft delete - make active to false.
        bookEntity.setActive(false);

        bookRepository.save(bookEntity);
        //
        return new AppResponse<>(bundleService.getMessage("book.deleted.successfully", lang));
    }

    public AppResponse<Page<BookResponseDTO>> advancedBookFilter(AdvancedFilterDTO dto, int page, int size, AppLanguage lang) {
        int currentPage = PageUtil.page(page);
        PageRequest pageRequest = PageRequest.of(currentPage, size);

        // convert keyword to lowercase for case-insensitive search ...
        String keyword = dto.getKeyword() != null ? dto.getKeyword().toLowerCase() : null;

        /*String query = "SELECT b FROM BookEntity b WHERE 1=1";

        if (dto.getAuthor() != null && !dto.getAuthor().isEmpty()) {
            query += " AND b.author = :author";
        }

        if (dto.getGenre() != null) {
            query += " AND b.genre = :genre";
        }

        if (dto.getPublishedYear() != null) {
            query += " AND b.publishedYear = :publishedYear";
        }

        if (dto.getKeyword() != null && !dto.getKeyword().isEmpty()) {
            query += " AND (b.title LIKE :keyword OR b.description LIKE :keyword)";
        }*/

        Page<BookEntity> bookPage = bookRepository.advancedCheckingBooks(dto.getAuthor(),
                dto.getGenre(),
                dto.getPublishedYear(),
                keyword,
                pageRequest);

        if (bookPage.isEmpty()) {
            return new AppResponse<>(
                    Page.empty(),
                    "No books found with given filters"
            );
        }

        // map Page<BookEntity> → Page<BookResponseDTO>
        Page<BookResponseDTO> dtoPage = bookPage.map(this::toDTO);


        return new  AppResponse<>(dtoPage, bundleService.getMessage("book.list", lang));
    }




    // Map bookEntity to BookDetailDTO ...
    public BookDetailDTO toBookDetailDTO(BookEntity book){
        String baseURL = "http://localhost:8080/attach/open/";
        if (book == null) return null;

        BookPhotoResponseDTO photoDTO = null;
        if (book.getPhoto() != null) {
            // generate URL using file ID (no path needed)
            String url = baseURL + book.getPhoto().getId();
            photoDTO = new BookPhotoResponseDTO();
            photoDTO.setId(book.getPhoto().getId());
            photoDTO.setUrl(url);
        }

        BookDetailDTO dto = new BookDetailDTO();
        dto.setId(book.getId());
        dto.setTitle(book.getTitle());
        dto.setAuthor(book.getAuthor());
        dto.setDescription(book.getDescription());
        dto.setGenre(book.getGenre());
        dto.setPublishedYear(book.getPublishedYear());
        dto.setPhoto(photoDTO);
        dto.setCreatedDate(book.getCreatedDate());

        return dto;
    }

    // Map single BookEntity to BookResponseDTO
    public BookResponseDTO toDTO(BookEntity book){

        String baseURL = "http://localhost:8080/attach/open/";

        if (book == null) return null;

        BookPhotoResponseDTO photoDTO = null;
        if (book.getPhoto() != null){
            // generate URL using file ID (no path needed)
            String url = baseURL + book.getPhoto().getId();
            photoDTO = new BookPhotoResponseDTO();
            photoDTO.setId(book.getPhoto().getId());
            photoDTO.setUrl(url);
        }

        BookResponseDTO dto = new BookResponseDTO();
        dto.setId(book.getId());
        dto.setTitle(book.getTitle());
        dto.setAuthor(book.getAuthor());
        dto.setCreatedDate(book.getCreatedDate());
        dto.setPhoto(photoDTO);

        return dto;
    }

    // map a list of BookEntity to list of BookResponseDTO ...
    /*public List<BookResponseDTO> toDtoList(List<BookEntity> books){

        return books.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }*/
}
