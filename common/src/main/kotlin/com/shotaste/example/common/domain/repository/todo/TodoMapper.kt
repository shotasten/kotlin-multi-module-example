package com.shotaste.example.common.domain.repository.todo

import org.apache.ibatis.annotations.Delete
import org.apache.ibatis.annotations.Insert
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Options
import org.apache.ibatis.annotations.Param
import org.apache.ibatis.annotations.Select
import org.apache.ibatis.annotations.Update

@Mapper
interface TodoMapper {
    @Select(
        """
        SELECT
            *
        FROM
            todo
        ORDER BY
            id ASC
    """,
    )
    fun findAll(): List<TodoRecord>

    @Select(
        """
        SELECT
            *
        FROM
            todo
        WHERE
            id = #{id}
    """,
    )
    fun findById(id: Int): TodoRecord?

    @Insert(
        """
        INSERT INTO todo 
        SET    
        title = #{dto.title},
        description = #{dto.description},
        status = #{dto.status},
        category = #{dto.category}
    """,
    )
    @Options(useGeneratedKeys = true, keyProperty = "id")
    fun insert(
        @Param("dto") insertDto: TodoInsertDto,
    ): Int

    @Update(
        """
        UPDATE
            todo
        SET
            status = #{status}
        WHERE
            id = #{id}
    """,
    )
    fun updateStatusById(
        id: Int,
        status: TodoStatus,
    ): Int

    @Delete(
        """
        DELETE FROM
            todo
        WHERE
            id = #{id}
    """,
    )
    fun deleteById(id: Int): Int
}
