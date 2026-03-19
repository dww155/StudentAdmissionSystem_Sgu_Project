package org.example.studentadmissionsystem.controller;

import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

import java.util.function.Consumer;

public class SidebarController {

    @FXML
    private VBox navBox;

    private Consumer<PageId> onNavigate = page -> {};

    public void setOnNavigate(Consumer<PageId> onNavigate) {
        this.onNavigate = onNavigate == null ? page -> {} : onNavigate;
    }

    @FXML
    private void onNav(javafx.event.ActionEvent e) {
        if (!(e.getSource() instanceof Button btn)) return;
        PageId page = PageId.fromId(String.valueOf(btn.getUserData()));
        onNavigate.accept(page);
    }

    @FXML
    private void onChangePassword(ActionEvent e) {
        onNavigate.accept(PageId.ADMIN_CHANGE_PASSWORD);
    }

    public void setActive(PageId page) {
        if (navBox == null) return;
        for (Node n : navBox.getChildren()) {
            if (n instanceof Button b) {
                b.getStyleClass().remove("sidebar-item-active");
                if (String.valueOf(b.getUserData()).equalsIgnoreCase(page.id())) {
                    b.getStyleClass().add("sidebar-item-active");
                }
            }
        }
    }
}

