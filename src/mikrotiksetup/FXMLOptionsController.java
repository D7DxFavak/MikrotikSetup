/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mikrotiksetup;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Favak-ntb
 */
public class FXMLOptionsController implements Initializable {

    @FXML
    private Button jBResetovat;
    @FXML
    private Button jBNastavitPristup;
    @FXML
    private Button jBZrusitPristup;

    private String adresaIPReset;
    private String hesloReset;

    private JSch jsch;
    private com.jcraft.jsch.Session session;
    private Properties config;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    @FXML
    private void resetovatZarizeni(ActionEvent event) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("FXMLDialog.fxml"));
        Parent parent = fxmlLoader.load();
        FXMLDialogController dialogController = fxmlLoader.<FXMLDialogController>getController();

        Scene scene = new Scene(parent, 500, 150);
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.setTitle("Mikrotik Reset");
        stage.getIcons().add(new Image("/images/mikrotikICO.png"));
        stage.showAndWait();

        adresaIPReset = dialogController.getIpAdresa();
        hesloReset = dialogController.getHeslo();

        try {
            jsch = new JSch();
            session = jsch.getSession("admin", adresaIPReset, 22);
            if (!hesloReset.isEmpty()) {
                session.setPassword(hesloReset);
            }
            config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.connect();
            ChannelExec channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand("system reset-configuration");
            channel.connect();
            Thread.sleep(1000);
            channel.disconnect();
            channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand("y");
            channel.connect();
            Thread.sleep(1000);
            channel.disconnect();

            session.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @FXML
    private void nastavitPristup(ActionEvent event) throws Exception{
         try {
            jsch = new JSch();
            session = jsch.getSession("admin", "192.168.88.1", 22);            
            config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.connect();
            ChannelExec channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand("ip address add address= 10.10.200.150/24 network=10.10.200.0 interface=wlan1");
            channel.connect();
            Thread.sleep(1000);
            channel.disconnect();         

            session.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }
       
    }
    
    @FXML
    private void zrusitPristup(ActionEvent event) {
        try {
            jsch = new JSch();
            session = jsch.getSession("admin", "192.168.88.1", 22);            
            config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.connect();
            ChannelExec channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand("ip address remove [find address=\"10.10.200.150/24\"]");
            channel.connect();
            Thread.sleep(1000);
            channel.disconnect();         

            session.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }
       
    }
    
}
