package com.example.demo.reply;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReplyRepository extends JpaRepository<Reply, Integer> {

    @Modifying
    @Query("delete from Reply r where r.user.id = :userId")
    void deleteByUserId(@Param("userId") Integer userId);

    @Modifying
    @Query("delete from Reply r where r.board.id in (select b.id from Board b where b.user.id = :userId)")
    void deleteByBoardUserId(@Param("userId") Integer userId);
}
