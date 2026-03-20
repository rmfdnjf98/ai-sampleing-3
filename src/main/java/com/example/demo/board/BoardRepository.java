package com.example.demo.board;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardRepository extends JpaRepository<Board, Integer> {

    @Modifying
    @Query("delete from Board b where b.user.id = :userId")
    void deleteByUserId(@Param("userId") Integer userId);

    @Query(value = "select * from board_tb order by id desc limit :offset, :size", nativeQuery = true)
    List<Board> findAll(@Param("offset") int offset, @Param("size") int size);

    @Query(value = "select count(*) from board_tb", nativeQuery = true)
    Long countAll();
}
