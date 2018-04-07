package br.com.cinemafx.models;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.*;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.ImageViewBuilder;
import javafx.util.Duration;

public interface CadCtrlIntface {

    Timeline timeline = new Timeline();
    BooleanProperty atualizando = new SimpleBooleanProperty(false);
    IntegerProperty propFrameStatus = new SimpleIntegerProperty(0);
    FrameStatus frameStatus = new FrameStatus();
    ImageView imgForm = ImageViewBuilder.create().image(new Image("/br/com/cinemafx/views/images/Icone_Modo_Formulario.png"))
            .fitHeight(31)
            .fitWidth(35)
            .build();
    ImageView imgGrade = ImageViewBuilder.create().image(new Image("/br/com/cinemafx/views/images/Icone_Modo_Grade.png"))
            .fitHeight(31)
            .fitWidth(35)
            .build();
    ImageView imgSucesso = ImageViewBuilder.create().image(new Image("/br/com/cinemafx/views/images/Sucesso.png")).build();
    ImageView imgAlerta = ImageViewBuilder.create().image(new Image("/br/com/cinemafx/views/images/Alerta.png")).build();

    void estrutura();

    void appCalls();

    void init();

    void loadTableValues();

    TableColumn[] getTableColumns();

    void ctrlAction(FrameAction frameAction);

    default void sendMensagem(Label lbl, Boolean sucesso, String mensagem) {
        if (sucesso) {
            timeline.stop();
            lbl.setGraphic(imgSucesso);
            lbl.setVisible(true);
            lbl.setText(mensagem);
            timeline.getKeyFrames().clear();
            timeline.getKeyFrames().add(
                    new KeyFrame(Duration.millis(2000),
                            ae -> lbl.setVisible(false)));
            timeline.play();
        } else {
            timeline.stop();
            lbl.setGraphic(imgAlerta);
            lbl.setVisible(true);
            lbl.setText(mensagem);
            timeline.getKeyFrames().clear();
            timeline.getKeyFrames().add(
                    new KeyFrame(Duration.millis(4000),
                            ae -> lbl.setVisible(false)));
            timeline.play();
        }
    }

    default boolean isAtualizando() {
        return atualizando.getValue();
    }

    default void setAtualizando(Boolean valor) {
        atualizando.setValue(valor);
    }

    default FrameStatus.Status getFrameStatus() {
        return frameStatus.status;
    }

    default void setFrameStatus(FrameStatus.Status status) {
        frameStatus.setStatus(status);
        propFrameStatus.setValue(status.getValor());
    }

    default void notifyEdit(Runnable changes) {
        if (getFrameStatus() == FrameStatus.Status.Visualizando) {
            setFrameStatus(FrameStatus.Status.Alterando);
        }
        changes.run();
    }

    default void runEdits(Runnable runnable) {
        setAtualizando(true);
        runnable.run();
        setAtualizando(false);
    }
}
