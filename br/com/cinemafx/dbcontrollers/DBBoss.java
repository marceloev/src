package br.com.cinemafx.dbcontrollers;

import br.com.cinemafx.methods.Functions;
import br.com.cinemafx.models.*;
import br.com.cinemafx.views.dialogs.ModelDialog;
import br.com.cinemafx.views.dialogs.ModelException;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert.AlertType;
import javafx.util.Pair;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DBBoss {

    public static int checkIfExists(Class invocador, String nomeTabela, Pair<String, Object> filtro) throws Exception {
        Conexao conex = new Conexao(invocador); //No Pair, a chave já tem que vir com o operador relacional Ex: Pair<>("CODSALA <>", 1)
        try {
            conex.createStatement(String.format("SELECT COUNT(1) FROM %s\n" +
                    "WHERE 1 = 1\n" +
                    "AND %s ?", nomeTabela, filtro.getKey()));
            conex.addParametro(filtro.getValue());
            conex.createSet();
            conex.rs.next();
            return conex.rs.getInt(1);
        } catch (Exception ex) {
            throw new Exception(ex);
        } finally {
            conex.desconecta();
        }
    }

    public static int checkIfExists(Class invocador, String nomeTabela, ArrayList<Pair<String, Object>> filtros) throws Exception {
        Conexao conex = new Conexao(invocador);
        try {
            StringBuilder strBuildFiltros = new StringBuilder();
            ArrayList<Object> objFiltros = new ArrayList<>();
            for (Pair<String, Object> filtro : filtros) {
                objFiltros.add(filtro.getValue());
                strBuildFiltros.append(String.format("\nAND %s ?", filtro.getKey()));
            }
            conex.createStatement(String.format("SELECT COUNT(1) FROM %s\n" +
                    "WHERE 1 = 1 %s", nomeTabela, strBuildFiltros.toString()));
            conex.addParametro(objFiltros);
            conex.createSet();
            conex.rs.next();
            return conex.rs.getInt(1);
        } catch (Exception ex) {
            throw new Exception(ex);
        } finally {
            conex.desconecta();
        }
    }

    public static int inseriSala(Class invocador, Sala sala) throws Exception {
        if (checkIfExists(invocador, "TSALAS", new Pair<>("REFSALA = ", sala.getRefSala())) > 0)
            throw new Exception("Já existe uma sala com esta referência");
        Conexao conex = new Conexao(invocador);
        try {
            conex.createStatement("INSERT INTO TSALAS (REFSALA, CAPACIDADE)\n" +
                    "VALUES (?, ?)");
            conex.addParametro(sala.getRefSala(), sala.getCapacidade());
            conex.execute();
            conex.createStatement("SELECT LAST_INSERT_ID()");
            conex.createSet();
            conex.rs.next();
            return conex.rs.getInt(1);
        } catch (Exception ex) { //Precisamos fazer o catch mesmo trhows por causa do desconecta.
            throw new Exception(ex);
        } finally {
            conex.desconecta();
        }
    }

    public static void alteraSala(Class invocador, Sala sala) throws Exception {
        ArrayList<Pair<String, Object>> filtros = new ArrayList();
        filtros.add(new Pair<>("REFSALA =", sala.getRefSala()));
        filtros.add(new Pair<>("CODSALA <>", sala.getCodSala()));
        if (checkIfExists(invocador, "TSALAS", filtros) > 0)
            throw new Exception("Já existe uma sala com esta referência");
        Conexao conex = new Conexao(invocador);
        try {
            conex.createStatement("UPDATE TSALAS\n" +
                    "SET REFSALA = ?,\n" +
                    "CAPACIDADE = ?\n" +
                    "WHERE CODSALA = ?");
            conex.addParametro(sala.getRefSala(), sala.getCapacidade(), sala.getCodSala());
            conex.execute();
        } catch (Exception ex) {
            throw new Exception(ex);
        } finally {
            conex.desconecta();
        }
    }

    public static void excluiSala(Class invocador, ArrayList<Integer> salas) throws Exception {
        Conexao conex = new Conexao(invocador);
        try {
            conex.createStatement(String.format("DELETE FROM TSALAS WHERE CODSALA IN (%s)", Functions.paramBuilder(salas)));
            conex.addParametro(salas);
            conex.execute();
        } catch (Exception ex) {
            throw new Exception(ex);
        } finally {
            conex.desconecta();
        }
    }

    public static int inseriExibicao(Class invocador, Exibicao exibicao) throws Exception {
        Conexao conex = new Conexao(invocador);
        try {
            conex.createStatement("INSERT INTO TEXIBS (NOMEEXIB, VLREXIB)\n" +
                    "VALUES (?, ?)");
            conex.addParametro(exibicao.getNomeExibicao(), exibicao.getVlrExibicao());
            conex.execute();
            conex.createStatement("SELECT LAST_INSERT_ID()");
            conex.createSet();
            conex.rs.next();
            return conex.rs.getInt(1);
        } catch (Exception ex) {
            throw new Exception(ex);
        } finally {
            conex.desconecta();
        }
    }

    public static void alteraExibicao(Class invocador, Exibicao exibicao) throws Exception {
        Conexao conex = new Conexao(invocador);
        try {
            conex.createStatement("UPDATE TEXIBS\n" +
                    "SET NOMEEXIB = ?,\n" +
                    "VLREXIB = ?\n" +
                    "WHERE CODEXIB = ?");
            conex.addParametro(exibicao.getNomeExibicao(), exibicao.getVlrExibicao(), exibicao.getCodExibicao());
            conex.execute();
        } catch (Exception ex) {
            throw new Exception(ex);
        } finally {
            conex.desconecta();
        }
    }

    public static void excluiExibicao(Class invocador, ArrayList<Integer> exibicoes) throws Exception {
        Conexao conex = new Conexao(invocador);
        try {
            conex.createStatement(String.format("DELETE FROM TEXIBS WHERE CODEXIB IN (%s)", Functions.paramBuilder(exibicoes)));
            conex.addParametro(exibicoes);
            conex.execute();
        } catch (Exception ex) {
            throw new Exception(ex);
        } finally {
            conex.desconecta();
        }
    }

    public static Genero inseriGenero(Class invocador, Genero genero) throws Exception {
        if (checkIfExists(invocador, "TGENEROS", new Pair<>("NOMEGENERO = ", genero.getNomeGenero())) > 0) {
            throw new Exception("Já existe um gênero com este nome");
        }
        Conexao conex = new Conexao(invocador);
        try {
            conex.createStatement("INSERT INTO TGENEROS (NOMEGENERO)\n" +
                    "VALUES (?)");
            conex.addParametro(genero.getNomeGenero());
            conex.execute();
            conex.createStatement("SELECT LAST_INSERT_ID()");
            conex.createSet();
            conex.rs.next();
            return new Genero(conex.rs.getInt(1), genero.getNomeGenero());
        } catch (Exception ex) {
            throw new Exception(ex);
        } finally {
            conex.desconecta();
        }
    }

    public static int inseriFilme(Class invocador, Filme filme) throws Exception {
        if (filme.getGenero().getCodGenero() == -1)
            filme.setGenero(inseriGenero(invocador, filme.getGenero()));
        Conexao conex = new Conexao(invocador);
        try {
            conex.createStatement("INSERT INTO TFILMES (NOMEFILME, CUSTOFILME, MINFILME, CODGENERO, SINOPSE, IMAGEM)\n" +
                    "VALUES (?, ?, ?, ?, ?, ?)");
            conex.addParametro(filme.getNomeFilme(), filme.getCustoFilme(), filme.getMinFilme(),
                    filme.getGenero().getCodGenero(), filme.getSinopse(), filme.getCartazFilme());
            conex.execute();
            conex.createStatement("SELECT LAST_INSERT_ID()");
            conex.createSet();
            conex.rs.next();
            return conex.rs.getInt(1);
        } catch (Exception ex) {
            throw new Exception(ex);
        } finally {
            conex.desconecta();
        }
    }

    public static void alteraFilme(Class invocador, Filme filme) throws Exception {
        if (filme.getGenero().getCodGenero() == -1)
            filme.setGenero(inseriGenero(invocador, filme.getGenero()));
        Conexao conex = new Conexao(invocador);
        try {
            conex.createStatement("UPDATE TFILMES\n" +
                    "SET NOMEFILME = ?,\n" +
                    "CUSTOFILME = ?,\n" +
                    "MINFILME = ?,\n" +
                    "CODGENERO = ?,\n" +
                    "SINOPSE = ?,\n" +
                    "IMAGEM = ?\n" +
                    "WHERE CODFILME = ?");
            conex.addParametro(filme.getNomeFilme(), filme.getCustoFilme(), filme.getMinFilme(),
                    filme.getGenero().getCodGenero(), filme.getSinopse(), filme.getCartazFilme(), filme.getCodFilme());
            conex.execute();
        } catch (Exception ex) {
            throw new Exception(ex);
        } finally {
            conex.desconecta();
        }
    }

    public static void excluiFilme(Class invocador, ArrayList<Integer> filmes) throws Exception {
        Conexao conex = new Conexao(invocador);
        try {
            conex.createStatement(String.format("DELETE FROM TFILMES WHERE CODFILME IN (%s)", Functions.paramBuilder(filmes)));
            conex.addParametro(filmes);
            conex.execute();
        } catch (Exception ex) {
            throw new Exception(ex);
        } finally {
            conex.desconecta();
        }
    }

    public static void excluiSessao(Class invocador, ArrayList<Integer> sessoes) throws Exception {
        Conexao conex = new Conexao(invocador);
        try {
            conex.createStatement(String.format("DELETE FROM TSESSOES WHERE CODSESSAO IN (%s)", Functions.paramBuilder(sessoes)));
            conex.addParametro(sessoes);
            conex.execute();
        } catch (Exception ex) {
            throw new Exception(ex);
        } finally {
            conex.desconecta();
        }
    }

    public static void inseriSessao(Class invocador, ObservableList<Sessao> sessoes) throws Exception {
        if (sessoes == null || sessoes.isEmpty()) {
            throw new Exception("Não foram definidas sessões para o cadastro");
        }
        Conexao conex = null;
        ArrayList<Sessao> sessoesToExc = new ArrayList<>();
        try {
            for (Sessao sessao : sessoes) {
                conex = new Conexao(invocador);
                conex.createStatement("INSERT INTO TSESSOES (CODSALA, CODFILME, CODEXIB, DATAHORA)\n" +
                        "VALUES (?, ?, ?, ?)");
                conex.addParametro(sessao.getSala().getCodSala(),
                        sessao.getFilme().getCodFilme(),
                        sessao.getExibicao().getCodExibicao(),
                        sessao.getDataHoraExib());
                conex.execute();
                sessoesToExc.add(sessao);
                conex.desconecta();
            }
        } catch (Exception ex) {
            throw new Exception(ex);
        } finally {
            conex.desconecta();
            sessoes.removeAll(sessoesToExc);
        }
    }
}
