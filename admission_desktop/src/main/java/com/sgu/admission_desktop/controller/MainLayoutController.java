package com.sgu.admission_desktop.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.net.URL;
import java.util.EnumMap;
import java.util.Map;
import java.util.ResourceBundle;

public class MainLayoutController implements Initializable {

    @FXML
    private StackPane pageHost;

    @FXML
    private SidebarController sidebarController;

    @FXML
    private HeaderController headerController;

    private final Map<PageId, Node> pageCache = new EnumMap<>(PageId.class);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        sidebarController.setOnNavigate(this::navigateTo);
        navigateTo(PageId.DASHBOARD);
    }

    public void navigateTo(PageId page) {
        if (page == null) page = PageId.DASHBOARD;
        sidebarController.setActive(page);
        headerController.setTitleDesc(page.title(), page.desc());

        Node content = pageCache.computeIfAbsent(page, this::loadPage);
        pageHost.getChildren().setAll(content);
    }

    private Node loadPage(PageId page) {
        try {
            FXMLLoader loader = new FXMLLoader(MainLayoutController.class.getResource("/com/sgu/admission_desktop/fxml/" + page.fxmlPath()));
            return loader.load();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load page: " + page.id(), e);
        }
    }
}

