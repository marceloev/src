package br.com.cinemafx.dbcontrollers;

import br.com.cinemafx.methods.Functions;
import br.com.cinemafx.models.Sala;
import br.com.cinemafx.views.dialogs.ModelDialog;
import br.com.cinemafx.views.dialogs.ModelException;
import javafx.scene.control.Alert.AlertType;

import java.util.ArrayList;

public class DBBoss {

    public static void inseriSala(Class invocador, Sala sala) {
        Conexao conex = new Conexao(invocador);
        try {
            conex.createStatement("INSERT INTO TSALAS (REFSALA, CAPACIDADE)\n" +
                    "VALUES (?, ?)");
            conex.addParametro(sala.getRefSala(), sala.getCapacidade());
            conex.execute();
            new ModelDialog(invocador,
                    AlertType.INFORMATION,
                    String.format("Sala %s cadastrada com sucesso", sala.getRefSala())).getAlert().showAndWait();
        } catch (Exception ex) {
            new ModelException(invocador,
                    String.format("Erro ao tentar cadastrar sala\n %s", ex.getMessage()), ex).getAlert().showAndWait();
        } finally {
            conex.desconecta();
        }
    }

    public static void alteraSala(Class invocador, Sala sala) {
        Conexao conex = new Conexao(invocador);
        try {
            conex.createStatement("UPDATE TSALAS\n" +
                    "SET REFSALA = ?,\n" +
                    "CAPACIDADE = ?\n" +
                    "WHERE CODSALA = ?");
            conex.addParametro(sala.getRefSala(), sala.getCapacidade(), sala.getCodSala());
            conex.execute();
            new ModelDialog(invocador,
                    AlertType.INFORMATION,
                    String.format("Sala %s atualizada com sucesso", sala.getRefSala())).getAlert().showAndWait();
        } catch (Exception ex) {
            new ModelException(invocador,
                    String.format("Erro ao tentar atualizar sala\n %s", ex.getMessage()), ex).getAlert().showAndWait();
        } finally {
            conex.desconecta();
        }
    }

    public static void excluiSala(Class invocador, ArrayList<Integer> salas) {
        Conexao conex = new Conexao(invocador);
        try {
            conex.createStatement(String.format("DELETE FROM TSALAS WHERE CODSALA IN (%s)", Functions.paramBuilder(salas)));
            conex.addParametro(salas);
            conex.execute();
            new ModelDialog(invocador,
                    AlertType.INFORMATION,
                    String.format("Sala(s) excluída(s) com sucesso: %s", salas.toString())).getAlert().showAndWait();
        } catch (Exception ex) {
            new ModelException(invocador,
                    String.format("Erro ao tentar excluir sala(s)\n %s", ex.getMessage()), ex).getAlert().showAndWait();
        } finally {
            conex.desconecta();
        }
    }
}
