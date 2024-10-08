package ExchangeApp;

import javafx.event.ActionEvent;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.Alert;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class Database {


    private static final String DB_URL = "jdbc:mysql://localhost:3306/fumcoin";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";


    public static void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.getDialogPane().setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    private static boolean userExists(PreparedStatement checkStmt, String uname, String uemail, String uphone) throws SQLException {
        checkStmt.setString(1, uname);
        checkStmt.setString(2, uemail);
        checkStmt.setString(3, uphone);
        try (ResultSet result = checkStmt.executeQuery()) {
            return result.next();
        }
    }

    public static void signUpDB(ActionEvent event, String fname, String lname, String uname, String upass, String uemail, String uphone, String imgPath) {
        String checkSQL = "SELECT * FROM users WHERE user_name = ? OR email = ? OR phone_number = ?";
        String insertSQL = "INSERT INTO users (first_name, last_name, user_name, password, email, phone_number, imagePath) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = getConnection(); PreparedStatement checkStmt = connection.prepareStatement(checkSQL); PreparedStatement insertStmt = connection.prepareStatement(insertSQL)) {

            if (userExists(checkStmt, uname, uemail, uphone)) {
                showAlert(Alert.AlertType.WARNING, "ثبت نام کاربر", "کاربری با این نام کاربری، ایمیل یا شماره تلفن وجود دارد!");
            } else {
                insertStmt.setString(1, fname);
                insertStmt.setString(2, lname);
                insertStmt.setString(3, uname);
                insertStmt.setString(4, upass);
                insertStmt.setString(5, uemail);
                insertStmt.setString(6, uphone);
                insertStmt.setString(7, imgPath);
                insertStmt.executeUpdate();

                showAlert(Alert.AlertType.INFORMATION, "ثبت نام کاربر", "ثبت نام با موفقیت انجام شد!");
                Main.stageChanger(event, "Login.fxml");
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "خطا", "خطایی در ثبت نام رخ داد: " + e.getMessage());
        }
    }

    public static void loginDB(ActionEvent event, String uname, String upass) {
        String selectSQL = "SELECT * FROM users WHERE user_name = ? AND password = ?";

        try (Connection connection = getConnection(); PreparedStatement psmt = connection.prepareStatement(selectSQL)) {

            psmt.setString(1, uname);
            psmt.setString(2, upass);
            try (ResultSet result = psmt.executeQuery()) {
                if (result.next()) {
                    User.setUser(new User(result.getString("user_name"), result.getString("first_name"), result.getString("last_name"), result.getString("password"), result.getString("email"), result.getString("phone_number"), result.getString("imagePath"), result.getDouble("profit"), result.getDouble("Ethereum"), result.getDouble("Dogecoin"), result.getDouble("Notcoin"), result.getDouble("Hamester")));
                    Database.loadProfits();
                    showAlert(Alert.AlertType.INFORMATION, "ورود کاربر", "با موفقیت وارد شدید!");
                    Main.stageChanger(event, "Profile.fxml");
                    Main.immediateUpdate();
                } else {
                    showAlert(Alert.AlertType.INFORMATION, "ورود کاربر", "رمز عبور اشتباه است یا کاربری با این نام وجود ندارد!");
                }
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "خطا", "خطایی در ورود رخ داد: " + e.getMessage());
        }
    }

    public static void emailLogin(ActionEvent event, String email) {
        String selectSQL = "SELECT * FROM users WHERE email = ?";

        try (Connection connection = getConnection(); PreparedStatement psmt = connection.prepareStatement(selectSQL)) {

            psmt.setString(1, email);
            try (ResultSet result = psmt.executeQuery()) {
                if (result.next()) {
                    User.setUser(new User(result.getString("user_name"), result.getString("first_name"), result.getString("last_name"), result.getString("password"), result.getString("email"), result.getString("phone_number"), result.getString("imagePath"), result.getDouble("profit"), result.getDouble("Ethereum"), result.getDouble("Dogecoin"), result.getDouble("Notcoin"), result.getDouble("Hamester")));
                    Database.loadProfits();
                    Main.stageChanger(event, "Profile.fxml");
                    Main.immediateUpdate();
                } else {
                    showAlert(Alert.AlertType.INFORMATION, "ورود کاربر", "کاربری با این ایمیل حساب کاربری ندارد!");
                }
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "خطا", "خطایی در ورود رخ داد: " + e.getMessage());
        }
    }

    public static void update(String desUser, String token, double value) {
        String query = "UPDATE users SET " + token + " = " + token + " + ? WHERE user_name = ?";
        try (Connection connection = getConnection(); PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setDouble(1, value);
            stmt.setString(2, desUser);
            stmt.executeUpdate();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "خطا", "خطا در آپدیت اطلاعات: " + e.getMessage());
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "خطا", "مقدار وارد شده معتبر نمی‌باشد: " + e.getMessage());
        }
    }

    public static void periodicUpdate() {
        try (Connection connection = getConnection()) {
            String query = "UPDATE users SET profit = ?, `profit-chart` = ?, Ethereum = ?, Dogecoin = ?, Notcoin = ?, Hamester = ? WHERE user_name = ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setDouble(1, User.getUser().getpD());
                stmt.setString(2, User.getUser().getProfitsAsString());
                stmt.setDouble(3, User.getUser().getEth());
                stmt.setDouble(4, User.getUser().getDog());
                stmt.setDouble(5, User.getUser().getBit());
                stmt.setDouble(6, User.getUser().getTet());
                stmt.setString(7, User.getUser().getUserShow());
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "خطا", "خطا در آپدیت اطلاعات: " + e.getMessage());
        }
    }

    public static void updateInfo(String fname, String lname, String upass, String uemail, String uphone, String imagePath) {
        String updateSql = "UPDATE users SET first_name=?, last_name=?, password=?, email=?, phone_number=?,imagePath = ? WHERE user_name=?";
        if (imagePath == null) {
            imagePath = User.getUser().getUserImage();
        }

        try (Connection connection = getConnection(); PreparedStatement psmt = connection.prepareStatement(updateSql)) {

            psmt.setString(1, fname);
            psmt.setString(2, lname);
            psmt.setString(3, upass);
            psmt.setString(4, uemail);
            psmt.setString(5, uphone);
            psmt.setString(6, imagePath);
            psmt.setString(7, User.getUser().getUserShow());

            int result = psmt.executeUpdate();
            if (result > 0) {
                showAlert(Alert.AlertType.INFORMATION, "اطلاعات کاربر", "اطلاعات کاربر با موفقیت بروزرسانی شد!");
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "خطا", "خطایی در بروزرسانی رخ داد: " + e.getMessage());
        }
    }

    public static void saveBills(String status, String type, String sender, String token, double amount, double value) {
        String insertSQL = "INSERT INTO bills (status, type, sender, token, amount, value) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection = getConnection(); PreparedStatement insertStmt = connection.prepareStatement(insertSQL)) {
            insertStmt.setString(1, status);
            insertStmt.setString(2, type);
            insertStmt.setString(3, sender);
            insertStmt.setString(4, token);
            insertStmt.setDouble(5, amount);
            insertStmt.setDouble(6, value);
            insertStmt.executeUpdate();
            showAlert(Alert.AlertType.INFORMATION, "خرید/فروش ارز", "سفارش شما با موفقیت ثبت گردید!");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "خطا", "خطایی در عملیات رخ داد: " + e.getMessage());
        }
    }

    public static List<Object[]> showBills(String par) {
        List<Object[]> bills = new ArrayList<>();
        String selectSQL;
        if (par.equals(User.getUser().getUserShow())) {
            selectSQL = "SELECT * FROM bills WHERE sender = ?";
            if (User.getUser().getUserShow().equals("admin2024")) {
                selectSQL = "SELECT * FROM bills";
                try (Connection connection = getConnection(); PreparedStatement psmt = connection.prepareStatement(selectSQL)) {
                    try (ResultSet result = psmt.executeQuery()) {
                        while (result.next()) {
                            Object[] record = new Object[5];
                            record[0] = result.getString("status");
                            record[1] = result.getString("type");
                            record[2] = result.getString("token");
                            record[3] = result.getDouble("amount");
                            record[4] = result.getDouble("value");
                            bills.add(record);
                        }
                    }
                } catch (SQLException e) {
                    showAlert(Alert.AlertType.ERROR, "خطا", "خطایی در اطلاعات رخ داد: " + e.getMessage());
                }
            } else {
                try (Connection connection = getConnection(); PreparedStatement psmt = connection.prepareStatement(selectSQL)) {
                    psmt.setString(1, par);
                    try (ResultSet result = psmt.executeQuery()) {
                        while (result.next()) {
                            Object[] record = new Object[5];
                            record[0] = result.getString("status");
                            record[1] = result.getString("type");
                            record[2] = result.getString("token");
                            record[3] = result.getDouble("amount");
                            record[4] = result.getDouble("value");
                            bills.add(record);
                        }
                    }
                } catch (SQLException e) {
                    showAlert(Alert.AlertType.ERROR, "خطا", "خطایی در اطلاعات رخ داد: " + e.getMessage());
                }
            }
        } else {
            selectSQL = "SELECT * FROM bills WHERE token = ?";
            try (Connection connection = getConnection(); PreparedStatement psmt = connection.prepareStatement(selectSQL)) {
                psmt.setString(1, par);
                try (ResultSet result = psmt.executeQuery()) {
                    while (result.next()) {
                        Object[] record = new Object[3];
                        record[0] = result.getString("type");
                        record[1] = result.getDouble("amount");
                        record[2] = result.getDouble("value");
                        bills.add(record);
                    }
                }
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "خطا", "خطایی در اطلاعات رخ داد: " + e.getMessage());
            }
        }
        return bills;
    }

    public static double checkBills(String type, String sender, String token, double num, double value) {
        double money = 0.0;
        String opposit = type.equals("فروش") ? "خرید" : "فروش";
        String matchSQL;
        if (type.equals("فروش")) {
            matchSQL = "SELECT * FROM bills WHERE type = ? AND token = ? AND `value` >= ? AND amount = ? AND sender <> ? AND status = 'pending' ORDER BY id ASC";
        } else {
            matchSQL = "SELECT * FROM bills WHERE type = ? AND token = ? AND `value` <= ? AND amount = ? AND sender <> ? AND status = 'pending' ORDER BY id ASC";
        }
        try (Connection connection = getConnection(); PreparedStatement psmt = connection.prepareStatement(matchSQL)) {
            psmt.setString(1, opposit);
            psmt.setString(2, token);
            psmt.setDouble(3, value);
            psmt.setDouble(4, num);
            psmt.setString(5, sender);
            try (ResultSet result = psmt.executeQuery()) {
                if (result.next()) {
                    String buyerBalance;
                    String buyer = result.getString("sender");
                    money = result.getDouble("value");
                    if (opposit.equals("خرید")) {
                        buyerBalance = "UPDATE users SET profit = profit - ?, " + token + " = " + token + " + ? WHERE user_name = ?";
                    } else {
                        buyerBalance = "UPDATE users SET profit = profit + ?, " + token + " = " + token + " - ? WHERE user_name = ?";
                    }
                    try (PreparedStatement buyerBalanceStmt = connection.prepareStatement(buyerBalance)) {
                        buyerBalanceStmt.setDouble(1, ((double) 99 / 100) * money);
                        buyerBalanceStmt.setDouble(2, num);
                        buyerBalanceStmt.setString(3, buyer);
                        buyerBalanceStmt.executeUpdate();
                    } catch (SQLException e) {
                        showAlert(Alert.AlertType.ERROR, "خطا", "خطایی در بروزرسانی رخ داد: " + e.getMessage());
                    }
                    Database.update("admin2024", "profit", ((double) 1 / 100) * money);
                    updateBillStatus(sender, token, num, value);
                    updateBillStatus(buyer, token, num, money);
                    return money;
                }
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "خطا", "خطایی در اطلاعات رخ داد: " + e.getMessage());
        }
        return money;
    }

    public static void updateBillStatus(String man, String token, double num, double value) {
        String update = "UPDATE bills SET status = 'success' WHERE sender = ? AND token = ? AND amount = ? AND `value` = ?";
        try (PreparedStatement smt = getConnection().prepareStatement(update)) {
            smt.setString(1, man);
            smt.setString(2, token);
            smt.setDouble(3, num);
            smt.setDouble(4, value);
            smt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void saveProfits() {
        String INSERT_NUMBERS = "UPDATE users SET `profit-chart` = ? WHERE user_name = ?";
        String profitsString = User.getUser().getProfitsAsString();
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(INSERT_NUMBERS)) {
            pstmt.setString(1, profitsString);
            pstmt.setString(2, User.getUser().getUserShow());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void loadProfits() {
        String SELECT_NUMBERS = "SELECT `profit-chart` FROM users WHERE user_name = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(SELECT_NUMBERS)) {
            stmt.setString(1, User.getUser().getUserShow());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String profitsString = rs.getString("profit-chart");
                    User.getUser().setProfitsFromString(profitsString);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateUserData(User user) {
        String query = "SELECT * FROM users WHERE user_name = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, user.getUserShow());
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                user.setProfitsFromString(rs.getString("profit-chart"));
                user.setpD(rs.getDouble("profit"));
                user.setEth(rs.getDouble("Ethereum"));
                user.setDog(rs.getDouble("Dogecoin"));
                user.setBit(rs.getDouble("Notcoin"));
                user.setTet(rs.getDouble("Hamester"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}