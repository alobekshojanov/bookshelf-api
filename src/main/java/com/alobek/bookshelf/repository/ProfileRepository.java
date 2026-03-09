package com.alobek.bookshelf.repository;

import com.alobek.bookshelf.entity.ProfileEntity;
import com.alobek.bookshelf.enums.GeneralStatus;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface ProfileRepository extends JpaRepository<ProfileEntity, Integer>, PagingAndSortingRepository<ProfileEntity, Integer> {


        Optional<ProfileEntity> findByUsernameAndVisibleTrueAndStatus(String username,  GeneralStatus status);
        Optional<ProfileEntity> findByUsernameAndVisibleTrue(String username);
        Optional<ProfileEntity> findByIdAndVisibleTrue(Integer id);
        Optional<ProfileEntity> findByIdAndVisibleTrueAndStatus(Integer profileId,  GeneralStatus status);

        @Modifying
        @Transactional
        @Query(" update ProfileEntity set status = ?2 where id = ?1")
        void changeStatus(Integer id, GeneralStatus status);

        @Modifying
        @Transactional
        @Query(" update ProfileEntity set photoId = ?2 where id = ?1 ")
        void updatePhoto(Integer id, String photoId);


        @Transactional
        @Modifying
        @Query("update ProfileEntity set name = ?2 where id = ?1 ")
        void updateDetail(Integer id, String name);

        @Transactional
        @Modifying
        @Query(" update ProfileEntity set password = ?2 where id = ?1")
        void updatePassword(Integer profileId, String password);

        @Transactional
        @Modifying
        @Query(" update ProfileEntity set tempUsername = ?2 where id = ?1 ")
        void updateTempUsername(Integer profileId, String tempUsername);

        @Transactional
        @Modifying
        @Query(" update ProfileEntity set username = ?2 where id = ?1 ")
        void updateUsername(Integer id, String username);

        @Modifying(clearAutomatically = true)
        @Transactional
        @Query(" update ProfileEntity set status = ?2 where id = ?1 ")
        void blockStatus(Integer id, GeneralStatus status);

        @Modifying(clearAutomatically = true)
        @Transactional
        @Query(" update ProfileEntity set visible = ?2 where id = ?1 ")
        void deleteProfile(Integer id, Boolean visible);
}
