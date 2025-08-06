package com.epam.campstone.eventbookingsystem.repository;

import com.epam.campstone.eventbookingsystem.model.UserPassword;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserPasswordRepository extends JpaRepository<UserPassword, Long> {

    /**
     * Finds all passwords associated with the user identified by the given email.
     *
     * @param email the email of the user whose passwords are to be retrieved
     * @return a list of UserPassword objects associated with the user's email
     */
    @Query("SELECT p FROM UserPassword p WHERE p.user.email = :email")
    List<UserPassword> findUserPasswordByUser_Email(@Param("email") String email);


    /**
     * Finds the latest password associated with the user identified by the given email.
     *
     * @param email the email of the user whose latest password is to be retrieved
     * @return the latest UserPassword object associated with the user's email
     */
    @Query("SELECT p FROM UserPassword p WHERE p.user.email = :email ORDER BY p.createdAt DESC LIMIT 1")
    UserPassword findLatestUserPasswordByEmail(@Param("email") String email);

}
