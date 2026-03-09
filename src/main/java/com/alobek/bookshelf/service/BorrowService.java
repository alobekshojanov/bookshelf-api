package com.alobek.bookshelf.service;

import com.alobek.bookshelf.config.CustomUserDetails;
import com.alobek.bookshelf.dto.AppResponse;
import com.alobek.bookshelf.dto.borrow.*;
import com.alobek.bookshelf.entity.BookEntity;
import com.alobek.bookshelf.entity.BorrowEntity;
import com.alobek.bookshelf.enums.AppLanguage;
import com.alobek.bookshelf.enums.BorrowStatus;
import com.alobek.bookshelf.exps.AppBadException;
import com.alobek.bookshelf.repository.BookRepository;
import com.alobek.bookshelf.repository.BorrowRepository;
import com.alobek.bookshelf.util.PageUtil;
import com.alobek.bookshelf.util.SpringSecurityUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BorrowService {

    @Autowired
    private BorrowRepository borrowRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private ResourceBundleService  bundleService;

    @Value("${borrow.duration}")
    private Integer duration;


    @Transactional
    public AppResponse<BorrowResponseDTO> borrowBook(Integer bookId, AppLanguage lang) {
        // check if book exists ...
        BookEntity book = bookRepository.findById(bookId).orElseThrow(() ->
                new AppBadException(bundleService.getMessage("book.not.found", lang)));
        if (book.getQuantity() <= 0) {
            throw new AppBadException(bundleService.getMessage("book.not.available", lang));
        }

        // check user borrow limit ... ?
        // Shu userning status = BORROWED bo‘lgan recordlarini sanaymiz...
        CustomUserDetails profile = SpringSecurityUtil.getCurrentProfile();
        Integer profileId = profile.getId();
        Integer borrowCount = borrowRepository.countByProfileIdAndStatus(profileId, BorrowStatus.BORROWED);
        if (borrowCount >= 3) {
            throw new AppBadException(bundleService.getMessage("no.left.limit", lang));
        }

        // check if user borrowed this book?
        if (borrowRepository.existsByProfileIdAndBookIdAndStatus(profileId, bookId, BorrowStatus.BORROWED)) {
            throw new AppBadException(bundleService.getMessage("same.book.borrowed", lang));
        }


        // check if there is overDue ...
        List<BorrowEntity> borrowList = borrowRepository.findOverdueBorrows(profileId, BorrowStatus.BORROWED, LocalDate.now());
        if (!borrowList.isEmpty()) {
            throw new AppBadException(bundleService.getMessage("overdue.borrow", lang));
        }

        // decrease book's quantity ...
        book.setQuantity(book.getQuantity() - 1);
        bookRepository.save(book);


        // create borrow record ...

        LocalDate borrowedDate = LocalDate.now();

        BorrowEntity borrowEntity = new BorrowEntity();
        borrowEntity.setProfileId(profileId);
        borrowEntity.setBookId(bookId);
        borrowEntity.setBorrowedDate(borrowedDate);
        borrowEntity.setDueDate(borrowedDate.plusDays(duration));
        borrowEntity.setStatus(BorrowStatus.BORROWED);
        borrowEntity.setCreatedDate(LocalDateTime.now());

        borrowRepository.save(borrowEntity);

        // mapping borrowEntity to dto ...
        BorrowResponseDTO dto = new BorrowResponseDTO();
        dto.setBorrowId(borrowEntity.getId());
        dto.setBookId(bookId);
        dto.setBookTitle(book.getTitle());
        dto.setBorrowedDate(borrowedDate);
        dto.setDueDate(borrowEntity.getDueDate());
        dto.setBorrowStatus(BorrowStatus.BORROWED);

        // response
        return new AppResponse<>(dto, bundleService.getMessage("borrow.success", lang));
    }

    @Transactional
    public AppResponse<ReturnBookResponseDTO> returnBook(Integer bookId, AppLanguage lang) {
        // authorize user ... login
        CustomUserDetails currentUser = SpringSecurityUtil.getCurrentProfile();
        Integer userId = currentUser.getId();

        // find borrow with this bookId and user and status not returned ...
        BorrowEntity borrow = borrowRepository.findByProfileIdAndBookIdAndStatus(userId, bookId, BorrowStatus.BORROWED).orElseThrow(() ->
                new AppBadException(bundleService.getMessage("borrow.not.found", lang)));

        // update borrow fields relevant to returning process ...
        borrow.setReturnedDate(LocalDate.now());
        borrow.setStatus(BorrowStatus.RETURNED);
        borrowRepository.save(borrow);

        // increase quantity of the book ...
        BookEntity book = bookRepository.findByIdAndActiveTrue(bookId).orElseThrow(() ->
                new AppBadException(bundleService.getMessage("book.not.found", lang)));
        book.setQuantity(book.getQuantity() + 1);

        bookRepository.save(book);

        // mapping result to special dto ...
        ReturnBookResponseDTO dto = new ReturnBookResponseDTO();
        dto.setBorrowId(borrow.getId());
        dto.setBookTitle(book.getTitle());
        dto.setDueDate(borrow.getDueDate());
        dto.setReturnDate(LocalDate.now());

        // response with special dto...
        return new AppResponse<>(dto, bundleService.getMessage("return.success", lang));
    }

    public AppResponse<List<BorrowHistoryDTO>> borrowHistory(AppLanguage lang){
        // find current user ...
        CustomUserDetails currentUser = SpringSecurityUtil.getCurrentProfile();
        Integer userId = currentUser.getId();


        // check whether current user borrowed books ...
        List<BorrowEntity> borrowList = borrowRepository.findAllByProfileId(userId);
        if (borrowList.isEmpty()) {
            throw new AppBadException(bundleService.getMessage("no.borrows.found", lang));
        }


        // crete List with borrows ...
        List<BorrowHistoryDTO> dtoList = borrowList.stream()
                .map(borrow -> toDTO(borrow, lang))
                .collect(Collectors.toList());

        // return response ...
        return new AppResponse<>(dtoList, bundleService.getMessage("your borrow history", lang));
    }

    public AppResponse<Page<AdminBorrowDTO>> allBorrowings(int page, int size, AppLanguage lang) {
        int currentPage = PageUtil.page(page);
        PageRequest pageRequest = PageRequest.of(currentPage,size, Sort.by(Sort.Direction.DESC, "createdDate"));


        Page<BorrowEntity> borrowPage = borrowRepository.findAll(pageRequest);

        List<AdminBorrowDTO> dtoList = borrowPage.getContent().stream()
                .map(borrow -> toAdminBorrowDTO(borrow, lang))
                .collect(Collectors.toList());

        Page<AdminBorrowDTO> dtoPage = new PageImpl<>(
                dtoList,
                borrowPage.getPageable(),
                borrowPage.getTotalElements()
        );

        // return response ...
        return new AppResponse<>(dtoPage, bundleService.getMessage("all borrow list", lang));
    }



    public AdminBorrowDTO toAdminBorrowDTO(BorrowEntity borrow, AppLanguage lang) {
        if ( borrow == null ) return null;

        ProfileInfoDTO profileInfoDTO = new ProfileInfoDTO();
        profileInfoDTO.setId(borrow.getProfileId());
        //profileInfoDTO.setUsername(borrow.getProfile().getUsername());
        profileInfoDTO.setUsername(borrow.getProfile() != null ? borrow.getProfile().getUsername() : "Unknown");

        BookInfoDTO bookInfoDTO = new BookInfoDTO();
        bookInfoDTO.setId(borrow.getBookId());
        //bookInfoDTO.setTitle(borrow.getBook().getTitle());
        bookInfoDTO.setTitle(borrow.getBook() != null ? borrow.getBook().getTitle() : "Unknown");

        AdminBorrowDTO dto = new AdminBorrowDTO();
        dto.setBorrowId(borrow.getId());
        dto.setBook(bookInfoDTO);
        dto.setProfile(profileInfoDTO);
        dto.setBorrowedDate(borrow.getBorrowedDate());
        dto.setReturnedDate(borrow.getReturnedDate());
        dto.setStatus(borrow.getStatus());

        return dto;
    }

    public BorrowHistoryDTO toDTO(BorrowEntity borrow, AppLanguage lang) {

        if (borrow == null) return null;
        BookInfoDTO bookInfoDTO = new BookInfoDTO();
        bookInfoDTO.setId(borrow.getBookId());
        bookInfoDTO.setTitle(borrow.getBook() != null ? borrow.getBook().getTitle() : "Unknown");


        BorrowHistoryDTO dto = new BorrowHistoryDTO();
        dto.setBook(bookInfoDTO);
        dto.setBorrowedDate(borrow.getBorrowedDate());
        dto.setReturnedDate(borrow.getReturnedDate());

        return dto;

    }


}
