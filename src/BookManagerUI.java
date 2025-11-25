import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.util.List;

public class BookManagerUI extends JFrame {
    private BookDAO dao = new BookDAO();
    private BookTableModel tableModel;
    private JTable table;

    private JTextField txtId = new JTextField(5);
    private JTextField txtISBN = new JTextField(15);
    private JTextField txtTitle = new JTextField(25);
    private JTextField txtAuthor = new JTextField(20);
    private JTextField txtPublisher = new JTextField(20);
    private JTextField txtYear = new JTextField(6);
    private JTextField txtQuantity = new JTextField(6);
    private JTextField txtPrice = new JTextField(8);

    private JTextField txtSearch = new JTextField(20);

    public BookManagerUI() {
        setTitle("Ứng dụng quản lý sách - Java Swing");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
        initComponents();
        loadData();
    }

    private void initComponents() {
        // Top: form
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4,4,4,4);
        gbc.anchor = GridBagConstraints.WEST;

        int row=0;
        addToForm(form, gbc, row++, "ID:", txtId, true);
        addToForm(form, gbc, row++, "ISBN:", txtISBN, false);
        addToForm(form, gbc, row++, "Tiêu đề:", txtTitle, false);
        addToForm(form, gbc, row++, "Tác giả:", txtAuthor, false);
        addToForm(form, gbc, row++, "Nhà xuất bản:", txtPublisher, false);
        addToForm(form, gbc, row++, "Năm:", txtYear, false);
        addToForm(form, gbc, row++, "Số lượng:", txtQuantity, false);
        addToForm(form, gbc, row++, "Giá:", txtPrice, false);

        txtId.setEditable(false);

        // Buttons
        JPanel btnPanel = new JPanel();
        JButton btnAdd = new JButton("Thêm");
        JButton btnUpdate = new JButton("Sửa");
        JButton btnDelete = new JButton("Xóa");
        JButton btnClear = new JButton("Xóa form");
        btnPanel.add(btnAdd); btnPanel.add(btnUpdate); btnPanel.add(btnDelete); btnPanel.add(btnClear);

        // Table
        tableModel = new BookTableModel(null);
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Search
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Tìm:"));
        searchPanel.add(txtSearch);
        JButton btnSearch = new JButton("Tìm");
        JButton btnRefresh = new JButton("Tải lại");
        searchPanel.add(btnSearch);
        searchPanel.add(btnRefresh);

        // Layout
        JPanel left = new JPanel(new BorderLayout());
        left.add(new JScrollPane(form), BorderLayout.CENTER);
        left.add(btnPanel, BorderLayout.SOUTH);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, left, new JScrollPane(table));
        split.setDividerLocation(380);

        getContentPane().add(searchPanel, BorderLayout.NORTH);
        getContentPane().add(split, BorderLayout.CENTER);

        // action listeners
        btnAdd.addActionListener(e -> onAdd());
        btnUpdate.addActionListener(e -> onUpdate());
        btnDelete.addActionListener(e -> onDelete());
        btnClear.addActionListener(e -> clearForm());
        btnSearch.addActionListener(e -> onSearch());
        btnRefresh.addActionListener(e -> loadData());

        table.getSelectionModel().addListSelectionListener(e -> onTableSelect(e));

        // double click to edit
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) fillFormFromSelected();
            }
        });
    }

    private void addToForm(JPanel form, GridBagConstraints gbc, int row, String label, JComponent field, boolean fullWidth) {
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1;
        form.add(new JLabel(label), gbc);
        gbc.gridx = 1;
        if (fullWidth) {
            gbc.gridwidth = 2;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            form.add(field, gbc);
            gbc.fill = GridBagConstraints.NONE;
        } else {
            form.add(field, gbc);
        }
    }

    private void loadData() {
        try {
            List<Book> list = dao.getAll();
            tableModel.setData(list);
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void onAdd() {
        try {
            Book b = readForm(false);
            int newId = dao.insert(b);
            if (newId > 0) {
                JOptionPane.showMessageDialog(this, "Thêm thành công (ID=" + newId + ")");
                loadData();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Thêm thất bại");
            }
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void onUpdate() {
        try {
            Book b = readForm(true);
            boolean ok = dao.update(b);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Cập nhật thành công");
                loadData();
            } else {
                JOptionPane.showMessageDialog(this, "Cập nhật thất bại");
            }
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void onDelete() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Chọn 1 sách để xóa");
            return;
        }
        Book b = tableModel.getBookAt(row);
        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa sách: " + b.getTitle() + " ?", "Xóa", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        try {
            boolean ok = dao.delete(b.getId());
            if (ok) {
                JOptionPane.showMessageDialog(this, "Xóa thành công");
                loadData();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Xóa thất bại");
            }
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void onSearch() {
        String k = txtSearch.getText().trim();
        try {
            List<Book> list;
            if (k.isEmpty()) list = dao.getAll();
            else list = dao.search(k);
            tableModel.setData(list);
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void onTableSelect(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            fillFormFromSelected();
        }
    }

    private void fillFormFromSelected() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        Book b = tableModel.getBookAt(row);
        if (b == null) return;
        txtId.setText(String.valueOf(b.getId()));
        txtISBN.setText(b.getIsbn());
        txtTitle.setText(b.getTitle());
        txtAuthor.setText(b.getAuthor());
        txtPublisher.setText(b.getPublisher());
        txtYear.setText(b.getYear() == null ? "" : String.valueOf(b.getYear()));
        txtQuantity.setText(String.valueOf(b.getQuantity()));
        txtPrice.setText(String.valueOf(b.getPrice()));
    }

    private Book readForm(boolean requireId) {
        Book b = new Book();
        if (requireId) {
            String sId = txtId.getText().trim();
            if (sId.isEmpty()) throw new IllegalArgumentException("ID rỗng");
            b.setId(Integer.parseInt(sId));
        }
        b.setIsbn(txtISBN.getText().trim());
        b.setTitle(txtTitle.getText().trim());
        b.setAuthor(txtAuthor.getText().trim());
        b.setPublisher(txtPublisher.getText().trim());
        String sy = txtYear.getText().trim();
        if (sy.isEmpty()) b.setYear(null); else b.setYear(Integer.valueOf(sy));
        String sq = txtQuantity.getText().trim();
        b.setQuantity(sq.isEmpty() ? 0 : Integer.parseInt(sq));
        String sp = txtPrice.getText().trim();
        b.setPrice(sp.isEmpty() ? 0.0 : Double.parseDouble(sp));
        return b;
    }

    private void clearForm() {
        txtId.setText("");
        txtISBN.setText("");
        txtTitle.setText("");
        txtAuthor.setText("");
        txtPublisher.setText("");
        txtYear.setText("");
        txtQuantity.setText("");
        txtPrice.setText("");
    }

    private void showError(Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        // Optional: set look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}
        SwingUtilities.invokeLater(() -> {
            BookManagerUI ui = new BookManagerUI();
            ui.setVisible(true);
        });
    }
}
