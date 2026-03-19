package org.example.studentadmissionsystem.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class HeaderController {

    @FXML
    private Label pageTitle;

    @FXML
    private Label pageDesc;

    public void setTitleDesc(String title, String desc) {
        if (pageTitle != null) pageTitle.setText(title == null ? "" : title);
        if (pageDesc != null) pageDesc.setText(desc == null ? "" : desc);
    }
}

