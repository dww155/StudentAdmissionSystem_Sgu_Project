package com.sgu.admission_desktop.util;

public final class URLUtil {

    private URLUtil() {
    }

    // ===================== BASE URL =====================
    public static final String BASE_URL = "http://localhost:8080/sas";

    private static String build(String endpoint) {
        return BASE_URL + endpoint;
    }

    // ===================== AUTH =====================
    public static final class AUTH {
        private AUTH() {
        }

        public static final String LOGIN = build("/auth/login");
        public static final String INTROSPECT = build("/auth/introspect");
    }

    // ===================== SUBJECT COMBINATIONS =====================
    public static final class SUBJECT_COMBINATION {
        private SUBJECT_COMBINATION() {
        }

        public static final String GET_ALL = build("/subject-combinations");
        public static final String CREATE = build("/subject-combinations");
        public static final String CREATE_BULK = build("/subject-combinations/bulk");

        public static String GET_BY_ID(int id) {
            return build("/subject-combinations/" + id);
        }

        public static String UPDATE(int id) {
            return build("/subject-combinations/" + id);
        }

        public static String DELETE(int id) {
            return build("/subject-combinations/" + id);
        }
    }

    // ===================== MAJORS =====================
    public static final class MAJOR {
        private MAJOR() {
        }

        public static final String GET_ALL = build("/majors");
        public static final String CREATE = build("/majors");
        public static final String CREATE_BULK = build("/majors/bulk");

        public static String GET_BY_ID(int id) {
            return build("/majors/" + id);
        }

        public static String UPDATE(int id) {
            return build("/majors/" + id);
        }

        public static String DELETE(int id) {
            return build("/majors/" + id);
        }
    }

    // ===================== MAJOR SUBJECT GROUPS =====================
    public static final class MAJOR_SUBJECT_GROUP {
        private MAJOR_SUBJECT_GROUP() {
        }

        public static final String GET_ALL = build("/major-subject-groups");
        public static final String CREATE = build("/major-subject-groups");
        public static final String CREATE_BULK = build("/major-subject-groups/bulk");

        public static String GET_BY_ID(int id) {
            return build("/major-subject-groups/" + id);
        }

        public static String UPDATE(int id) {
            return build("/major-subject-groups/" + id);
        }

        public static String DELETE(int id) {
            return build("/major-subject-groups/" + id);
        }
    }

    // ===================== EXAM SCORES =====================
    public static final class EXAM_SCORE {
        private EXAM_SCORE() {
        }

        public static final String GET_ALL = build("/exam-scores");
        public static final String CREATE = build("/exam-scores");
        public static final String CREATE_BULK = build("/exam-scores/bulk");

        public static String GET_BY_ID(int id) {
            return build("/exam-scores/" + id);
        }

        public static String UPDATE(int id) {
            return build("/exam-scores/" + id);
        }

        public static String DELETE(int id) {
            return build("/exam-scores/" + id);
        }
    }

    // ===================== CONVERSION RULES =====================
    public static final class CONVERSION_RULE {
        private CONVERSION_RULE() {
        }

        public static final String GET_ALL = build("/conversion-rules");
        public static final String CREATE = build("/conversion-rules");
        public static final String CREATE_BULK = build("/conversion-rules/bulk");

        public static String GET_BY_ID(int id) {
            return build("/conversion-rules/" + id);
        }

        public static String UPDATE(int id) {
            return build("/conversion-rules/" + id);
        }

        public static String DELETE(int id) {
            return build("/conversion-rules/" + id);
        }
    }

    // ===================== APPLICANTS =====================
    public static final class APPLICANT {
        private APPLICANT() {
        }

        public static final String GET_ALL = build("/applicant");
        public static final String CREATE = build("/applicant");
        public static final String CREATE_BULK = build("/applicant/bulk");

        public static String GET_PAGINATED(int page, int size, String sortBy, String sortDir) {
            return build("/applicant/paginated?page=" + page + "&size=" + size + "&sortBy=" + sortBy + "&sortDir=" + sortDir);
        }

        public static String GET_BY_ID(int id) {
            return build("/applicant/" + id);
        }

        public static String UPDATE(int id) {
            return build("/applicant/" + id);
        }

        public static String DELETE(int id) {
            return build("/applicant/" + id);
        }
    }

    // ===================== ADMISSION PREFERENCES =====================
    public static final class ADMISSION_PREFERENCE {
        private ADMISSION_PREFERENCE() {
        }

        public static final String GET_ALL = build("/admission-preferences");
        public static final String CREATE = build("/admission-preferences");
        public static final String CREATE_BULK = build("/admission-preferences/bulk");

        public static String GET_BY_ID(int id) {
            return build("/admission-preferences/" + id);
        }

        public static String UPDATE(int id) {
            return build("/admission-preferences/" + id);
        }

        public static String DELETE(int id) {
            return build("/admission-preferences/" + id);
        }
    }

    // ===================== ADMISSION BONUS SCORES =====================
    public static final class ADMISSION_BONUS_SCORE {
        private ADMISSION_BONUS_SCORE() {
        }

        public static final String GET_ALL = build("/admission-bonus-scores");
        public static final String CREATE = build("/admission-bonus-scores");
        public static final String CREATE_BULK = build("/admission-bonus-scores/bulk");

        public static String GET_BY_ID(int id) {
            return build("/admission-bonus-scores/" + id);
        }

        public static String UPDATE(int id) {
            return build("/admission-bonus-scores/" + id);
        }

        public static String DELETE(int id) {
            return build("/admission-bonus-scores/" + id);
        }
    }

    // ===================== TEST =====================
    public static final class TEST {
        private TEST() {
        }

        public static final String HELLO = build("/hello");
    }

    // ===================== STORAGE KEYS =====================
    public static final class STORAGE_KEYS {
        private STORAGE_KEYS() {
        }

        public static final String AUTH_TOKEN = "authToken";
        public static final String USER_NAME = "userName";
    }
}
