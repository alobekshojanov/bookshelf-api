package com.alobek.bookshelf.repository;

import com.alobek.bookshelf.entity.ProfileRoleEntity;
import com.alobek.bookshelf.enums.ProfileRole;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ProfileRoleRepository extends CrudRepository<ProfileRoleEntity, Integer> {

    @Transactional
    @Modifying
    void deleteByProfileId(Integer profileId);

    List<ProfileRoleEntity> getByProfileId(Integer profileId);

    @Query("select p.roles from ProfileRoleEntity p where p.profileId = ?1 ")
    List<ProfileRole> getAllRolesByProfileId(Integer profileId);

    @Query(" select p.roles from ProfileRoleEntity p where p.profileId = ?1 ")
    List<ProfileRole> getAllRolesListByProfileId(Integer id);
}
