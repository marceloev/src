package br.com.cinemafx.controllers;

import br.com.cinemafx.dbcontrollers.Conexao;
import br.com.cinemafx.dbcontrollers.DBObjects;
import br.com.cinemafx.methods.MaskField;
import br.com.cinemafx.methods.SearchFieldTable;
import br.com.cinemafx.models.*;
import br.com.cinemafx.views.dialogs.FormattedDialog;
import br.com.cinemafx.views.dialogs.ModelDialog;
import br.com.cinemafx.views.dialogs.ModelException;
import com.jfoenix.controls.JFXTimePicker;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.image.ImageViewBuilder;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;

import java.net.URL;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ResourceBundle;

import static br.com.cinemafx.methods.Functions.Nvl;

public class SessaoCtrl implements Initializable, CadCtrlIntface {

    private ImageView imgView = ImageViewBuilder.create().image(imgGrade).fitHeight(31).fitWidth(35).build();
    private ObservableList<Sessao> sessaoObservableList = FXCollections.observableArrayList();
    private Sessao cachedSessao;
    private Conexao conex = new Conexao(this.getClass());
    /*Adicionar no FXML Filme-Cod, Nome e Img*/
    @FXML
    private AnchorPane paneGrade, paneForm;
    @FXML
    private TableView<Sessao> tbvSessoes, tbvLoteSessoes;
    @FXML
    private Button btnView, btnAtualizar, btnAdicionar, btnSalvar, btnCancelar,
            btnEditar, btnDuplicar, btnExcluir, btnPrimeiro, btnAnterior, btnProximo, btnUltimo;
    @FXML
    private Label lblMensagem;
    @FXML
    private TextField txfCodSalaGrade, txfNomeSalaGrade, txfCodSala, txfNomeSala,
            txfCodExib, txfNomeExib, txfCodFilme, txfNomeFilme;
    @FXML
    private ImageView imgBuscaSalaGrade, imgBuscaSala, imgBuscaExib, imgBuscaFilme;
    @FXML
    private DatePicker dtpDataIniGrade, dtpDataFimGrade, dtpDataSes;
    @FXML
    private JFXTimePicker tmpHoraSes;

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
        paneGrade.setVisible(true);
        paneForm.setVisible(false);
        dtpDataIniGrade.setValue(LocalDate.now());
        dtpDataFimGrade.setValue(LocalDate.now().plusDays(1));
        tmpHoraSes.setIs24HourView(true);
        paneForm.visibleProperty().addListener((obs, oldV, newV) -> {
            paneGrade.setVisible(oldV);
            if (newV) {
                imgView.setImage(imgForm);
                btnView.getTooltip().setText("Modo Formulário");
            } else {
                imgView.setImage(imgGrade);
                btnView.getTooltip().setText("Modo Grade");
            }
        });
        tbvSessoes.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tbvSessoes.getSelectionModel().selectedItemProperty().addListener((obs, oldItem, newItem) -> showInForm(newItem));
        tbvSessoes.setOnMouseClicked(e -> {
            if (e.getClickCount() > 1) paneForm.setVisible(true);
        });
        btnView.setGraphic(imgView);
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
        MaskField.NumberField(txfCodSalaGrade, 11);
        propFrameStatus.addListener((obs, oldV, newV) -> {
            if (newV.intValue() == 1) btnEditar.fire(); //Alterando
        });
        txfCodSalaGrade.focusedProperty().addListener((obs, oldV, newV) -> {
            if (oldV)
                loadTableValues();
            if (oldV && Nvl(txfCodSalaGrade.getText()).isEmpty())
                txfNomeSalaGrade.clear();
            else if (oldV && !Nvl(txfCodSalaGrade.getText()).isEmpty()) {
                if (DBObjects.salaContains(Integer.valueOf(txfCodSalaGrade.getText())))
                    txfNomeSalaGrade.setText(DBObjects.getSalaByCod(this.getClass(),
                            Integer.valueOf(txfCodSalaGrade.getText())).getRefSala());
                else {
                    new ModelDialog(this.getClass(), Alert.AlertType.WARNING,
                            String.format("Sala não encontrada para código %s", txfCodSalaGrade.getText())).getAlert().showAndWait();
                    txfCodSalaGrade.clear();
                    txfNomeSalaGrade.clear();
                }
            }
        });
        SearchFieldTable searchSalaGrade = new SearchFieldTable(imgBuscaSalaGrade, "Salas",
                new String[]{"Código", "Referência", "Capacidade"}, "SELECT CODSALA, REFSALA, CAPACIDADE FROM TSALAS ORDER BY 1");
        searchSalaGrade.getStage().setOnCloseRequest(e -> {
            if (searchSalaGrade.getKeyReturn() != null) {
                txfCodSalaGrade.setText(searchSalaGrade.getKeyReturn().get(0));
                txfNomeSalaGrade.setText(searchSalaGrade.getKeyReturn().get(1));
                loadTableValues();
            }
        });
        dtpDataIniGrade.valueProperty().addListener((obs, oldV, newV) -> {
            if (newV == null) {
                dtpDataIniGrade.setValue(oldV);
                new ModelDialog(this.getClass(), Alert.AlertType.WARNING,
                        String.format("É obrigatório informar data inicial para filtro")).getAlert().showAndWait();
            } else if (newV.isAfter(dtpDataFimGrade.getValue())) {
                dtpDataIniGrade.setValue(oldV);
                new ModelDialog(this.getClass(), Alert.AlertType.WARNING,
                        String.format("Data inicial não pode ser posterior a data final")).getAlert().showAndWait();
            } else
                loadTableValues();
        });
        dtpDataFimGrade.valueProperty().addListener((obs, oldV, newV) -> {
            if (newV == null) {
                dtpDataFimGrade.setValue(oldV);
                new ModelDialog(this.getClass(), Alert.AlertType.WARNING,
                        String.format("É obrigatório informar data final para filtro")).getAlert().showAndWait();
            } else if (newV.isBefore(dtpDataIniGrade.getValue())) {
                dtpDataFimGrade.setValue(oldV);
                new ModelDialog(this.getClass(), Alert.AlertType.WARNING,
                        String.format("Data final não pode ser anterior a data inicial")).getAlert().showAndWait();
            } else
                loadTableValues();
        });
        txfCodSala.focusedProperty().addListener((obs, oldV, newV) -> {
            if (oldV)
                loadTableValues();
            if (oldV && Nvl(txfCodSala.getText()).isEmpty())
                txfNomeSala.clear();
            else if (oldV && !Nvl(txfCodSala.getText()).isEmpty()) {
                if (DBObjects.salaContains(Integer.valueOf(txfCodSala.getText())))
                    txfNomeSala.setText(DBObjects.getSalaByCod(this.getClass(),
                            Integer.valueOf(txfCodSala.getText())).getRefSala());
                else {
                    new ModelDialog(this.getClass(), Alert.AlertType.WARNING,
                            String.format("Sala não encontrada para código %s", txfCodSala.getText())).getAlert().showAndWait();
                    txfCodSala.clear();
                    txfNomeSala.clear();
                }
            }
        });
        SearchFieldTable searchSala = new SearchFieldTable(imgBuscaSalaGrade, "Salas",
                new String[]{"Código", "Referência", "Capacidade"}, "SELECT CODSALA, REFSALA, CAPACIDADE FROM TSALAS ORDER BY 1");
        searchSala.getStage().setOnCloseRequest(e -> {
            if (searchSala.getKeyReturn() != null) {
                txfCodSala.setText(searchSala.getKeyReturn().get(0));
                txfNomeFilme.setText(searchSala.getKeyReturn().get(1));
                loadTableValues();
            }
        });
        /*txfCodExib.focusedProperty().addListener((obs, oldV, newV) -> {
            if (oldV)
                loadTableValues();
            if (oldV && Nvl(txfCodExib.getText()).isEmpty())
                txfNomeExib.clear();
            else if (oldV && !Nvl(txfCodExib.getText()).isEmpty()) {
                if (DBObjects.salaContains(Integer.valueOf(txfCodSala.getText())))
                    txfNomeSala.setText(DBObjects.getSalaByCod(this.getClass(),
                            Integer.valueOf(txfCodSala.getText())).getRefSala());
                else {
                    new ModelDialog(this.getClass(), Alert.AlertType.WARNING,
                            String.format("Sala não encontrada para código %s", txfCodSala.getText())).getAlert().showAndWait();
                    txfCodSala.clear();
                    txfNomeSala.clear();
                }
            }
        });*/
    }

    @Override
    public void init() {
        loadTableValues();
        tbvSessoes.getSelectionModel().clearAndSelect(0);
    }

    @Override
    public void loadTableValues() {
        conex.remakeConexao();
        DBObjects.reloadAll();
        try {
            sessaoObservableList.clear();
            conex.createStatement("SELECT SES.CODSESSAO, SAL.CODSALA, FIL.CODFILME, EXB.CODEXIB, SES.DATAHORA\n" +
                    "    FROM TSESSOES SES\n" +
                    "        JOIN TFILMES FIL ON (SES.CODFILME = FIL.CODFILME)\n" +
                    "        JOIN TGENEROS GEN ON (FIL.CODGENERO = GEN.CODGENERO)\n" +
                    "        JOIN TSALAS SAL ON (SES.CODSALA = SAL.CODSALA)\n" +
                    "        JOIN TEXIBS EXB ON (SES.CODEXIB = EXB.CODEXIB)\n" +
                    "    WHERE SES.CODSESSAO <> 0\n" +
                    "    AND DATE_FORMAT(SES.DATAHORA, '%Y-%m-%d') >= ?\n" +
                    "    AND DATE_FORMAT((SES.DATAHORA + INTERVAL FIL.MINFILME MINUTE), '%Y-%m-%d') <= ?\n" +
                    "    AND (? = 0 OR SAL.CODSALA = ?)\n" +
                    "    ORDER BY 1");
            conex.addParametro(dtpDataIniGrade.getValue(), dtpDataFimGrade.getValue(),
                    Nvl(txfCodSalaGrade.getText(), "0"), Nvl(txfCodSalaGrade.getText(), "0"));
            conex.createSet();
            while (conex.rs.next()) {
                Sessao sessao = new Sessao(
                        conex.rs.getInt(1),
                        DBObjects.getSalaByCod(this.getClass(), conex.rs.getInt(2)),
                        DBObjects.getFilmeByCod(this.getClass(), conex.rs.getInt(3)),
                        DBObjects.getExibicaoByCod(this.getClass(), conex.rs.getInt(4)),
                        conex.rs.getTimestamp(5));
                sessaoObservableList.add(sessao);
            }
            sendMensagem(lblMensagem, true, "Tabela de Sessões atualizada com sucesso!");
        } catch (Exception ex) {
            new ModelException(this.getClass(),
                    String.format("Erro ao tentar atualizar tabela de sessões\n%s", ex.getMessage()), ex)
                    .getAlert().showAndWait();
            sessaoObservableList.clear();
        } finally {
            conex.desconecta();
        }
    }

    @Override
    public TableColumn[] getTableColumns() {
        TableColumn[] tableColumns = new TableColumn[7];
        tableColumns[0] = new ModelTableColumn<Sessao, Integer>("#", "codSessao", TableColumnType.Inteiro).setPercentSize(2.5);

        /*---------------------------> Coluna de Salas*/
        tableColumns[1] = new ModelTableColumn<Sessao, Object>("Sala", null, null);
        TableColumn[] tbColunasSala = new TableColumn[3];
        tbColunasSala[0] = new ModelTableColumn<Sessao, Integer>("#", null, TableColumnType.Inteiro).setPercentSize(3.5);
        tbColunasSala[0].setCellValueFactory((Callback<TableColumn.CellDataFeatures<Sessao, Integer>, ObservableValue<Integer>>)
                p -> new ReadOnlyObjectWrapper(p.getValue().getSala().getCodSala()));
        tbColunasSala[1] = new ModelTableColumn<Sessao, Integer>("Referência", null, TableColumnType.Texto_Pequeno).setPercentSize(10.0);
        tbColunasSala[1].setCellValueFactory((Callback<TableColumn.CellDataFeatures<Sessao, String>, ObservableValue<String>>)
                p -> new ReadOnlyObjectWrapper(p.getValue().getSala().getRefSala()));
        tbColunasSala[2] = new ModelTableColumn<Sessao, Integer>("Capacidade", null, TableColumnType.Texto_Pequeno);
        tbColunasSala[2].setCellValueFactory((Callback<TableColumn.CellDataFeatures<Sessao, String>, ObservableValue<Integer>>)
                p -> new ReadOnlyObjectWrapper(p.getValue().getSala().getCapacidade()));
        tableColumns[1].getColumns().addAll(tbColunasSala);
        /*Fim da Coluna de Salas <--------------------------*/

        /*---------------------------> Coluna de Filmes*/
        tableColumns[2] = new ModelTableColumn<Sessao, Object>("Filme", null, null);
        TableColumn[] tbColunasFilme = new TableColumn[4];
        tbColunasFilme[0] = new ModelTableColumn<Sessao, Integer>("#", null, TableColumnType.Inteiro).setPercentSize(3.5);
        tbColunasFilme[0].setCellValueFactory((Callback<TableColumn.CellDataFeatures<Sessao, Integer>, ObservableValue<Integer>>)
                p -> new ReadOnlyObjectWrapper(p.getValue().getFilme().getCodFilme()));
        tbColunasFilme[1] = new ModelTableColumn<Sessao, Integer>("Nome", null, TableColumnType.Texto_Pequeno).setPercentSize(20.0);
        tbColunasFilme[1].setCellValueFactory((Callback<TableColumn.CellDataFeatures<Sessao, String>, ObservableValue<String>>)
                p -> new ReadOnlyObjectWrapper(p.getValue().getFilme().getNomeFilme()));
        tbColunasFilme[2] = new ModelTableColumn<Sessao, Integer>("Gênero", null, TableColumnType.Texto_Pequeno).setPercentSize(7.5);
        tbColunasFilme[2].setCellValueFactory((Callback<TableColumn.CellDataFeatures<Sessao, String>, ObservableValue<Integer>>)
                p -> new ReadOnlyObjectWrapper(p.getValue().getFilme().getGenero().getNomeGenero()));
        tbColunasFilme[3] = new ModelTableColumn<Sessao, Integer>("Duração(Min.)", null, TableColumnType.Inteiro).setPercentSize(5.0);
        tbColunasFilme[3].setCellValueFactory((Callback<TableColumn.CellDataFeatures<Sessao, String>, ObservableValue<Integer>>)
                p -> new ReadOnlyObjectWrapper(p.getValue().getFilme().getMinFilme()));
        tableColumns[2].getColumns().addAll(tbColunasFilme);
        /*Fim da Coluna de Filmes <--------------------------*/

        tableColumns[3] = new ModelTableColumn<Sessao, String>("Exibição", null, TableColumnType.Texto_Pequeno);
        tableColumns[3].setCellValueFactory((Callback<TableColumn.CellDataFeatures<Sessao, String>, ObservableValue<String>>)
                p -> new ReadOnlyObjectWrapper(p.getValue().getExibicao().getNomeExibicao()));

        tableColumns[4] = new ModelTableColumn<Sessao, Timestamp>("Data/Hora Exib.", "dataHoraExib", TableColumnType.Data_Hora).setPercentSize(12.0);
        tableColumns[5] = new ModelTableColumn<Sessao, Timestamp>("Data/Hora Fim", "dataHoraFim", TableColumnType.Data_Hora).setPercentSize(12.0);

        /*---------------------------> Coluna de Preços*/
        tableColumns[6] = new ModelTableColumn<Sessao, Double>("Preços", null, null);
        TableColumn[] tbColunasPrecos = new TableColumn[2];
        tbColunasPrecos[0] = new ModelTableColumn<Sessao, Integer>("Inteira", null, TableColumnType.Dinheiro).setPercentSize(5.0);
        tbColunasPrecos[0].setCellValueFactory((Callback<TableColumn.CellDataFeatures<Sessao, Double>, ObservableValue<Double>>)
                p -> new ReadOnlyObjectWrapper(p.getValue().getExibicao().getVlrExibicao()));
        tbColunasPrecos[1] = new ModelTableColumn<Sessao, Integer>("Meia", null, TableColumnType.Dinheiro).setPercentSize(5.0);
        tbColunasPrecos[1].setCellValueFactory((Callback<TableColumn.CellDataFeatures<Sessao, Double>, ObservableValue<Double>>)
                p -> new ReadOnlyObjectWrapper(p.getValue().getExibicao().getVlrExibicao() / 2));
        tableColumns[6].getColumns().addAll(tbColunasPrecos);
        /*Fim da Coluna de Preços <--------------------------*/

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
        txfCodSala.setText(String.valueOf(sessao.getSala().getCodSala()));
        txfNomeSala.setText(sessao.getSala().getRefSala());
        txfCodExib.setText(String.valueOf(sessao.getExibicao().getCodExibicao()));
        txfNomeExib.setText(sessao.getExibicao().getNomeExibicao());
        dtpDataSes.setValue(sessao.getDataHoraExib().toLocalDateTime().toLocalDate());
        tmpHoraSes.setValue(sessao.getDataHoraExib().toLocalDateTime().toLocalTime());
        txfCodFilme.setText(String.valueOf(sessao.getFilme().getCodFilme()));
        txfNomeFilme.setText(sessao.getFilme().getNomeFilme());
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
