package br.com.cinemafx.methods;

import javafx.application.Platform;
import javafx.scene.control.TextField;

public class MaskField {

    private static Boolean IsAtualizando = true;

    public static void MaxCharField(TextField Txt, int Max) {
        Txt.caretPositionProperty().addListener((observableValue, oldValue, newValue) -> {
            if (Txt.getText() != null && Txt.getLength() > Max) {
                if (IsAtualizando == true) {
                    Platform.runLater(() -> {
                        IsAtualizando = false;
                        Txt.deleteText(Max, Txt.getLength());
                        IsAtualizando = true;
                    });
                }
            }
        });
    }

    public static void MaxUpperCharField(TextField Txt, int Max) {
        Txt.caretPositionProperty().addListener((observableValue, oldValue, newValue) -> {
            if (Txt.getText() != null) {
                if (IsAtualizando) {
                    Platform.runLater(() -> {
                        IsAtualizando = false;
                        if (Txt.getLength() > Max) {
                            Txt.deleteText(Max, Txt.getLength());
                        }
                        Txt.setText(Txt.getText().toUpperCase());
                        Txt.positionCaret(Txt.getLength()); //É necessário pois, no setText() o cursor fica desconfigurado
                        IsAtualizando = true;
                    });
                }
            }
        });
    }
}
