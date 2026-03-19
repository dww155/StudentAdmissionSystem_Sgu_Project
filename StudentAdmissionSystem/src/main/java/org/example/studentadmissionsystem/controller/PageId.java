package org.example.studentadmissionsystem.controller;

public enum PageId {
    DASHBOARD("dashboard", "Tổng quan", "Thống kê tổng hợp hệ thống tuyển sinh", "pages/dashboard.fxml"),
    STUDENTS("students", "Quản lý thí sinh", "Xem, thêm, sửa, xóa thông tin thí sinh", "pages/students.fxml"),
    MAJORS("majors", "Ngành tuyển sinh", "Quản lý danh sách ngành đào tạo", "pages/majors.fxml"),
    SUBJECTS("subjects", "Tổ hợp môn", "Quản lý tổ hợp môn xét tuyển", "pages/subjects.fxml"),
    MAJOR_SUBJECTS("major-subjects", "Ngành - Tổ hợp", "Liên kết ngành với tổ hợp môn", "pages/major-subjects.fxml"),
    SCORES("scores", "Điểm thí sinh", "Quản lý điểm THPT, VSAT, ĐGNL", "pages/scores.fxml"),
    BONUS("bonus", "Điểm cộng", "Quản lý điểm ưu tiên, khuyến khích", "pages/bonus.fxml"),
    WISHES("wishes", "Nguyện vọng", "Quản lý đăng ký và xét tuyển", "pages/wishes.fxml"),
    CONVERSION("conversion", "Bảng quy đổi", "Quản lý quy đổi điểm giữa các loại", "pages/conversion.fxml"),
    ADMIN_CHANGE_PASSWORD("admin-change-password", "Đổi mật khẩu", "Đổi mật khẩu cho quản trị viên", "pages/admin-change-password.fxml");

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
        if (id == null) return DASHBOARD;
        for (PageId p : values()) {
            if (p.id.equalsIgnoreCase(id)) return p;
        }
        return DASHBOARD;
    }
}

