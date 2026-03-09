package com.alobek.bookshelf.repository;

import com.alobek.bookshelf.entity.AttachEntity;
import jakarta.persistence.ManyToOne;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AttachRepository extends CrudRepository<AttachEntity, String> {

    @Transactional
    @Modifying
    @Query(" update AttachEntity set visible = false where id = ?1 ")
    void changeVisibility(String id);
}
