package br.com.cinemafx.controllers;

import br.com.cinemafx.dbcontrollers.DBFunctions;
import br.com.cinemafx.methods.AppInfo;
import br.com.cinemafx.methods.CreateLogFile;
import br.com.cinemafx.methods.log.GravaLog;
import br.com.cinemafx.models.ParametroType;
import br.com.cinemafx.views.dialogs.FormattedDialog;
import br.com.cinemafx.views.dialogs.ModelException;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.transitions.hamburger.HamburgerBasicCloseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TabPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ResourceBundle;

public class PrincipalCtrl implements Initializable {

    private final String versaoExec = "1.0.0.1";

    @FXML
    private TabPane mainTabPane;
    @FXML
    private JFXHamburger hbgPanel;
    @FXML
    private VBox boxOptions;
    @FXML
    private Button btnLog, btnAutoria, btnAttSenha, btnDeslogar, btnSair;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        paneEstrutura();
        hbgEstrutura();
        Platform.runLater(() -> feedAppProperties());
    }

    private void paneEstrutura() {
        String telaLoad = null;
        try {
            telaLoad = "Sessões";
            Parent rootSessoes = FXMLLoader.load(getClass().getResource("/br/com/cinemafx/views/fxml/Sessao.fxml"));
            mainTabPane.getTabs().get(1).setContent(rootSessoes);
            telaLoad = "Filmes";
            Parent rootFilmes = FXMLLoader.load(getClass().getResource("/br/com/cinemafx/views/fxml/Filme.fxml"));
            mainTabPane.getTabs().get(2).setContent(rootFilmes);
            telaLoad = "Exibições";
            Parent rootExibicoes = FXMLLoader.load(getClass().getResource("/br/com/cinemafx/views/fxml/Exibicao.fxml"));
            mainTabPane.getTabs().get(3).setContent(rootExibicoes);
            telaLoad = "Salas";
            Parent rootSalas = FXMLLoader.load(getClass().getResource("/br/com/cinemafx/views/fxml/Sala.fxml"));
            mainTabPane.getTabs().get(4).setContent(rootSalas);
        } catch (IOException ex) {
            new ModelException(this.getClass(),
                    String.format("Erro ao tentar abrir tela de menu: %s\n%s\nA aplicação será finalizada", telaLoad, ex.getMessage()), ex).
                    getAlert().showAndWait();
            System.exit(0);
        }
    }

    private void hbgEstrutura() {
        HamburgerBasicCloseTransition basicTransition = new HamburgerBasicCloseTransition(hbgPanel);
        basicTransition.rateProperty().addListener((obs, oldV, newV) -> {
            boxOptions.setVisible((newV.intValue() > 0));
        });
        basicTransition.setRate(-1);
        hbgPanel.setOnMouseClicked(e -> {
            basicTransition.setRate(basicTransition.getRate() * -1);
            basicTransition.play();
        });
        Platform.runLater(() -> {
            Stage stage = (Stage) btnDeslogar.getScene().getWindow();
            btnAutoria.setOnAction(e -> {
                basicTransition.setRate(basicTransition.getRate() * -1);
                basicTransition.play();
                AutoriaDlg.show();
            });
            btnLog.setOnAction(e -> {
                basicTransition.setRate(basicTransition.getRate() * -1);
                basicTransition.play();
                GravaLog.gravaInfo(this.getClass(), "Efetuando download do log");
                CreateLogFile createLogFile = new CreateLogFile();
                createLogFile.create(btnLog);
            });
            btnAttSenha.setOnAction(e -> {
                basicTransition.setRate(basicTransition.getRate() * -1);
                basicTransition.play();
                AltSenhaDlg altSenhaDlg = new AltSenhaDlg();
                altSenhaDlg.show();
            });
            btnDeslogar.setOnAction(e -> {
                basicTransition.setRate(basicTransition.getRate() * -1);
                basicTransition.play();
                int resp = FormattedDialog.getYesNoDialog(this.getClass(),
                        "Deseja realmente deslogar do sistema?", new String[]{"Deslogar", "Cancelar"});
                if (resp == 0) {
                    Main main = new Main();
                    main.start(new Stage());
                    stage.close();
                }
            });
            btnSair.setOnAction(e -> {
                basicTransition.setRate(basicTransition.getRate() * -1);
                basicTransition.play();
                stage.fireEvent(new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST));
            });
        });
    }

    private void feedAppProperties() {
        Platform.runLater(() -> {
            try {
                AppInfo.getInfo().setVersaoBD(String.valueOf(DBFunctions.getUserParametro(this.getClass(),
                        "VERSAOATUALDB", ParametroType.Texto, 0)));
                AppInfo.getInfo().setVersaoExec(versaoExec);
                AppInfo.getInfo().setNomeMaquina(InetAddress.getLocalHost().getHostName());
                AppInfo.getInfo().setIPMaquina(InetAddress.getLocalHost().getHostAddress());
                AppInfo.getInfo().setDhLogin(Timestamp.from(Instant.now()));
            } catch (UnknownHostException ex) {
                new ModelException(this.getClass(),
                        "Erro ao tentar capturar informações da máquina", ex).getAlert().showAndWait();
            }
        });
    }

}
