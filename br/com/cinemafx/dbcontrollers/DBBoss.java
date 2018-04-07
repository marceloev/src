package br.com.cinemafx.dbcontrollers;

import br.com.cinemafx.methods.Functions;
import br.com.cinemafx.models.Sala;
import br.com.cinemafx.views.dialogs.ModelDialog;
import br.com.cinemafx.views.dialogs.ModelException;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Alert.AlertType;
import javafx.util.Pair;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DBBoss {

    public static int checkIfExists(Class invocador, String nomeTabela, Pair<String, Object> filtro) throws Exception {
        Conexao conex = new Conexao(invocador);
        try {
            conex.createStatement(String.format("SELECT COUNT(1) FROM %s\n" +
                    "WHERE 1 = 1\n" +
                    "AND %s = ?", nomeTabela, filtro.getKey()));
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
                strBuildFiltros.append(String.format("\nAND %s = ?", filtro.getKey()));
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
        if (checkIfExists(invocador, "TSALAS", new Pair<>("REFSALA", sala.getRefSala())) > 0)
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
            //if(ex.ge)
            throw new Exception(ex);
        } finally {
            conex.desconecta();
        }
    }

    public static void alteraSala(Class invocador, Sala sala) throws Exception {
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
}
