package br.com.cinemafx.controllers;

import br.com.cinemafx.dbcontrollers.DBObjects;
import br.com.cinemafx.models.*;
import br.com.cinemafx.views.dialogs.ModelDialog;
import br.com.cinemafx.views.dialogs.ModelException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class SalaCtrl implements Initializable, CadCtrlIntface {

    ObservableList<Sala> salaObservableList = FXCollections.observableArrayList();
    private Sala cachedSala = new Sala();

    @FXML
    private AnchorPane paneGrade, paneForm;
    @FXML
    private TableView<Sala> tbvSalas;
    @FXML
    private Button btnView, btnAtualizar, btnAdicionar, btnSalvar, btnCancelar,
            btnEditar, btnDuplicar, btnExcluir, btnPrimeiro, btnAnterior, btnProximo, btnUltimo;
    @FXML
    private Label lblMensagem;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        estrutura();
        appCalls();
        init();
    }


    @Override
    public void estrutura() {
        tbvSalas.getColumns().addAll(getTableColumns());
        tbvSalas.setItems(salaObservableList);
    }

    @Override
    public void appCalls() {
        paneForm.setVisible(false);
        paneGrade.setVisible(true);
        paneForm.visibleProperty().addListener((obs, isV, wasV) -> {
            paneGrade.setVisible(wasV);
            System.out.println("paneGrade is Visible: " + wasV);
            if (isV) {
                btnView.setGraphic(imgForm);
                btnView.getTooltip().setText("Modo Formulário");
            } else {
                btnView.setGraphic(imgGrade);
                btnView.getTooltip().setText("Modo Grade");
            }
        });
        tbvSalas.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tbvSalas.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> showInForm(newV));
        btnView.setOnAction(e -> ctrlAction(FrameAction.ChangeView));
        btnAtualizar.setOnAction(e -> ctrlAction(FrameAction.Atualizar));
        btnPrimeiro.setOnAction(e -> ctrlAction(FrameAction.Primeiro));
        btnAnterior.setOnAction(e -> ctrlAction(FrameAction.Anterior));
        btnProximo.setOnAction(e -> ctrlAction(FrameAction.Proximo));
        btnUltimo.setOnAction(e -> ctrlAction(FrameAction.Ultimo));
    }

    @Override
    public void init() {
        loadTableValues();
    }

    @Override
    public void loadTableValues() {
        try {
            salaObservableList.clear();
            salaObservableList.addAll(DBObjects.reloadSala().stream().filter(sala -> sala.getCodSala() != 0).collect(Collectors.toList()));
            sendMensagem(lblMensagem, "Tabela de Salas atualizada com sucesso!");
        } catch (Exception ex) {
            new ModelException(this.getClass(),
                    String.format("Erro ao tentar atualizar tabela de salas\n%s", ex.getMessage()), ex)
                    .getAlert().showAndWait();
            salaObservableList.clear();
        }
    }

    @Override
    public TableColumn[] getTableColumns() {
        //Constrained sized table
        TableColumn[] tableColumns = new TableColumn[3];
        tableColumns[0] = new ModelTableColumn<Sala, Integer>("#", "codSala", TableColumnType.Inteiro);
        tableColumns[1] = new ModelTableColumn<Sala, String>("Referência", "refSala", TableColumnType.Texto_Pequeno);
        tableColumns[2] = new ModelTableColumn<Sala, Integer>("Capacidade", "capacidade", TableColumnType.Inteiro);
        return tableColumns;
    }

    private void showInForm(Sala sala) {
        if (sala == null) return;
        ctrlLinhasTab(tbvSalas.getItems().indexOf(sala), true);
        setAtualizando(true);
        setCachedSala(sala);
        setAtualizando(false);
    }

    @Override
    public void ctrlAction(FrameAction frameAction) {
        switch (frameAction) {
            case ChangeView:
                paneForm.setVisible(!paneForm.isVisible());
                break;
            case Atualizar:
                loadTableValues();
                tbvSalas.getSelectionModel().select(tbvSalas.getItems().stream()
                        .filter(sala -> sala.getCodSala() == getCachedSala().getCodSala())
                        .findFirst().get()); //Keep old Selection, não fazer try catch pois causa NuSuchFileException
                break;
            case Adicionar:
                break;
            case Salvar:
                break;
            case Cancelar:
                break;
            case Editar:
                break;
            case Duplicar:
                break;
            case Excluir:
                break;
            case Primeiro:
                ctrlLinhasTab(0, false);
                break;
            case Anterior:
                int selectedIndexA = tbvSalas.getSelectionModel().getSelectedIndex();
                if (selectedIndexA == -1) ctrlLinhasTab(0, false);
                else ctrlLinhasTab(selectedIndexA - 1, false);
                break;
            case Proximo:
                int selectedIndexB = tbvSalas.getSelectionModel().getSelectedIndex();
                if (selectedIndexB == -1) ctrlLinhasTab(0, false);
                else ctrlLinhasTab(selectedIndexB + 1, false);
                break;
            case Ultimo:
                ctrlLinhasTab(tbvSalas.getItems().size() - 1, false);
                break;
        }
    }

    private void ctrlLinhasTab(int newIndex, boolean alreadySelected) {
        btnPrimeiro.setDisable(true);
        btnAnterior.setDisable(true);
        btnProximo.setDisable(true);
        btnUltimo.setDisable(true);
        if (tbvSalas.getItems().size() == 1) return;
        if (!alreadySelected) tbvSalas.getSelectionModel().clearAndSelect(newIndex);
        if (newIndex == 0) {
            btnProximo.setDisable(false);
            btnUltimo.setDisable(false);
        } else if (newIndex < tbvSalas.getItems().size() - 1) {
            btnPrimeiro.setDisable(false);
            btnAnterior.setDisable(false);
            btnProximo.setDisable(false);
            btnUltimo.setDisable(false);
        } else if (newIndex == tbvSalas.getItems().size() - 1) {
            btnPrimeiro.setDisable(false);
            btnAnterior.setDisable(false);
        }
    }

    public Sala getCachedSala() {
        return cachedSala;
    }

    public void setCachedSala(Sala cachedSala) {
        this.cachedSala = cachedSala;
    }
}
