/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mikrotiksetup;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

/**
 *
 * @author Favak-ntb
 */
public class FXMLDocumentController implements Initializable {

    @FXML
    private RadioButton jRBBridge;
    @FXML
    private RadioButton jRBRouter;
    @FXML
    private RadioButton jRBModA;
    @FXML
    private RadioButton jRBModAN;
    @FXML
    private RadioButton jRBModN;
    @FXML
    private CheckBox jCBDHCP;
    @FXML
    private TextField jTFAdresa;
    @FXML
    private TextField jTFMaska;
    @FXML
    private TextField jTFBrana;
    @FXML
    private TextField jTFSSID;
    @FXML
    private TextField jTFWep;
    @FXML
    private ChoiceBox jChoiceBVykon;
    @FXML
    private TextField jTFVzdalenost;
    @FXML
    private CheckBox jCBKonfRate;
    @FXML
    private TextField jTFNazevKlienta;
    @FXML
    private TextField jTFHeslo;
    @FXML
    private TextField jTFPrubeh;
    @FXML
    private Button jBPotvrdit;
    @FXML
    private Button jBZrusit;
    @FXML
    private Button jBResetovat;
    @FXML
    private Button jBFirmwareUpload;
    @FXML
    private Button jBFirmwareUpdate;
    @FXML
    private ComboBox jComboBoxZarizeni;
    @FXML
    private ComboBox jComboBoxKlic;
    @FXML
    private ComboBox jComboBoxKanal;
    @FXML
    private ImageView iVStatus;
    @FXML
    private TextField jTFMacAdresa;

    private JSch jsch;
    private com.jcraft.jsch.Session session;
    private List<String> commands;
    private Properties config;
    private String vyberFWNazev;

    private String klientMac;
    private boolean settingInProgress;

    @FXML
    private void zmenitZarizeni(ActionEvent event) {
        if (jComboBoxZarizeni.getSelectionModel().getSelectedItem().toString().equals("DiscLite 5ac")
                || jComboBoxZarizeni.getSelectionModel().getSelectedItem().toString().equals("LHG 5ac")
                || jComboBoxZarizeni.getSelectionModel().getSelectedItem().toString().equals("SXTsq 5ac")) {
            vyberFWNazev = "Vyber arm soubor";
            jRBModA.setText("A");
            jRBModAN.setText("A/N");
            jRBModN.setText("N");
            jComboBoxKanal.setVisible(false);
            jCBKonfRate.setVisible(true);
        } else if (jComboBoxZarizeni.getSelectionModel().getSelectedItem().toString().equals("hAP Lite")) {
            vyberFWNazev = "Vyber smips soubor";
            jRBModA.setText("B");
            jRBModAN.setText("B/G");
            jRBModN.setText("B/G/N");
            jComboBoxKanal.setVisible(true);
            jCBKonfRate.setVisible(false);

        }
    }

