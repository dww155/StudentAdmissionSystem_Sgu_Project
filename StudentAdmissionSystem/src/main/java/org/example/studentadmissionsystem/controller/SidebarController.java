package org.example.studentadmissionsystem.controller;

import javafx.fxml.FXML;
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

