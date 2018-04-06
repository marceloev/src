package br.com.cinemafx.methods;

import javafx.scene.image.Image;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;

public class Functions {

    private static final Image noImageFilme = new Image("/br/com/cinemafx/views/images/Icon_Sem_Imagem.png");

    public static final String Nvl(String valor) {
        if (valor == null) return "";
        else return valor;
    }

    public static Boolean ToBoo(String valor) {
        if(valor.equals("S") || valor.equals("Sim") || valor.equals("s") || valor.equals("sim") || valor.equals("1"))
            return true;
        else
            return false;
    }

    public static final Image getImgFromBlob(byte[] bytes) {
        if (bytes != null) {
            InputStream input = new ByteArrayInputStream(bytes);
            Image imgUsu = new Image(input);
            return imgUsu;
        } else return noImageFilme;
    }

    public static StringBuilder paramBuilder(ArrayList<?> array) {
        StringBuilder paramBuilder = new StringBuilder();
        for (int i = 0; i < array.size(); i++) {
            if (i == 0)  paramBuilder.append("?");
            else paramBuilder.append(", ?");
        }
        return paramBuilder;
    }
}
