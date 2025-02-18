package services;

import entities.User;
import entities.Location;
import utils.DataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import com.password4j.Password;

public class UserService implements IService<User> {

    private final Connection connection;

    public UserService() {
        connection = DataBase.getInstance().getConnection();
    }

    @Override
    public boolean create(User user) throws SQLException {
        String sql = "INSERT INTO `user`(`name`, `email`, `password`, `phone_number`, `role`, `id_location`, `gender`, `profile_picture`, `is_verified`, `account_status`, `date_of_birth`, `status`) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        String hashedPassword = Password.hash(user.getPassword()).withBcrypt().getResult();

        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, hashedPassword);
            ps.setString(4, user.getPhone());
            ps.setString(5, user.getRole().name());
            ps.setInt(6, user.getLocation().getId());
            ps.setString(7, user.getGender().name());
            ps.setString(8, user.getProfilePicture());
            ps.setBoolean(9, user.isVerified());
            ps.setString(10, user.getAccountStatus().name());
            ps.setDate(11, user.getDateOfBirth() != null ? Date.valueOf(user.getDateOfBirth()) : null);
            ps.setString(12, user.getStatus().name());

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating user failed, no rows affected.");
            }

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    user.setId(rs.getInt(1));
                } else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }
        }
        return true;
    }

    @Override
    public void update(User user) throws SQLException {
        String sql = "UPDATE `user` SET `name`=?, `email`=?, `password`=?, `phone_number`=?, `role`=?, `id_location`=?, `gender`=?, `profile_picture`=?, `is_verified`=?, `account_status`=?, `date_of_birth`=?, `status`=? WHERE id_user = ?";

        String hashedPassword = Password.hash(user.getPassword()).withBcrypt().getResult();

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, hashedPassword);
            ps.setString(4, user.getPhone());
            ps.setString(5, user.getRole().name());
            ps.setInt(6, user.getLocation().getId());
            ps.setString(7, user.getGender().name());
            ps.setString(8, user.getProfilePicture());
            ps.setBoolean(9, user.isVerified());
            ps.setString(10, user.getAccountStatus().name());
            ps.setDate(11, user.getDateOfBirth() != null ? Date.valueOf(user.getDateOfBirth()) : null);
            ps.setString(12, user.getStatus().name());
            ps.setInt(13, user.getId());

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Updating user failed, no rows affected.");
            }
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM `user` WHERE id_user = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Deleting user failed, no rows affected.");
            }
        }
    }

    public boolean verifyPassword(String email, String inputPassword) throws SQLException {
        String sql = "SELECT password FROM `user` WHERE email = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String storedHash = rs.getString("password");
                    return Password.check(inputPassword, storedHash).withBcrypt();
                }
            }
        }
        return false;
    }

    @Override
    public List<User> read() throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM `user` u JOIN `location` l ON u.id_location = l.id_location";
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id_user"));
                user.setName(rs.getString("name"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password"));
                user.setPhone(rs.getString("phone_number"));
                user.setRole(User.Role.valueOf(rs.getString("role")));
                user.setLocation(new Location(
                        rs.getInt("id_location"),
                        rs.getString("address"),
                        rs.getBigDecimal("latitude").floatValue(),
                        rs.getBigDecimal("longitude").floatValue()
                ));
                user.setGender(User.Gender.valueOf(rs.getString("gender")));
                user.setProfilePicture(rs.getString("profile_picture"));
                user.setVerified(rs.getBoolean("is_verified"));
                user.setAccountStatus(User.AccountStatus.valueOf(rs.getString("account_status")));
                user.setDateOfBirth(rs.getDate("date_of_birth") != null ? rs.getDate("date_of_birth").toLocalDate() : null);
                user.setStatus(User.Status.valueOf(rs.getString("status")));

                users.add(user);
            }
        }
        return users;
    }

    public User getById(int id) throws SQLException {
        String sql = "SELECT * FROM `user` u JOIN `location` l ON u.id_location = l.id_location WHERE u.id_user = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setId(rs.getInt("id_user"));
                    user.setName(rs.getString("name"));
                    user.setEmail(rs.getString("email"));
                    user.setPassword(rs.getString("password"));
                    user.setPhone(rs.getString("phone_number"));
                    user.setRole(User.Role.valueOf(rs.getString("role")));
                    user.setLocation(new Location(
                            rs.getInt("id_location"),
                            rs.getString("address"),
                            rs.getBigDecimal("latitude").floatValue(),
                            rs.getBigDecimal("longitude").floatValue()
                    ));
                    user.setGender(User.Gender.valueOf(rs.getString("gender")));
                    user.setProfilePicture(rs.getString("profile_picture"));
                    user.setVerified(rs.getBoolean("is_verified"));
                    user.setAccountStatus(User.AccountStatus.valueOf(rs.getString("account_status")));
                    user.setDateOfBirth(rs.getDate("date_of_birth") != null ? rs.getDate("date_of_birth").toLocalDate() : null);
                    user.setStatus(User.Status.valueOf(rs.getString("status")));

                    return user;
                }
            }
        }
        return null;
    }

    public List<User> sortUsers(String sortField, boolean ascending) throws SQLException {
        StringBuilder sqlQuery = new StringBuilder("SELECT * FROM `user` u JOIN `location` l ON u.id_location = l.id_location");

        if (sortField != null && !sortField.isEmpty()) {
            sqlQuery.append(" ORDER BY ");

            switch (sortField) {
                case "name":
                    sqlQuery.append("name ").append(ascending ? "ASC" : "DESC");
                    break;
                case "email":
                    sqlQuery.append("email ").append(ascending ? "ASC" : "DESC");
                    break;
                case "phone":
                    sqlQuery.append("phone_number ").append(ascending ? "ASC" : "DESC");
                    break;
                case "role":
                    sqlQuery.append("role ").append(ascending ? "ASC" : "DESC");
                    break;
                case "gender":
                    sqlQuery.append("gender ").append(ascending ? "ASC" : "DESC");
                    break;
                case "accountStatus":
                    sqlQuery.append("account_status ").append(ascending ? "ASC" : "DESC");
                    break;
                case "dateOfBirth":
                    sqlQuery.append("date_of_birth ").append(ascending ? "ASC" : "DESC");
                    break;
                case "status":
                    sqlQuery.append("status ").append(ascending ? "ASC" : "DESC");
                    break;
                case "location":
                    sqlQuery.append("address ").append(ascending ? "ASC" : "DESC");
                    break;
                default:
                    throw new IllegalArgumentException("Unknown sort field: " + sortField);
            }
        }

        List<User> sortedUsers = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sqlQuery.toString());
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id_user"));
                user.setName(rs.getString("name"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password"));
                user.setPhone(rs.getString("phone_number"));
                user.setRole(User.Role.valueOf(rs.getString("role")));
                user.setLocation(new Location(
                        rs.getInt("id_location"),
                        rs.getString("address"),
                        rs.getBigDecimal("latitude").floatValue(),
                        rs.getBigDecimal("longitude").floatValue()
                ));
                user.setGender(User.Gender.valueOf(rs.getString("gender")));
                user.setProfilePicture(rs.getString("profile_picture"));
                user.setVerified(rs.getBoolean("is_verified"));
                user.setAccountStatus(User.AccountStatus.valueOf(rs.getString("account_status")));
                user.setDateOfBirth(rs.getDate("date_of_birth") != null ? rs.getDate("date_of_birth").toLocalDate() : null);
                user.setStatus(User.Status.valueOf(rs.getString("status")));

                sortedUsers.add(user);
            }
        }

        return sortedUsers;
    }

    public List<User> searchUsers(String searchField, String searchValue) throws SQLException {
        StringBuilder sqlQuery = new StringBuilder("SELECT * FROM `user` u JOIN `location` l ON u.id_location = l.id_location");

        if (searchField != null && !searchField.isEmpty() && searchValue != null && !searchValue.isEmpty()) {
            sqlQuery.append(" WHERE ");

            switch (searchField) {
                case "name":
                    sqlQuery.append("name LIKE ?");
                    break;
                case "email":
                    sqlQuery.append("email LIKE ?");
                    break;
                case "phone":
                    sqlQuery.append("phone_number LIKE ?");
                    break;
                case "role":
                    sqlQuery.append("role LIKE ?");
                    break;
                case "gender":
                    sqlQuery.append("gender LIKE ?");
                    break;
                case "accountStatus":
                    sqlQuery.append("account_status LIKE ?");
                    break;
                case "dob":
                    sqlQuery.append("date_of_birth LIKE ?");
                    break;
                case "status":
                    sqlQuery.append("status LIKE ?");
                    break;
                case "location":
                    sqlQuery.append("address LIKE ?");
                    break;
                default:
                    throw new IllegalArgumentException("Unknown search field: " + searchField);
            }
        }

        List<User> users = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sqlQuery.toString())) {
            ps.setString(1, "%" + searchValue + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    User user = new User();
                    user.setId(rs.getInt("id_user"));
                    user.setName(rs.getString("name"));
                    user.setEmail(rs.getString("email"));
                    user.setPassword(rs.getString("password"));
                    user.setPhone(rs.getString("phone_number"));
                    user.setRole(User.Role.valueOf(rs.getString("role")));
                    user.setLocation(new Location(
                            rs.getInt("id_location"),
                            rs.getString("address"),
                            rs.getBigDecimal("latitude").floatValue(),
                            rs.getBigDecimal("longitude").floatValue()
                    ));
                    user.setGender(User.Gender.valueOf(rs.getString("gender")));
                    user.setProfilePicture(rs.getString("profile_picture"));
                    user.setVerified(rs.getBoolean("is_verified"));
                    user.setAccountStatus(User.AccountStatus.valueOf(rs.getString("account_status")));
                    user.setDateOfBirth(rs.getDate("date_of_birth") != null ? rs.getDate("date_of_birth").toLocalDate() : null);
                    user.setStatus(User.Status.valueOf(rs.getString("status")));

                    users.add(user);
                }
            }
        }

        return users;
    }

        public void exportToPdf(String filePath) {
            Document document = new Document(PageSize.A4);

            try {
                PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filePath));
                document.open();

                addDocumentHeader(document);

                document.add(new Paragraph("\nUsers List:\n", getFont(14, Font.BOLD, BaseColor.BLACK)));
                document.add(createStyledTable(
                        "SELECT id_user, name, email, phone_number, role, gender, account_status, status, date_of_birth FROM `user`",
                        new String[]{"ID", "Name", "Email", "Phone", "Role", "Gender", "Account Status", "Status", "Date of Birth"},
                        new String[]{"id_user", "name", "email", "phone_number", "role", "gender", "account_status", "status", "date_of_birth"}
                ));

                document.add(new Paragraph("\nDrivers List:\n", getFont(14, Font.BOLD, BaseColor.BLACK)));
                document.add(createStyledTable(
                        "SELECT d.id_driver, u.name, u.email, d.permit_number, d.role, d.status FROM `driver` d " +
                                "JOIN `user` u ON d.id_user = u.id_user",
                        new String[]{"Driver ID", "Name", "Email", "Permit Number", "Role", "Status"},
                        new String[]{"id_driver", "name", "email", "permit_number", "role", "status"}
                ));

                addDocumentFooter(document);

                document.close();
                writer.close();

                System.out.println("PDF created successfully: " + filePath);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void addDocumentHeader(Document document) throws DocumentException {
            Font titleFont = getFont(18, Font.BOLD, BaseColor.BLUE);
            Paragraph title = new Paragraph("Users and Drivers Report\n\n", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
        }

        private void addDocumentFooter(Document document) throws DocumentException {
            Font footerFont = getFont(10, Font.ITALIC, BaseColor.GRAY);
            Paragraph footer = new Paragraph("\nGenerated by Wamia - " + java.time.LocalDate.now(), footerFont);
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);
        }

        private PdfPTable createStyledTable(String query, String[] columnNames, String[] dbColumns) throws Exception {
            PdfPTable table = new PdfPTable(columnNames.length);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10);
            table.setSpacingAfter(10);
            table.setHeaderRows(1);

            float[] columnWidths = new float[columnNames.length];
            for (int i = 0; i < columnWidths.length; i++) {
                columnWidths[i] = 1.5f;
            }
            table.setWidths(columnWidths);

            for (String columnName : columnNames) {
                PdfPCell headerCell = new PdfPCell(new Phrase(columnName, getFont(8, Font.BOLD, BaseColor.WHITE)));
                headerCell.setBackgroundColor(BaseColor.DARK_GRAY);
                headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                headerCell.setPadding(5);
                table.addCell(headerCell);
            }

            try (PreparedStatement ps = connection.prepareStatement(query);
                 ResultSet rs = ps.executeQuery()) {
                boolean alternate = false;
                while (rs.next()) {
                    for (String dbColumn : dbColumns) {
                        PdfPCell cell = new PdfPCell(new Phrase(rs.getString(dbColumn) != null ? rs.getString(dbColumn) : "N/A",
                                getFont(8, Font.NORMAL, BaseColor.BLACK)));
                        cell.setPadding(5);
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);

                        if (alternate) {
                            cell.setBackgroundColor(new BaseColor(230, 230, 230)); // Light gray
                        }
                        table.addCell(cell);
                    }
                    alternate = !alternate;
                }
            }
            return table;
        }

        private Font getFont(int size, int style, BaseColor color) {
            Font font = FontFactory.getFont(FontFactory.HELVETICA, size, style, color);
            return font;
        }

    public User authenticateUser(String email, String password) throws SQLException {
        String sql = "SELECT * FROM `user` WHERE email = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String storedHash = rs.getString("password");
                    if (Password.check(password, storedHash).withBcrypt()) {
                        User user = new User();
                        user.setId(rs.getInt("id_user"));
                        user.setName(rs.getString("name"));
                        user.setEmail(rs.getString("email"));
                        user.setPhone(rs.getString("phone_number"));
                        user.setRole(User.Role.valueOf(rs.getString("role")));
                        user.setGender(User.Gender.valueOf(rs.getString("gender")));
                        user.setProfilePicture(rs.getString("profile_picture"));
                        user.setVerified(rs.getBoolean("is_verified"));
                        user.setAccountStatus(User.AccountStatus.valueOf(rs.getString("account_status")));
                        user.setDateOfBirth(rs.getDate("date_of_birth") != null ? rs.getDate("date_of_birth").toLocalDate() : null);
                        user.setStatus(User.Status.valueOf(rs.getString("status")));
                        return user;
                    }
                }
            }
        }
        return null;
    }
}


