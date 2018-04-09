package br.com.cinemafx.controllers;

import br.com.cinemafx.views.dialogs.ModelException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.TabPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class PrincipalCtrl implements Initializable {

    @FXML
    private TabPane mainTabPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        estrutura();
    }

    private void estrutura() {
        /*
         * 0 - Ingressos
         * 1 - Sessões
         * 2 - Filmes
         * 3 - Exibições
         * 4 - Salas
         * */
        String telaLoad = null;
        try {
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
}
