package com.shotaste.example.common.domain.framework.mybatis

import org.apache.ibatis.type.BaseTypeHandler
import org.apache.ibatis.type.JdbcType
import java.sql.CallableStatement
import java.sql.PreparedStatement
import java.sql.ResultSet

class IntEnumTypeHandler<E>(
    private val type: Class<E>,
) : BaseTypeHandler<E>() where E : Enum<E>, E : IntEnumBase<E> {

    override fun setNonNullParameter(ps: PreparedStatement, i: Int, parameter: E, jdbcType: JdbcType?) {
        ps.setInt(i, parameter.value)
    }

    override fun getNullableResult(rs: ResultSet, columnName: String): E? {
        return getEnumConstant(rs.getInt(columnName))
    }

    override fun getNullableResult(rs: ResultSet, columnIndex: Int): E? {
        return getEnumConstant(rs.getInt(columnIndex))
    }

    override fun getNullableResult(cs: CallableStatement, columnIndex: Int): E? {
        return getEnumConstant(cs.getInt(columnIndex))
    }

    private fun getEnumConstant(value: Int?): E? {
        return if (value == null) {
            null
        } else {
            type.enumConstants.first { (it as IntEnumBase<*>).value == value }
        }
    }
}