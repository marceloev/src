package br.com.cinemafx.methods;

import br.com.cinemafx.dbcontrollers.Conexao;
import br.com.cinemafx.views.dialogs.ModelDialog;
import br.com.cinemafx.views.dialogs.ModelException;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;

import java.util.ArrayList;
import java.util.Arrays;

public class SearchFieldTable {

    private Stage stage = new Stage();
    private Conexao conex = new Conexao(this.getClass());
    private TableView<String[]> TbViewRetorno = new TableView<String[]>();
    private ArrayList<String> KeyReturn;

    public SearchFieldTable(ImageView searchImage, String descrField, String[] colunas, String query) {
        createTableSearch(searchImage, descrField);
        reloadTableValue(descrField, colunas, query);
        createColumns(colunas);
    }

    private void createTableSearch(ImageView searchImage, String descrField) {
        Platform.runLater(() -> {
            StackPane newRoot = new StackPane();
            Scene newScene = new Scene(newRoot, 600, 500);
            getStage().setScene(newScene);
            getStage().setTitle("Pesquisa: " + descrField);
            getStage().getIcons().add(new Image("/br/com/cinemafx/views/images/Icone_Pesquisa.png"));
            getStage().initModality(Modality.WINDOW_MODAL);
            getStage().initOwner(searchImage.getScene().getWindow());
            Button[] BtnFrame = new Button[2];
            BtnFrame[0] = new Button("Escolher");
            BtnFrame[0].setOnAction(e -> {
                if (TbViewRetorno.getSelectionModel().getSelectedItem() == null) {
                    new ModelDialog(this.getClass(), Alert.AlertType.WARNING,
                            "Selecione uma linha para utilização").getAlert().showAndWait();
                } else {
                    String[] objSelected = TbViewRetorno.getSelectionModel().getSelectedItem();
                    setKeyReturn(objSelected);
                    getStage().fireEvent(new WindowEvent(getStage(), WindowEvent.WINDOW_CLOSE_REQUEST));
                }
            });
            BtnFrame[1] = new Button("Sair");
            BtnFrame[1].setOnAction(event -> getStage().close());
            HBox hBox = new HBox(BtnFrame);
            hBox.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
            hBox.setPadding(new Insets(5, 10, 5, 10));
            hBox.setSpacing(7);
            hBox.setStyle("-fx-border-color: GhostWhite; -fx-background-color: GhostWhite;");
            VBox vBox = new VBox(TbViewRetorno, hBox);
            TbViewRetorno.setTableMenuButtonVisible(true);
            TbViewRetorno.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            TbViewRetorno.prefWidthProperty().bind(newScene.widthProperty().subtract(20));
            TbViewRetorno.prefHeightProperty().bind(newScene.heightProperty().subtract(20));
            newRoot.getChildren().add(vBox);
            getStage().getScene().setOnKeyReleased(key -> {
                if (key.getCode() == KeyCode.ESCAPE)
                    getStage().fireEvent(new WindowEvent(getStage(), WindowEvent.WINDOW_CLOSE_REQUEST));
            });
        });
        Platform.runLater(() -> {
            searchImage.setOnMouseClicked(e -> showFrameSearch());
            TbViewRetorno.setOnMouseClicked(e -> {
                if (e.getClickCount() > 1) {
                    if (TbViewRetorno.getItems().get(TbViewRetorno.getSelectionModel().getSelectedIndex()) != null) {
                        String[] objSelected = TbViewRetorno.getSelectionModel().getSelectedItem();
                        setKeyReturn(objSelected);
                        getStage().fireEvent(new WindowEvent(getStage(), WindowEvent.WINDOW_CLOSE_REQUEST));
                    }
                }
            });
        });
    }

    public void addImageToSearch(ImageView searchImage) {
        searchImage.setOnMouseClicked(e -> showFrameSearch());
    }

    private void showFrameSearch() {
        setKeyReturn(null);
        getStage().show();
    }

    private void createColumns(String[] strColunas) {
        ArrayList<String> colunas = new ArrayList<>(Arrays.asList(strColunas));
        TableColumn[] tableColumns = new TableColumn[colunas.size()];
        int index = 0;
        for(String coluna : colunas) {
            int finalIndex = index;
            tableColumns[index] = new TableColumn<>(coluna);
            tableColumns[index].setCellValueFactory((Callback<TableColumn.CellDataFeatures<String[], String>, ObservableValue<String>>)
                    p -> new SimpleStringProperty((p.getValue()[finalIndex])));
            tableColumns[finalIndex].setStyle("-fx-alignment: CENTER; -fx-border-color: #F8F8FF;");
            index++;
        }
        TbViewRetorno.getColumns().addAll(tableColumns);
    }

    public void reloadTableValue(String descrField, String[] strColunas, String query) {
        setKeyReturn(null);
        conex.remakeConexao();
        ArrayList<String> colunas = new ArrayList<>(Arrays.asList(strColunas));
        try {
            if (colunas == null || colunas.isEmpty()) {
                new ModelException(this.getClass(), String.format("Estrutura de colunas não programada para pesquisa de: %s\n" +
                        "Não será possível exibir os dados, favor revise", descrField)).getAlert().showAndWait();
                return;
            }
            conex.createStatement(query);
            conex.createSet();
            int countRow = conex.countRows();
            int countCol = conex.rs.getMetaData().getColumnCount();
            String[][] DadosTabela = new String[countRow][countCol];
            for (int rowAtual = 0; rowAtual < countRow; rowAtual++) {
                conex.rs.next();
                ArrayList<String> ArrayColuna = new ArrayList<>();
                for (int colAtual = 1; colAtual <= countCol; colAtual++) {
                    ArrayColuna.add(conex.rs.getString(colAtual));
                }
                String[] strArray = new String[ArrayColuna.size()];
                strArray = ArrayColuna.toArray(strArray);
                DadosTabela[rowAtual] = ArrayColuna.toArray(strArray);
            }
            ObservableList<String[]> Dados = FXCollections.observableArrayList();
            Dados.addAll(Arrays.asList(DadosTabela));
            TbViewRetorno.setItems(Dados);
        } catch (Exception ex) {
            new ModelException(this.getClass(), String.format("Erro ao tentar criar tabela de pesquisa\n%s", ex.getMessage()))
                    .getAlert().showAndWait();
            TbViewRetorno.setItems(null);
        } finally {
            conex.desconecta();
        }
    }

    public void setKeyReturn(String[] keyReturn) {
        if (keyReturn == null) KeyReturn = null;
        else KeyReturn = new ArrayList<>(Arrays.asList(keyReturn));
    }

    public ArrayList<String> getKeyReturn() {
        return KeyReturn;
    }


    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}