import javax.swing.table.AbstractTableModel;
import java.util.List;

public class BookTableModel extends AbstractTableModel {
    private final String[] cols = {"ID", "ISBN", "Tiêu đề", "Tác giả", "NXB", "Năm", "Số lượng", "Giá"};
    private List<Book> data;

    public BookTableModel(List<Book> data) {
        this.data = data;
    }

    public void setData(List<Book> data) {
        this.data = data;
        fireTableDataChanged();
    }

    public Book getBookAt(int row) {
        if (data == null || row < 0 || row >= data.size()) return null;
        return data.get(row);
    }

    @Override
    public int getRowCount() {
        return data == null ? 0 : data.size();
    }

    @Override
    public int getColumnCount() {
        return cols.length;
    }

    @Override
    public String getColumnName(int column) {
        return cols[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Book b = data.get(rowIndex);
        switch (columnIndex) {
            case 0: return b.getId();
            case 1: return b.getIsbn();
            case 2: return b.getTitle();
            case 3: return b.getAuthor();
            case 4: return b.getPublisher();
            case 5: return b.getYear();
            case 6: return b.getQuantity();
            case 7: return b.getPrice();
            default: return null;
        }
    }
}
