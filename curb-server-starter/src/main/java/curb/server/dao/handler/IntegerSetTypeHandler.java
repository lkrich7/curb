package curb.server.dao.handler;

import com.google.common.base.Splitter;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class IntegerSetTypeHandler extends BaseTypeHandler<Set<Integer>> {

    private static Splitter SPLITTER = Splitter.on(',').omitEmptyStrings().trimResults();
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Set<Integer> parameter, JdbcType jdbcType) throws SQLException {
        String value = parameter == null || parameter.isEmpty() ? "" : parameter.stream()
                .filter(Objects::nonNull)
                .map(e -> e.toString())
                .collect(Collectors.joining(","));
        ps.setString(i, value);
    }

    @Override
    public Set<Integer> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String str = rs.getString(columnName);
        return toSet(str);
    }

    @Override
    public Set<Integer> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String str = rs.getString(columnIndex);
        return toSet(str);
    }

    @Override
    public Set<Integer> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String str = cs.getString(columnIndex);
        return toSet(str);
    }

    private Set<Integer> toSet(String str) {
        if (str == null) {
            return new TreeSet<>();
        }
        Set<Integer> set = SPLITTER.splitToList(str).stream()
                .map(this::toInteger)
                .sorted()
                .collect(Collectors.toSet());
        return set;
    }

    private Integer toInteger(String s) {
        try {
            return Integer.valueOf(s);
        } catch (Exception e) {
            return null;
        }
    }
}
