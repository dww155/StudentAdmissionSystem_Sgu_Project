package com.sgu.admission_desktop.controller;

public enum PageId {
    DASHBOARD("dashboard", "Tong quan", "Thong ke tong hop he thong tuyen sinh", "pages/dashboard.fxml"),
    STUDENTS("students", "Quan ly thi sinh", "Xem, them, sua, xoa thong tin thi sinh", "pages/students.fxml"),
    MAJORS("majors", "Nganh tuyen sinh", "Quan ly danh sach nganh dao tao", "pages/majors.fxml"),
    SUBJECTS("subjects", "To hop mon", "Quan ly to hop mon xet tuyen", "pages/subjects.fxml"),
    MAJOR_SUBJECTS("major-subjects", "Nganh - To hop", "Lien ket nganh voi to hop mon", "pages/major-subjects.fxml"),
    SCORES("scores", "Diem THPT", "Quan ly diem thi THPT", "pages/scores.fxml"),
    VSAT_RESULTS("vsat-results", "Ket qua VSAT", "Quan ly ket qua thi VSAT", "pages/vsat-results.fxml"),
    ENGLISH_CERTIFICATIONS(
            "english-certifications",
            "Chung chi tieng Anh",
            "Quan ly chung chi tieng Anh va diem quy doi",
            "pages/english-certifications.fxml"
    ),
    BONUS("bonus", "Diem cong", "Quan ly diem uu tien, khuyen khich", "pages/bonus.fxml"),
    PRIORITY_BONUS_POINTS(
            "priority-bonus-points",
            "Diem cong uu tien",
            "Quan ly diem cong uu tien theo CCCD",
            "pages/priority-bonus-points.fxml"
    ),
    WISHES("wishes", "Nguyen vong", "Quan ly dang ky va xet tuyen", "pages/wishes.fxml"),
    CONVERSION("conversion", "Bang quy doi", "Quan ly quy doi diem giua cac loai", "pages/conversion.fxml"),
    ADMIN_CHANGE_PASSWORD("admin-change-password", "Doi mat khau", "Doi mat khau cho quan tri vien", "pages/admin-change-password.fxml");

    private final String id;
    private final String title;
    private final String desc;
    private final String fxmlPath;

    PageId(String id, String title, String desc, String fxmlPath) {
        this.id = id;
        this.title = title;
        this.desc = desc;
        this.fxmlPath = fxmlPath;
    }

    public String id() {
        return id;
    }

    public String title() {
        return title;
    }

    public String desc() {
        return desc;
    }

    public String fxmlPath() {
        return fxmlPath;
    }

    public static PageId fromId(String id) {
        if (id == null) {
            return DASHBOARD;
        }

        for (PageId page : values()) {
            if (page.id.equalsIgnoreCase(id)) {
                return page;
            }
        }

        return DASHBOARD;
    }
}
