CREATE OR REPLACE PACKAGE SERVICE_ELIGIBILITY_PKG AS
    /*
     * Legacy eligibility checks. Rules overlap with SHIPMENT_RATING_PKG by design.
     * Error codes are negative integers consumed by upstream callers.
     */
    PROCEDURE check_service (
        p_service_code   IN  VARCHAR2,
        p_weight_lbs     IN  NUMBER,
        p_dg_flag        IN  CHAR,
        p_dest_region    IN  VARCHAR2,
        p_oversize_flag  IN  CHAR,
        p_error_code     OUT NUMBER,
        p_error_msg      OUT VARCHAR2
    );
END SERVICE_ELIGIBILITY_PKG;
/

CREATE OR REPLACE PACKAGE BODY SERVICE_ELIGIBILITY_PKG AS

    c_max_air_weight CONSTANT NUMBER := 150;

    PROCEDURE check_service (
        p_service_code   IN  VARCHAR2,
        p_weight_lbs     IN  NUMBER,
        p_dg_flag        IN  CHAR,
        p_dest_region    IN  VARCHAR2,
        p_oversize_flag  IN  CHAR,
        p_error_code     OUT NUMBER,
        p_error_msg      OUT VARCHAR2
    ) IS
    BEGIN
        p_error_code := 0;
        p_error_msg  := NULL;

        IF p_dest_region <> 'US' AND p_service_code <> 'EXP' THEN
            p_error_code := -2050;
            p_error_msg  := 'International destination requires EXPRESS';
            RETURN;
        END IF;

        IF p_weight_lbs > c_max_air_weight AND p_service_code IN ('EXP', 'PO') THEN
            p_error_code := -2041;
            p_error_msg  := 'Weight exceeds air service limit';
            RETURN;
        END IF;

        IF p_dg_flag = 'Y' AND p_service_code = 'PO' THEN
            p_error_code := -2042;
            p_error_msg  := 'Dangerous goods not allowed on Priority Overnight';
            RETURN;
        END IF;

        IF p_oversize_flag = 'Y' AND p_service_code IN ('EXP', 'PO') THEN
            p_error_code := -2043;
            p_error_msg  := 'Oversize package limited to Ground';
            RETURN;
        END IF;
    END check_service;

END SERVICE_ELIGIBILITY_PKG;
/
