package com.shotaste.example.common.framework.mybatis.handler

import org.apache.ibatis.type.BaseTypeHandler
import org.apache.ibatis.type.JdbcType
import java.sql.CallableStatement
import java.sql.PreparedStatement
import java.sql.ResultSet

class StringEnumTypeHandler<E>(
    private val type: Class<E>,
) : BaseTypeHandler<E>() where E : Enum<E>, E : StringEnumBase<E> {
    override fun setNonNullParameter(
        ps: PreparedStatement,
        i: Int,
        parameter: E,
        jdbcType: JdbcType?,
    ) {
        ps.setString(i, parameter.value)
    }

    override fun getNullableResult(
        rs: ResultSet,
        columnName: String,
    ): E? {
        return getEnumConstant(rs.getString(columnName))
    }

    override fun getNullableResult(
        rs: ResultSet,
        columnIndex: Int,
    ): E? {
        return getEnumConstant(rs.getString(columnIndex))
    }

    override fun getNullableResult(
        cs: CallableStatement,
        columnIndex: Int,
    ): E? {
        return getEnumConstant(cs.getString(columnIndex))
    }

    private fun getEnumConstant(value: String?): E? {
        return if (value == null) {
            null
        } else {
            type.enumConstants.first { (it as StringEnumBase<*>).value == value }
        }
    }
}
