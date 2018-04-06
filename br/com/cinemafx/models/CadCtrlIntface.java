package br.com.cinemafx.models;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.ImageViewBuilder;
import javafx.util.Duration;

public interface CadCtrlIntface {

    Timeline timeline = new Timeline();
    BooleanProperty atualizando = new SimpleBooleanProperty(false);
    FrameStatus framestatus = new FrameStatus();
    ImageView imgForm = ImageViewBuilder.create()
            .image(new Image("/br/com/cinemafx/views/images/Icone_Modo_Formulario.png"))
            .fitHeight(31)
            .fitWidth(35)
            .build();
    ImageView imgGrade = ImageViewBuilder.create()
            .image(new Image("/br/com/cinemafx/views/images/Icone_Modo_Grade.png"))
            .fitHeight(31)
            .fitWidth(35)
            .build();

    void estrutura();

    void appCalls();

    void init();

    void loadTableValues();

    TableColumn[] getTableColumns();

    void ctrlAction(FrameAction frameAction);

    default void sendMensagem(Label lbl, String mensagem) {
        timeline.stop();
        lbl.setVisible(true);
        lbl.setText(mensagem);
        timeline.getKeyFrames().clear();
        timeline.getKeyFrames().add(
                new KeyFrame(Duration.millis(2000),
                        ae -> lbl.setVisible(false)));
        timeline.play();
    }

    default boolean isAtualizando() {
        return atualizando.getValue();
    }

    default void setAtualizando(Boolean valor) {
        atualizando.setValue(valor);
    }

    default FrameStatus.Status getStatus() {
        return framestatus.getStatus();
    }

    default void setFrameStatus(FrameStatus.Status status) {
        this.framestatus.setStatus(status);
    }

    default void runEdits(Runnable runnable) {
        setAtualizando(false);
        runnable.run();
        setAtualizando(true);
    }
}
