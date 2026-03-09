package com.alobek.bookshelf.repository;

import com.alobek.bookshelf.entity.BorrowEntity;
import com.alobek.bookshelf.enums.BorrowStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.query.QueryByExampleExecutor;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
    public interface BorrowRepository extends JpaRepository<BorrowEntity, Integer>, PagingAndSortingRepository<BorrowEntity, Integer> {


    @Query(" SELECT COUNT(b) FROM BorrowEntity b" +
            " WHERE b.profileId = :profileId " +
            " AND b.status = :statusBorrow ")
    int countByProfileIdAndStatus(@Param("profileId") Integer profileId,
                                  @Param("statusBorrow") BorrowStatus statusBorrow);

    boolean existsByProfileIdAndBookIdAndStatus(Integer profileId, Integer bookId, BorrowStatus statusBorrow);

    @Query(" SELECT b FROM BorrowEntity b " +
            " WHERE b.profileId = :profileId " +
            " AND b.status = :statusBorrow" +
            " AND b.dueDate < :today")
    List<BorrowEntity> findOverdueBorrows(@Param("profileId") Integer profileId,
                                          @Param("statusBorrow") BorrowStatus statusBorrow,
                                          @Param("today") LocalDate today);

    Optional<BorrowEntity> findByProfileIdAndBookIdAndStatus(Integer profileId, Integer bookId, BorrowStatus status);

    List<BorrowEntity> findAllByProfileId(Integer profileId);

    Page<BorrowEntity> findAllByOrderByCreatedDateDesc(Pageable pageable);
    
}
