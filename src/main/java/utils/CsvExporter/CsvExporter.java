package utils.CsvExporter;

import entities.Bicycle;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;

public class CsvExporter {
    public static void exportBicyclesToCsv(List<Bicycle> bicycles, File file) {
        try (PrintWriter writer = new PrintWriter(file)) {
            writer.println("id_bike,id_station,status,battery_level,range_km,last_updated");
            for (Bicycle bicycle : bicycles) {
                writer.println(bicycle.getId() + "," + bicycle.getStation().getId() + "," + bicycle.getStatus() + "," + bicycle.getBattery_level() + "," + bicycle.getRange_km() + "," + bicycle.getLast_updated());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
