    package services;
    import entities.Reclamation;
    import entities.Response;
    import utils.DataBase;

    import java.util.List;
    import java.util.ArrayList;
    import java.sql.Statement;
    import java.sql.ResultSet;
    import java.sql.SQLException;
    import java.sql.PreparedStatement;
    import java.sql.Connection;

    public class ResponseService implements IService<Response> {
        private Connection connection;

        public ResponseService() {
            connection = DataBase.getInstance().getConnection();
        }

        public void create(Response response) throws SQLException {
            String sql = "INSERT INTO response(id_reclamation, content, date) VALUES (?, ?, ?)";

            try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setInt(1, response.getReclamation().getIdReclamation());
                pstmt.setString(2, response.getContent());
                pstmt.setTimestamp(3, response.getDate());

                pstmt.executeUpdate();
                System.out.println("Response created successfully.");

                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int generatedId = generatedKeys.getInt(1);
                        response.setId_response(generatedId);
                    } else {
                        System.out.println("Failed to retrieve generated ID.");
                    }
                }
            }
        }

        @Override
        public void update(Response response) throws SQLException {
            String sql = "UPDATE response SET content=?, date=? WHERE id_response=?";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, response.getContent());
                pstmt.setTimestamp(2, new java.sql.Timestamp(response.getDate().getTime()));
                pstmt.setInt(3, response.getReclamation().getIdReclamation());
                pstmt.executeUpdate();
                System.out.println("Response updated successfully.");
            }

        }

        @Override
        public void delete(int id) throws SQLException {
            String sql = "DELETE FROM response WHERE id_response=?";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setInt(1, id);
                pstmt.executeUpdate();
                System.out.println("Response deleted successfully.");
            }

        }

        @Override
        public List<Response> read() throws SQLException {
            String sql = "SELECT * FROM response";
            List<Response> responses = new ArrayList<>();

            try (PreparedStatement pstmt = connection.prepareStatement(sql);
                 ResultSet rs = pstmt.executeQuery()) {

                while (rs.next()) {
                    Response response = new Response();

                    Reclamation reclamation = new Reclamation();
                    reclamation.setIdReclamation(rs.getInt("id_reclamation"));
                    response.setReclamation(reclamation);

                    response.setContent(rs.getString("content"));
                    response.setDate(rs.getTimestamp("date"));

                    responses.add(response);
                }
            }
            return responses;
        }
    }

