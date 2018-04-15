package br.com.cinemafx.controllers;

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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static com.sun.javafx.event.EventUtil.fireEvent;

public class PrincipalCtrl implements Initializable {

    @FXML
    private TabPane mainTabPane;
    @FXML
    private JFXHamburger hbgPanel;
    @FXML
    private VBox boxOptions;
    @FXML
    private Button btnAttSenha, btnDeslogar, btnSair;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        paneEstrutura();
        hbgEstrutura();
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

}
