package br.com.cinemafx.methods;

import javafx.application.Platform;
import javafx.scene.control.*;

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

    public static void CharField(TextField Txt, int Max) {
        Txt.lengthProperty().addListener((obs, oldV, newV) -> {
            if (Txt.getLength() > Max && Max > 0) {
                Txt.deleteText(Max, Txt.getLength());
            }
        });
    }

    public static void CharField(TextArea Txt, int Max) {
        Txt.lengthProperty().addListener((obs, oldV, newV) -> {
            if (Txt.getLength() > Max && Max > 0) {
                Txt.deleteText(Max, Txt.getLength());
            }
        });
    }

    public static void CharField(ComboBox Cbb, int Max) {
        Cbb.cellFactoryProperty().addListener((obs, oldV, newV) -> {
            if (Cbb.getValue().toString().length() > Max && Max > 0) {
                Cbb.setValue(Cbb.getValue().toString().substring(0, Max));
            }
        });
    }

    public static void NumberField(TextField Txt, int Max) {
        Txt.lengthProperty().addListener((observable, oldValue, newValue) -> {
            if (Txt.getLength() > 0 && !Txt.getText().matches("^[0-9]{" + Txt.getLength() + "}")) {
                Txt.setText(getOnlyDigits(Txt.getText()));
            }
            if (Txt.getLength() > Max && Max > 0) {
                Txt.deleteText(Max, Txt.getLength());
            }
        });
    }

    public static void NCMField(TextField Txt) {
        Txt.lengthProperty().addListener((observableValue, oldV, newV) -> {
            if (IsAtualizando) { //Não pode ser Platform.RunLater, se não cai no textproperty
                IsAtualizando = false;
                String text = getOnlyDigits(Txt.getText());
                if (text.length() > 8) text = text.substring(0, 8);
                text = text.replaceFirst("(\\d{6})(\\d)", "$1.$2");
                text = text.replaceFirst("(\\d{4})(\\d)", "$1.$2");
                Txt.setText(text);
                Platform.runLater(() -> Txt.positionCaret(Txt.getLength()));
                IsAtualizando = true;
            }
        });
    }

    public static void SpnFieldCtrl(Spinner<Integer> Spn, Integer Min, Integer Max) {
        Spn.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(Min, Max, Min));
        String value = "" + Max;
        NumberField(Spn.getEditor(), value.length());
        Spn.getEditor().textProperty().addListener((obs, oldV, newV) -> {
            if (!newV.matches(oldV)) {
                Platform.runLater(() -> {
                    if (IsAtualizando) {
                        if (Spn.getEditor().getLength() == 0 || Functions.getOnlyNumbers(Spn.getEditor().getText()) < Min) {
                            IsAtualizando = false;
                            Spn.getEditor().setText("" + Min);
                            IsAtualizando = true;
                        } else if (Integer.valueOf(Spn.getEditor().getText()) > Max) {
                            IsAtualizando = false;
                            Spn.getEditor().setText("" + Max);
                            IsAtualizando = true;
                        }
                    }
                });
            }
        });
        Spn.focusedProperty().addListener((obs, wasF, isF) -> {
            if (wasF) {
                if (Spn.getEditor().getLength() == 0) {
                    Spn.getEditor().setText("" + Min);
                }
                Spn.getValueFactory().setValue(Functions.getOnlyNumbers(Spn.getEditor().getText()));
            }
        });
    }

    public static void SpnFieldCtrl(Spinner<Double> Spn, Double Min, Double Max) {
        Spn.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(Min, Max, Min));
        Spn.getEditor().textProperty().addListener((obs, oldV, newV) -> {
            if (!newV.matches(oldV)) {
                if (newV.length() != 0 &&
                        !newV.matches("[0-9]{1,3}") &&
                        !newV.matches("[0-9]{1,3}[,]{1}") &&
                        !newV.matches("[0-9]{1,3}[.]{1}") &&
                        !newV.matches("[0-9]{1,3}[,]{1}[0-9]{1,2}") &&
                        !newV.matches("[0-9]{1,3}[.]{1}[0-9]{1,2}")) {
                    Platform.runLater(() -> {
                        int Pos = Spn.getEditor().getCaretPosition();
                        Spn.getEditor().setText(oldV);
                        Spn.getEditor().positionCaret(Pos - 1);
                    });
                }
            }
        });
        Spn.focusedProperty().addListener((obs, wasF, isF) -> {
            if (wasF) {
                if (Spn.getEditor().getLength() == 0) {
                    Spn.getEditor().setText("" + Min.toString().replace(".", ","));
                }
                Spn.getValueFactory().setValue(Double.parseDouble(Spn.getEditor().getText().replace(",", ".")));
            }
        });
    }

    public static String getOnlyDigits(String strTarget) {
        String strResult = strTarget.replaceAll("[^0-9]", "");
        return strResult;
    }
}
