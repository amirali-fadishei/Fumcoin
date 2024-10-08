package ExchangeApp;

import java.util.ArrayList;

public class User {

    private static User user;
    private String userShow;
    private String userFirstName;
    private String userLastName;
    private String userPassword;
    private String userEmail;
    private String userPhone;
    private String userImage;
    private double pD;
    private double eth;
    private double dog;
    private double bit;
    private double tet;
    private ArrayList<Double> profits = new ArrayList<>();

    public User(String userShow, String userFirstName, String userLastName, String userPassword, String userEmail, String userPhone, String userImage, double pD, double eth, double dog, double bit, double tet) {
        setUserShow(userShow);
        setUserFirstName(userFirstName);
        setUserLastName(userLastName);
        setUserPassword(userPassword);
        setUserEmail(userEmail);
        setUserPhone(userPhone);
        setUserImage(userImage);
        this.pD = pD;
        this.eth = eth;
        this.dog = dog;
        this.bit = bit;
        this.tet = tet;
    }

    public static User getUser() {
        return user;
    }

    public static void setUser(User user) {
        User.user = user;
    }

    public void updateFromDatabase() {
        Database.updateUserData(this);
    }

    public String getUserShow() {
        return userShow;
    }

    public void setUserShow(String userShow) {
        this.userShow = userShow;
    }

    public String getUserFirstName() {
        return userFirstName;
    }

    public void setUserFirstName(String userFirstName) {
        this.userFirstName = userFirstName;
    }

    public String getUserLastName() {
        return userLastName;
    }

    public void setUserLastName(String userLastName) {
        this.userLastName = userLastName;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public double getpD() {
        return pD;
    }

    public void setpD(double pD) {
        this.pD = pD;
    }

    public double getEth() {
        return eth;
    }

    public void setEth(double eth) {
        this.eth = eth;
    }

    public double getDog() {
        return dog;
    }

    public void setDog(double dog) {
        this.dog = dog;
    }

    public double getBit() {
        return bit;
    }

    public void setBit(double bit) {
        this.bit = bit;
    }

    public double getTet() {
        return tet;
    }

    public void setTet(double tet) {
        this.tet = tet;
    }

    public ArrayList<Double> getProfits() {
        return profits;
    }

    public String getProfitsAsString() {
        return String.join(",", profits.stream().map(String::valueOf).toArray(String[]::new));
    }

    public void setProfitsFromString(String profitsString) {
        profits = new ArrayList<>();
        try {
            for (String profit : profitsString.split(",")) {
                profits.add(Double.parseDouble(profit));
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    public void updateProfit(double number) {
        if (profits.isEmpty() || !profits.getLast().equals(number)) {
            profits.add(number);
            Database.saveProfits();
        }
    }
}
