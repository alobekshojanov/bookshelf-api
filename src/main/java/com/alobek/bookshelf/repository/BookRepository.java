package com.alobek.bookshelf.repository;

import com.alobek.bookshelf.entity.BookEntity;
import com.alobek.bookshelf.enums.BookGenre;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends CrudRepository<BookEntity, Integer>, PagingAndSortingRepository<BookEntity,Integer> {

    Optional<BookEntity> findByIdAndActiveTrue(Integer bookId);

    //Optional<BookEntity> findByIdAndQuantityGreaterThan(Integer bookId);

    Optional<BookEntity> findByTitleIgnoreCaseAndAuthorIgnoreCaseAndActiveTrue(String title, String author);

    Page<BookEntity> findByTitleContainingIgnoreCaseAndActiveTrueOrderByCreatedDateDesc(String title, Pageable pageable);

    Page<BookEntity> findByAuthorContainingIgnoreCaseAndActiveTrueOrderByCreatedDateDesc(String author, Pageable pageable);

    //
    @Query("SELECT b FROM BookEntity b WHERE "
    + "(:author IS NULL OR LOWER(b.author) LIKE LOWER(CONCAT('%', :author, '%'))) AND "
    + "(:genre IS NULL OR b.genre = :genre) AND "
    + "(:publishedYear IS NULL OR b.publishedYear = :publishedYear) AND "
    + "(:keyword IS NULL OR (LOWER(b.title) LIKE CONCAT('%', :keyword, '%') OR LOWER(b.description) LIKE CONCAT('%', :keyword, '%')))"
    + " AND b.active = true "
            + "ORDER BY b.createdDate DESC")
    Page<BookEntity> advancedCheckingBooks(@Param("author") String author,
                                    @Param("genre") BookGenre genre,
                                    @Param("publishedYear") Integer publishedYear,
                                    @Param("keyword") String keyword,
                                           Pageable pageable);
}
