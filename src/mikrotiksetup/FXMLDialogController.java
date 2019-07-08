/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mikrotiksetup;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Favak-ntb
 */
public class FXMLDialogController implements Initializable {

    @FXML
    private Label jLMessage;
    @FXML
    private Button jBPotvrdit;
    @FXML
    private Button jBZrusit;
    @FXML
    private TextField jTFIPTextField;
    @FXML
    private TextField jTFHeslo;
    @FXML
    private ImageView iVStatus;
    private String ipAdresa;
    private String heslo;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Image alertDot = new Image(getClass().getResourceAsStream("/images/alertDot.png"));
        iVStatus.setImage(alertDot);
        ipAdresa = "";
        setHeslo("");
    }

    @FXML
    private void potvrdit(ActionEvent event) {
        ipAdresa = jTFIPTextField.getText().trim();
        setHeslo(jTFHeslo.getText().trim());
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void zrusit(ActionEvent event) {
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

    /**
     * @return the ipAdresa
     */
    public String getIpAdresa() {
        return ipAdresa;
    }

    /**
     * @param ipAdresa the ipAdresa to set
     */
    public void setIpAdresa(String ipAdresa) {
        this.ipAdresa = ipAdresa;
    }

    /**
     * @return the heslo
     */
    public String getHeslo() {
        return heslo;
    }

    /**
     * @param heslo the heslo to set
     */
    public void setHeslo(String heslo) {
        this.heslo = heslo;
    }

}
