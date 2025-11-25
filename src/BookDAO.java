import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookDAO {

    public List<Book> getAll() throws SQLException {
        List<Book> list = new ArrayList<>();
        String sql = "SELECT * FROM Books ORDER BY id DESC";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    public List<Book> search(String keyword) throws SQLException {
        List<Book> list = new ArrayList<>();
        String sql = "SELECT * FROM Books WHERE title LIKE ? OR author LIKE ? OR isbn LIKE ? ORDER BY id DESC";
        try (Connection c = DBConnection.getConnection();
            PreparedStatement ps = c.prepareStatement(sql)) {
            String k = "%" + keyword + "%";
            ps.setString(1, k);
            ps.setString(2, k);
            ps.setString(3, k);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }
        return list;
    }

    public int insert(Book b) throws SQLException {
        String sql = "INSERT INTO Books(isbn, title, author, publisher, year, quantity, price) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, b.getIsbn());
            ps.setString(2, b.getTitle());
            ps.setString(3, b.getAuthor());
            ps.setString(4, b.getPublisher());
            if (b.getYear() == null) ps.setNull(5, Types.INTEGER);
            else ps.setInt(5, b.getYear());
            ps.setInt(6, b.getQuantity());
            ps.setDouble(7, b.getPrice());
            int affected = ps.executeUpdate();
            if (affected == 0) return -1;
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
            return -1;
        }
    }

    public boolean update(Book b) throws SQLException {
        String sql = "UPDATE Books SET isbn=?, title=?, author=?, publisher=?, year=?, quantity=?, price=? WHERE id=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, b.getIsbn());
            ps.setString(2, b.getTitle());
            ps.setString(3, b.getAuthor());
            ps.setString(4, b.getPublisher());
            if (b.getYear() == null) ps.setNull(5, Types.INTEGER);
            else ps.setInt(5, b.getYear());
            ps.setInt(6, b.getQuantity());
            ps.setDouble(7, b.getPrice());
            ps.setInt(8, b.getId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM Books WHERE id = ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    private Book mapRow(ResultSet rs) throws SQLException {
        Book b = new Book();
        b.setId(rs.getInt("id"));
        b.setIsbn(rs.getString("isbn"));
        b.setTitle(rs.getString("title"));
        b.setAuthor(rs.getString("author"));
        b.setPublisher(rs.getString("publisher"));
        int y = rs.getInt("year");
        if (rs.wasNull()) b.setYear(null); else b.setYear(y);
        b.setQuantity(rs.getInt("quantity"));
        b.setPrice(rs.getDouble("price"));
        return b;
    }
}