    @FXML
    private void nahratFirmware(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(vyberFWNazev);
        File firmwareLocal = fileChooser.showOpenDialog(jBFirmwareUpload.getScene().getWindow());
        System.out.println("Soubor : " + firmwareLocal.getName());

        String server = "192.168.88.1";
        int port = 21;
        String uzivatel = "admin";
        String heslo = "";

        FTPClient mikrotikFTP = new FTPClient();
        try {
            settingInProgress = true;
            mikrotikFTP.connect(server, port);
            mikrotikFTP.login(uzivatel, heslo);
            mikrotikFTP.enterLocalActiveMode();

            mikrotikFTP.setFileType(FTP.BINARY_FILE_TYPE);

            String firmwareRemote = firmwareLocal.getName();
            InputStream inputStream = new FileInputStream(firmwareLocal);

            boolean done = mikrotikFTP.storeFile(firmwareRemote, inputStream);
            inputStream.close();
            Thread.sleep(1000);
            jsch = new JSch();
            session = jsch.getSession("admin", "192.168.88.1", 22);
            commands = nactiPrikazy();
            //session.setPassword(heslo);
            config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.connect();
            ChannelExec channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand("system reboot");
            channel.connect();
            Thread.sleep(1000);
            channel.disconnect();
            channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand("y");
            channel.connect();
            Thread.sleep(1000);
            channel.disconnect();
            session.disconnect();
            settingInProgress = false;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void updateFirmware(ActionEvent event) {
        try {
            settingInProgress = true;
            jsch = new JSch();
            session = jsch.getSession("admin", "192.168.88.1", 22);
            commands = nactiPrikazy();
            //session.setPassword(heslo);
            config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.connect();
            ChannelExec channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand("system routerboard upgrade");
            channel.connect();
            Thread.sleep(1000);
            channel.disconnect();
            channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand("system reboot");
            channel.connect();
            Thread.sleep(1000);
            channel.disconnect();
            channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand("y");
            channel.connect();
            Thread.sleep(1000);
            channel.disconnect();
            session.disconnect();
            settingInProgress = false;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void povrditNastaveni(ActionEvent event) {
        try {
            jsch = new JSch();
            session = jsch.getSession("admin", "192.168.88.1", 22);
            commands = nactiPrikazy();
            //session.setPassword(heslo);
            config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);

            if (jComboBoxZarizeni.getSelectionModel().getSelectedItem().toString().equals("DiscLite 5ac")
                    || jComboBoxZarizeni.getSelectionModel().getSelectedItem().toString().equals("LHG 5ac")
                    || jComboBoxZarizeni.getSelectionModel().getSelectedItem().toString().equals("SXTsq 5ac")) {
                nastavZarizeniPrijem(jComboBoxZarizeni.getSelectionModel().getSelectedItem().toString());
            } else if (jComboBoxZarizeni.getSelectionModel().getSelectedItem().toString().equals("hAP Lite")) {
                nastavZarizeniAP(jComboBoxZarizeni.getSelectionModel().getSelectedItem().toString());
            }

            Thread t = new Thread() {
                @Override
                public void run() {
                    try {
                        settingInProgress = true;
                        session.connect();
                        if (session.isConnected()) {
                            jTFPrubeh.setText("connected");
                        }
                        for (String command : commands) {
                            ChannelExec channel = (ChannelExec) session.openChannel("exec");
                            channel.setCommand(command);

                            channel.connect();
                            // System.out.println(command);
                            jTFPrubeh.setText(command);
                            Thread.sleep(1000);
                            channel.disconnect();
                        }
                        session.disconnect();
                        settingInProgress = false;
                        jTFPrubeh.setText("hotovo");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };

            t.start();

        } catch (Exception e) {
            jTFPrubeh.setText(e.getMessage());
            e.printStackTrace();
        }
    }

    private void nastavZarizeniPrijem(String typ) {
        if (jRBRouter.isSelected()) {
            commands.add("ip address set [/ip address find address=\"192.168.88.1/24\"] interface=ether1");
            commands.add("interface bridge port remove [/interface bridge port find interface=\"ether1\"]");
            commands.add("interface bridge port remove [/interface bridge port find interface=\"wlan1\"]");
            commands.add("interface bridge remove [/interface bridge find name=\"bridge1\"]");
            // commands.add("ip firewall nat add action=masquerade chain=srcnat out-interface=wlan1");
        }

        if (jRBBridge.isSelected()) {
            commands.add("interface bridge add name=bridge1 protocol-mode=none arp=enabled");
            commands.add("interface bridge port add bridge=bridge1 interface=wlan1");
            commands.add("interface bridge port add bridge=bridge1 interface=ether1");
            commands.add("ip dhcp-client set interface=wlan1 disabled=yes numbers=0");
            commands.add("ip dhcp-server set interface=ether1 disabled=yes numbers=0");
            commands.add("interface wireless set wlan1 mode=station-pseudobridge");

            if (!jTFBrana.getText().trim().isEmpty()) {
                commands.add("ip dhcp-relay add interface=bridge1 dhcp-server=" + jTFBrana.getText().trim() + " disabled=no");
                commands.add("ip route add gateway=" + jTFBrana.getText().trim());
            }

            if (!jTFAdresa.getText().trim().isEmpty()) {
                commands.add("ip address add address=" + jTFAdresa.getText().trim() + "/24 network=10.10.200.0 interface=bridge1");
            }

            //  
            commands.add("ip firewall nat disable numbers=0");

        }
        if (!jTFNazevKlienta.getText().trim().isEmpty()) {
            commands.add("system identity set name=" + jTFNazevKlienta.getText().trim());
        }
        if (!jTFHeslo.getText().trim().isEmpty()) {
            commands.add("user set admin password=" + jTFHeslo.getText().trim());
        }

        if (!jComboBoxKlic.getItems().isEmpty() && !jTFWep.getText().isEmpty()) {
            if (jComboBoxKlic.getSelectionModel().getSelectedItem().toString().equals("WPA2-AES")) {
                commands.add("interface wireless security-profiles set default mode=dynamic-keys authentication-types=wpa2-psk wpa2-pre-shared-key=" + jTFWep.getText() + " comment=" + jTFWep.getText());
            } else if (jComboBoxKlic.getSelectionModel().getSelectedItem().toString().equals("WEP-128bit")) {
                StringBuilder builder = new StringBuilder();
                for (char c : jTFWep.getText().toCharArray()) {
                    int i = (int) c;
                    builder.append(Integer.toHexString(i).toUpperCase());
                }
                commands.add("interface wireless security-profiles set default mode=static-keys-required static-transmit-key=key-0 static-algo-0=104bit-wep static-key-0=" + builder.toString() + " comment=" + jTFWep.getText() + " - " + builder.toString());
            }
        }

        String band = "";
        if (jRBModA.isSelected()) {
            band = "band=5ghz-a ";
        } else if (jRBModAN.isSelected()) {
            band = "band=5ghz-a/n ";
        } else if (jRBModN.isSelected()) {
            band = "band=5ghz-onlyn ";
        }
        String ssid = "";
        if (!jTFSSID.getText().trim().isEmpty()) {
            ssid = "ssid=" + jTFSSID.getText().trim() + " ";
        }
        String distance = "";
        if (!jTFVzdalenost.getText().trim().isEmpty()) {
            distance = "distance=" + jTFVzdalenost.getText().trim() + " ";
        }
        String konfRate = "";
        if (jCBKonfRate.isSelected()) {
            konfRate = "rate-set=configured ht-supported-mcs=mcs-12,mcs-13,mcs-14,mcs-15 ht-basic-mcs=mcs-12 supported-rates-a/g=54Mbps basic-rates-a/g=54Mbps ";
        }

        String txPower = "";
        if (!jChoiceBVykon.getItems().isEmpty()) {

            txPower = "tx-power-mode=all-rates-fixed " + "tx-power=" + jChoiceBVykon.getValue() + " ";
        }
        String fullCommand = "interface wireless set wlan1 preamble-mode=long channel-width=20mhz country=debug station-roaming=disabled wmm-support=enabled ";
        //System.out.println(fullCommand + " " + band + ssid + distance + konfRate + txPower);
        commands.add(fullCommand + " " + band + ssid + distance + konfRate + txPower);

        if (jRBBridge.isSelected()) {
            commands.add("ip address remove [find address=\"192.168.88.1/24\"]");
        }
    }

    private void nastavZarizeniAP(String typ) {
        if (jRBRouter.isSelected()) {
            commands.add("interface bridge set bridge protocol-mode=none");
        }

        if (jRBBridge.isSelected()) {
            commands.add("interface bridge set bridge protocol-mode=none");
            commands.add("ip dhcp-client set interface=ether1 disabled=yes numbers=0");
            commands.add("ip dhcp-server set interface=bridge disabled=yes numbers=0");
            commands.add("interface bridge port add bridge=bridge interface=ether1");
            commands.add("ip firewall nat disable numbers=0");
            if (!jTFBrana.getText().trim().isEmpty()) {
                commands.add("ip dhcp-relay add interface=bridge dhcp-server=" + jTFBrana.getText().trim() + " disabled=no");
                commands.add("ip route add gateway=" + jTFBrana.getText().trim());
            }

            if (!jTFAdresa.getText().trim().isEmpty()) {
                String network = jTFAdresa.getText().substring(0, jTFAdresa.getText().lastIndexOf(".") + 1) + "0";
                commands.add("ip address add address=" + jTFAdresa.getText().trim() + "/24 network=" + network + " interface=bridge");
            }
        }

        if (!jTFNazevKlienta.getText().trim().isEmpty()) {
            commands.add("system identity set name=" + jTFNazevKlienta.getText().trim());
        }
        if (!jTFHeslo.getText().trim().isEmpty()) {
            commands.add("user set admin password=" + jTFHeslo.getText().trim());
        }

        //String klic = "";
        if (!jComboBoxKlic.getItems().isEmpty() && !jTFWep.getText().isEmpty()) {
            if (jComboBoxKlic.getSelectionModel().getSelectedItem().toString().equals("WPA2-AES")) {
                commands.add("interface wireless security-profiles set default mode=dynamic-keys authentication-types=wpa2-psk wpa2-pre-shared-key=" + jTFWep.getText() + " comment=" + jTFWep.getText());
            } else if (jComboBoxKlic.getSelectionModel().getSelectedItem().toString().equals("WEP-128bit")) {
                StringBuilder builder = new StringBuilder();
                for (char c : jTFWep.getText().toCharArray()) {
                    int i = (int) c;
                    builder.append(Integer.toHexString(i).toUpperCase());
                }
                commands.add("interface wireless security-profiles set default mode=static-keys-required static-transmit-key=key-0 static-algo-0=104bit-wep static-key-0=" + builder.toString() + " comment=" + builder.toString());
            }
        }

        String band = "";
        if (jRBModA.isSelected()) {
            band = "band=2ghz-b ";
        } else if (jRBModAN.isSelected()) {
            band = "band=2ghz-b/g ";
        } else if (jRBModN.isSelected()) {
            band = "band=2ghz-b/g/n ";
        }

        String ssid = "";
        if (!jTFSSID.getText().trim().isEmpty()) {
            ssid = "ssid=" + jTFSSID.getText().trim() + " ";
        }

        String frekvence = "";
        if (!jComboBoxKanal.getItems().isEmpty()) {
            frekvence = "frequency=" + jComboBoxKanal.getSelectionModel().getSelectedItem().toString();
        }

        String txPower = "";
        if (!jChoiceBVykon.getItems().isEmpty()) {
            txPower = "tx-power-mode=all-rates-fixed " + "tx-power=" + jChoiceBVykon.getValue() + " ";
        }
        String fullCommand = "interface wireless set wlan1 channel-width=20mhz country=debug station-roaming=disabled wmm-support=enabled ";
        //System.out.println(fullCommand + " " + band + ssid + distance + konfRate + txPower);
        commands.add(fullCommand + " " + ssid + txPower + frekvence);

        if (jRBBridge.isSelected()) {
            commands.add("ip address remove [find address=\"192.168.88.1/24\"]");
        }

    }

    @FXML
    private void modRouterKlik(ActionEvent e) {
        zmenitMod(0);
    }

    @FXML
    private void modBridgeKlik(ActionEvent e) {
        zmenitMod(1);
    }

    @FXML
    private void bandAKlik(ActionEvent e) {
        zmenitBand(0);
    }

    @FXML
    private void bandANKlik(ActionEvent e) {
        zmenitBand(1);
    }

    @FXML
    private void bandNKlik(ActionEvent e) {
        zmenitBand(2);
    }

    @FXML
    private void dhcpKlik(ActionEvent e) {
        if (jCBDHCP.isSelected()) {
            jTFAdresa.setEditable(false);
            jTFMaska.setEditable(false);
            jTFBrana.setEditable(false);
        } else {
            jTFAdresa.setEditable(true);
            jTFMaska.setEditable(true);
            jTFBrana.setEditable(true);
        }
    }

    @FXML
    private void zmenitBand(int id) {
        switch (id) {
            case 0:
                jRBModA.setSelected(true);
                jRBModAN.setSelected(false);
                jRBModN.setSelected(false);
                break;
            case 1:
                jRBModA.setSelected(false);
                jRBModAN.setSelected(true);
                jRBModN.setSelected(false);
                break;
            default:
                jRBModA.setSelected(false);
                jRBModAN.setSelected(false);
                jRBModN.setSelected(true);
                break;
        }
    }

    @FXML
    private void zmenitMod(int id) {
        switch (id) {
            case 0:
                jRBBridge.setSelected(false);
                jRBRouter.setSelected(true);
                break;
            case 1:
                jRBBridge.setSelected(true);
                jRBRouter.setSelected(false);
                break;
            default:
                jRBModA.setSelected(false);
                jRBModAN.setSelected(false);
                break;
        }
    }

    @FXML
    private void ostatniNastaveni(ActionEvent event) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("FXMLOptions.fxml"));
        Parent parent = fxmlLoader.load();
        FXMLOptionsController optionsController = fxmlLoader.<FXMLOptionsController>getController();

        Scene scene = new Scene(parent, 300, 250);
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.setTitle("Ostatní nastavení");
        stage.getIcons().add(new Image("/images/mikrotikICO.png"));
        stage.showAndWait();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        klientMac = "";
        settingInProgress = false;
        ObservableList<String> zarizeni = FXCollections.observableArrayList();
        zarizeni.add("DiscLite 5ac");
        zarizeni.add("LHG 5ac");
        zarizeni.add("SXTsq 5ac");
        zarizeni.add("hAP Lite");
        jComboBoxZarizeni.setItems(zarizeni);
        ObservableList<String> kanal24 = FXCollections.observableArrayList();
        kanal24.add("2412");
        kanal24.add("2417");
        kanal24.add("2422");
        kanal24.add("2427");
        kanal24.add("2432");
        kanal24.add("2437");
        kanal24.add("2442");
        kanal24.add("2447");
        kanal24.add("2452");
        kanal24.add("2457");
        kanal24.add("2462");
        kanal24.add("2467");
        kanal24.add("2472");
        jComboBoxKanal.setItems(kanal24);
        ObservableList<String> typKlic = FXCollections.observableArrayList();
        typKlic.add("WEP-128bit");
        typKlic.add("WPA2-AES");
        jComboBoxKlic.setItems(typKlic);
        jRBModA.setSelected(true);
        jRBRouter.setSelected(true);
        jCBDHCP.setSelected(true);
        jTFAdresa.setEditable(false);
        jTFMaska.setEditable(false);
        jTFBrana.setEditable(false);
        List<Integer> vykony = new ArrayList();
        for (int i = 0; i < 27; i = i + 2) {
            vykony.add(i);
        }
        ObservableList<Integer> chbVykony = FXCollections.observableList(vykony);
        jChoiceBVykon.setItems(chbVykony);
        jChoiceBVykon.setValue(20);

        Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    Image greenDot = new Image(getClass().getResourceAsStream("/images/greenDot.png"));
                    Image redDot = new Image(getClass().getResourceAsStream("/images/redDot.png"));
                    iVStatus.setImage(redDot);
                    while (true) {
                        if (!settingInProgress) {
                            InetAddress address = InetAddress.getByName("192.168.88.1");
                            boolean reachable = address.isReachable(4);
                            if (reachable) {
                                iVStatus.setImage(greenDot);
                                if (klientMac == null || klientMac.isEmpty()) {
                                    klientMac = getKlientMacAddress();
                                    jTFMacAdresa.setText(klientMac);
                                }
                            } else {
                                iVStatus.setImage(redDot);
                                klientMac = "";
                                jTFMacAdresa.setText(klientMac);
                            }
                        }
                        Thread.sleep(500);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        t.start();

    }

    private String getKlientMacAddress() throws Exception {
        String klientMacTemp = "";

        jsch = new JSch();
        session = jsch.getSession("admin", "192.168.88.1", 22);
        config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);
        session.connect();
        if (jComboBoxZarizeni.getSelectionModel().getSelectedItem() != null) {
            if (jComboBoxZarizeni.getSelectionModel().getSelectedItem().toString().equals("DiscLite 5ac")
                    || jComboBoxZarizeni.getSelectionModel().getSelectedItem().toString().equals("LHG 5ac")
                    || jComboBoxZarizeni.getSelectionModel().getSelectedItem().toString().equals("SXTsq 5ac")) {
                ChannelExec channel = (ChannelExec) session.openChannel("exec");
                channel.setCommand("interface wireless print advanced");
                BufferedReader in = new BufferedReader(new InputStreamReader(channel.getInputStream()));
                channel.connect();
                String msg = null;
                while ((msg = in.readLine()) != null) {
                    if (msg.contains("mac-address=")) {
                        klientMacTemp = msg.substring(msg.indexOf("mac-address=") + 12, msg.indexOf("mac-address=") + 29);
                        break;
                    }
                }
                Thread.sleep(1000);
                channel.disconnect();
            } else if (jComboBoxZarizeni.getSelectionModel().getSelectedItem().toString().equals("hAP Lite")) {
                ChannelExec channel = (ChannelExec) session.openChannel("exec");
                channel.setCommand("interface ethernet print");
                BufferedReader in = new BufferedReader(new InputStreamReader(channel.getInputStream()));
                channel.connect();
                String msg = null;
                while ((msg = in.readLine()) != null) {
                    if (msg.contains("ether1")) {
                        klientMacTemp = msg.substring(msg.indexOf("1500") + 5, msg.indexOf("1500") + 22);
                        break;
                    }
                }
                Thread.sleep(1000);
                channel.disconnect();
            }
        }
        session.disconnect();

        return klientMacTemp;
    }

    private List<String> nactiPrikazy() {
        try {
            List<String> commands = new ArrayList<>();
            File f = new File("nastaveni.ini");
            BufferedReader input = new BufferedReader(new FileReader(f));
            String line = null;
            ArrayList sirky = new ArrayList();
            while ((line = input.readLine()) != null) {
                commands.add(line);
            }
            input.close();
            return commands;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
