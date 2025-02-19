package services;

import entities.Location;
import entities.Ride;
import entities.Request;
import entities.Driver;
import utils.DataBase;


import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
//pdf
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import java.io.FileOutputStream;


public class RideService implements IService<Ride> {
    private final Connection connection;

    public RideService() {
        this.connection = DataBase.getInstance().getConnection();
    }

    @Override
    public boolean create(Ride ride) throws SQLException {
        // Step 1: Retrieve departure and arrival location IDs from the request table
        String getLocationQuery = "SELECT id_departure_location, id_arrival_location FROM request WHERE id_request = ?";
        try (PreparedStatement getLocationStmt = connection.prepareStatement(getLocationQuery)) {
            getLocationStmt.setInt(1, ride.getRequest().getIdRequest());
            try (ResultSet locationResult = getLocationStmt.executeQuery()) {
                if (locationResult.next()) {
                    int departureLocationId = locationResult.getInt("id_departure_location");
                    int arrivalLocationId = locationResult.getInt("id_arrival_location");

                    // Step 2: Calculate distance using LocationService
                    double distance = new Location().calculateDistance(new LocationService().getById(departureLocationId),new LocationService().getById( arrivalLocationId));

                    // Step 3: Insert the ride into the database
                    String sql = "INSERT INTO ride (id_request, id_taxi, distance, duration, price, status, ride_date) VALUES (?,?,?,?,?,?,?)";
                    try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                        preparedStatement.setInt(1, ride.getRequest().getIdRequest());
                        preparedStatement.setInt(2, ride.getDriver().getIdDriver()); // Add driver ID
                        preparedStatement.setBigDecimal(3, new BigDecimal(distance)); // Use calculated distance
                        preparedStatement.setInt(4, ride.getDuration());
                        preparedStatement.setBigDecimal(5, new BigDecimal(ride.getPrice()));
                        preparedStatement.setString(6, ride.getStatus().toString());
                        preparedStatement.setTimestamp(7, ride.getRideDate());
                        preparedStatement.executeUpdate();
                    }
                    System.out.println("✅ Ride created successfully");
                    return true;

                } else {
                    System.out.println("❌ Request not found");
                    return false;
                }
            }
        }
    }


    @Override
    public void update(Ride ride) throws SQLException {
        String sql = "UPDATE ride SET id_request = ?, id_taxi = ?, distance = ?, duration = ?, price = ?, status = ?, ride_date = ? WHERE id_ride = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, ride.getRequest().getIdRequest());
        preparedStatement.setInt(2, ride.getDriver().getIdDriver());  // Ajouter l'id du driver
        preparedStatement.setBigDecimal(3, new BigDecimal(ride.getDistance()));
        preparedStatement.setInt(4, ride.getDuration());
        preparedStatement.setBigDecimal(5, new BigDecimal(ride.getPrice()));
        preparedStatement.setString(6, ride.getStatus().toString());
        preparedStatement.setTimestamp(7, ride.getRideDate());
        preparedStatement.setInt(8, ride.getIdRide());
        preparedStatement.executeUpdate();
        System.out.println("✅ Ride updated successfully");
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM ride WHERE id_ride = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, id);
        preparedStatement.executeUpdate();
        System.out.println("✅ Ride deleted successfully");
    }

    @Override
    public List<Ride> read() throws SQLException {
        List<Ride> rides = new ArrayList<>();

        String sql = "SELECT * FROM ride";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            // Pour récupérer la Request et le Driver associés
            RequestService requestService = new RequestService();
            DriverService driverService = new DriverService();  // Service pour récupérer un driver

            while (resultSet.next()) {
                Request request = requestService.getById(resultSet.getInt("id_request"));
                Driver driver = driverService.getById(resultSet.getInt("id_taxi"));  // Récupérer le driver
                Ride ride = new Ride(
                        resultSet.getInt("id_ride"),
                        request,
                        driver,
                        resultSet.getDouble("distance"),
                        resultSet.getInt("duration"),
                        resultSet.getDouble("price"),
                        Ride.Status.valueOf(resultSet.getString("status")),
                        resultSet.getTimestamp("ride_date")
                );
                rides.add(ride);
            }
        }
        return rides;
    }

    public Ride getById(int id) throws SQLException {
        String sql = "SELECT * FROM ride WHERE id_ride = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    RequestService requestService = new RequestService();
                    DriverService driverService = new DriverService();
                    Request request = requestService.getById(rs.getInt("id_request"));
                    Driver driver = driverService.getById(rs.getInt("id_taxi"));
                    return new Ride(
                            rs.getInt("id_ride"),
                            request,
                            driver,
                            rs.getDouble("distance"),
                            rs.getInt("duration"),
                            rs.getDouble("price"),
                            Ride.Status.valueOf(rs.getString("status")),
                            rs.getTimestamp("ride_date")
                    );
                }
            }
        }
        return null;
    }

    // Autres méthodes
    public List<Ride> getByClient(entities.User client) throws SQLException {
        List<Ride> rides = new ArrayList<>();
        // On joint la table ride et request pour filtrer par id_client dans request
        String sql = "SELECT r.* FROM ride r JOIN request req ON r.id_request = req.id_request WHERE req.id_client = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, client.getId());
            try (ResultSet rs = ps.executeQuery()) {
                RequestService requestService = new RequestService();
                DriverService driverService = new DriverService();
                while (rs.next()) {
                    Request request = requestService.getById(rs.getInt("id_request"));
                    Driver driver = driverService.getById(rs.getInt("id_taxi"));
                    Ride ride = new Ride(
                            rs.getInt("id_ride"),
                            request,
                            driver,  // Ajouter le driver ici
                            rs.getDouble("distance"),
                            rs.getInt("duration"),
                            rs.getDouble("price"),
                            Ride.Status.valueOf(rs.getString("status")),
                            rs.getTimestamp("ride_date")
                    );
                    rides.add(ride);
                }
            }
        }
        return rides;
    }

    public List<Ride> getByStatus(Ride.Status status) throws SQLException {
        List<Ride> rides = new ArrayList<>();
        String sql = "SELECT * FROM ride WHERE status = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, status.toString());
            try (ResultSet rs = ps.executeQuery()) {
                RequestService requestService = new RequestService();
                DriverService driverService = new DriverService();
                while (rs.next()) {
                    Request request = requestService.getById(rs.getInt("id_request"));
                    Driver driver = driverService.getById(rs.getInt("id_taxi"));
                    Ride ride = new Ride(
                            rs.getInt("id_ride"),
                            request,
                            driver,
                            rs.getDouble("distance"),
                            rs.getInt("duration"),
                            rs.getDouble("price"),
                            Ride.Status.valueOf(rs.getString("status")),
                            rs.getTimestamp("ride_date")
                    );
                    rides.add(ride);
                }
            }
        }
        return rides;
    }

    public List<Ride> sortRidesByDate(boolean ascending) throws SQLException {
        // Build the SQL query to fetch rides, joined with the request and driver tables
        String sqlQuery = "SELECT * FROM ride r " +
                "JOIN request req ON r.id_request = req.id_request " +
                "JOIN driver d ON r.id_taxi = d.id_driver ";

        // Add sorting by ride_date to the query
        sqlQuery += "ORDER BY r.ride_date " + (ascending ? "ASC" : "DESC");

        List<Ride> sortedRides = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sqlQuery);
             ResultSet rs = ps.executeQuery()) {

            // Process the result set and build the list of sorted Rides
            RequestService requestService = new RequestService();
            DriverService driverService = new DriverService();

            while (rs.next()) {
                Request request = requestService.getById(rs.getInt("id_request"));
                Driver driver = driverService.getById(rs.getInt("id_taxi"));
                Ride ride = new Ride(
                        rs.getInt("id_ride"),
                        request,
                        driver,
                        rs.getDouble("distance"),
                        rs.getInt("duration"),
                        rs.getDouble("price"),
                        Ride.Status.valueOf(rs.getString("status")),
                        rs.getTimestamp("ride_date")
                );
                sortedRides.add(ride);
            }
        }
        return sortedRides;
    }

    public void exportRidesToPdf(String filePath) {
        Document document = new Document(PageSize.A4);

        try {
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();

            addDocumentHeader(document);

            document.add(new Paragraph("\nRides List:\n", getFont(14, Font.BOLD, BaseColor.BLACK)));
            document.add(createStyledTable(
                    "SELECT id_ride, r.id_taxi, r.id_client, r.id_request, r.distance, r.duration, r.price, r.status, r.ride_date, t.license_plate, c.name " +
                            "FROM `ride` r " +
                            "JOIN `taxi` t ON r.id_taxi = t.id_taxi " +
                            "JOIN `client` c ON r.id_client = c.id_client",
                    new String[]{"Ride ID", "Taxi ID", "Client ID", "Request ID", "Distance", "Duration", "Price", "Status", "Ride Date", "License Plate", "Client Name"},
                    new String[]{"id_ride", "id_taxi", "id_client", "id_request", "distance", "duration", "price", "status", "ride_date", "license_plate", "name"}
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
        Paragraph title = new Paragraph("Rides Report\n\n", titleFont);
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


}
