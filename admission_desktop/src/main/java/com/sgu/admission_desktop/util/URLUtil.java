package com.sgu.admission_desktop.util;

public class URLUtil {

    // ===================== BASE URL =====================
    public static final String BASE_URL = "http://localhost:8080/sas";

    // ===================== HELPER =====================
    private static String build(String endpoint) {
        return BASE_URL + endpoint;
    }

    // ===================== AUTH =====================
    public static class AUTH {
        public static final String LOGIN = build("/auth/login");
        public static final String REGISTER = build("/auth/register");
        public static final String LOGOUT = build("/auth/logout");
        public static final String REFRESH = build("/auth/refresh");
        public static final String INTROSPECT = build("/auth/introspect");
    }

    // ===================== USER =====================
    public static class USER {
        public static final String CREATE = build("/users");
        public static final String GET_ALL = build("/users");
        public static final String GET_INFO = build("/users/info");

        public static String GET_BY_ID(String id) {
            return build("/users/" + id);
        }

        public static String UPDATE(String id) {
            return build("/users/" + id);
        }

        public static String UPDATE_PASSWORD(String id) {
            return build("/users/" + id);
        }

        public static String DELETE(String id) {
            return build("/users/" + id);
        }
    }

    // ===================== PRODUCT =====================
    public static class PRODUCT {
        public static final String CREATE = build("/products");
        public static final String GET_ALL = build("/products");
        public static final String PAGINATE = build("/products/paginate");

        public static String GET_BY_ID(String id) {
            return build("/products/" + id);
        }

        public static String UPDATE(String id) {
            return build("/products/" + id);
        }

        public static String DELETE(String id) {
            return build("/products/" + id);
        }
    }

    // ===================== STAFF =====================
    public static class STAFF {
        public static final String CREATE = build("/staffs");
        public static final String GET_ALL = build("/staffs");
        public static final String GET_INFO = build("/staffs/info");

        public static String GET_BY_ID(String id) {
            return build("/staffs/" + id);
        }

        public static String UPDATE(String id) {
            return build("/staffs/" + id);
        }

        public static String UPDATE_SHIFT(String id, int shift) {
            return build("/staffs/" + id + "/shift?shift=" + shift);
        }

        public static String DELETE(String id) {
            return build("/staffs/" + id);
        }
    }

    // ===================== CUSTOMER =====================
    public static class CUSTOMER {
        public static final String CREATE = build("/customers");
        public static final String GET_ALL = build("/customers");
        public static final String GET_INFO = build("/customers/info");

        public static String GET_BY_ID(String id) {
            return build("/customers/" + id);
        }

        public static String UPDATE(String id) {
            return build("/customers/" + id);
        }

        public static String ADD_POINTS(String id) {
            return build("/customers/" + id + "/points");
        }

        public static String ADD_CART_ITEM(String customerId) {
            return build("/customers/" + customerId + "/items");
        }

        public static String DELETE(String id) {
            return build("/customers/" + id);
        }
    }

    // ===================== INVOICE =====================
    public static class INVOICE {
        public static final String CREATE = build("/invoices");
        public static final String REVIEW = build("/invoices/review");
        public static final String GET_ALL = build("/invoices");

        public static String GET_BY_ID(String id) {
            return build("/invoices/" + id);
        }

        public static String GET_BY_CUSTOMER(String customerId) {
            return build("/invoices/customer/" + customerId);
        }

        public static String GET_BY_STAFF(String staffId) {
            return build("/invoices/staff/" + staffId);
        }

        public static String DELETE(String id) {
            return build("/invoices/" + id);
        }
    }

    // ===================== PET =====================
    public static class PET {
        public static final String CREATE = build("/pets");
        public static final String GET_ALL = build("/pets");
        public static final String PAGINATE = build("/pets/paginate");

        public static String GET_BY_ID(String id) {
            return build("/pets/" + id);
        }

        public static String UPDATE(String id) {
            return build("/pets/" + id);
        }

        public static String MARK_SOLD(String id) {
            return build("/pets/" + id + "/sold");
        }

        public static String DELETE(String id) {
            return build("/pets/" + id);
        }
    }

    // ===================== PURCHASE =====================
    public static class PURCHASE {
        public static final String CREATE = build("/purchases");
        public static final String GET_ALL = build("/purchases");

        public static String GET_BY_ID(String id) {
            return build("/purchases/" + id);
        }

        public static String DELETE(String id) {
            return build("/purchases/" + id);
        }
    }

    // ===================== PROMOTION =====================
    public static class PROMOTION {
        public static final String CREATE = build("/promotions");
        public static final String GET_ALL = build("/promotions");

        public static String GET_BY_ID(String id) {
            return build("/promotions/" + id);
        }

        public static String DELETE(String id) {
            return build("/promotions/" + id);
        }
    }

    // ===================== SUPPLIER =====================
    public static class SUPPLIER {
        public static final String CREATE = build("/suppliers");
        public static final String GET_ALL = build("/suppliers");

        public static String GET_BY_ID(String id) {
            return build("/suppliers/" + id);
        }

        public static String UPDATE(String id) {
            return build("/suppliers/" + id);
        }

        public static String DELETE(String id) {
            return build("/suppliers/" + id);
        }
    }

    // ===================== CATEGORY =====================
    public static class CATEGORY {
        public static final String CREATE = build("/categories");
        public static final String GET_ALL = build("/categories");

        public static String GET_BY_ID(String id) {
            return build("/categories/" + id);
        }

        public static String UPDATE(String id) {
            return build("/categories/" + id);
        }

        public static String DELETE(String id) {
            return build("/categories/" + id);
        }
    }

    // ===================== INVENTORY =====================
    public static class INVENTORY {
        public static final String GET = build("/inventories");
    }

    // ===================== STORAGE KEYS =====================
    public static class STORAGE_KEYS {
        public static final String AUTH_TOKEN = "authToken";
        public static final String USER_NAME = "userName";
    }
}