package br.com.cinemafx.controllers;

import br.com.cinemafx.dbcontrollers.DBObjects;
import br.com.cinemafx.methods.Functions;
import br.com.cinemafx.models.*;
import br.com.cinemafx.views.dialogs.FormattedDialog;
import br.com.cinemafx.views.dialogs.ModelException;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.util.Callback;

import java.net.URL;
import java.sql.Timestamp;
import java.util.NoSuchElementException;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class SessaoCtrl implements Initializable, CadCtrlIntface {

    private ObservableList<Sessao> sessaoObservableList = FXCollections.observableArrayList();
    private Sessao cachedSessao = new Sessao();

    @FXML
    private AnchorPane paneForm, paneGrade;
    @FXML
    private TableView<Sessao> tbvSessoes;
    @FXML
    private TextField txfCodFilme, txfNomeFilme;
    @FXML
    private Button btnView, btnAtualizar, btnAdicionar, btnSalvar, btnCancelar,
            btnEditar, btnDuplicar, btnExcluir, btnPrimeiro, btnAnterior, btnProximo, btnUltimo;
    @FXML
    private Label lblMensagem;
    @FXML
    private TextArea txaSinopse;
    @FXML
    private Text txtGenero, txtDuracao, txtHoraFim;
    @FXML
    private ImageView imgFilme;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        estrutura();
        appCalls();
        init();
    }

    @Override
    public void estrutura() {
        tbvSessoes.getColumns().addAll(getTableColumns());
        tbvSessoes.setItems(sessaoObservableList);
    }

    @Override
    public void appCalls() {
        paneForm.setVisible(false);
        paneGrade.setVisible(true);
        paneForm.visibleProperty().addListener((obs, oldV, newV) -> {
            paneGrade.setVisible(oldV);
            if (newV) {
                btnView.setGraphic(imgForm);
                btnView.getTooltip().setText("Modo Formulário");
            } else {
                btnView.setGraphic(imgGrade);
                btnView.getTooltip().setText("Modo Grade");
            }
        });
        tbvSessoes.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tbvSessoes.getSelectionModel().selectedItemProperty().addListener((obs, oldItem, newItem) -> showInForm(newItem));
        tbvSessoes.setOnMouseClicked(e -> {
            if (e.getClickCount() > 1) paneForm.setVisible(true);
        });
        btnView.setOnAction(e -> ctrlAction(FrameAction.ChangeView));
        btnAtualizar.setOnAction(e -> ctrlAction(FrameAction.Atualizar));
        btnAdicionar.setOnAction(e -> ctrlAction(FrameAction.Adicionar));
        btnSalvar.setOnAction(e -> ctrlAction(FrameAction.Salvar));
        btnCancelar.setOnAction(e -> ctrlAction(FrameAction.Cancelar));
        btnEditar.setOnAction(e -> ctrlAction(FrameAction.Editar));
        btnDuplicar.setOnAction(e -> ctrlAction(FrameAction.Duplicar));
        btnExcluir.setOnAction(e -> ctrlAction(FrameAction.Excluir));
        btnPrimeiro.setOnAction(e -> ctrlAction(FrameAction.Primeiro));
        btnAnterior.setOnAction(e -> ctrlAction(FrameAction.Anterior));
        btnProximo.setOnAction(e -> ctrlAction(FrameAction.Proximo));
        btnUltimo.setOnAction(e -> ctrlAction(FrameAction.Ultimo));
    }

    @Override
    public void init() {
        loadTableValues();
        tbvSessoes.getSelectionModel().clearAndSelect(0);
    }

    @Override
    public void loadTableValues() {
        try {
            sessaoObservableList.clear();
            sessaoObservableList.addAll(DBObjects.reloadSessoes().stream()
                    .filter(sessao -> sessao.getCodSessao() != 0).collect(Collectors.toList()));
            sendMensagem(lblMensagem, true, "Tabela de Sessões atualizada com sucesso!");
        } catch (Exception ex) {
            new ModelException(this.getClass(),
                    String.format("Erro ao tentar atualizar tabela de sessões\n%s", ex.getMessage()), ex)
                    .getAlert().showAndWait();
            sessaoObservableList.clear();
        }
    }

    @Override
    public TableColumn[] getTableColumns() {
        /*CODSESSAO, DATAINI, DATAFIM, SALA (CODSALA, REFSALA, CAPACIDADE), FILME(CODFILME, NOMEFILME, MINFILME, NOMEGENERO), EXIB(INTEIRA, MEIA)*/
        TableColumn[] tableColumns = new TableColumn[6];
        tableColumns[0] = new ModelTableColumn<Sessao, Integer>("#", "codSessao", TableColumnType.Inteiro).setPercentSize(3.5);
        tableColumns[1] = new ModelTableColumn<Sessao, Timestamp>("Data/Hora Exib.", "dataHoraExib", TableColumnType.Data_Hora).setPercentSize(10.0);
        tableColumns[2] = new ModelTableColumn<Sessao, Timestamp>("Data/Hora Fim", "dataHoraFim", TableColumnType.Data_Hora).setPercentSize(10.0);

        /*---------------------------> Coluna de Preços*/
        tableColumns[3] = new ModelTableColumn<Sessao, Double>("Preços", null, null);
        TableColumn[] tbColunasPrecos = new TableColumn[2];
        tbColunasPrecos[0] = new ModelTableColumn<Sessao, Integer>("Inteira", null, TableColumnType.Dinheiro);
        tbColunasPrecos[0].setCellValueFactory((Callback<TableColumn.CellDataFeatures<Sessao, Double>, ObservableValue<Double>>)
                p -> new ReadOnlyObjectWrapper(p.getValue().getExibicao().getVlrExibicao()));
        tbColunasPrecos[1] = new ModelTableColumn<Sessao, Integer>("Meia", null, TableColumnType.Dinheiro);
        tbColunasPrecos[1].setCellValueFactory((Callback<TableColumn.CellDataFeatures<Sessao, Double>, ObservableValue<Double>>)
                p -> new ReadOnlyObjectWrapper(p.getValue().getExibicao().getVlrExibicao() / 2));
        tableColumns[3].getColumns().addAll(tbColunasPrecos);
        /*Fim da Coluna de Preços <--------------------------*/

        /*---------------------------> Coluna de Salas*/
        tableColumns[4] = new ModelTableColumn<Sessao, Object>("Sala", null, null);
        TableColumn[] tbColunasSala = new TableColumn[3];
        tbColunasSala[0] = new ModelTableColumn<Sessao, Integer>("#", null, TableColumnType.Inteiro);
        tbColunasSala[0].setCellValueFactory((Callback<TableColumn.CellDataFeatures<Sessao, Integer>, ObservableValue<Integer>>)
                p -> new ReadOnlyObjectWrapper(p.getValue().getSala().getCodSala()));
        tbColunasSala[1] = new ModelTableColumn<Sessao, Integer>("Referência", null, TableColumnType.Texto_Pequeno);
        tbColunasSala[1].setCellValueFactory((Callback<TableColumn.CellDataFeatures<Sessao, String>, ObservableValue<String>>)
                p -> new ReadOnlyObjectWrapper(p.getValue().getSala().getRefSala()));
        tbColunasSala[2] = new ModelTableColumn<Sessao, Integer>("Capacidade", null, TableColumnType.Texto_Pequeno);
        tbColunasSala[2].setCellValueFactory((Callback<TableColumn.CellDataFeatures<Sessao, String>, ObservableValue<Integer>>)
                p -> new ReadOnlyObjectWrapper(p.getValue().getSala().getCapacidade()));
        tableColumns[4].getColumns().addAll(tbColunasSala);
        /*Fim da Coluna de Salas <--------------------------*/

        /*---------------------------> Coluna de Filmes*/
        tableColumns[5] = new ModelTableColumn<Sessao, Object>("Filme", null, null);
        TableColumn[] tbColunasFilme = new TableColumn[4];
        tbColunasFilme[0] = new ModelTableColumn<Sessao, Integer>("#", null, TableColumnType.Inteiro).setPercentSize(3.5);
        tbColunasFilme[0].setCellValueFactory((Callback<TableColumn.CellDataFeatures<Sessao, Integer>, ObservableValue<Integer>>)
                p -> new ReadOnlyObjectWrapper(p.getValue().getFilme().getCodFilme()));
        tbColunasFilme[1] = new ModelTableColumn<Sessao, Integer>("Nome", null, TableColumnType.Texto_Pequeno).setPercentSize(17.0);
        tbColunasFilme[1].setCellValueFactory((Callback<TableColumn.CellDataFeatures<Sessao, String>, ObservableValue<String>>)
                p -> new ReadOnlyObjectWrapper(p.getValue().getFilme().getNomeFilme()));
        tbColunasFilme[2] = new ModelTableColumn<Sessao, Integer>("Gênero", null, TableColumnType.Texto_Pequeno).setPercentSize(7.5);
        tbColunasFilme[2].setCellValueFactory((Callback<TableColumn.CellDataFeatures<Sessao, String>, ObservableValue<Integer>>)
                p -> new ReadOnlyObjectWrapper(p.getValue().getFilme().getGenero().getNomeGenero()));
        tbColunasFilme[3] = new ModelTableColumn<Sessao, Integer>("Duração(Min.)", null, TableColumnType.Inteiro).setPercentSize(5.0);
        tbColunasFilme[3].setCellValueFactory((Callback<TableColumn.CellDataFeatures<Sessao, String>, ObservableValue<Integer>>)
                p -> new ReadOnlyObjectWrapper(p.getValue().getFilme().getMinFilme()));
        tableColumns[5].getColumns().addAll(tbColunasFilme);
        /*Fim da Coluna de Filmes <--------------------------*/

        return tableColumns;
    }

    private void showInForm(Sessao sessao) {
        if (getFrameStatus() != FrameStatus.Status.Visualizando) {
            int choice = FormattedDialog.getYesNoDialog(this.getClass(),
                    "Foram detectadas alterações não salvas\nDeseja salvar estas alterações antes de sair do registro?",
                    new String[]{"Salvar", "Cancelar"});
            if (choice == 0)
                btnSalvar.fire();
            if (getFrameStatus() != FrameStatus.Status.Visualizando) //Deu erro na tentativa de salvar
                return;
        }
        if (sessao == null) return;
        setAtualizando(true);
        setCachedSessao(sessao);
        txfCodFilme.setText(String.valueOf(sessao.getFilme().getCodFilme()));
        txfNomeFilme.setText(sessao.getFilme().getNomeFilme());
        imgFilme.setImage(sessao.getFilme().getCartazFilme());
        txtGenero.setText(sessao.getFilme().getGenero().getNomeGenero());
        txtDuracao.setText(sessao.getFilme().getMinFilme() + "min.");
        txtHoraFim.setText(Functions.getDataFormatted(Functions.dataHoraFormater, sessao.getDataHoraFim()));
        txaSinopse.setText(sessao.getFilme().getSinopse());
        setAtualizando(false);
        ctrlLinhasTab(tbvSessoes.getItems().indexOf(sessao), true);
    }

    @Override
    public void ctrlAction(FrameAction frameAction) {
        switch (frameAction) {
            case ChangeView:
                paneForm.setVisible(!paneForm.isVisible());
                break;
            case Atualizar:
                setFrameStatus(FrameStatus.Status.Visualizando);
                disableButtons(false);
                loadTableValues();
                try {
                    tbvSessoes.getSelectionModel().select(tbvSessoes.getItems().stream()
                            .filter(sessao -> sessao.getCodSessao() == getCachedSessao().getCodSessao())
                            .findFirst().get());
                } catch (NoSuchElementException ex) {
                    sendMensagem(lblMensagem, false, "Registro pré-selecionado não existe mais");
                    tbvSessoes.getSelectionModel().clearAndSelect(0);
                }
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
                int selectedIndexA = tbvSessoes.getSelectionModel().getSelectedIndex();
                if (selectedIndexA == -1) ctrlLinhasTab(0, false);
                else ctrlLinhasTab(selectedIndexA - 1, false);
                break;
            case Proximo:
                int selectedIndexB = tbvSessoes.getSelectionModel().getSelectedIndex();
                if (selectedIndexB == -1) ctrlLinhasTab(0, false);
                else ctrlLinhasTab(selectedIndexB + 1, false);
                break;
            case Ultimo:
                ctrlLinhasTab(tbvSessoes.getItems().size() - 1, false);
                break;
        }
    }

    private void disableButtons(Boolean disable) {
        if (disable) {
            btnAtualizar.setDisable(true);
            btnAdicionar.setDisable(true);
            btnSalvar.setDisable(false);
            btnCancelar.setDisable(false);
            btnEditar.setDisable(true);
            btnDuplicar.setDisable(true);
            btnExcluir.setDisable(true);
        } else {
            btnAtualizar.setDisable(false);
            btnAdicionar.setDisable(false);
            btnSalvar.setDisable(true);
            btnCancelar.setDisable(true);
            btnEditar.setDisable(false);
            btnDuplicar.setDisable(false);
            btnExcluir.setDisable(false);
        }
    }

    private void ctrlLinhasTab(int newIndex, boolean alreadySelected) {
        btnPrimeiro.setDisable(true);
        btnAnterior.setDisable(true);
        btnProximo.setDisable(true);
        btnUltimo.setDisable(true);
        if (tbvSessoes.getItems().size() == 1) return;
        if (!alreadySelected) tbvSessoes.getSelectionModel().clearAndSelect(newIndex);
        if (newIndex == 0) {
            btnProximo.setDisable(false);
            btnUltimo.setDisable(false);
        } else if (newIndex < tbvSessoes.getItems().size() - 1) {
            btnPrimeiro.setDisable(false);
            btnAnterior.setDisable(false);
            btnProximo.setDisable(false);
            btnUltimo.setDisable(false);
        } else if (newIndex == tbvSessoes.getItems().size() - 1) {
            btnPrimeiro.setDisable(false);
            btnAnterior.setDisable(false);
        }
    }

    public Sessao getCachedSessao() {
        return cachedSessao;
    }

    public void setCachedSessao(Sessao cachedSessao) {
        this.cachedSessao = cachedSessao;
    }
}
