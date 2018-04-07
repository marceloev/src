package br.com.cinemafx.dbcontrollers;

import br.com.cinemafx.methods.Functions;
import br.com.cinemafx.models.Sala;
import br.com.cinemafx.views.dialogs.ModelDialog;
import br.com.cinemafx.views.dialogs.ModelException;
import javafx.scene.control.Alert.AlertType;

import java.sql.SQLException;
import java.util.ArrayList;

public class DBBoss {

    public static void inseriSala(Class invocador, Sala sala) throws Exception {
        Conexao conex = new Conexao(invocador);
        try {
            conex.createStatement("INSERT INTO TSALAS (REFSALA, CAPACIDADE)\n" +
                    "VALUES (?, ?)");
            conex.addParametro(sala.getRefSala(), sala.getCapacidade());
            conex.execute();
        } catch (Exception ex) { //Precisamos fazer o catch mesmo trhows por causa do desconecta.
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
