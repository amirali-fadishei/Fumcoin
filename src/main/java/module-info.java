module Login_SignUp {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires net.synedra.validatorfx;
    requires java.desktop;
    requires javafx.swing;
    requires captcha;
    requires java.mail;
    requires java.sql;
    requires java.compiler;
    requires org.apache.httpcomponents.httpcore;
    requires org.apache.httpcomponents.httpclient;
    requires com.fasterxml.jackson.databind;
    requires java.net.http;
    requires org.json;

    opens ExchangeApp to javafx.fxml;
    exports ExchangeApp;
}