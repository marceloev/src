package br.com.cinemafx.models;

import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.ImageViewBuilder;

public class ModelTableColumn<S, T> extends TableColumn {

    private final ImageView imgFilterEmpty = ImageViewBuilder.create()
            .image(new Image("/br/com/cinemafx/views/images/Icone_Filtro_Vazio.png"))
            .style("-fx-alignment: RIGHT")
            .build();
    private final ImageView imgFilterFill = ImageViewBuilder.create()
            .image(new Image("/br/com/cinemafx/views/images/Icone_Filtro_Cheio.png"))
            .style("-fx-alignment: RIGHT")
            .build();

    public ModelTableColumn(String titulo, String key, TableColumnType tipo) {
        this.setText(titulo);
        this.setStyle("-fx-alignment: CENTER");
        if (key != null) createCellValueFactory(key);
        if (tipo != null) createCellFactory(tipo);
    }

    private void createCellValueFactory(String key) {
        this.setCellValueFactory(new PropertyValueFactory<>(key));
    }

    private void createCellFactory(TableColumnType tipo) {
        switch (tipo) {
            case Lógico:
                System.err.println("ModelTableColumn Lógico not programmed");
                break;
            case Opcoes:
                //this.setCellFactory(col -> );
                break;
            case Inteiro:
                break;
            case Texto_Grande:
                break;
            case Texto_Pequeno:
                break;
        }
    }
}
