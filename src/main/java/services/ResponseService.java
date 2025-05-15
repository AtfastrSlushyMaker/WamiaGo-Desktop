package services;
import entities.Reclamation;
import entities.Response;
import entities.User;
import utils.DataBase;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

import java.util.List;
import java.util.ArrayList;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.Connection;

public class ResponseService implements IService<Response> {
    private Connection connection;
    private static final String ACCOUNT_SID = "AC9738676af94f3fa665bb80f4dd28c65d";
    private static final String AUTH_TOKEN = "ba7a0b39a74a59ca0f5b9991311ccf0a";
    private static final String TWILIO_PHONE_NUMBER = "+13157848171";

    public ResponseService() {
        connection = DataBase.getInstance().getConnection();
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
    }

    public boolean create(Response response) throws SQLException {
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
                    
                    // Send SMS notification to the user
                    sendSMSNotification(response);
                    
                    return true;
                } else {
                    System.out.println("Failed to retrieve generated ID.");
                    return false;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void sendSMSNotification(Response response) {
        try {
            // Get the user's phone number from the reclamation
            User user = response.getReclamation().getUser();
            String userPhone = user.getPhone();
            
            System.out.println("Attempting to send SMS to phone number: " + userPhone);
            
            if (userPhone != null && !userPhone.isEmpty()) {
                String messageBody = "🚨 WamiaGo Notification 🚨\n\n" +
                        "You have received a response to your reclamation:\n" +
                        "📝 Title: " + response.getReclamation().getTitle() + "\n" +
                        "💬 Response: " + response.getContent() + "\n\n" +
                        "Thank you for using WamiaGo!";

                System.out.println("Sending SMS with message: " + messageBody);
                
                try {
                    // Format phone number to international format if not already
                    String formattedNumber = formatPhoneNumber(userPhone);
                    System.out.println("Sending SMS to formatted number: " + formattedNumber);

                    Message message = Message.creator(
                            new PhoneNumber(formattedNumber),
                            new PhoneNumber(TWILIO_PHONE_NUMBER),
                            messageBody
                    ).create();

                    System.out.println("✅ SMS sent successfully with SID: " + message.getSid());
                } catch (Exception smsEx) {
                    System.out.println("❌ Error sending SMS: " + smsEx.getMessage());
                    smsEx.printStackTrace();
                }
            } else {
                System.out.println("❌ Could not send SMS: User phone number is missing or empty");
                System.out.println("User details: " + user.toString());
            }
        } catch (Exception e) {
            System.out.println("❌ Failed to send SMS notification: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String formatPhoneNumber(String phoneNumber) {
        // Remove any spaces, dashes, or parentheses
        phoneNumber = phoneNumber.replaceAll("[\\s\\-()]", "");
        
        // If number doesn't start with +, add the country code
        if (!phoneNumber.startsWith("+")) {
            // Assuming Tunisian numbers (216)
            if (phoneNumber.startsWith("0")) {
                phoneNumber = "+216" + phoneNumber.substring(1);
            } else if (phoneNumber.startsWith("216")) {
                phoneNumber = "+" + phoneNumber;
            } else {
                phoneNumber = "+216" + phoneNumber;
            }
        }
        
        return phoneNumber;
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
                response.setId_response(rs.getInt("id_response"));
                Reclamation reclamation = new ReclamationService().getById(rs.getInt("id_reclamation"));
                response.setReclamation(reclamation);
                response.setContent(rs.getString("content"));
                response.setDate(rs.getTimestamp("date"));

                responses.add(response);
            }
        }
        return responses;
    }

    public List<Response> getResponsesByReclamationId(int reclamationId) throws SQLException {
        List<Response> responses = new ArrayList<>();
        String query = "SELECT * FROM RESPONSE WHERE id_reclamation=?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, reclamationId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Response response = new Response();
                response.setId_response(resultSet.getInt("id_response"));
                response.setContent(resultSet.getString("content"));
                response.setDate(resultSet.getTimestamp("date"));
                // Set reclamation reference
                ReclamationService reclamationService = new ReclamationService();
                response.setReclamation(reclamationService.getById(reclamationId));

                responses.add(response);
            }
        }

        return responses;
    }

    public List<Response> getResponseByUser(User user) throws SQLException {
        String sql = "SELECT * FROM response as R , Reclamation as rec where rec.id_user = ? and r.id_reclamation=rec.id_reclamation";
        List<Response> responses = new ArrayList<>();
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, user.getId());
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            Response response = new Response();
            response.setId_response(resultSet.getInt("id_response"));
            response.setReclamation(new ReclamationService().getById(resultSet.getInt("id_reclamation")));
            response.setContent(resultSet.getString("content"));
            response.setDate(new java.sql.Timestamp(resultSet.getTimestamp("timestamp").getTime()));
            responses.add(response);

        }
        return responses;
    }

    public User getUserFromResponse(Response response) throws SQLException {
        String sql = "SELECT id_user from reclamation as rec ,response as res where rec.id_reclamation=res.id_reclamation AND ?=res.id_response ";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, response.getReclamation().getIdReclamation());
        User user=new User();
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            user = new UserService().getById(resultSet.getInt("id_user"));

        }
    return user;
    }


}

