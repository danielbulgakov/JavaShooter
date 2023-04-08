package com.example.javashooter.myobjects;

import com.example.javashooter.connection.ClientInfo;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class PlayerInfoBox {

    public static VBox createVbox(ClientInfo cd) {
        VBox vb = new VBox();
        vb.setAlignment(Pos.TOP_CENTER);
        vb.prefWidth(250);
        Label l; Text t;

        l = new Label("Имя игрока"); l.getStyleClass().add("text-label");
        vb.getChildren().add(l);
        t = new Text(cd.getPlayerName()); t.getStyleClass().add("text");
        vb.getChildren().add(t);

        l = new Label("Кол-во выстрелов"); l.getStyleClass().add("text-label");
        vb.getChildren().add(l);
        t = new Text(Integer.toString(cd.getArrowsShoot())); t.getStyleClass().add("text");
        vb.getChildren().add(t);

        l = new Label("Кол-во  очков"); l.getStyleClass().add("text-label");
        vb.getChildren().add(l);
        t = new Text(Integer.toString(cd.getPointsEarned())); t.getStyleClass().add("text");
        vb.getChildren().add(t);

        return vb;
    }

    public static void setPlayerName (VBox vb, String s) {
        Text text = (Text) (((VBox) vb).getChildren().get(1));
        text.setText(s);
    }

    public static void setPlayerShots (VBox vb, int a) {
        Text text = (Text) ((vb).getChildren().get(3));
        text.setText(Integer.toString(a));
    }

    public static void setPlayerPoints (VBox vb, int a) {
        Text text = (Text) ((vb).getChildren().get(5));
        text.setText(Integer.toString(a));
    }


}
