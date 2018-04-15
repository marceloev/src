package br.com.cinemafx.dbcontrollers;

import static br.com.cinemafx.methods.Functions.paramBuilder;

import br.com.cinemafx.models.ParametroType;
import br.com.cinemafx.views.dialogs.ModelException;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Arrays;

public class DBFunctions {

    public static int checkIfExists(Class invocador, String tabela, Pair<String, Object[]> filter) {
        Conexao conex = new Conexao(invocador);
        int count = 0;
        try {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(String.format("SELECT COUNT(1) FROM %s", tabela));
            if (filter != null)
                stringBuilder.append(String.format("\nWHERE %s IN (%s)", filter.getKey(),
                        paramBuilder(new ArrayList<>(Arrays.asList(filter.getValue()))).toString()));
            conex.createStatement(stringBuilder.toString());
            conex.addParametro(filter.getValue());
            conex.createSet();
            conex.rs.next();
            count = conex.rs.getInt(1);
        } catch (Exception ex) {
            new ModelException(invocador, String.format("Erro ao tentar contar registros na tabela %s\nFavor contate o suporte", tabela), ex)
                    .getAlert().showAndWait();
            count = -1;
        } finally {
            conex.desconecta();
            return count;
        }
    }

    public static Object getUserParametro(Class invocador, String chave, ParametroType parametroType, int codUsu) {
        Conexao conex = new Conexao(invocador);
        Object resp = -1;
        try {
            conex.createStatement(String.format("SELECT %s FROM TPARAMETROS\n" +
                            "WHERE CHAVE = ?\n" +
                            "AND CODUSU IN (?, 0)\n" +
                            "ORDER BY CODUSU DESC", parametroType.toString().toUpperCase()));
            conex.addParametro(chave, codUsu);
            conex.createSet();
            if (conex.rs.next()) {
                resp = conex.rs.getObject(1);
            } else {
                resp = -1;
            }
        } catch (Exception ex) {
            new ModelException(invocador, String.format("Erro ao tentar buscar parâmetro %s\n%s", chave, ex.getMessage()), ex)
                    .getAlert().showAndWait();
        } finally {
            conex.desconecta();
            return resp;
        }
    }
}
