import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
 
public class UploadFile {
 
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:33060/databoxdb";
        String user = "root";
        String password = "databoxdb";
 
        String filePath = "/homes/ji43/Desktop/eastern-gray-squirrel.jpg";
 
        try {
            Connection conn = DriverManager.getConnection(url, user, password);
 
            String sql = "INSERT INTO file (userName,fileName,content,type) values (?, ?, ?,?)";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, "FattyMatthew");
            statement.setString(2, "squirrel");
            InputStream inputStream = new FileInputStream(new File(filePath));
 
            statement.setBlob(3, inputStream);
 			statement.setString(4, "jpg");
            int row = statement.executeUpdate();
            if (row > 0) {
                System.out.println("A contact was inserted with photo image.");
            }
            conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
